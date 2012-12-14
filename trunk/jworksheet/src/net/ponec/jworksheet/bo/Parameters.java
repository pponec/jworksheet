/**
 * Copyright (C) 2007-8, Paul Ponec, contact: http://ponec.net/
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

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import net.ponec.jworksheet.core.*;
import net.ponec.jworksheet.gui.JWorkSheet;
import org.ujorm.Key;
import org.ujorm.KeyList;
import org.ujorm.extensions.Property;
import org.ujorm.UjoAction;
import static org.ujorm.UjoAction.*;
import org.ujorm.core.KeyFactory;
import org.ujorm.extensions.AbstractUjo;

/**
 * Parameters of the application.
 * @author Pavel Ponec
 */
public class Parameters extends AbstractUjo {

    /** A configuration subdirectory of a user home directory. */
    public static final String CONFIG_DIR = ".jWorkSheet";

    /** A "Default Value" Label */
    private static final String VALUE_DEFAULT = "<default>";
    
    private static final KeyFactory<Parameters> f = KeyFactory.CamelBuilder.get(Parameters.class);

    /** LocaleText lcalization */
    public static final Key<Parameters,Locale> P_LANG = f.newKey("Language", Locale.getDefault());
    /** Working Hours */
    public static final Key<Parameters,Float> P_WORKING_HOURS = f.newKey("WorkingHours", 8f);
    /** The First Day of the Week Day. */
    public static final Key<Parameters,Integer> P_FIRST_DAY_OF_WEEK = f.newKey("FirstDayOfWeek", Calendar.getInstance().getFirstDayOfWeek());
    /** Decimal time format. */
    public static final Key<Parameters,Boolean> P_DECIMAL_TIME_FORMAT = f.newKey("DecimalTimeFormat", true);
    /** The Main selecton format. */
    public static final Key<Parameters,String> P_DATE_MAIN_FORMAT = f.newKey("DateMainFormat", "EE, yyyy/MM/dd'  %s: 'ww");
    /** The Export Date Selection. */
    public static final Key<Parameters,String> P_DATE_REPORT_FORMAT = f.newKey("DateReportFormat", P_DATE_MAIN_FORMAT.getDefault() );
    /** The Export Date Selection. */
    public static final Key<Parameters,String> P_DATE_REPORT_FORMAT2 = f.newKey("DateReportFormat2", "d'<br/><span class=\"smallMonth\">'MMMM'</span>'" );
    /** The complementary report CSS style. */
    public static final Key<Parameters,String> P_REPORT_CSS = f.newKey("ReportCSS", "styles/style.css" );
    /** The Goto Date format. */
    public static final Key<Parameters,String> P_DATE_GOTO_FORMAT = f.newKey("DateGotoFormat", "yyyy/MM/dd");
    /** Nimbus Look & Feel support */
    public static final Key<Parameters,Boolean> P_NIMBUS_LAF = f.newKey("NimbusL&FSupport", !ApplTools.isWindowsOS());
    /** A Color of a private project. */
    public static final Key<Parameters,Color> P_COLOR_PRIVATE = f.newKey("ColorOfPrivateProject", new Color(0x5DA158));
    /** A Color of finished project. */
    public static final Key<Parameters,Color> P_COLOR_FINISHED_PROJ = f.newKey("ColorOfFinishedProject", new Color(0xA9AC88));
    /** A Color of an editable area. */
    public static final Key<Parameters,Color> P_COLOR_EDITABLE = f.newKey("ColorOfEditableArea", new Color(0xFFFACD));
    /** Is a System Tray Enabled? */
    public static final Key<Parameters,Boolean> P_SYSTRAY_ENABLED = f.newKey("SystemTrayEnabled", ApplTools.isWindowsOS());
    /** Action on a second click */
    public static final Key<Parameters,SysTray.Action> P_SYSTRAY_SECOND_CLICK = f.newKey("SystemTraySecondClick", SysTray.Action.NONE);
    /** Modify value of finished project or task. */
    public static final Key<Parameters,Boolean> P_MODIFY_FINESHED_PROJ = f.newKey("ModifyFinishedProject", false);
    /** Create a new Event on an EXIT action. */
    public static final Key<Parameters,Boolean> P_EXIT_EVENT_CREATE = f.newKey("ExitEventCreating", true);
    /** Description of an EXIT action. */
    public static final Key<Parameters,String> P_EXIT_EVENT_DESCR = f.newKey("ExitEventDescription", "EXIT");
    /** Hide Buoon Icon. */
    public static final Key<Parameters,Boolean> P_HIDE_ICONS = f.newKey("HideButtonIcons", false);
    /** Last window size and position. */
    public static final Key<Parameters,Rectangle> P_WINDOW_SIZE = f.newKey("WindowSize", new Rectangle(-1, -1, 622, 405));
    /** Restore the last application window size and position. */
    public static final Key<Parameters,Boolean> P_WINDOW_SIZE_RESTORATION = f.newKey("WindowSizeRestoration", true);
    /** Automatic sorting of the events by time. */
    public static final Key<Parameters,Boolean> P_AUTOMATIC_SORTING_BY_TIME = f.newKey("AutomaticSortingByTime", true);
    /** Check new release on the home page */
    public static final Key<Parameters,Boolean> P_CHECK_NEW_RELEASE = f.newKey("CheckNewRelease", true);
    /** A full path to a system browser. */
    public static final Key<Parameters,String> P_SYSTEM_BROWSER_PATH = f.newKey("SystemBrowserPath", VALUE_DEFAULT);
    /** Close Report Dialog after OK. */
    // public static final Key<Parameters,Boolean> P_CLOSE_REPORT_DIALOG = newKey("CloseReportDialogOK", Boolean.FALSE);
    /** A full path to a DataFile. */
    public static final Key<Parameters,File> P_DATA_FILE_PATH = f.newKey("DataFilePath", new File(VALUE_DEFAULT));
    /** Project table sorted column */
    public static final Key<Parameters,String> P_SORT_PROJ_COLUMN = f.newKey("SortProjColumn", Project.P_ID.getName());
    /** A full path to a Shared Projects DataFile. */
    public static final Key<Parameters,File> P_PROJECTS_FILE_PATH = f.newKey("SharedProjectsFilePath", new File(VALUE_DEFAULT));
    /** Name of user of this application. */
    public static final Key<Parameters,String> P_USERNAME = f.newKey("Username", getSystemLogin());

    // --- An optional property unique name test ---
    static { f.lock(); }

    /** Decimal Formatter */
    private DecimalFormat decimalFormat = null;

    /** WebRelease */
    private Version webRelease;

    /** Returns an operation system login */
    public static String getSystemLogin() {
        String result = System.getProperty("user.name");
        return ApplTools.isValid(result)
             ? Character.toUpperCase(result.charAt(0)) + result.substring(1)
             : "?"
             ;
    }
    
    /** Parameters constructor */
    public Parameters() {
        // Default initialization:
        for (Key par : readKeys()) {
            writeValue(par, par.getDefault());
        }
    }
    
    /** Returns a propertyCount value */
    @Override
    public KeyList<?> readKeys() {
        return f.getKeys();
    }
    
    /** Overrided for additional features */
    @Override
    public void writeValue(Key property, Object value) {
        
        if (P_SYSTEM_BROWSER_PATH==property
        && !ApplTools.isValid((String)value)) {
            value = P_SYSTEM_BROWSER_PATH.getDefault();
        } else if (P_DATA_FILE_PATH==property) {
            String name
            = value!=null
            ? ((File)value).getName().trim()
            : VALUE_DEFAULT
            ;
            if (VALUE_DEFAULT.equals(name)
            ||  name.length()==0 ){
                value = P_DATA_FILE_PATH.getDefault();
            }
            if (value!=P_DATA_FILE_PATH.getDefault()
            && !canWrite((File)value)
            ){
                throw new MessageException("Can't write data to " + value);
            }
        }
        
        super.writeValue(property, value);
        
        if (P_LANG==property) {
            setDecimalFormat();
        } else if (P_DATE_MAIN_FORMAT  ==property
        ||         P_DATE_REPORT_FORMAT==property
        ){
            // A validaton only:
            new SimpleDateFormat((String) value);
        } else if (P_FIRST_DAY_OF_WEEK==property) {
            Integer day  = (Integer) value;
            if (day < 1 || 7 < day) {
                // Fix the value:
                day = ((day + 7*1000 - 1) % 7) + 1;
                super.writeValue(P_FIRST_DAY_OF_WEEK, day);
            }
        }
    }
    
    /** Enables the file writing or creating changes? */
    private boolean canWrite(File file) {
        try {
            final boolean result
            = file.isFile()
            ? file.canWrite()
            : file.createNewFile()
            ;
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** An authorization settings. */
    @Override
    @SuppressWarnings("unchecked")
    public boolean readAuthorization(UjoAction action, Key property, Object value) {
        switch(action.getType()) {
            case ACTION_RESBUNDLE_EXPORT:
                return !property.isDefault(this);
            case ACTION_TABLE_SHOW:
                if (property==P_SORT_PROJ_COLUMN){
                    return false;
                }
                final boolean result
                = property ==P_DATA_FILE_PATH
                ? (value   !=P_DATA_FILE_PATH.getDefault()
                && value   != null)
                :  property!=P_WINDOW_SIZE
                ;
                
                return result;
            default:
                return super.readAuthorization(action, property, value);
        }
    }
    
    // --------------------------------

    /** Returns a localized date format */
    public String getDateFormat(Key<Parameters,String> property, ApplContext context) {
        String result = get(property);
        String week   = context.getLanguageManager().getTextAllways("Week");
        result = String.format(result, week);
        return result;
    }

    
    /** Set new Decimal FormatsetLocale  */
    private void setDecimalFormat() {
        Locale locale = get(P_LANG);
        decimalFormat = ApplTools.createDecimalFormat("0.00", locale);
    }
    
    /** WriteValueString */
    @Override
    @SuppressWarnings("unchecked")
    public void writeValueString(Key property, String value, Class type, UjoAction action) {
      
        if (P_SYSTRAY_SECOND_CLICK==property) {
            if (ApplTools.isValid(value)) {
               super.writeValueString(property, value.toUpperCase(), type, action);
            } else {
               ((Property) property).setValueFromDefault(this);
            }
        } else {
              super.writeValueString(property, value, type, action);
        }
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
        if (webRelease==null) {
            webRelease = ApplTools.getWebRelease(JWorkSheet.APPL_JNLP);
        }
        return webRelease;
    }


    @SuppressWarnings("unchecked")
    public <UJO extends Parameters, VALUE> VALUE get(Key<UJO, VALUE> up) {
        return up.of((UJO)this);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends Parameters, VALUE> UJO set(Key<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }


    
}
