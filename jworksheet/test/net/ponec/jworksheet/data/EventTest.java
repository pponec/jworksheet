/*
 * EventTest.java
 * JUnit based test
 *
 * Created on 29. kvìten 2008, 21:10
 */

package net.ponec.jworksheet.data;

import net.ponec.jworksheet.bo.Event;
import junit.framework.*;
import net.ponec.jworksheet.bo.item.Time;

/**
 *
 * @author Pavel Ponec
 */
public class EventTest extends TestCase {
    
    public EventTest(String testName) {
        super(testName);
    }
    
    private static Class suite() {
        return EventTest.class;
    }    

    /**
     * Test of setTime method, of class net.ponec.jworksheet.data.Event.
     */
    public void testSetTime() {
        Event event = new Event();
        
        Time result = new Time(10);
        event.setTime(result);
        Time value = event.getTime();
        assertEquals(result, value);
    }

    /**
     * Test of setTime method, of class net.ponec.jworksheet.data.Event.
     */
    public void testSetTime2() {
        Event event = new Event();
        
        Time result = new Time(10);
        Event.P_TIME.setValue(event, result);
        Time value = Event.P_TIME.getValue(event);
        assertEquals(result, value);
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(suite());
    }

    
}
