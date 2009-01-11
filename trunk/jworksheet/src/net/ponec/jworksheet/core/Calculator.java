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
