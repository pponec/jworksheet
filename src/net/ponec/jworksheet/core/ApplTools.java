/*
 * ApplTools.java
 *
 * Created on 1. èervenec 2007, 7:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.ponec.jworksheet.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.ponec.jworksheet.gui.JWorkSheet;
import net.ponec.jworksheet.resources.ResourceProvider;

/**
 * Static methods.
 * @author Pavel Ponec
 */
public class ApplTools {
    
    public static final Logger LOGGER = Logger.getLogger("ApplTools");
    
    /**
     * Get StackTrace from an Exception.
     */
    public static StringBuffer getStackTraceBuf(Throwable anException) {
        StringWriter stringWriter = new StringWriter();
        
        if (anException==null) {
            stringWriter.write("Undefined exception (null).");
        } else {
            PrintWriter tempWriter = new PrintWriter(stringWriter);
            tempWriter.println(""+anException);
            anException.printStackTrace(tempWriter);
            tempWriter.flush();
        }
        return stringWriter.getBuffer();
    }
    
    public static DecimalFormat createDecimalFormat(String format) {
        return createDecimalFormat(format, Locale.US);
    }
    
    public static DecimalFormat createDecimalFormat(String format, Locale locale) {
        final DecimalFormat result = (DecimalFormat) DecimalFormat.getNumberInstance(locale);
        result.applyPattern(format);
        return result;
    }
    
    /** Copy a file to a target file. */
    public static void copy(File source, File target) throws IOException {
        InputStream  is = null;
        try {
            is = new BufferedInputStream( new FileInputStream(source));
            copy(is, target);
            target.setLastModified(source.lastModified()); // Set the time
        } finally {
            if (is!=null) { is.close(); }
        }
    }
    
    /** Copy a file to a target file. */
    public static void copy(InputStream is, File target) throws IOException {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(target));
            
            int c;
            while ((c=is.read()) != -1) {
                os.write(c);
            }
        } finally {
            if (os!=null) {
                os.close();
            }
        }
    }
    
    /** Reset Time to 12:00:00.000 . */
    public static void resetTime(Calendar calednar) {
        calednar.set(Calendar.HOUR_OF_DAY, 12);
        calednar.set(Calendar.MINUTE, 0);
        calednar.set(Calendar.SECOND, 0);
        calednar.set(Calendar.MILLISECOND, 0);
    }
    
    /** This method writes a string to the system clipboard, otherwise it returns null.*/
    public static void setClipboard(String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }
    
    /** Returns a current year */
    public static int getCurrentYear() {
        String sDate = JWorkSheet.APPL_RELEASED;
        int i = sDate.indexOf('/');
        try {
            sDate = sDate.substring(0, i);
            return Integer.parseInt(sDate);
        } catch (Throwable e) {
            LOGGER.severe("Bad year: " + sDate);
            return -1;
        }
    }
    
    /** Display an About program message.
     * @param frame The one of types: JFrame, JDialog, Component, String, Object (ClassName)
     * @param version
     * @param licence
     * @param homePage
     * @param date
     * @param ide
     * @param iconLicence
     * @param logo Logo of the program. Parameter is NOT mandatory.
     */
    public static void aboutApplication
    ( Object frame
    , String title
    , String version
    , String licence
    , String homePage
    , String date
    , String ide
    , String iconLicence
    , String libraryLicence
    , Icon   logo
    , JButton okButton
    ) {
        
        int currentYear = getCurrentYear();
        int copyrightYear = Math.max(currentYear, Calendar.getInstance().get(Calendar.YEAR));
        if (copyrightYear > currentYear+9) {
            copyrightYear = currentYear+9;
        }
        
        String _NEW_LINE_ = "</td></tr><tr><td valign=top>";
        
        String[] msg =
        { "<html>"
          , "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  style=\"font-weight:bold;\">"
          //
          , "<tr><td>"
          , "Application"
          , "</td><td>: "
          ,  title
          //
          , _NEW_LINE_
          , "Version"
          , "</td><td>: "
          ,  version
          //
          , _NEW_LINE_
          , "Copyright&nbsp;"
          , "</td><td>: "
          , "\u00a9 " + copyrightYear + " PPonec"
          //
          , _NEW_LINE_
          , "License"
          , "</td><td>: "
          ,  licence
          //
          , _NEW_LINE_
          , "Home"
          , "</td><td>: "
          , homePage
          //
          , _NEW_LINE_
          , "Finished"
          , "</td><td>: "
          ,  date
          //
          , _NEW_LINE_
          , "Library"
          , "</td><td>: "
          ,  libraryLicence
          , "</td></tr>"
          //
          , _NEW_LINE_
          , "Icons"
          , "</td><td>: "
          ,  iconLicence
          //
          , _NEW_LINE_
          , "Java"
          , "</td><td>: "
          ,  System.getProperty("java.version")
          , " - "
          ,  System.getProperty("java.vendor")
          //
          , _NEW_LINE_
          , "IDE"
          , "</td><td>: "
          ,  ide // NetBeans
          //
          , "</td></tr>"
          , "</table></html>"
        } ;
        
        
        javax.swing.JOptionPane.showOptionDialog
        ( frame instanceof Component ? (Component) frame : null
        , stringCat("", msg)
        , "About"
        , javax.swing.JOptionPane.OK_OPTION
        , javax.swing.JOptionPane.INFORMATION_MESSAGE
        , logo
        , new JButton[] {okButton}
        , okButton
        );
        
    }
    
    /**
     * StringConcatenation
     *
     * @param lines The Array of Strings for concatenation. Any items can be null.
     * @param separator String for separating Fields of the Array. Separator can be null.
     */
    public static String stringCat(String separator, String ... lines) {
        StringBuilder result = new StringBuilder(256);
        if (separator==null) { separator = ""; }
        for (int i=0; i<lines.length; i++) {
            if (i>0) { result.append(separator); }
            String text = lines[i];
            if (text!=null) { result.append(text); }
        }
        lines = null;
        return result.toString();
    }
    
    /** Returns true, if text is not null and not empty. */
    public static final boolean isValid(CharSequence text) {
        final boolean result = text!=null && text.length()>0;
        return result;
    }
    
    /** Returns true, if environment is a Windows OS. */
    public static boolean isWindowsOS() {
        final boolean result = System.getProperty("os.name").startsWith("Windows");
        return result;
    }
    
    /** Returns true, if environment is a Linux OS. */
    public static boolean isLinuxOS() {
        final boolean result = System.getProperty("os.name").startsWith("Linux");
        return result;
    }
    
    /**
     * Factory for creating Date Spinner (unlimited).
     * @param dateFormat
     * @param language
     */
    public static javax.swing.JSpinner createSpinnerDate( String dateFormat, Locale language ) {
        JSpinner result = new JSpinner
        (new javax.swing.SpinnerDateModel());
        result.setLocale(language);
        
        result.setEditor(new javax.swing.JSpinner.DateEditor
        (result, dateFormat));
        
        result.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                valueMouseWheelMoved(evt);
            }
        });
        return result;
    }
    
    
    /**
     * Value Weel Moved for JSpinner.
     * @param evt
     */
    public static void valueMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        try {
            JSpinner spinner = (JSpinner) evt.getSource();
            Object value
            = evt.getWheelRotation()<=0
            ? spinner.getNextValue()
            : spinner.getPreviousValue()
            ;
            if (value!=null) {
                spinner.setValue( value );
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "A spinner exception.", e);
        }
    }
    
    /** Align a JSpinner.
     * @see JFormattedTextField#LEFT */
    public static void setAlign(JSpinner spinner, int aligment) {
        
        JFormattedTextField tf = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
        tf.setHorizontalAlignment(aligment);
    }
    
    /** Rename a source to target. */
    public static void rename(File source, File target) throws IOException {
        File tmpFile = null ;
        if (target.exists()) {
            tmpFile = new File(target.getParent(), target.getName() + "$");
            tmpFile.delete();
            target.renameTo(tmpFile);
        }
        // CORE:
        final boolean result = source.renameTo(target);
        if (!result) {
            throw new IOException("Can't rename temporary file " + source);
        }
        if (tmpFile!=null) {
            tmpFile.delete();
        }
    }
    
    /** Make a XSL transformation. */
    public static File makeXslTransformation(StreamSource source, StreamSource xsl, ArrayList<String[]> params)
    throws TransformerConfigurationException, TransformerException, IOException {
        // Create transformer factory
        TransformerFactory factory = TransformerFactory.newInstance();
        
        // Use the factory to create a template containing the xsl file
        Templates template = factory.newTemplates(xsl);
        
        // Use the template to create a transformer
        Transformer xformer = template.newTransformer();
        if (params!=null) for (String[] p : params) {
            xformer.setParameter(p[0], p[1]);
        }
        
        //CharArrayWriter writer = new CharArrayWriter(128);
        File file = File.createTempFile("_report.", ".html");
        file.deleteOnExit();
        
        Result result = new StreamResult(file);
        
        // Apply the xsl file to the source file and write the result to the output file
        xformer.transform(source, result);
        
        return file;
    }
    
    /** Creates a new instance of Str */
    public static String getFileContent(File file, Charset enc, int maxLength) throws IOException {
        if (file==null || !file.isFile()) { return ""; }
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, enc);
            int length = (int) Math.min(maxLength, file.length());
            char[] chars = new char[length];
            int loaded = isr.read(chars);
            return new String(chars, 0, loaded);
        } finally {
            if (fis!=null) {
                fis.close();
            }
        }
    }
    
    /** Register Escape Action. */
    public static void registerEscapeAction(JComponent component, Action listener) {
        final String ACTION_ESCAPE = "Cancely";  // Action Escape
        final KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        registerAction(key, ACTION_ESCAPE, component, listener);
    }
    
    /** Register Escape Action. */
    public static void registerAction(KeyStroke key, String action, JComponent component, Action listener) {
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, action);
        component.getActionMap().put(action, listener);
    }
    
    
    
    /** Window Sizing */
    public static void windowsSizing(java.awt.Window window, Rectangle rect) {
        if (rect.x<0 || rect.y<0) {
            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            rect   = (Rectangle) rect.clone();
            rect.x = (screenSize.width  - rect.width ) >> 1 ;
            rect.y = (screenSize.height - rect.height) >> 1 ;
        }
        window.setBounds(rect);
    }
    
    /**
     * Open uri by system browser
     *
     * @param uri
     * @param sysBrowser sysBrowser is mandatory for Java 1.5 only.
     * @throws java.io.IOException
     * @throws java.lang.UnsupportedOperationException
     */
    @SuppressWarnings("unchecked")
    public static void browse(URI uri, String sysBrowser) throws IOException, UnsupportedOperationException  {
        
        sysBrowser = sysBrowser!=null ? sysBrowser.trim() : "" ;
        if (isValid(sysBrowser)){
            String quotation = sysBrowser.indexOf(' ')>=0 ? "\"" : "" ;
            String command = quotation + sysBrowser + quotation + " " + uri.toString();
            Process child = Runtime.getRuntime().exec(command);
        } else try {
            Class desktopClass = Class.forName("java.awt.Desktop");
            final Method isSupported = desktopClass.getMethod("isDesktopSupported");
            final Method getDesktop  = desktopClass.getMethod("getDesktop");
            final Method browse      = desktopClass.getMethod("browse", uri.getClass());
            final Boolean supported  = (Boolean) isSupported.invoke(null);
            if (supported) {
                Object desktop = getDesktop.invoke(null);
                browse.invoke(desktop, uri);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (Exception e) {
            String command = isWindowsOS()
            //? "CMD.EXE /C START /B "
            ? "rundll32 url.dll,FileProtocolHandler "
            : "firefox "
            ;
            command += uri.toString();
            Process child = Runtime.getRuntime().exec(command);
        }
    }
    
    /**  Change cursor. */
    public static void setCursorWait(boolean aWait, ApplContext context) {
        final int cursor
        = aWait
        ? java.awt.Cursor.WAIT_CURSOR
        : java.awt.Cursor.DEFAULT_CURSOR
        ;
        context.getTopFrame().setCursor(java.awt.Cursor.getPredefinedCursor(cursor));
    }
    
    /** Sleep */
    public static void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /** Create a close button for a JOptionPane. */
    public static JButton createCloseButton(String text) {
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComponent comp = (JComponent) e.getSource();
                JDialog dialog = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, comp);
                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        
        JButton ok = new JButton(text, new ResourceProvider().getIcon(ResourceProvider.IMG_TICK));
        ok.setMnemonic(text.charAt(0));
        ok.addActionListener(action);
        return ok;
    }
    
    /** Converts bytes to String on Java 5.0. */
    public static String newString(byte[] bytes, Charset charset) {
        String result;
        try {
            result = new String(bytes, charset.name());
        } catch (UnsupportedEncodingException e) {
            result = new String(bytes);
        }
        return result;
    }
    
    /** Converts String to bytes on Java 5.0. */
    public static byte[] getBytes(String text, Charset charset) {
        byte[] result;
        try {
            result = text.getBytes(charset.name());
        } catch (UnsupportedEncodingException e) {
            result = text.getBytes();
        }
        return result;
    }
        
    /** Set a default look and feel */
    public static void initLookAndFeel(boolean nimbus) throws UnsupportedLookAndFeelException {
        String property = System.getProperty("swing.defaultlaf");
        
        if (property==null) try {
            if (nimbus) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } else {
                throw new UnsupportedLookAndFeelException(""); 
            }
        } catch (Exception e1) {
            try {
               UIManager.setLookAndFeel(
                   isLinuxOS()
                   ? "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
                   : UIManager.getSystemLookAndFeelClassName()
                   );
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Look&Feel", e);
            }
       }     
    }
    
    /** Is was set a Nimbus L&F ? */
    public static boolean isNimbusLAF()  {
        return "Nimbus".equals(UIManager.getLookAndFeel().getName());
    }
    
    /** Modify a color */
    public static Color modify(Color color, int light) {
        //        final Color result = dark 
        //            ? color.darker() 
        //            : color.brighter()
        //            ;
        //        return result;        
        
        final int r = colorLimit(light + color.getRed());
        final int g = colorLimit(light + color.getGreen());
        final int b = colorLimit(light + color.getBlue());
        
        return new Color(r,g,b);
    }
    
    private static int colorLimit(int baseColor) {
        final int result = baseColor>254 ? 254 : Math.max(0, baseColor);
        return result;
    }
    
    /** Call the class from another JAR */
    @SuppressWarnings("unchecked")
    public static final <T> Class<T> getClass(String className, File jar)
        throws MalformedURLException, ClassNotFoundException 
    {
        final URL[] urls = new URL[]{ jar.toURI().toURL() };
        final URLClassLoader child = new URLClassLoader(urls, ApplTools.class.getClassLoader());
        final Class result = Class.forName (className, true, child);
        return result;
    }
    
}
