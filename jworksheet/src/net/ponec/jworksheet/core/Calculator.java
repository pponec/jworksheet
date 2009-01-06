/*
 * Calculator.java
 *
 * Created on 28. srpen 2007, 21:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.ponec.jworksheet.core;

import java.io.IOException;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.item.YearMonthDay;

/**
 * Calculate all events.
 * @author Pavel Ponec
 */
public interface Calculator {
    
    /** Calculate required times. */
    public void calculate(WorkDay workDay, Event event);
    
    /** An application context */
    public ApplContext getApplContext();
    
    /** An general initializaton */
    public void init
    ( ApplContext applContext
    , YearMonthDay dateFrom
    , YearMonthDay dateTo
    , String reportTitle
    );
    
    /** Print the result in HTML report. */
    public String print() throws IOException;
    
}
