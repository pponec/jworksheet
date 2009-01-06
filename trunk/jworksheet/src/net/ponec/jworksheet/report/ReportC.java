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


package net.ponec.jworksheet.report;

import java.io.IOException;
import net.ponec.jworksheet.core.MessageException;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.WorkDay;

/**
 * Days Report
 * @author Pavel Ponec
 */
public class ReportC extends SuperReport {
    
    
    /** Last WorkDay. */
    protected WorkDay lastWorkDay;
    
    /** Last report */
    protected ReportA report = null;
    
    /** Report body */
    protected StringBuilder repBody = new StringBuilder(256);
    
//    @Override
//    public void init(ApplContext applContext, YearMonthDay dateFrom, YearMonthDay dateTo, String title) {
//        super.init(applContext, dateFrom, dateTo, title);
//    }
    
    public void calculate(WorkDay workDay, Event event) {
        if (lastWorkDay!= workDay) {
            lastWorkDay = workDay;
            printDay();
        }
    }
    
    private void printDay() {
        report = new ReportA();
        report.init
        ( applContext
        , WorkDay.P_DATE.of(lastWorkDay)
        , WorkDay.P_DATE.of(lastWorkDay)
        , reportTitle
        );
        
        try {
            report.printFilter(repBody);
            report.printTableBeg(repBody);
            int total = report.printDetail(repBody);
            report.printTotal(total, true, repBody);
            report.printTableEnd(repBody);
        } catch (IOException ex) {
            throw new MessageException("Can't write report");
        }
    }
    
    public String print() throws IOException {
        return getReport(repBody.toString(), reportTitle);
    }
    
}
