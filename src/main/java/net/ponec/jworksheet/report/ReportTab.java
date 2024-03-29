/**
 * Copyright (C) 2007-2022, Pavel Ponec, contact: http://ponec.net/
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujorm.core.UjoComparator;

/**
 * Day Report with a task resolution.
 * @author Pavel Ponec
 */
public class ReportTab extends SuperReport {
    
    /** Table of results */
    protected List<GroupSet> data = new ArrayList<GroupSet>();
    
    /** Last WorkDay. */
    protected WorkDay lastWorkDay;
    
    protected UjoComparator<TaskGroup> comparator;
    
    /** Last report */
    protected ReportA report = null;
    
    @Override
    public void init(ApplContext applContext, YearMonthDay dateFrom, YearMonthDay dateTo, String reportTitle) {
        super.init(applContext, dateFrom, dateTo, reportTitle);
        comparator = createUjoComparator();
    }
    
    public void calculate(WorkDay workDay, Event event) {
        
        if (lastWorkDay!= workDay) {
            lastWorkDay = workDay;
            saveDay();
        }
    }
    
    /** Create Comparator */
    protected UjoComparator<TaskGroup> createUjoComparator() {
        return UjoComparator.<TaskGroup>newInstance(TaskGroup.P_PROJ, TaskGroup.P_TASK);
    }
    
    private void saveDay() {
        YearMonthDay ymd = lastWorkDay.get(WorkDay.P_DATE);
        
        report = new ReportA();
        GroupSet groupSet = new GroupSet(createUjoComparator(), Event.P_PERIOD);
        groupSet.setShowEmptyProject(true);
        report.setGroupSet(groupSet);
        
        report.init
        ( applContext
        , ymd
        , ymd
        , reportTitle
        );
        
        //GroupSet groupSet = report.getGroupSet();
        for(TaskGroup tg : groupSet.getGroups() ) {
            TaskGroup.P_DAY.setValue(tg, ymd);
        }
        data.add(report.getGroupSet());
    }
    
    @Override
    public String print() throws IOException {
        String result = getReport(printBody(), reportTitle);
        return result;
    }
    
    public String printBody() throws IOException {
        // Report body:
        StringBuilder sb = new StringBuilder(256);
        
        printFilter(sb);
        printTableBeg(sb);
        
        List<TaskGroup> projs = getProjects();
        List<YearMonthDay> days = getDays();
        
        SimpleDateFormat exportDateFormat = new SimpleDateFormat(applContext.getParameters().get(Parameters.P_DATE_REPORT_FORMAT2), applContext.getLanguage());
        String tipFormat = applContext.getParameters().getDateFormat(Parameters.P_DATE_REPORT_FORMAT, applContext);
        SimpleDateFormat tooltipDateFormat = new SimpleDateFormat(tipFormat, applContext.getLanguage());
        
        // Header ---
        sb.append("<tr>\n<th>");
        sb.append(getText(".TabbedPane.Projects"));
        sb.append("</th>\n");
        for(YearMonthDay day : days) {
            boolean dayOff = isDayOff(day);
            sb.append("<th title=\"" + escape((dayOff?(getText("DayOff")+": "):"") + tooltipDateFormat.format(day.getTime())) + "\"");
            if (dayOff) { sb.append(" class=\"dayOff\""); }
            sb.append(">");
            sb.append(exportDateFormat.format(day.getTime()));
            sb.append("</th>\n");
        }
        sb.append("<th>&nbsp;&nbsp;");
        sb.append(getText("Total"));
        sb.append("</th>");
        sb.append("</tr>\n");
        
        // Body ---
        for (TaskGroup proj : projs) {
            sb.append("<tr>");
            
            sb.append("<td>");
            sb.append(escape(proj.get(TaskGroup.P_PROJ)));
            sb.append(" / ");
            sb.append(escape(proj.get(TaskGroup.P_TASK)));
            sb.append("</td>\n");
            
            int total = 0;
            for(YearMonthDay day : days) {
                int time = getCellTime(proj, day);
                total += time;
                sb.append("<td class=\"num\">");
                sb.append(formatTimeZero(time));
                sb.append("</td>\n");
            }
            sb.append("<td class=\"total\">");
            sb.append(formatTime(total));
            sb.append("</td>\n");
            sb.append("</tr>\n");
        }
        
        // Total time ---
        LinkedList<Integer> totals = new LinkedList<Integer>();
        int total = 0;
        sb.append("<tr class=\"total\">\n<td class=\"alignLeft\">");
        sb.append(getText("TotalTime"));
        sb.append("</td>\n");
        for(YearMonthDay day : days) {
            int time = getDayTime(day);
            totals.add(time);
            total += time;
            
            sb.append("<td title=\"" + escape(tooltipDateFormat.format(day.getTime())) + "\">");
            sb.append(formatTime(time));
            sb.append("</td>\n");
        }
        sb.append("<td>");
        sb.append(formatTime(total));
        sb.append("</td>\n");
        sb.append("</tr>\n");
        
        // Overtime ---
        total = 0;
        int workingMinutes = Math.round(applContext.getParameters().get(Parameters.P_WORKING_HOURS)*60);
        sb.append("<tr class=\"total\">\n<td class=\"alignLeft\">");
        sb.append(getText("Overtime"));
        sb.append("</td>\n");
        for(YearMonthDay day : days) {
            int time = totals.removeFirst();
            if (!isDayOff(day)) {
                time -= workingMinutes;
            }
            total += time;
            
            sb.append("<td title=\"" + escape(tooltipDateFormat.format(day.getTime())) + "\">");
            sb.append(formatTime(time));
            sb.append("</td>\n");
        }
        sb.append("<td>");
        sb.append(formatTime(total));
        sb.append("</td>\n");
        sb.append("</tr>\n");
        
        printTableEnd(sb);
        return sb.toString();
    }
    
    /** Returns a time in minutes for selected day and project. */
    private int getCellTime(TaskGroup aProj, YearMonthDay aDay) {
        
        for (GroupSet set : data) {
            for (TaskGroup proj : set.getGroups()) {
                final YearMonthDay day = proj.get(TaskGroup.P_DAY);
                if (aDay.equals(day)
                &&  comparator.equals(aProj, proj)
                ){
                    return proj.getTime();
                }
            }
        }
        return 0;
    }
    
    /** Returns a time in minutes for a selected day */
    private int getDayTime(YearMonthDay aDay) {
        int result = 0;
        
        for (GroupSet set : data) {
            for (TaskGroup proj : set.getGroups()) {
                final YearMonthDay day = proj.get(TaskGroup.P_DAY);
                if (aDay.equals(day)) {
                    result += proj.getBusinessTime();
                }
            }
        }
        return result;
    }
    
    public List<YearMonthDay> getDays() {
        List<YearMonthDay> result = new ArrayList<YearMonthDay>();
        for (GroupSet set : data) {
            for (TaskGroup group : set.getGroups()) {
                final YearMonthDay ymd = group.get(TaskGroup.P_DAY);
                if (ymd!=null
                &&! result.contains(ymd)
                ){
                    result.add(ymd);
                }
            }
        }
        return result;
    }
    
    /** Get NoPrivate projects */
    public List<TaskGroup> getProjects() {
        List<TaskGroup> result = new ArrayList<TaskGroup>();
        
        for (GroupSet set : data) {
            for (TaskGroup group : set.getGroups()) {
                
                if (group.getBusinessTime()==0) {
                    continue;
                }
                
                boolean contains = false;
                for (TaskGroup task : result) {
                    if (comparator.equals(task, group)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    result.add(group);
                }
            }
        }

        comparator.sort(result);
        return result;
    }
}
