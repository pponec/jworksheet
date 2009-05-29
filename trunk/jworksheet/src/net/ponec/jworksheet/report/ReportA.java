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
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.WorkDay;
import org.ujoframework.core.UjoComparator;

/**
 * Standard report
 * @author Pavel Ponec
 */
public class ReportA extends SuperReport {
    
    /** Group Set */
    protected GroupSet groupSet = null;
    
    /** An Initializaton */
    @Override
    protected void init() {
        if (groupSet==null) {
            UjoComparator comparator = createUjoComparator();
            groupSet = new GroupSet(comparator, Event.P_PERIOD);
        }
    }
    
    /** Create Comparator */
    protected UjoComparator createUjoComparator() {
        UjoComparator comparator = UjoComparator.newInstance(TaskGroup.P_PROJ, TaskGroup.P_TASK);
        return comparator;
    }
    
    /** Create report */
    public void calculate(WorkDay workDay, Event event) {
        groupSet.addTime(event);
    }
    
    /** Print the data result */
    public String print() throws IOException {
        StringBuilder repBody = new StringBuilder(256);
        boolean showTasks = !ReportB.class.equals(getClass());
        
        printFilter(repBody);
        printTableBeg(repBody);
        int total = printDetail(repBody);
        printTotal(total, showTasks, repBody);
        printTableEnd(repBody);
        String result = getReport(repBody.toString(), reportTitle);
        
        return result;
    }
    
    /**
     * Print the data result
     * @param sb
     * @throws java.io.IOException
     * @return totalTime
     */
    public int printDetail(StringBuilder sb) throws IOException {
        int total = 0;
        
        sb.append("<tr>");
        sb.append("<th align=\"left\">").append(getText(Event.P_PROJ)).append("</th>");
        sb.append("<th align=\"left\">").append(getText(Event.P_TASK)).append("</th>");
        sb.append("<th align=\"right\">").append(getText("Time[hours]")).append("</th>");
        sb.append("</tr>");
        
        for(TaskGroup group : groupSet.getGroups()) {
            Project  proj = TaskGroup.P_PROJ.of(group);
            TaskType task = TaskGroup.P_TASK.of(group);
            
            if (proj==null || !Project.P_PRIVATE.of(proj)) {
                sb.append("<tr>");
                sb.append("<td>").append( escape(proj) ).append("</td>");
                sb.append("<td>").append( escape(task) ).append("</td>");
                sb.append("<td class=\"num\">").append(formatTime(group.getTime())).append("</td>");
                sb.append("</tr>");
                total += group.getTime();
            }
        }
        return total;
    }
    
    /** Print Total Time */
    protected void printTotal(int total, boolean tasks, StringBuilder sb) {
        
        // Total time:
        sb.append("<tr class=\"total\">");
        sb.append("<td class=\"alignLeft\">").append(getText("TotalTime")).append("</td>");
        if (tasks) {
            sb.append("<td>").append("&nbsp;").append("</td>");
        }
        sb.append("<td>").append(formatTime(total)).append("</td>");
        sb.append("</tr>");
        
        // Overtime:
        float requiredTime = getWorkDayCount()*Parameters.P_WORKING_HOURS.of(applContext.getParameters());
        sb.append("<tr class=\"total\">");
        sb.append("<td class=\"alignLeft\">").append(getText("Overtime")).append("</td>");
        if (tasks) {
            sb.append("<td>").append("&nbsp;").append("</td>");
        }
        sb.append("<td>").append(formatTime(total - Math.round(requiredTime*60))).append("</td>");
        sb.append("</tr>");
    }
    
    
    /** Vrací sadu skupin: */
    protected GroupSet getGroupSet() {
        return groupSet;
    }
    
    public void setGroupSet(GroupSet groupSet) {
        this.groupSet = groupSet;
    }
    
}
