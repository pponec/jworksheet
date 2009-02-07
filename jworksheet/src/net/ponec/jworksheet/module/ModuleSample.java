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
