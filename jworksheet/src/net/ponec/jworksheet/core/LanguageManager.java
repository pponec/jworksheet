/**
 * Copyright (C) 2007-9, Paul Ponec, contact: http://ponec.net/
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

/*
 * LanguageManager.java
 * Created on 2001/10/26
 */

import java.awt.Component;
import javax.swing.*;
import java.awt.event.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ujoframework.UjoProperty;


/**
 * Tool provide two services
 * <br>First: in the debug mode it display to user a language property parameters
 * from top hierarchy komponent to down recursively.
 * <br>Second: allways set Name parameter (if one is not only assigned) for all compoments.<br>
 * Examples for JFrame:
 * <ul>
 *      languageManager = new LanguageManager(this, false);<br>
 *      languageManager.setLocale();
 * </ul>
 * Examples for JDialog called from JFrame:<br>
 * <ul>
 *      languageManager.setFirstRunTexts(this);
 * </ul>
 *
 * <p>
 * <br/>Note 1: For new JLabel("Label") -> jLabel.setName("~Label"); // Default usage
 * <br/>Note 2: jLabel.setName(".myPrefix.Label");                   // User usage
 * <br/>Note 3: jLabel.setName("#");                                 // Ignore It
 * </p>
 *
 * @author  Pavel Ponec
 * @version 1.2
 */
public class LanguageManager {

    private static final Logger LOGGER = Logger.getLogger(LanguageManager.class.getName());

    /** A key word to skipping a generic translation for marked component.  */
    public static final String STOP_TRANSLATION = "~STOP~TRANSLATION";
    
    // --- Konstants ----
    
    /** A Locale for default property: */
    public static final Locale DEFAULT_LOCALE = new Locale("");

    /** Swing class prefix */
    private final String SWING_PREFIX = "javax.swing.J";
    private final String DOT  = ".";
    
    /** Debug Mode */
    public static final boolean DEBUG_MODE = false;
    
    // --- Other ----
    
    /** Attribute for enable timing (outupt to console). */
    public static final boolean enableTiming = false;
    /** Top level container. */
    protected java.awt.Container basicContainer;
    /** Basic Bundle Name. */
    protected String bundleName;
    /** Bundle like weak map. */
    protected WeakHashMap<Locale, ResourceBundle> bundleMap;
    /** Locale */
    protected Locale locale;
    /** The first conversion. */
    protected boolean firstRuning = true;
    /** Sign for show Dialog. */
    private boolean show;
    /** Key convertor buffer (for text2key() method). */
    private StringBuilder keyConvertorBuffer = null;
    /** Name of The Method. */
    private static final String[] mText  = {"getText" , "setText" };
    /** Name of The Method. */
    private static final String[] mTitle = {"getTitle", "setTitle"};
    /** Arguments of The Method getText: */
    private static final Class[] parameterTypes1 = new Class[0];
    /** Arguments of The Method setText: */
    private static final Class[] parameterTypes2 = {String.class};
    /** New line. */
    private static final String nl = "\n";
    
    /** New solution for Creating Key Method. */
    private final boolean newSolution4Key = true;
    /** Buffer for store properties content. */
    private StringBuilder properties;
    /** Buffer for store warnings content. */
    private StringBuilder warnings;
    
    // ---------- Constructor -------------------
    
   
    /** Creates new LanguageManager class.
     * @param basicContainer Parameter is default Root. It is used alse for PROPERTY file name.
     * @param bundlePattern A bundlePattern class determine a bundle name.
     * @param showDebugWindow
     */
    public LanguageManager(java.awt.Container basicContainer, Class bundlePattern, boolean showDebugWindow) {
        this.basicContainer = basicContainer!=null ? basicContainer : new JPanel();
        this.bundleName     = bundlePattern.getPackage().getName() + ".text";
        this.bundleMap      = new WeakHashMap<Locale,ResourceBundle>(1);
        this.show           = showDebugWindow;

        if (this.show) {
            properties = new StringBuilder();
            warnings   = new StringBuilder();
        }
    }    
    
    // ----------- Methods --------------------
    
    /**
     * Create an key Name for the <b>param</b> container
     * and their subcomponents for the first run.
     * This was meaning for a DIALOG box.
     * @param topContainer A root of the component tree.  */
    public void setFirstRunTexts(java.awt.Container topContainer) {
        setFirstRunTexts(topContainer, locale);
    }
    
    /**
     * Create an key Name for the <b>param</b> container
     * and their subcomponents for the first run.
     * This was meaning for a DIALOG box.
     * @param topContainer A root of the component tree.  */
    public void setFirstRunTexts(java.awt.Container topContainer, Locale aLocale) {
        firstRuning = true;
        //setTexts(aLocale, topContainer);
        setTexts(null, topContainer);
    }
    
   
    /**
     * Set new locale and change texts in GUI on
     * the top container and his subcomponents.
     * <br />Modify to an supported locale
     * @param locale The Language
     */
    public void setLocaleAndTranslate(java.util.Locale locale, boolean translate) {

       this.locale = getSupportedLocale(locale);

       // Localize tree component model:
       if (translate) {
           setTexts(null, basicContainer);
       }
    }

    /** Returns a supported Locale. */
    private Locale getSupportedLocale(Locale locale) {

        final boolean result = findPropertyBundle(locale);

        if (!result) {
            locale = DEFAULT_LOCALE;
        }

        return locale;
    }
    
    /** Find a bundle or return null. */
    private boolean findPropertyBundle(Locale locale) {
        String myBundle  = "/" + bundleName.replace('.','/');
        String localeStr = "_" + locale.toString();
        int i = localeStr.length();

        while (i>0) {
            String suffix = localeStr.substring(0, i);
            String fileName = myBundle + suffix + ".properties";
            URL url = getClass().getResource(fileName);
            if (url!=null) { return true; }
            i = localeStr.substring(0, i).lastIndexOf('_');
        }
        return false;
    }

    /**
     * Create an key Name for the <b>param</b> container
     * and their subcomponents.
     * @param locale The Language
     * @param changeGuiText if value is false, don't change text on a GUI containter.
     */
    public void setLocale(java.util.Locale locale, boolean changeGuiText) {
        setTexts(locale, changeGuiText ? basicContainer : null);
    }
    
    /**
     * Create an key Name for the <b>top</b> container and their subcomponents.
     * @param locale
     * @param topContainer If the Continer is null, only locale is assigned. */
    public void setTexts(java.util.Locale locale, java.awt.Container topContainer) {
        if (locale!=null) {
           setLocale(locale, true);
        }
        
        // Text Change:
        if (topContainer==null) { return; }
        
        // TimeStore:
        long timeStore = 0L;
        
        // Initial:
        if (this.show) {
            properties.setLength(0);
            warnings.setLength(0);
            
            properties.append("# Properties for "
            + bundleName
            + " object ("
            + new java.util.Date()
            + ") :"
            + nl + nl
            );
            
            warnings.append("# Warnings for "
            + bundleName
            + " object ("
            + new java.util.Date()
            + ") :"
            + nl + nl
            );
        }
        
        if (enableTiming){
            timeStore = System.currentTimeMillis();
        }
        
        // Conversion:
        keyConvertorBuffer = new StringBuilder(50);
        try {
            createName2(topContainer);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Language", e);
        }
        keyConvertorBuffer = null;
        
        
        // Show a Result:
        if (show && firstRuning) show();
        if (enableTiming){
            timeStore = System.currentTimeMillis() - timeStore;
            java.lang.System.err.println(">>> Time: " + timeStore + " [ms] <<<");
        }
        
        // take off attribute:
        firstRuning = false;
    }
    
    /** Show GUI Dialog. */
    private void show() {
        new LanguageManagerDialog
        ( basicContainer
        , properties.toString()
        , warnings.toString()
        );
    }
    
    /** Returns a Class Type and insert a dot (.) character on first position. */
    private String getClassType(java.awt.Container aCont) {
        String result;
        
        result = aCont.getClass().getName(); // Sample Class: "javax.swing.JPanel"
        int i = result.lastIndexOf(DOT);
        if (i>=0 ) {
            result = result.startsWith(SWING_PREFIX)
            ? (DOT + result.substring(SWING_PREFIX.length()))
            : result.substring(i)
            ;
        }
        return result;
    }
    
    /**
     * Create an key Name for an container "aCont" and their subcomponents.
     */
    private void createName2(java.awt.Container aCont) {
        
        if (DEBUG_MODE) {
            showDebugData(aCont);
        }
        
        java.lang.reflect.Method me;
        String text;
        String name;
        
        int i;
        String clas = null;
        
        // ---- Null Test -------------------
        
        if (aCont==null || STOP_TRANSLATION.equals(aCont.getName())) {
            return;
        }
     
        
        // --- Recursion: ---------------------
        
        // For all Standard Components ---
        if (aCont instanceof javax.swing.JToolBar) {
            javax.swing.JToolBar tp = (javax.swing.JToolBar) aCont;
            try {
                for (i=0; i<1000; i++) createName3(tp.getComponentAtIndex(i));
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "Language", e);
            }
        } else if (aCont instanceof javax.swing.JTabbedPane) {
            javax.swing.JTabbedPane tp = (javax.swing.JTabbedPane) aCont;
            for (i=0; i<tp.getTabCount(); i++) {
                createName3(tp.getComponentAt(i));
            }
        } else if (aCont instanceof javax.swing.JScrollPane) {
            javax.swing.JScrollPane sp = (javax.swing.JScrollPane) aCont;
            createName3(sp.getViewport().getView());
        } else if (aCont instanceof javax.swing.JPanel
        ||       aCont instanceof javax.swing.JSplitPane
        ||       aCont instanceof javax.swing.JLayeredPane
        ||       aCont instanceof javax.swing.JOptionPane
        ){
            java.awt.Component[] cs = aCont.getComponents();
            for (i=0; i<cs.length; i++) createName3(cs[i]);
        } else if (aCont instanceof javax.swing.MenuElement) {
            javax.swing.MenuElement[] mt = ((javax.swing.MenuElement)aCont).getSubElements();
            for (i=0; i<mt.length; i++) createName3(mt[i]);
        } else if (aCont instanceof javax.swing.JFrame) {
            createName2(((javax.swing.JFrame)aCont).getJMenuBar());
            createName2(((javax.swing.JFrame)aCont).getContentPane());
        } else if (aCont instanceof javax.swing.JDialog) {
            createName2(((javax.swing.JDialog)aCont).getJMenuBar());
            createName2(((javax.swing.JDialog)aCont).getContentPane());
        }
        
        // DEBUG: System.err.println("aCont.getName() : " + aCont.getClass().getName() + "\t" + aCont.getName() );
        
        // --- Don't continue: -------------------------------
        
        if (aCont instanceof javax.swing.text.JTextComponent
        ||  aCont instanceof javax.swing.JTable
        ||  aCont instanceof javax.swing.JTree
        ) {
            aCont.setLocale(locale);
            return;
        }
        
        // --- Get ClassType: ----------------------------
        
        clas = getClassType(aCont);
        name = aCont.getName();
        
        // --- JTabbedPane: -------------------------------
        
        if (aCont instanceof javax.swing.JTabbedPane) {
            javax.swing.JTabbedPane tp = (javax.swing.JTabbedPane) aCont;
            // SetName:
            if (firstRuning && isEmptyName(name) && tp.getTabCount()>0) {
                
                tp.setName("~TabbedPane");
                for (i=0; i<tp.getTabCount(); i++) {
                    Component subComp = tp.getComponentAt(i);
                    if (subComp!=null && isEmptyName(subComp.getName())) {

                        text = tp.getTitleAt(i);
                        name = text2key(text);
                        subComp.setName("~" + name);

                        if (show) {
                            properties.append
                            ( clas
                            + DOT
                            + text2key(name)
                            + "="
                            + tp.getTitleAt(i)
                            + nl
                            ) ;
                        }
                    }
                }
            }
            
            // SetText:
            if (isDefinedName(tp.getName())) {
                for (i=0; i<tp.getTabCount(); i++) {

                    Component subComp = tp.getComponentAt(i);
                    if (subComp!=null && isDefinedName(subComp.getName())) {

                        name = subComp.getName();
                        text = name.startsWith("~")
                        ? clas
                        + DOT
                        + name.substring(1)
                        : name
                        ;

                        tp.setTitleAt(i, getText(text));
                    }
                }
            }
            aCont.setLocale(locale);
            return;
        }
        
        // --- TitledBorder --------------
        
        if(aCont instanceof javax.swing.JComponent) {
            javax.swing.border.Border border_ = ((javax.swing.JComponent)aCont).getBorder();
            if (border_ instanceof javax.swing.border.TitledBorder) {
                javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) border_;
                
                // SetName:
                if (firstRuning && isEmptyName(name)) {
                    text = border.getTitle();
                    name = "~" + text2key(text);
                    aCont.setName(name);
                    
                    if (show) {
                        properties.append
                        ( clas
                        + DOT
                        + text2key(text)
                        + "="
                        + text
                        + nl
                        ) ;
                    }
                }
                
                // SetText:
                if (isDefinedName(name)) {
                    text = name.startsWith("~")
                    ? clas
                    + DOT
                    + name.substring(1)
                    : name
                    ;
                    try {
                        border.setTitle(getText(text));
                        aCont.repaint(); // Border is not repainted automaticaly.
                    } catch (Throwable e) {
                        LOGGER.log(Level.WARNING, "Language", e);
                    }
                }
                aCont.setLocale(locale);
                return;
            }
        }
        
        
        // --- GetText: ------------------------------------------
        
        
        if (aCont.getClass().equals(javax.swing.JPanel.class)
        ||  aCont.getClass().equals(javax.swing.JSplitPane.class)
        ||  aCont.getClass().equals(javax.swing.JScrollPane.class)
        ) {
            // Do nothing;
        } else try {
            
            // Prepare params:
            String[] mt // Name of the Method:
            =  (aCont instanceof javax.swing.JFrame
            ||  aCont instanceof javax.swing.JDialog)
            ? mTitle
            : mText
            ;
           
            // SetName:
            if (firstRuning && isEmptyName(name)) {
                me   = aCont.getClass().getMethod(mt[0], parameterTypes1);
                text = (String) me.invoke(aCont, new Object[0]);
                
                if (ApplTools.isValid(text)) {
                    name = "~" + text2key(text);
                    aCont.setName(name);
                    
                    if (show) {
                        properties.append
                        ( clas
                        + DOT
                        + text2key(text)
                        + "="
                        + text
                        + nl
                        ) ;
                    }
                }
            }
            
            // SetText:
            if (isDefinedName(name)) {
                me   = aCont.getClass().getMethod(mt[1], parameterTypes2);
                text = name.startsWith("~")
                ? clas
                + DOT
                + name.substring(1)
                : name
                ;
                
                // Set Tooltilp text:
                if (aCont instanceof JButton
                ||  aCont instanceof JToggleButton
                ||  aCont instanceof JLabel
                ){
                    try {
                        String tip = getText(text + ".TIP");
                        if (tip!=null) {
                            ((JComponent)aCont).setToolTipText(tip);
                        }
                    } catch (java.util.MissingResourceException e) {}
                }
                
                // Set Normal Text:
                String theText = getText(text);
                me.invoke( aCont, new Object[] {theText} );
                
                // Set Mnemonic Keys (Buttons & MenuItem):
                if (aCont instanceof AbstractButton && theText!=null) {
                    
//                    if (theText.startsWith("&")) {
//                        int DELETE_DEBUG = 0;
//                    }
                    
                    AbstractButton btn = (AbstractButton) aCont;
                    i = theText.indexOf('&');
                    if (i>=0 
                    && (i+1)<theText.length() 
                    && theText.charAt(i+1)!='&') {
                        StringBuilder sb = new StringBuilder(theText);
                        sb.deleteCharAt(i);
                        sb.setLength(i);
                        sb.append(theText.substring(i+1));
                        theText = sb.toString();
                        btn.setText(theText);
                        btn.setMnemonic( (int) Character.toUpperCase(theText.charAt(i)) );
                        btn.setDisplayedMnemonicIndex(i);
                    } else {
                        btn.setMnemonic(KeyEvent.VK_UNDEFINED);
                    }
                }
            }
        } catch (Throwable e) {
            err(aCont, "Not assigned by the Exception:"
            + e.getClass().getName()
            + " ("
            + e.getMessage()
            + ")"
            );
        }
        
        aCont.setLocale(locale);
        return;
    }
    
    /** Name attribute of the component is already defined. */
    private final boolean isDefinedName(String name) {
        return ApplTools.isValid(name)
        && "~.".indexOf(name.charAt(0))>=0
        ;
    }
    
    /** Name attribute of the Cotainer is allready defined. */
    private final boolean isEmptyName(String name) {
        return !ApplTools.isValid(name);
    }
    
    
    /**
     * Conditional call createName2 method.
     */
    private final void createName3(Object aObj) {
        if (aObj instanceof java.awt.Container)
            createName2((java.awt.Container) aObj);
    }
    
    /** Show error */
    private void err(java.awt.Container aCont, String msg) {
        if (show) warnings.append
        ( "Container: "
        + aCont.getClass().getName()
        + " - "
        + msg
        + nl
        );
    }
    
    /** Modification from text to key properties. */
    private String text2key(String key) {
        
        keyConvertorBuffer.setLength(0);
        char    ch;
        boolean isBlank1 = false;
        boolean isBlank2 = isBlank1;
        
        for (int i=0; i<key.length(); i++) {
            ch     = key.charAt(i);
            isBlank2
            =    Character.isWhitespace(ch)
            || (!Character.isLetterOrDigit(ch)
            &&  ".=+&".indexOf(ch)<0 ) // Exception characters
            ;
            
            if (ch=='=') keyConvertorBuffer.append("\\=");
            else if (ch==':' || isBlank2) ; // Empty Statement;
            else if (isBlank1) {            // The New Word Case:
                
                if (newSolution4Key) {
                    /** New Solution: */
                    if (bufferEndsUpperChar(keyConvertorBuffer)) {
                        keyConvertorBuffer.append('_');
                    }
                    keyConvertorBuffer.append(Character.toUpperCase(ch));
                } else {
                    /* Last Solution (Obsoleted): */
                    if (Character.isUpperCase(ch)) {
                        keyConvertorBuffer.append('_');
                        keyConvertorBuffer.append(ch );
                    } else {
                        keyConvertorBuffer.append(Character.toUpperCase(ch));
                    }
                }
            } // :isBlank1
            else keyConvertorBuffer.append(ch);
            isBlank1 = isBlank2;
        }
        
        // DEBUG ONLY: java.lang.System.err.println(">> " + key + "\t" + keyConvertorBuffer.toString());
        return keyConvertorBuffer.toString();
    }
    
    /** Modification from text to key properties. */
    private boolean bufferEndsUpperChar(StringBuilder key) {
        boolean result;
        
        if (key.length()==0) {
            result = false;
        } else {
            char c = key.charAt(key.length()-1);
            result = Character.isUpperCase(c);
        }
        return result;
    }
    
    
    /** Language Sensitive Text. Throws an exception, if appears any mistake!
     * @param key
     * @return Text from properties. */
    public String getText(Class anOwnerClass, String key, Locale aLocale) {
        aLocale = getSupportedLocale(aLocale);
        String result = java.util.ResourceBundle
        .getBundle(anOwnerClass.getName(), aLocale)
        .getString(key);
        return result;
    }
    
    /** Language Sensitive Text. Throws an exception, if appears any mistake!
     * @param key
     * @return Text from properties. */
    public String getTextAllways(Class anOwnerClass, String key, Locale aLocale) {
        return getTextAllways(anOwnerClass, key, null, aLocale);
    }
    
    /** Language Sensitive Text. Throws an exception, if appears any mistake!
     * @param key
     * @return Text from properties. */
    public String getTextAllways(Class anOwnerClass, String key, String[] parameters, Locale aLocale) {
        String result;
        try {
            result = getText(anOwnerClass, key, aLocale);
            if (parameters!=null) {
                result = MessageFormat.format(result, (Object[]) parameters);
            }
        } catch (Throwable e) {
            result = key + " (an language message not found!)";
        }
        return result;
    }
    
    /** Language Sensitive Text. Throws an exception, if exists any mistake!
     * @param key
     * @return Text from properties. */
    public String getText(String key, boolean exception) throws MissingResourceException {
        try {
            return getText(key);
        } catch (MissingResourceException e) {
            if (exception) {
                throw e;
            } else {
                System.err.println("KEY:" + key);
                return "[" + key + "]";
            }
        }
    }
    
    /** Language Sensitive Text. Throws an exception, if exists any mistake!
     * @param key
     * @return Text from properties. */
    public String getText(String key) {
        ResourceBundle rb = (ResourceBundle) bundleMap.get(locale);
        if (rb==null) {
            rb = java.util.ResourceBundle.getBundle(bundleName, locale);
            bundleMap.clear();
            bundleMap.put(locale, rb);
        }
        return rb.getString(key);
    }


    /** Language Sensitive Text. Does not throw any exception.
     * Does not return null nor empty String!
     * @param key
     * @return Text from properties. */
    public String getTextAllways(UjoProperty key) {
        return getTextAllways("tab." + key.getName());
    }

    /** Language Sensitive Text. Does not throw any exception.
     * Does not return null nor empty String!
     * @param key
     * @return Text from properties. */
    public String getTextAllways(String key) {
        String result;
        
        // Test 1:
        if (key==null) {
            key=""+key;
        }
        
        // Reading:
        try {
            result = getText(key);
        } catch (Throwable e) {
            result = key;
        }
        
        // Test 2:
        if (result==null || result.length()==0) {
            result = key;
        }
        
        return result;
    }
    
    /** Language Sensitive Text. Throws an exception, if exists any mistake!
     * @param key
     * @param parameters Any parameters of the message.
     * @return Text from properties. */
    public String getText(String key, Object... parameters) {
        String result = getText(key);
        result = MessageFormat.format(result, parameters);
        return result;
    }
    
    /** Language Sensitive Text. Throws an exception, if exists any mistake!
     * @param key
     * @param parameter The one parameter of the message.
     * @return Text from properties. */
    public String getText(String key, Object parameter) {
        String result
        = parameter!=null
        ? getText(key, new Object[]{parameter})
        : getText(key)
        ;
        return result;
    }
    
    /** Return current locale:
     * @return  */
    public Locale getLocale() {
        return locale;
    }
    
    /** Only for DEBUG: !!! */
    private void showDebugData(java.awt.Container aCont) {
        if (DEBUG_MODE) {
            
            if (aCont==null ) { return; }
            
            java.lang.reflect.Method me;
            String text;
            
            // Prepare params:
            String[] mt // Name of the Method:
            =  (aCont instanceof javax.swing.JFrame
            ||  aCont instanceof javax.swing.JDialog)
            ? mTitle
            : mText
            ;
            
            try {
                me   = aCont.getClass().getMethod(mt[0], parameterTypes1);
                text = (String) me.invoke(aCont, new Object[0]);
            } catch (Throwable e) { text = "<???>"; }
            
            java.lang.System.err.println("Container: "
            + aCont.getClass().getName() + "\t" + text);
            
        }
    }    

    
}
