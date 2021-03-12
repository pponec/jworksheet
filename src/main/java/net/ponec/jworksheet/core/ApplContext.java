/**
 * Copyright (C) 2007-9, Pavel Ponec, contact: http://ponec.net/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/gpl-2.0.txt
 */


package net.ponec.jworksheet.core;

import net.ponec.jworksheet.module.JwsContext;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.gui.JWorkSheet;
import net.ponec.jworksheet.module.ModuleApi;
import net.ponec.jworksheet.report.MetaReport;
import net.ponec.jworksheet.resources.ResourceProvider;
import org.ujorm.core.UjoManagerRBundle;
import org.ujorm.core.UjoManagerXML;
import org.ujorm.core.XmlHeader;

/**
 * A Main Application Context
 * @author Pavel Ponec
 */
public class ApplContext implements TableModelListener, Runnable, JwsContext {

    /** Is the language manager enabled? */
    public static final boolean LANGUAGE_MANAGER_ENABLED = true;

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ApplContext.class.getName());

    /** Lock File */
    public static final String FILE_LOCK = "locker.lck";

    /** Data XML File */
    public static final String FILE_DATA = "data.xml";

    /** Shared Projects Data XML File */
    public static final String PROJECTS_DATA = "projects.xml";

    /** Style subdirectory */
    public static final String FILE_STYLES = "styles";

    /** Subdirectory of modules */
    public static final String FILE_MODULES = "modules";

    /** A temporarry extension */
    public static final String EXTENSION_TMP = ".tmp";

    public static final String MODULE_ENTRY = "META-INF/JWS-MODULE.MF";

    /** Basic window */
    protected JWorkSheet topFrame;

    /** LanguageMananer */
    protected LanguageManager languageManager;

    /** SysTray */
    private SysTray systray;

    /** The User config dir (from an application parameter) */
    private String userConfigDir = null;


    /** Parameters of the application. */
    protected Parameters parameters = new Parameters();

    protected WorkSpace workSpace;

    protected WorkDay currentDay;

    /** Last Saved Time */
    protected long timeSaveExpected  = 0L;
    protected final int timeInterval = 1000*30; // [sec];

    /** Initializaton flag */
    private boolean initialized = false;

    /** Warning: data is restored from a backup! */
    private boolean dataRestored = false;

    private List<ModuleApi> modules;

    // -----------------------------------------------------------------

    /** Creates a new instance of ApplContext */
    public ApplContext() {
        systray = SysTray.getInstance(this);
    }

    /** Returns WorkSpace */
    public WorkSpace getWorkSpace(){
        return workSpace;
    }

    /** Load Parameters & Data.
     * Set the current day. */
    public void loadData() {
        if (true) {
            float oldVersion = getDataVersion();
            if (oldVersion>0
            &&  oldVersion<0.78f
            ){
                makeDataConversion(oldVersion);
            }
        }

        deleteTempFiles();
        saveStyleFiles();
        loadParameters();

        final File dataFile   = getDataFile();
        final File backupFile = getDataFileBackup();
        final File projectsFile = getProjectsFile();
        boolean dataFileExists = dataFile.isFile() &&  dataFile.length()>0;
        boolean backupFileExists = dataFileExists ? false : (backupFile.isFile() &&  backupFile.length()>0);
        boolean projectsFileExists = projectsFile.isFile() && projectsFile.length()>0;

        if (!dataFileExists && backupFileExists) try {
            ApplTools.copy(backupFile, dataFile);
            dataRestored = true;
            dataFileExists = backupFileExists;
        } catch (Throwable e) {
            throw new MessageException("Can't restore data file\n "
                + dataFile
                + " from\n "
                + backupFile
                , e
                );
        }

        if (dataFileExists) try {
            WorkSpace result = UjoManagerXML.getInstance().parseXML(getDataFile(), WorkSpace.class, "Data Loading");
            workSpace = (WorkSpace) result;
            workSpace.assingTasks();
        } catch (Throwable e) {
            throw new MessageException("Can't load file: " + dataFile, e);
        } else {
            workSpace = new WorkSpace();
            workSpace.createDemoData();
        }

        // sync projects from shared list
        if (projectsFileExists) try {
            WorkSpace result = UjoManagerXML.getInstance().parseXML(getProjectsFile(), WorkSpace.class, "Shared Projects Loading");
            workSpace.syncProjects(result);
        } catch (Throwable e) {
            throw new MessageException("Can't load file: " + projectsFile, e);
        }

        // Backup an Original File, if parsing and loading was successful.
        if (isTimeToBackup() && dataFile.exists()) try {
            ApplTools.copy(dataFile, backupFile);
            WorkSpace.P_ARCHIVED.setValue(workSpace, new Date());
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Can't create backup", e);
        }

        // Select TODAY
        selectWorkDay(new YearMonthDay());
    }

    public void initModules() {
        modules = new ArrayList<ModuleApi>();
        String fileName = "?";
        for (File file : getModulesDir().listFiles()) {
            if (file.getName().endsWith(".jar")) try {
                fileName = file.getPath();
                JarFile jarFile = new JarFile(file);
                JarEntry jarEntry = jarFile.getJarEntry(MODULE_ENTRY);
                if (jarEntry!=null) {
                    InputStream is = new BufferedInputStream(jarFile.getInputStream(jarEntry));
                    ByteArrayOutputStream os = new ByteArrayOutputStream(64);
                    int c;
                    while ((c=is.read())!=-1) { os.write(c); }
                    String content = os.toString("UTF-8");
                    int i = 1+content.indexOf(':');
                    String className = content.substring(i).trim();
                    if (ApplTools.isValid(content)) {
                        Class<ModuleApi> mod = ApplTools.getClass(className, file);
                        ModuleApi mapi = mod.newInstance();
                        mapi.setJwsContext(this);
                        this.modules.add(mapi);
                    }
                }
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "Can't load module from file: " + fileName, e);
            }
        }
    }


    /** Is the time to backup data?
     *  If the archived timestamp is undefined, then method setup the timestamp to current date only
     *  however it does not overwrite the previous backup file by reason of  better security.
     * <br>WARNING: The method must be called allways, because it can initialize a WorkSpace.P_ARCHIVED property.
     */
    private boolean isTimeToBackup() {
        Calendar cal = Calendar.getInstance();
        long day2 = getDayCount(cal);
        Date date = WorkSpace.P_ARCHIVED.of(workSpace);

        if (date==null) {
            WorkSpace.P_ARCHIVED.setValue(workSpace, cal.getTime());
            return false;
        } else {
            cal.setTime(date);
            long day1 = getDayCount(cal);
            return (day2 > day1) && !isDataRestored();
        }
    }

    /** Count of day from year 0 (an approximately number). */
    private long getDayCount(Calendar cal) {
        return cal.get(Calendar.DAY_OF_YEAR) + cal.get(Calendar.YEAR)*365;
    }

    /** Save data into file(s) */
    public synchronized void saveData(boolean includeParams) {
        try {
            // WorkSpace attributes
            workSpace.set(WorkSpace.P_CREATED, new Date());
            workSpace.set(WorkSpace.P_VERSN  , JWorkSheet.APPL_VERSION);
            workSpace.set(WorkSpace.P_USERNAME, Parameters.P_USERNAME.of(getParameters()));

            String header = false ? null // Default Header
            : UjoManagerXML.XML_HEADER
                    + "\n<!-- <?xml-stylesheet type=\"text/xsl\" href=\"styles/"
                    + ResourceProvider.REPORT_BASE
                    + "\"?> -->";
            File dataTempFile = getDataFileTemp();
            UjoManagerXML.getInstance().saveXML(dataTempFile, workSpace, new XmlHeader(header), this);
            ApplTools.rename(dataTempFile, getDataFile());
        } catch (IOException e) {
            throw new MessageException("Can't save: " + getDataFile(), e);
        }

        if (includeParams) {
            // Window Sizing:
            Rectangle rect
            = Parameters.P_WINDOW_SIZE_RESTORATION.of(parameters)
            ? topFrame.getBounds()
            : Parameters.P_WINDOW_SIZE.getDefault() ;
            Parameters.P_WINDOW_SIZE.setValue(parameters,rect);

            saveParameters();
        }
    }

    /** Close The Application (no exit) */
    public void closeAppl(boolean closeWindow) {

        createExitEvent();
        saveData(true);

        if (closeWindow) {
            getTopFrame().setVisible(false);
            getTopFrame().dispose();
        }

        // Unlock the application:
        final File lock = new File(getConfigDir(), FILE_LOCK);
        if (lock.exists()) {
            lock.delete();
        }

        fireModuleEvent();

    }

    /** Create an Exit row, if it is enabled. */
    public void createExitEvent() {

        if (Parameters.P_EXIT_EVENT_CREATE.of(parameters) ) {
            WorkDay today = workSpace.findWorkDay(new YearMonthDay());
            List<Event> events = WorkDay.P_EVENTS.getList(today);

            if (events.size()>=1) {
                Event lastEvent   = events.get(events.size()-1);
                Time finishedTime = lastEvent.getTimeFinished();
                Time currentTime  = new Time(true);

                if (currentTime.compareTo(finishedTime)>=0) {
                    String exitDescr = Parameters.P_EXIT_EVENT_DESCR.of(parameters);
                    Event exitEvent;

                    if (Event.P_PROJ.of(lastEvent)==null
                    &&  Event.P_TASK.of(lastEvent)==null
                    &&  Event.P_DESCR.equals(lastEvent, exitDescr)
                    &&  events.size()>=2
                    ){
                        exitEvent = lastEvent;
                        lastEvent = events.get(events.size()-2);
                    } else {
                        exitEvent = new Event();
                        events.add(exitEvent);
                    }

                    Event.P_TIME .setValue(exitEvent, currentTime);
                    Event.P_DESCR.setValue(exitEvent, exitDescr);
                    Event.P_PERIOD.setValue(lastEvent, currentTime.substract( Event.P_TIME.of(lastEvent) ));
                }
            }
        }
    }


    /** Configuration Directory */
    public File getConfigDir() {
        File result;
        if (userConfigDir!=null) {
            result = new File(userConfigDir);
            if (!result.isDirectory() || !result.canWrite()) {
                throw new MessageException("Can't write data to a parameter directory: " + userConfigDir);
            }
        } else {
            result = new File(System.getProperty("user.home"), Parameters.CONFIG_DIR);
            if (!result.isDirectory()) {
                if (!result.mkdirs()) {
                    throw new MessageException("Can't create directory: " + result);
                }
            }
        }
        return result;
    }

    /** Style Directory */
    public File getStyleDir() {
        final File result = new File(getConfigDir(), FILE_STYLES);
        return result;
    }

    /** Get Modules directory and create one if the directory is missing. */
    public File getModulesDir() {
        final File result = new File(getConfigDir(), FILE_MODULES);
        if (!result.isDirectory()) {
            result.mkdirs();
        }
        return result;
    }


    /** Configuration Directory */
    public File getConfigFile() {
        final File result = new File(getConfigDir(), "config.properties");
        return result;
    }

    /** Data File */
    public File getDataFile() {
        File result = Parameters.P_DATA_FILE_PATH.of(getParameters());
        if (result==Parameters.P_DATA_FILE_PATH.getDefault()) {
            result = new File(getConfigDir(), FILE_DATA);
        }
        return result;
    }

    /** Shared Projects Data File */
    public File getProjectsFile() {
        File result = Parameters.P_PROJECTS_FILE_PATH.of(getParameters());
        if (result==Parameters.P_PROJECTS_FILE_PATH.getDefault()) {
            result = new File(getConfigDir(), PROJECTS_DATA);
        }
        return result;
    }

    /** Data File for a temporarry usage. */
    public File getDataFileTemp() throws IOException {
        final File result = File.createTempFile("data", EXTENSION_TMP, getConfigFile().getParentFile());
        return result;
    }

    /** Delete all temporarry files from a Config Directory. */
    public void deleteTempFiles() {
        File configDir = getConfigDir();
        for (String item : configDir.list()) {
            if (item.endsWith(EXTENSION_TMP)) {
                File file = new File(configDir, item);
                file.delete();
            }
        }
    }

    /** Data File Backup */
    public File getDataFileBackup() {
        final File result = new File(getConfigDir(), "data.backup.xml");
        return result;
    }

    /** Get a style file */
    public File getStyleFile(String fileName) {
        final File result = new File(getConfigDir(), FILE_STYLES+"/"+fileName);
        return result;
    }


    /** Get "Selected Day" - don't modify it! */
    public YearMonthDay getSelectedDay() {
        return WorkDay.P_DATE.of(currentDay);
    }

    /** GetCurrentDay */
    public String getSelectedDayStr() {
        return getSelectedDay().toString(this);
    }

    /** Is selected time Today? */
    public boolean isToday() {
        return new YearMonthDay().equals(getSelectedDay());
    }

    /** Current Language */
    public Locale getLanguage() {
        return Parameters.P_LANG.of(parameters);
    }

    /**
     * Select a WorkDay by parameter
     * @see #getWorkDay()
     */
    public void selectWorkDay(YearMonthDay workDay) {
        currentDay = workSpace.findWorkDay(workDay);
    }

    /** Get Work Day */
    public WorkDay getWorkDay() {
        return currentDay;
    }

    /** Save a data in a special thread, if data is changed (TableModelListener). */
    public void tableChanged(TableModelEvent e) {
        if (initialized
        &&  timeSaveExpected<System.currentTimeMillis()
        ){
            timeSaveExpected = System.currentTimeMillis() + timeInterval;
            SwingUtilities.invokeLater(this);
        }
    }

    /** Save data (file: data.xml) */
    @Override
    public void run() {
        try {
            saveData(true);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Can't save data", e);
        }
    }

    /** Create new lock. */
    public boolean createLock(boolean force) {
        File lock = new File(getConfigDir(), FILE_LOCK);
        try {
            if (lock.createNewFile() || force) {
                lock.deleteOnExit();
                return true;
            }
            return false;
        } catch (IOException ex) {
            throw new MessageException("Can't create a lock " + lock);
        }
    }

    /** Parameters of the application. */
    public Parameters getParameters() {
        return parameters;
    }

    /** Load parameters from file. */
    protected void loadParameters() {
        File dataFile = getConfigFile();
        if (dataFile.exists() && dataFile.canRead()) try {
            parameters = UjoManagerRBundle.of(Parameters.class).loadResourceBundle(dataFile, false, "props");
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Can't load Parameters to " + dataFile, e);
        }
    }

    /** Save styles into config directory. */
    public void saveStyleFiles() {
        InputStream is = null;
        String fileName = null;
        File target;
        try {
            File xslDir = getStyleDir();
            if (!xslDir.isDirectory()) {
                if (!xslDir.mkdirs()) {
                    throw new RuntimeException("Can't create directory: " + xslDir);
                }
            }

            /** Copy files to target: */
            final String[] fileNames =
            { ResourceProvider.REPORT_BASE
            , ResourceProvider.REPORT_BASE2
            , ResourceProvider.REPORT_BASE3
            , ResourceProvider.FILE_CSS
            , ResourceProvider.LOGO16
            };
            for (String f : fileNames) {
                is = new ResourceProvider().getUrl(f).openStream();
                target = getStyleFile(f);
                ApplTools.copy(is, target);
                is.close();
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Can't create an file " + fileName);
        } finally {
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException ex) {}
            }
        }
    }

    /** Save parameters into file. */
    public void saveParameters() {
        try {
            final String msg = "A " + JWorkSheet.APPL_NAME + " configuration file:";
            UjoManagerRBundle.getInstance(Parameters.class).saveResourceBundle(getConfigFile(), parameters, msg, this);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Can't save Parameters to " + getConfigFile(), e);
        }
    }

//    /** Initializaton flag */
//    public boolean isInitialized() {
//        return initialized;
//    }

    /** Initializaton flag */
    public void setInitialized() {
        try {
           initModules();
        } catch (Throwable e ) {
           LOGGER.log(Level.WARNING, "err", e);
        }
        if (LANGUAGE_MANAGER_ENABLED) {
           // translate the application:
           languageManager.setLocaleAndTranslate(Parameters.P_LANG.of(parameters), true);
        }

        fireModuleEvent();
        this.initialized = true;
    }

    public boolean isStarting() {
        return !this.initialized;
    }

    /** File an module event. */
    @SuppressWarnings("unchecked")
    public void fireModuleEvent() {
        for (ModuleApi mod : this.modules) {
            mod.eventListener(isStarting());
        }
    }

    /** Returns an old version. */
    public float getDataVersion() {
        try {
            DecimalFormat form = ApplTools.createDecimalFormat("0.0");
            String tagContent = getTagContent(getDataFile(), WorkSpace.P_VERSN.getName());
            float result = tagContent!=null ? form.parse(tagContent).floatValue() : -1 ;
            return result;
        } catch (Throwable ex) {
            return -1f;
        }
    }

    /** Returns a tag content. */
    public String getTagContent(final File file, final String tagName) throws IOException {

        String data = ApplTools.getFileContent(file, Charset.forName("UTF-8"), 300);
        String tag1 = "<"  + tagName + ">";
        String tag2 = "</" + tagName + ">";
        int i1 = data.indexOf(tag1);
        int i2 = data.indexOf(tag2, i1+1);

        if (i1>0 && i2>i1) {
            final String result = data.substring(i1+tag1.length(), i2);
            return result;
        } else {
            return null;
        }
    }

    /** Get JWorkSheet */
    public JWorkSheet getTopFrame() {
        return topFrame;
    }

    /** Set JWorkSheet */
    public void setTopFrame(JWorkSheet topFrame, boolean showDebugWindow) {
        this.topFrame = topFrame;

        if (LANGUAGE_MANAGER_ENABLED) {
            this.languageManager = new LanguageManager(topFrame, ResourceProvider.class, showDebugWindow);
            this.languageManager.setLocaleAndTranslate(Parameters.P_LANG.of(parameters), false);
        }
    }

    /** Set a user configuration directory */
    public void setUserConfigDir(String userConfigDir) {
        this.userConfigDir = userConfigDir;
    }


    /**
     * Show a report in HTML viewer:
     * @param aData Null value means a BasicDataFile
     * @param aXsl
     * @throws javax.xml.transform.TransformerConfigurationException
     * @throws javax.xml.transform.TransformerException
     * @throws java.io.IOException
     */
    public void showReport(String aData, File aXsl, ArrayList<String[]> params) throws TransformerConfigurationException, TransformerException, IOException {
        StreamSource src = aData!=null ? new StreamSource(aData) : new StreamSource(getDataFile());
        StreamSource xsl = new StreamSource(aXsl);
        File result = ApplTools.makeXslTransformation(src, xsl, params);
        topFrame.browse(result.toURI().toString());
    }

    /** Get Sorted Reports. */
    public List<MetaReport> getMetaReports() {
        File reportDir = getStyleDir();
        String[] list = reportDir.list();
        List<MetaReport> result = new ArrayList<MetaReport>(list.length);

        for(String item : list) try {
            if (item.endsWith(".xsl")) {
                final File file = new File(reportDir, item);
                final String title = getTagContent(file, "title");
                final String copyright = getTagContent(file, "copyright");
                final String dataType  = FILE_DATA;
                final MetaReport report = new MetaReport(title, file, dataType );
                result.add(report);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Can't read an XSl file.", e);
        }
        Collections.sort(result);
        return result;
    }

    /** Systray is enabled in case a parameter is supported AND system is supported too */
    public boolean isSystrayEnabled() {
        boolean result
        = Parameters.P_SYSTRAY_ENABLED.of(getParameters())
        && systray.isSupported()
        ;
        return result;
    }


    /** Returns the Last Event Today, or null. */
    public Event findLastEventToday() {
        if (getWorkSpace()==null) { return null; }

        //WorkDay workDay = getWorkSpace().findWorkDay(getSelectedDay()); // YearMonthDay
        WorkDay workDay = getWorkSpace().findWorkDay(new YearMonthDay());
        List<Event> events = WorkDay.P_EVENTS.getList(workDay);
        int lastIndex = events.size()-1;
        return lastIndex>=0 ? events.get(lastIndex) : null;
    }

    /** Display a Tooltip into a Systray */
    public void setSystrayTooltip() {
        StringBuilder message = new StringBuilder();
        //
        Event event = findLastEventToday();
        if (event!=null) {
            Project  proj = Event.P_PROJ.of(event);
            if (proj!=null) {
                message.append(Project.P_DESCR.of(proj));
                //
                TaskType task = Event.P_TASK.of(event);
                if (task!=null) {
                    message.append(" / ");
                    message.append(TaskType.P_DESCR.of(task));
                }
            }
        }
        systray.setTooltip(message.toString());
        topFrame.setTitle(message.toString());
    }

    /** Show report in a browser. */
    public void showReport(String report) throws IOException {
        Writer writer = null;
        try {
            File tempFile = File.createTempFile("_report.", ".html");
            tempFile.deleteOnExit();
            FileOutputStream os = new FileOutputStream(tempFile);
            writer = new OutputStreamWriter(os, "utf-8");
            writer.write(report);
            writer.close();
            getTopFrame().browse(tempFile.toURI().toString());

        } catch (IOException e)  {
            if (writer!=null) {
                try {
                    writer.close();
                } catch (Throwable ex) {
                    LOGGER.log(Level.WARNING, "Can't close a temp file.");
                }
            }
            throw e;
        }
    }

    /** Warning: data is restored from a backup! */
    public boolean isDataRestored() {
        return dataRestored;
    }


    // ----------------- COVERSIONS ------------------------

    private void makeDataConversion(float oldVersion) {
        FileOutputStream os = null;
        try {
            String data = ApplTools.getFileContent(getDataFile(), Charset.forName("UTF-8"), Integer.MAX_VALUE);
            data = data.replaceAll("<Time javaClass=\"java.lang.String\">", "<Time>" );

            //data = data.replaceAll("<item>"  , " " );
            //data = data.replaceAll("</item>" , " " );

            File dataFileTemp = getDataFileTemp();
            File dataFileTarget = getDataFile();

            os = new FileOutputStream(dataFileTemp);
            os.write(data.getBytes("UTF-8"));
            os.close();
            ApplTools.rename(dataFileTemp, dataFileTarget);

        } catch (IOException e) {
            throw new UnsupportedOperationException("Can't convert data file from version: " + oldVersion);
        } finally {
            if (os!=null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Bug", ex);
                }
            }
        }
    }

    /** Returns manager */
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    /** Get a main Tabbed pane of the application */
    public JTabbedPane getTabbedPane() {
        return getTopFrame().getTabbedPane();
    }

}
