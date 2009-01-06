/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.jworksheet.module;

import java.util.Calendar;
import java.util.Date;
import net.ponec.jworksheet.gui.JWorkSheet;

/**
 * An sample moduel
 * @author pavel
 */
public class ModuleSample extends ModuleApiImpl {

    @Override
    public void eventListener(boolean start) {
        if (start) {
            System.out.println("STARTING jWorkSheet");
        } else {
            System.out.println("FINISHING jWorkSheet");
        }
    }

    public Date getCreated() {
        Calendar c = Calendar.getInstance();
        c.set(2009, Calendar.JANUARY, 05);
        return c.getTime();
    }

    public String getDescription() {
        return "A sample module<br>for an demonstration.";
    }

    public String getName() {
        return "Sample module";
    }

    public String getRelease() {
        return JWorkSheet.APPL_VERSION;
    }




}
