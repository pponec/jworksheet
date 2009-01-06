/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ponec.jworksheet.core;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.gui.JWorkSheet;

/**
 * ApplContextInterface
 * @author Ponec
 */
public interface ApplContextInterface {

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

    /** Get JWorkSheet */
    public JWorkSheet getTopFrame();

    /** Systray is enabled in case a parameter is supported AND system is supported too */
    public boolean isSystrayEnabled();
}
