/**
 * Copyright (C) 2007-2021, Pavel Ponec, contact: http://ponec.net/
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.WorkDay;

/**
 * Days Report
 * @author Pavel Ponec
 */
public class ReportAttendance extends SuperReport {


    /** Last WorkDay. */
    protected WorkDay lastWorkDay;

    /** Last report */
    protected ReportA report = null;

    /** Report body */
    protected StringBuilder repBody = new StringBuilder(256);

    // --------- ROW ITEMS ------------------

    private int workTime  = 0;
    private int breakTime = 0;
    private int arrival   = 0;
    private int departure = 0;
    private List<String> descrs = new ArrayList<String>();



//    @Override
//    public void init(ApplContext applContext, YearMonthDay dateFrom, YearMonthDay dateTo, String title) {
//        super.init(applContext, dateFrom, dateTo, title);
//    }

    public void calculate(WorkDay workDay, Event event) {
        if (lastWorkDay!= workDay) try {
            lastWorkDay = workDay;
            printDay();

            workTime  = 0;
            breakTime = 0;
            arrival   = 0;
            departure = 0;
            descrs.clear();

        } catch (IOException ex) {
            Logger.getLogger(ReportAttendance.class.getName()).log(Level.SEVERE, null, ex);

        } else {

            if (event.get(Event.P_PROJ).get(Project.P_PRIVATE)) {
                breakTime += event.get(Event.P_PERIOD);
            } else {
                workTime  += event.get(Event.P_PERIOD);

                String descr = event.get(Event.P_DESCR);
                if (!descrs.contains(descr)) {
                    descrs.add(descr);
                }
            }

            int startTime = event.get(Event.P_TIME).getMinutes();
            if (arrival > startTime) {
                arrival = startTime;
            }
            int endTime = startTime + event.get(Event.P_PERIOD);
            if (departure < endTime) {
                departure = endTime;
            }

        }
    }

    /** Print one day. */
    private void printDay() throws IOException {

        Appendable sb = repBody;

        sb.append("<tr>");
        sb.append("<th align=\"left\">").append(getText(WorkDay.P_DATE)).append("</th>");
        sb.append("<th align=\"left\">").append(escape(arrival)).append("</th>");
        sb.append("<th align=\"left\">").append(escape(departure)).append("</th>");
        sb.append("<th align=\"left\">").append(escape(breakTime)).append("</th>");
        sb.append("<th align=\"left\">").append(escape(workTime)).append("</th>");
        sb.append("<th align=\"left\"><ul>");

        for (String d : descrs) {
           sb.append("<li>");
           sb.append(escape(d));
           sb.append("</li>");
        }

        sb.append("</ul></th>");
        sb.append("<tr>");
    }

    public String print() throws IOException {
        return getReport(repBody.toString(), reportTitle);
    }

    /**
     * Print the data result
     * @param sb
     * @throws java.io.IOException
     * @return totalTime
     */
    public void printHeader(StringBuilder sb) throws IOException {

        sb.append("<tr>");
        sb.append("<th align=\"left\">").append(getText(WorkDay.P_DATE)).append("</th>");
        sb.append("<th align=\"left\">").append(getText("Arrival")).append("</th>");
        sb.append("<th align=\"left\">").append(getText("Departure")).append("</th>");
        sb.append("<th align=\"left\">").append(getText("Break")).append("</th>");
        sb.append("<th align=\"left\">").append(getText("WorkTime")).append("</th>");
        sb.append("<th align=\"left\">").append(getText("Tasks")).append("</th>");
        sb.append("<tr>");
    }


}
