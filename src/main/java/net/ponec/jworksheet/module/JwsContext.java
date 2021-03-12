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

package net.ponec.jworksheet.module;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.JTabbedPane;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.gui.JWorkSheet;

/**
 * The jWorkSeet module content.
 * The method provides a useful information about state of parameters the jWorkSheet application.
 *
 * @author Ponec
 * @since 0.85
 */
public interface JwsContext {

    /** Load WorkSpace */
    public WorkSpace getWorkSpace();

    /** Configuration Directory */
    public File getConfigDir();

    /** Style Directory */
    public File getStyleDir();

    /** Configuration Directory */
    public File getConfigFile();

    /** Data File */
    public File getDataFile();

    /** Data File for a temporarry usage. */
    public File getDataFileTemp() throws IOException;

    /** Data File Backup */
    public File getDataFileBackup();

    /** Get a style file */
    public File getStyleFile(String fileName);

    /** Get "Selected Day" - don't modify it! */
    public YearMonthDay getSelectedDay();

    /** Is selected time Today? */
    public boolean isToday();

    /** Current Language */
    public Locale getLanguage();

    /** Get Work Day */
    public WorkDay getWorkDay();

    /** Parameters of the application. */
    public Parameters getParameters();

    /** Does application starting */
    public boolean isStarting();

    /** Get JWorkSheet instance */
    public JWorkSheet getTopFrame();

    /** Get a main Tabbed pane of the application for adding a new panels. */
    public JTabbedPane getTabbedPane();

    /** Systray is enabled in case a parameter is supported AND system is supported too */
    public boolean isSystrayEnabled();
}
