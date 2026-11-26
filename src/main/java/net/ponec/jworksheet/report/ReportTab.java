/**
 * Copyright (C) 2007-2026, Pavel Ponec, contact: http://ponec.net/
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import java.time.LocalDate;
import net.ponec.jworksheet.core.ujo.UjoComparator;
import net.ponec.jworksheet.core.ujo.MetaAliases._EV;
import net.ponec.jworksheet.core.ujo.MetaAliases._PA;
import net.ponec.jworksheet.core.ujo.MetaAliases._TG;

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
    public void init(ApplContext applContext, LocalDate dateFrom, LocalDate dateTo, String reportTitle) {
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
        return UjoComparator.<TaskGroup>newInstance(_TG.project, _TG.task);
    }
    
    private void saveDay() {
        LocalDate ymd = lastWorkDay.getDate();
        
        report = new ReportA();
        GroupSet groupSet = new GroupSet(createUjoComparator(), _EV.period);
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
            tg.setDay(ymd);
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
        List<LocalDate> days = getDays();
        final Map<LocalDate, List<TaskGroup>> groupsByDay = getGroupsByDay();
        
        final Function<LocalDate, String> exportFormat = applContext.dateFormatter
                (applContext.getParameters().getDateReportFormat2());
        final Function<LocalDate, String> tipFormat = applContext.dateFormatter
                (applContext.getParameters().getDateFormat(_PA.dateReportFormat, applContext));
        
        // Header ---
        sb.append("<tr>\n<th>");
        sb.append(getText(".TabbedPane.Projects"));
        sb.append("</th>\n");
        for(LocalDate day : days) {
            boolean dayOff = isDayOff(day);
            sb.append("<th title=\"" + escape((dayOff?(getText("DayOff")+": "):"") + tipFormat.apply(day)) + "\"");
            if (dayOff) { sb.append(" class=\"dayOff\""); }
            sb.append(">");
            sb.append(exportFormat.apply(day));
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
            sb.append(escape(proj.getProject()));
            sb.append(" / ");
            sb.append(escape(proj.getTask()));
            sb.append("</td>\n");
            
            int total = 0;
            for(LocalDate day : days) {
                int time = getCellTime(groupsByDay, proj, day);
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
        for(LocalDate day : days) {
            int time = getDayTime(groupsByDay, day);
            totals.add(time);
            total += time;
            
            sb.append("<td title=\"" + escape(tipFormat.apply(day)) + "\">");
            sb.append(formatTime(time));
            sb.append("</td>\n");
        }
        sb.append("<td>");
        sb.append(formatTime(total));
        sb.append("</td>\n");
        sb.append("</tr>\n");
        
        // Overtime ---
        total = 0;
        int workingMinutes = Math.round(applContext.getParameters().getWorkingHours()*60);
        sb.append("<tr class=\"total\">\n<td class=\"alignLeft\">");
        sb.append(getText("Overtime"));
        sb.append("</td>\n");
        for(LocalDate day : days) {
            int time = totals.removeFirst();
            if (!isDayOff(day)) {
                time -= workingMinutes;
            }
            total += time;
            
            sb.append("<td title=\"" + escape(tipFormat.apply(day)) + "\">");
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
    
    /** Returns the groups of the data indexed by their day, so a cell value is found quickly. */
    private Map<LocalDate, List<TaskGroup>> getGroupsByDay() {
        return data.stream()
                .flatMap(set -> Arrays.stream(set.getGroups()))
                .filter(group -> group.getDay() != null)
                .collect(Collectors.groupingBy(TaskGroup::getDay));
    }

    /** Returns a time in minutes for selected day and project. */
    private int getCellTime(Map<LocalDate, List<TaskGroup>> groupsByDay, TaskGroup aProj, LocalDate aDay) {
        return groupsByDay.getOrDefault(aDay, List.of()).stream()
                .filter(group -> comparator.equals(aProj, group))
                .findFirst()
                .map(TaskGroup::getTime)
                .orElse(0);
    }

    /** Returns a time in minutes for a selected day */
    private int getDayTime(Map<LocalDate, List<TaskGroup>> groupsByDay, LocalDate aDay) {
        return groupsByDay.getOrDefault(aDay, List.of()).stream()
                .mapToInt(TaskGroup::getBusinessTime)
                .sum();
    }
    
    /** Returns the distinct days of the groups in the order of their occurrence. */
    public List<LocalDate> getDays() {
        return data.stream()
                .flatMap(set -> Arrays.stream(set.getGroups()))
                .map(TaskGroup::getDay)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
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
