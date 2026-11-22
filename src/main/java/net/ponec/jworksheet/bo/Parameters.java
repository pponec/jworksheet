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

import net.ponec.jworksheet.core.ujo.DomainObject;
import org.ujorm.annotation.UColumn;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.ponec.jworksheet.bo.meta.MetaParameters;
import net.ponec.jworksheet.core.*;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.Keys;
import net.ponec.jworksheet.core.ujo.ValueCoder;
import net.ponec.jworksheet.gui.JWorkSheet;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;
import org.ujorm.tools.Check;

/**
 * Parameters of the application.
 * @author Pavel Ponec
 */
@Domain
@Getter
@Setter
public class Parameters implements Exportable, DomainObject {

    /** A configuration subdirectory of a user home directory. */
    public static final String CONFIG_DIR = ".jWorkSheet";

    /** A "Default Value" Label */
    private static final String VALUE_DEFAULT = "<default>";

    /** A default format of the main date selection. It is shared by the report format. */
    private static final String DATE_FORMAT_DEFAULT = "EE, yyyy-MM-dd' %s: 'ww";

    // ------------------------ ATTRIBUTES ------------------------

    @UColumn(name = "Language")
    private Locale language = Locale.getDefault();

    @UColumn(name = "WorkingHours")
    private float workingHours = 8f;

    @UColumn(name = "FirstDayOfWeek")
    private int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();

    @UColumn(name = "DecimalTimeFormat")
    private boolean decimalTimeFormat = false;

    @UColumn(name = "DateMainFormat")
    private String dateMainFormat = DATE_FORMAT_DEFAULT;

    @UColumn(name = "DateReportFormat")
    private String dateReportFormat = DATE_FORMAT_DEFAULT;

    @UColumn(name = "DateReportFormat2")
    private String dateReportFormat2 = "d'<br/><span class=\"smallMonth\">'MMMM'</span>'";

    @UColumn(name = "ReportCSS")
    private String reportCss = "styles/style.css";

    @UColumn(name = "DateGotoFormat")
    private String dateGotoFormat = "yyyy-MM-dd";

    @UColumn(name = "NimbusL&FSupport")
    private boolean nimbusLaf = !ApplTools.isWindowsOS();

    @UColumn(name = "ColorOfPrivateProject")
    private Color colorOfPrivateProject = new Color(0x5DA158);

    @UColumn(name = "ColorOfFinishedProject")
    private Color colorOfFinishedProject = new Color(0xA9AC88);

    @UColumn(name = "ColorOfEditableArea")
    private Color colorOfEditableArea = new Color(0xFFFACD);

    @UColumn(name = "SystemTrayEnabled")
    private boolean systemTrayEnabled = true;

    @UColumn(name = "SystemTraySecondClick")
    private SysTray.Action systemTraySecondClick = SysTray.Action.HIDE;

    @UColumn(name = "ModifyFinishedProject")
    private boolean modifyFinishedProject = false;

    @UColumn(name = "ExitEventCreating")
    private boolean exitEventCreating = true;

    @UColumn(name = "ExitEventDescription")
    private String exitEventDescription = "EXIT";

    @UColumn(name = "HideButtonIcons")
    private boolean hideButtonIcons = false;

    @UColumn(name = "WindowSize")
    private Rectangle windowSize = new Rectangle(-1, -1, 622, 405);

    @UColumn(name = "WindowSizeRestoration")
    private boolean windowSizeRestoration = true;

    @UColumn(name = "AutomaticSortingByTime")
    private boolean automaticSortingByTime = true;

    @UColumn(name = "CheckNewRelease")
    private boolean checkNewRelease = true;

    @UColumn(name = "SystemBrowserPath")
    private String systemBrowserPath = VALUE_DEFAULT;

    @UColumn(name = "DataFilePath")
    private File dataFilePath = new File(VALUE_DEFAULT);

    @UColumn(name = "SortProjColumn")
    private String sortProjColumn = "ID";

    @UColumn(name = "SharedProjectsFilePath")
    private File sharedProjectsFilePath = new File(VALUE_DEFAULT);

    @UColumn(name = "Username")
    private String username = getSystemLogin();

    /** Decimal Formatter */
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private transient DecimalFormat decimalFormat = null;

    /** WebRelease.
     * <p>The field must be transient - it has got a getter but no setter, and such
     * a property breaks the runtime metamodel generator of the Ujorm. */
    @Setter(AccessLevel.NONE)
    private transient Version webRelease;

    /** Parameters constructor */
    public Parameters() {
        setDecimalFormat();
    }

    // ------------------------ JAVABEAN ------------------------

    public void setLanguage(final Locale language) {
        this.language = language;
        setDecimalFormat();
    }

    /** A value out of the range 1..7 is fixed. */
    public void setFirstDayOfWeek(final int firstDayOfWeek) {
        this.firstDayOfWeek = Math.floorMod(firstDayOfWeek - 1, 7) + 1;
    }

    public void setDateMainFormat(final String dateMainFormat) {
        this.dateMainFormat = validDateFormat(dateMainFormat);
    }

    public void setDateReportFormat(final String dateReportFormat) {
        this.dateReportFormat = validDateFormat(dateReportFormat);
    }

    public void setDateReportFormat2(final String dateReportFormat2) {
        this.dateReportFormat2 = validDateFormat(dateReportFormat2);
    }

    public void setDateGotoFormat(final String dateGotoFormat) {
        this.dateGotoFormat = validDateFormat(dateGotoFormat);
    }

    /** Returns the pattern, or throws an exception for an invalid one. */
    private static String validDateFormat(final String pattern) {
        new SimpleDateFormat(pattern);
        return pattern;
    }

    /** An undefined action is replaced by the default one. */
    public void setSystemTraySecondClick(final SysTray.Action systemTraySecondClick) {
        this.systemTraySecondClick = systemTraySecondClick != null
                ? systemTraySecondClick
                : SysTray.Action.HIDE;
    }

    /** An empty path is replaced by the default value. */
    public void setSystemBrowserPath(final String systemBrowserPath) {
        this.systemBrowserPath = Check.isEmpty(systemBrowserPath)
                ? VALUE_DEFAULT
                : systemBrowserPath;
    }

    /** An undefined path is replaced by the default value, otherwise the file must be writable. */
    public void setDataFilePath(final File dataFilePath) {
        final File path = pathOrDefault(dataFilePath);
        if (!isDefaultPath(path) && !canWrite(path)) {
            throw new MessageException("Can't write data to " + path);
        }
        this.dataFilePath = path;
    }

    /** An undefined path is replaced by the default value. The file is read only,
     * so it is not checked for writing. */
    public void setSharedProjectsFilePath(final File sharedProjectsFilePath) {
        this.sharedProjectsFilePath = pathOrDefault(sharedProjectsFilePath);
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

    /** Returns the default path for an undefined, an empty or a {@code <default>} path. */
    private static File pathOrDefault(final File path) {
        return path == null || isDefaultPath(path) || path.getName().isBlank()
                ? new File(VALUE_DEFAULT)
                : path;
    }

    /** Is the name of the path the {@code <default>} marker? */
    private static boolean isDefaultPath(final File path) {
        return VALUE_DEFAULT.equals(path.getName().trim());
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
    public boolean isExportable(final ExportAction action, final Key<?,?> key, final Object value) {
        return switch (action) {
            // A default value is not stored to the configuration file:
            case RESOURCE_BUNDLE -> !Keys.isDefaultValue(key, value);
            case TABLE_SHOW -> key != MetaParameters.sortProjColumn
                            && key != MetaParameters.windowSize
                            && (key != MetaParameters.dataFilePath || !Keys.isDefaultValue(key, value));
            default -> true;
        };
    }

    /** Returns a text representation of the key value. */
    public String readValueString(final Key<Parameters,?> key) {
        return ValueCoder.encode(key, key.getValue(this));
    }

    /** Returns a localized date format */
    public String getDateFormat(final Key<Parameters,String> key, final ApplContext context) {
        final String result = key.getValue(this);
        final String week = context.getLanguageManager().getTextAllways("Week");
        return String.format(result, week);
    }

    /** Set new Decimal FormatsetLocale  */
    private void setDecimalFormat() {
        decimalFormat = ApplTools.createDecimalFormat("0.00", language);
    }

    /** Returns a format time. */
    public String formatTime(int minutes) {
       if (decimalTimeFormat) {
            // Numeric format: 1.50
            return decimalFormat.format(minutes / 60f);
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
}
