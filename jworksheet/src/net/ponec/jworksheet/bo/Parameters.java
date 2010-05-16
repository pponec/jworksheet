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
import org.ujoframework.UjoProperty;
import org.ujoframework.extensions.Property;
import org.ujoframework.UjoAction;
import org.ujoframework.implementation.array.ArrayUjo;
import static org.ujoframework.UjoAction.*;

/**
 * Parameters of the application.
 * @author Pavel Ponec
 */
public class Parameters extends ArrayUjo {

    /** A configuration subdirectory of a user home directory. */
    public static final String CONFIG_DIR = ".jWorkSheet";

    /** A "Default Value" Label */
    private static final String VALUE_DEFAULT = "<default>";

    /** An Incrementator. Use a new counter for each subclass. */
    protected static int propertyCount = ArrayUjo.propertyCount;
    
    /** LocaleText lcalization */
    public static final UjoProperty<Parameters,Locale> P_LANG = newProperty("Language", Locale.getDefault(), propertyCount++);
    /** Working Hours */
    public static final UjoProperty<Parameters,Float> P_WORKING_HOURS = newProperty("WorkingHours", 8f, propertyCount++);
    /** The First Day of the Week Day. */
    public static final UjoProperty<Parameters,Integer> P_FIRST_DAY_OF_WEEK = newProperty("FirstDayOfWeek", Calendar.getInstance().getFirstDayOfWeek(), propertyCount++);
    /** Decimal time format. */
    public static final UjoProperty<Parameters,Boolean> P_DECIMAL_TIME_FORMAT = newProperty("DecimalTimeFormat", true, propertyCount++);
    /** The Main selecton format. */
    public static final UjoProperty<Parameters,String> P_DATE_MAIN_FORMAT = newProperty("DateMainFormat", "EE, yyyy/MM/dd'  %s: 'ww", propertyCount++);
    /** The Export Date Selection. */
    public static final UjoProperty<Parameters,String> P_DATE_REPORT_FORMAT = newProperty("DateReportFormat", P_DATE_MAIN_FORMAT.getDefault() , propertyCount++);
    /** The Export Date Selection. */
    public static final UjoProperty<Parameters,String> P_DATE_REPORT_FORMAT2 = newProperty("DateReportFormat2", "d'<br/><span class=\"smallMonth\">'MMMM'</span>'" , propertyCount++);
    /** The complementary report CSS style. */
    public static final UjoProperty<Parameters,String> P_REPORT_CSS = newProperty("ReportCSS", "styles/style.css" , propertyCount++);
    /** The Goto Date format. */
    public static final UjoProperty<Parameters,String> P_DATE_GOTO_FORMAT = newProperty("DateGotoFormat", "yyyy/MM/dd", propertyCount++);
    /** Nimbus Look & Feel support */
    public static final UjoProperty<Parameters,Boolean> P_NIMBUS_LAF = newProperty("NimbusL&FSupport", !ApplTools.isWindowsOS(), propertyCount++);
    /** A Color of a private project. */
    public static final UjoProperty<Parameters,Color> P_COLOR_PRIVATE = newProperty("ColorOfPrivateProject", new Color(0x5DA158), propertyCount++);
    /** A Color of finished project. */
    public static final UjoProperty<Parameters,Color> P_COLOR_FINISHED_PROJ = newProperty("ColorOfFinishedProject", new Color(0xA9AC88), propertyCount++);
    /** A Color of an editable area. */
    public static final UjoProperty<Parameters,Color> P_COLOR_EDITABLE = newProperty("ColorOfEditableArea", new Color(0xFFFACD), propertyCount++);
    /** Is a System Tray Enabled? */
    public static final UjoProperty<Parameters,Boolean> P_SYSTRAY_ENABLED = newProperty("SystemTrayEnabled", ApplTools.isWindowsOS(), propertyCount++);
    /** Action on a second click */
    public static final UjoProperty<Parameters,SysTray.Action> P_SYSTRAY_SECOND_CLICK = newProperty("SystemTraySecondClick", SysTray.Action.NONE, propertyCount++);
    /** Modify value of finished project or task. */
    public static final UjoProperty<Parameters,Boolean> P_MODIFY_FINESHED_PROJ = newProperty("ModifyFinishedProject", false, propertyCount++);
    /** Create a new Event on an EXIT action. */
    public static final UjoProperty<Parameters,Boolean> P_EXIT_EVENT_CREATE = newProperty("ExitEventCreating", true, propertyCount++);
    /** Description of an EXIT action. */
    public static final UjoProperty<Parameters,String> P_EXIT_EVENT_DESCR = newProperty("ExitEventDescription", "EXIT", propertyCount++);
    /** Hide Buoon Icon. */
    public static final UjoProperty<Parameters,Boolean> P_HIDE_ICONS = newProperty("HideButtonIcons", false, propertyCount++);
    /** Last window size and position. */
    public static final UjoProperty<Parameters,Rectangle> P_WINDOW_SIZE = newProperty("WindowSize", new Rectangle(-1, -1, 622, 405), propertyCount++);
    /** Restore the last application window size and position. */
    public static final UjoProperty<Parameters,Boolean> P_WINDOW_SIZE_RESTORATION = newProperty("WindowSizeRestoration", true, propertyCount++);
    /** Automatic sorting of the events by time. */
    public static final UjoProperty<Parameters,Boolean> P_AUTOMATIC_SORTING_BY_TIME = newProperty("AutomaticSortingByTime", true, propertyCount++);
    /** A full path to a system browser. */
    public static final UjoProperty<Parameters,String> P_SYSTEM_BROWSER_PATH = newProperty("SystemBrowserPath", VALUE_DEFAULT, propertyCount++);
    /** Close Report Dialog after OK. */
    // public static final UjoProperty<Parameters,Boolean> P_CLOSE_REPORT_DIALOG = newProperty("CloseReportDialogOK", Boolean.FALSE);
    /** A full path to a DataFile. */
    public static final UjoProperty<Parameters,File> P_DATA_FILE_PATH = newProperty("DataFilePath", new File(VALUE_DEFAULT), propertyCount++);
    /** Project table sorted column */
    public static final UjoProperty<Parameters,String> P_SORT_PROJ_COLUMN = newProperty("SortProjColumn", Project.P_ID.getName(), propertyCount++);

    // --- An optional property unique name test ---
    static { init(Parameters.class,true); }

    /** Decimal Formatter */
    private DecimalFormat decimalFormat = null;
    
    /** Parameters constructor */
    public Parameters() {
        // Default initialization:
        for (UjoProperty par : readProperties()) {
            writeValue(par, par.getDefault());
        }
    }
    
    /** Returns a propertyCount value */
    @Override
    public int readPropertyCount() {
        return propertyCount;
    }
    
    /** Overrided for additional features */
    @Override
    public void writeValue(UjoProperty property, Object value) {
        
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
    public boolean readAuthorization(UjoAction action, UjoProperty property, Object value) {
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
    public String getDateFormat(UjoProperty<Parameters,String> property, ApplContext context) {
        String result = property.of(this);
        String week   = context.getLanguageManager().getTextAllways("Week");
        result = String.format(result, week);
        return result;
    }

    
    /** Set new Decimal FormatsetLocale  */
    private void setDecimalFormat() {
        Locale locale = P_LANG.of(this);
        decimalFormat = ApplTools.createDecimalFormat("0.00", locale);
    }
    
    /** WriteValueString */
    @Override
    @SuppressWarnings("unchecked")
    public void writeValueString(UjoProperty property, String value, Class type, UjoAction action) {
      
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
       final boolean deci = P_DECIMAL_TIME_FORMAT.of(this);
       
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

    @SuppressWarnings("unchecked")
    public <UJO extends Parameters, VALUE> VALUE get(UjoProperty<UJO, VALUE> up) {
        return up.getValue((UJO)this);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends Parameters, VALUE> UJO set(UjoProperty<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }


    
}
