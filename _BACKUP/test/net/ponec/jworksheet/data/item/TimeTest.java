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

import net.ponec.jworksheet.bo.item.Time;
import junit.framework.*;

/**
 *
 * @author Pavel Ponec
 */
public class TimeTest extends TestCase {
    
    public TimeTest(String testName) {
        super(testName);
    }
    
    private static Class suite() {
        return TimeTest.class;
    }
    
    
    /**
     * Test of setTime method, of class net.ponec.jworksheet.core.Time.
     */
    public void testSetTime() {
        System.out.println("setTime");
        
        int hourse = 15;
        int minute = 45;
        Time instance = new Time("0:00");
        
        instance.setTime(hourse, minute);
        
        assertEquals(new Time(hourse+":"+minute), instance);
        
    }
    
    /**
     * Test of addMinute method, of class net.ponec.jworksheet.core.Time.
     */
    public void testAddMinute() {
        System.out.println("testAddMinute");
        
        int hourse = 0;
        int minute = 45;
        Time instance = new Time("0:00");
        
        instance.addMinute(minute);
        
        assertEquals(new Time(hourse+":"+minute), instance);
    }
    
    /**
     * Test of toString method, of class net.ponec.jworksheet.core.Time.
     */
    public void testToString() {
        System.out.println("toString");
        
        int hourse = 15;
        int minute = 45;
        String expected = hourse+":"+minute;
        Time instance = new Time(expected);
        assertEquals(expected, instance.toString());
        
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(suite());
    }
    
    
}
