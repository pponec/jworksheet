/**
  * Copyright (C) 2007, Pavel Ponec, contact: http://ponec.net
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


package net.ponec.jworksheet.data.item;

import net.ponec.jworksheet.bo.item.YearMonthDay;
import junit.framework.*;

/**
 *
 * @author Pavel Ponec
 */
public class YearMonthDayTest extends TestCase {
    
    public YearMonthDayTest(String testName) {
        super(testName);
    }

    private static Class suite() {
        return YearMonthDayTest.class;
    }
    
    
    /**
     * Test of toString method, of class net.ponec.jworksheet.core.YearMonthDay.
     */
    public void testToString() {
        System.out.println("toString");
        
        String expected = "2007-05-30";
        YearMonthDay instance = new YearMonthDay(expected);
        String result = instance.toString();
        assertEquals(expected, result);
        
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(suite());
    }
    
    
}
