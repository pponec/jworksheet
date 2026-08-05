/**
 * Copyright (C) 2007-2026, Pavel Ponec, contact: https://ponec.net/
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


package net.ponec.jworksheet.bo;

import jakarta.persistence.Column;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import net.ponec.jworksheet.bo.meta.MetaParameters;
import net.ponec.jworksheet.core.*;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.ValueCoder;
import net.ponec.jworksheet.gui.JWorkSheet;
import org.ujorm.annotation.Domain;
import org.ujorm.tools.Check;

/**
 * Parameters of the application.
 * @author Pavel Ponec
 */
@Domain
public class Parameters implements Exportable {

    /** A configuration subdirectory of a user home directory. */
    public static final String CONFIG_DIR = ".jWorkSheet";

    /** A "Default Value" Label */
    private static final String VALUE_DEFAULT = "<default>";

    /** A default format of the main date selection. It is shared by the report format. */
    private static final String DATE_FORMAT_DEFAULT = "EE, yyyy-MM-dd' %s: 'ww";

    /** LocaleText lcalization */
    public static final JKey<Parameters,Locale> P_LANG = JKey.of(MetaParameters.language);
    /** Working Hours */
    public static final JKey<Parameters,Float> P_WORKING_HOURS = JKey.of(MetaParameters.workingHours);
    /** The First Day of the Week Day. */
    public static final JKey<Parameters,Integer> P_FIRST_DAY_OF_WEEK = JKey.of(MetaParameters.firstDayOfWeek);
    /** Decimal time format. */
    public static final JKey<Parameters,Boolean> P_DECIMAL_TIME_FORMAT = JKey.of(MetaParameters.decimalTimeFormat);
    /** The Main selecton format. */
    public static final JKey<Parameters,String> P_DATE_MAIN_FORMAT = JKey.of(MetaParameters.dateMainFormat);
    /** The Export Date Selection. */
    public static final JKey<Parameters,String> P_DATE_REPORT_FORMAT = JKey.of(MetaParameters.dateReportFormat);
    /** The Export Date Selection. */
    public static final JKey<Parameters,String> P_DATE_REPORT_FORMAT2 = JKey.of(MetaParameters.dateReportFormat2);
    /** The complementary report CSS style. */
    public static final JKey<Parameters,String> P_REPORT_CSS = JKey.of(MetaParameters.reportCss);
    /** The Goto Date format. */
    public static final JKey<Parameters,String> P_DATE_GOTO_FORMAT = JKey.of(MetaParameters.dateGotoFormat);
    /** Nimbus Look &amp; Feel support */
    public static final JKey<Parameters,Boolean> P_NIMBUS_LAF = JKey.of(MetaParameters.nimbusLaf);
    /** A Color of a private project. */
    public static final JKey<Parameters,Color> P_COLOR_PRIVATE = JKey.of(MetaParameters.colorOfPrivateProject);
    /** A Color of finished project. */
    public static final JKey<Parameters,Color> P_COLOR_FINISHED_PROJ = JKey.of(MetaParameters.colorOfFinishedProject);
    /** A Color of an editable area. */
    public static final JKey<Parameters,Color> P_COLOR_EDITABLE = JKey.of(MetaParameters.colorOfEditableArea);
    /** Is a System Tray Enabled? */
    public static final JKey<Parameters,Boolean> P_SYSTRAY_ENABLED = JKey.of(MetaParameters.systemTrayEnabled);
    /** Action on a second click */
    public static final JKey<Parameters,SysTray.Action> P_SYSTRAY_SECOND_CLICK = JKey.of(MetaParameters.systemTraySecondClick);
    /** Modify value of finished project or task. */
    public static final JKey<Parameters,Boolean> P_MODIFY_FINESHED_PROJ = JKey.of(MetaParameters.modifyFinishedProject);
    /** Create a new Event on an EXIT action. */
    public static final JKey<Parameters,Boolean> P_EXIT_EVENT_CREATE = JKey.of(MetaParameters.exitEventCreating);
    /** Description of an EXIT action. */
    public static final JKey<Parameters,String> P_EXIT_EVENT_DESCR = JKey.of(MetaParameters.exitEventDescription);
    /** Hide Buoon Icon. */
    public static final JKey<Parameters,Boolean> P_HIDE_ICONS = JKey.of(MetaParameters.hideButtonIcons);
    /** Last window size and position. */
    public static final JKey<Parameters,Rectangle> P_WINDOW_SIZE = JKey.of(MetaParameters.windowSize);
    /** Restore the last application window size and position. */
    public static final JKey<Parameters,Boolean> P_WINDOW_SIZE_RESTORATION = JKey.of(MetaParameters.windowSizeRestoration);
    /** Automatic sorting of the events by time. */
    public static final JKey<Parameters,Boolean> P_AUTOMATIC_SORTING_BY_TIME = JKey.of(MetaParameters.automaticSortingByTime);
    /** Check new release on the home page */
    public static final JKey<Parameters,Boolean> P_CHECK_NEW_RELEASE = JKey.of(MetaParameters.checkNewRelease);
    /** A full path to a system browser. */
    public static final JKey<Parameters,String> P_SYSTEM_BROWSER_PATH = JKey.of(MetaParameters.systemBrowserPath);
    /** A full path to a DataFile. */
    public static final JKey<Parameters,File> P_DATA_FILE_PATH = JKey.of(MetaParameters.dataFilePath);
    /** Project table sorted column */
    public static final JKey<Parameters,String> P_SORT_PROJ_COLUMN = JKey.of(MetaParameters.sortProjColumn);
    /** A full path to a Shared Projects DataFile. */
    public static final JKey<Parameters,File> P_PROJECTS_FILE_PATH = JKey.of(MetaParameters.sharedProjectsFilePath);
    /** Name of user of this application. */
    public static final JKey<Parameters,String> P_USERNAME = JKey.of(MetaParameters.username);

    // ------------------------ ATTRIBUTES ------------------------

    @Column(name = "Language")
    private Locale language = Locale.getDefault();

    @Column(name = "WorkingHours")
    private float workingHours = 8f;

    @Column(name = "FirstDayOfWeek")
    private int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();

    @Column(name = "DecimalTimeFormat")
    private boolean decimalTimeFormat = false;

    @Column(name = "DateMainFormat")
    private String dateMainFormat = DATE_FORMAT_DEFAULT;

    @Column(name = "DateReportFormat")
    private String dateReportFormat = DATE_FORMAT_DEFAULT;

    @Column(name = "DateReportFormat2")
    private String dateReportFormat2 = "d'<br/><span class=\"smallMonth\">'MMMM'</span>'";

    @Column(name = "ReportCSS")
    private String reportCss = "styles/style.css";

    @Column(name = "DateGotoFormat")
    private String dateGotoFormat = "yyyy-MM-dd";

    @Column(name = "NimbusL&FSupport")
    private boolean nimbusLaf = !ApplTools.isWindowsOS();

    @Column(name = "ColorOfPrivateProject")
    private Color colorOfPrivateProject = new Color(0x5DA158);

    @Column(name = "ColorOfFinishedProject")
    private Color colorOfFinishedProject = new Color(0xA9AC88);

    @Column(name = "ColorOfEditableArea")
    private Color colorOfEditableArea = new Color(0xFFFACD);

    @Column(name = "SystemTrayEnabled")
    private boolean systemTrayEnabled = true;

    @Column(name = "SystemTraySecondClick")
    private SysTray.Action systemTraySecondClick = SysTray.Action.HIDE;

    @Column(name = "ModifyFinishedProject")
    private boolean modifyFinishedProject = false;

    @Column(name = "ExitEventCreating")
    private boolean exitEventCreating = true;

    @Column(name = "ExitEventDescription")
    private String exitEventDescription = "EXIT";

    @Column(name = "HideButtonIcons")
    private boolean hideButtonIcons = false;

    @Column(name = "WindowSize")
    private Rectangle windowSize = new Rectangle(-1, -1, 622, 405);

    @Column(name = "WindowSizeRestoration")
    private boolean windowSizeRestoration = true;

    @Column(name = "AutomaticSortingByTime")
    private boolean automaticSortingByTime = true;

    @Column(name = "CheckNewRelease")
    private boolean checkNewRelease = true;

    @Column(name = "SystemBrowserPath")
    private String systemBrowserPath = VALUE_DEFAULT;

    @Column(name = "DataFilePath")
    private File dataFilePath = new File(VALUE_DEFAULT);

    @Column(name = "SortProjColumn")
    private String sortProjColumn = "ID";

    @Column(name = "SharedProjectsFilePath")
    private File sharedProjectsFilePath = new File(VALUE_DEFAULT);

    @Column(name = "Username")
    private String username = getSystemLogin();

    /** Decimal Formatter */
    private transient DecimalFormat decimalFormat = null;

    /** WebRelease.
     * <p>The field must be transient - it has got a getter but no setter, and such
     * a property breaks the runtime metamodel generator of the Ujorm. */
    private transient Version webRelease;

    /** Parameters constructor */
    public Parameters() {
        setDecimalFormat();
    }

    // ------------------------ JAVABEAN ------------------------

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(final Locale language) {
        this.language = language;
        setDecimalFormat();
    }

    public float getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(final float workingHours) {
        this.workingHours = workingHours;
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /** A value out of the range 1..7 is fixed. */
    public void setFirstDayOfWeek(final int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek < 1 || firstDayOfWeek > 7
                ? ((firstDayOfWeek + 7 * 1000 - 1) % 7) + 1
                : firstDayOfWeek;
    }

    public boolean isDecimalTimeFormat() {
        return decimalTimeFormat;
    }

    public void setDecimalTimeFormat(final boolean decimalTimeFormat) {
        this.decimalTimeFormat = decimalTimeFormat;
    }

    public String getDateMainFormat() {
        return dateMainFormat;
    }

    public void setDateMainFormat(final String dateMainFormat) {
        new SimpleDateFormat(dateMainFormat); // A validation only
        this.dateMainFormat = dateMainFormat;
    }

    public String getDateReportFormat() {
        return dateReportFormat;
    }

    public void setDateReportFormat(final String dateReportFormat) {
        new SimpleDateFormat(dateReportFormat); // A validation only
        this.dateReportFormat = dateReportFormat;
    }

    public String getDateReportFormat2() {
        return dateReportFormat2;
    }

    public void setDateReportFormat2(final String dateReportFormat2) {
        this.dateReportFormat2 = dateReportFormat2;
    }

    public String getReportCss() {
        return reportCss;
    }

    public void setReportCss(final String reportCss) {
        this.reportCss = reportCss;
    }

    public String getDateGotoFormat() {
        return dateGotoFormat;
    }

    public void setDateGotoFormat(final String dateGotoFormat) {
        this.dateGotoFormat = dateGotoFormat;
    }

    public boolean isNimbusLaf() {
        return nimbusLaf;
    }

    public void setNimbusLaf(final boolean nimbusLaf) {
        this.nimbusLaf = nimbusLaf;
    }

    public Color getColorOfPrivateProject() {
        return colorOfPrivateProject;
    }

    public void setColorOfPrivateProject(final Color colorOfPrivateProject) {
        this.colorOfPrivateProject = colorOfPrivateProject;
    }

    public Color getColorOfFinishedProject() {
        return colorOfFinishedProject;
    }

    public void setColorOfFinishedProject(final Color colorOfFinishedProject) {
        this.colorOfFinishedProject = colorOfFinishedProject;
    }

    public Color getColorOfEditableArea() {
        return colorOfEditableArea;
    }

    public void setColorOfEditableArea(final Color colorOfEditableArea) {
        this.colorOfEditableArea = colorOfEditableArea;
    }

    public boolean isSystemTrayEnabled() {
        return systemTrayEnabled;
    }

    public void setSystemTrayEnabled(final boolean systemTrayEnabled) {
        this.systemTrayEnabled = systemTrayEnabled;
    }

    public SysTray.Action getSystemTraySecondClick() {
        return systemTraySecondClick;
    }

    /** An undefined action is replaced by the default one. */
    public void setSystemTraySecondClick(final SysTray.Action systemTraySecondClick) {
        this.systemTraySecondClick = systemTraySecondClick != null
                ? systemTraySecondClick
                : SysTray.Action.HIDE;
    }

    public boolean isModifyFinishedProject() {
        return modifyFinishedProject;
    }

    public void setModifyFinishedProject(final boolean modifyFinishedProject) {
        this.modifyFinishedProject = modifyFinishedProject;
    }

    public boolean isExitEventCreating() {
        return exitEventCreating;
    }

    public void setExitEventCreating(final boolean exitEventCreating) {
        this.exitEventCreating = exitEventCreating;
    }

    public String getExitEventDescription() {
        return exitEventDescription;
    }

    public void setExitEventDescription(final String exitEventDescription) {
        this.exitEventDescription = exitEventDescription;
    }

    public boolean isHideButtonIcons() {
        return hideButtonIcons;
    }

    public void setHideButtonIcons(final boolean hideButtonIcons) {
        this.hideButtonIcons = hideButtonIcons;
    }

    public Rectangle getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(final Rectangle windowSize) {
        this.windowSize = windowSize;
    }

    public boolean isWindowSizeRestoration() {
        return windowSizeRestoration;
    }

    public void setWindowSizeRestoration(final boolean windowSizeRestoration) {
        this.windowSizeRestoration = windowSizeRestoration;
    }

    public boolean isAutomaticSortingByTime() {
        return automaticSortingByTime;
    }

    public void setAutomaticSortingByTime(final boolean automaticSortingByTime) {
        this.automaticSortingByTime = automaticSortingByTime;
    }

    public boolean isCheckNewRelease() {
        return checkNewRelease;
    }

    public void setCheckNewRelease(final boolean checkNewRelease) {
        this.checkNewRelease = checkNewRelease;
    }

    public String getSystemBrowserPath() {
        return systemBrowserPath;
    }

    /** An empty path is replaced by the default value. */
    public void setSystemBrowserPath(final String systemBrowserPath) {
        this.systemBrowserPath = Check.isEmpty(systemBrowserPath)
                ? VALUE_DEFAULT
                : systemBrowserPath;
    }

    public File getDataFilePath() {
        return dataFilePath;
    }

    /** An undefined path is replaced by the default value, otherwise the file must be writable. */
    public void setDataFilePath(final File dataFilePath) {
        final String name = dataFilePath != null ? dataFilePath.getName().trim() : VALUE_DEFAULT;
        if (VALUE_DEFAULT.equals(name) || name.isEmpty()) {
            this.dataFilePath = new File(VALUE_DEFAULT);
            return;
        }
        if (!canWrite(dataFilePath)) {
            throw new MessageException("Can't write data to " + dataFilePath);
        }
        this.dataFilePath = dataFilePath;
    }

    public String getSortProjColumn() {
        return sortProjColumn;
    }

    public void setSortProjColumn(final String sortProjColumn) {
        this.sortProjColumn = sortProjColumn;
    }

    public File getSharedProjectsFilePath() {
        return sharedProjectsFilePath;
    }

    public void setSharedProjectsFilePath(final File sharedProjectsFilePath) {
        this.sharedProjectsFilePath = sharedProjectsFilePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    // ------------------------ BUSINESS ------------------------

    /** Returns an operation system login */
    public static String getSystemLogin() {
        final String result = System.getProperty("user.name");
        return Check.hasLength(result)
             ? Character.toUpperCase(result.charAt(0)) + result.substring(1)
             : "?"
             ;
    }

    /** Enables the file writing or creating changes? */
    private boolean canWrite(final File file) {
        try {
            return file.isFile()
                 ? file.canWrite()
                 : file.createNewFile()
                 ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** An authorization settings. */
    @Override
    public boolean isExportable(final ExportAction action, final JKey<?,?> key, final Object value) {
        switch (action) {
            case RESOURCE_BUNDLE:
                // A default value is not stored to the configuration file:
                return !Objects.equals(value, key.getDefault());
            case TABLE_SHOW:
                if (key == P_SORT_PROJ_COLUMN) {
                    return false;
                }
                if (key == P_DATA_FILE_PATH) {
                    return value != null && !value.equals(P_DATA_FILE_PATH.getDefault());
                }
                return key != P_WINDOW_SIZE;
            default:
                return true;
        }
    }

    /** Returns a text representation of the key value. */
    public String readValueString(final JKey<Parameters,?> key) {
        return ValueCoder.encode(key.getValue(this));
    }

    /** Returns a localized date format */
    public String getDateFormat(final JKey<Parameters,String> key, final ApplContext context) {
        final String result = get(key);
        final String week = context.getLanguageManager().getTextAllways("Week");
        return String.format(result, week);
    }

    /** Set new Decimal FormatsetLocale  */
    private void setDecimalFormat() {
        decimalFormat = ApplTools.createDecimalFormat("0.00", language);
    }

    /** Returns a format time. */
    public String formatTime(int minutes) {
       final boolean deci = get(P_DECIMAL_TIME_FORMAT);

       if (deci) {
            // Numeric format: 1.50
            final DecimalFormat NUM_FORMAT = decimalFormat;
            final float val = minutes / 60f;
            return NUM_FORMAT.format(val);
       } else {
            // HH:MM Format
            boolean minus = minutes<0;
            if (minus) { minutes = -minutes; }
            int hours = minutes / 60;
            int mins  = minutes % 60;
            StringBuilder sb = new StringBuilder(6);  // Sample: "-23:59"
            if (minus) { sb.append('-'); }
            sb.append(hours);
            sb.append(':');
            if (mins<10) { sb.append('0'); }
            sb.append(mins);
            return sb.toString();
       }
    }

    public Version getWebRelease() {
        if (webRelease == null) {
            webRelease = ApplTools.getWebRelease(JWorkSheet.APPL_JNLP);
        }
        return webRelease;
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final JKey<Parameters,V> key) {
        return key.getValue(this);
    }

    public <V> Parameters set(final JKey<Parameters,V> key, final V value) {
        key.setValue(this, value);
        return this;
    }
}
