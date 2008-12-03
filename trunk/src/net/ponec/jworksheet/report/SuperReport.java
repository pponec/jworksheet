/*
 * ReportXX.java
 *
 * Created on 1. zברם 2007, 10:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.ponec.jworksheet.report;

import java.io.IOException;
import java.util.ArrayList;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.core.Calculator;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.gui.JWorkSheet;

/**
 * Standard report
 * @author Pavel Ponec
 */
public abstract class SuperReport implements Calculator {
    
    /** An application context */
    protected ApplContext applContext;
    protected YearMonthDay dateFrom;
    protected YearMonthDay dateTo;
    protected String  reportTitle;
    
    /** Work Day Count */
    protected int workDayCount = 0;
    
    public SuperReport() {
    }
    
    /** General initialization, calculate all report.
     * Call "init()" method on the start.
     * For each Event call "calculate()" method.
     */
    public void init(ApplContext applContext, YearMonthDay dateFrom, YearMonthDay dateTo, String reportTitle) {
        this.applContext = applContext;
        this.dateFrom  = dateFrom;
        this.dateTo    = dateTo  ;
        this.reportTitle = reportTitle;
        init();
        
        // Loop:
        for(WorkDay workDay : WorkSpace.P_DAYS.of(applContext.getWorkSpace())) {
            YearMonthDay ymd = WorkDay.P_DATE.of(workDay);
            if (ymd.compareTo(dateFrom)>=0
            &&  ymd.compareTo(dateTo  )<=0
            &&  WorkDay.P_EVENTS.getItemCount(workDay)>0
            ){
                if (!WorkDay.P_DAYOFF.of(workDay)) {
                    ++workDayCount;
                }
                for (Event event : WorkDay.P_EVENTS.of(workDay)) {
                    calculate(workDay, event);
                }
            }
        }
    }
    
    /** An initialization code, overwrite it by a child. */
    protected void init() {
    }
    
    /** An application context */
    public ApplContext getApplContext() {
        return applContext;
    }
    
    /** Get work day count. */
    public int getWorkDayCount() {
        return workDayCount;
    }
    
    /** Escape a text parameter */
    protected String escape(Object aText) {
        return escape(aText, false);
    }
    /** Escape a text parameter */
    protected String escape(Object aText, boolean fixSpace) {
        if (aText==null) {
            return "&nbsp;" ;
        }
        String text = String.valueOf(aText);
        StringBuilder sb = new StringBuilder(text.length() + 8);
        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            switch(c) {
                case '<' : sb.append("&lt;")  ; break;
                case '>' : sb.append("&gt;")  ; break;
                case '&' : sb.append("&amp;") ; break;
                case '"' : sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case ' ' : sb.append(fixSpace ? "&nbsp;" : " "); break;
                default  : {
                    if (c<32) {
                        sb.append("&#");
                        sb.append(Integer.toString(c));
                        sb.append(';');
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
    
    // -------------- General Methods -----------------
    
    /** Create HTML report */
    public void printFilter(StringBuilder sb) {
        
        if (dateFrom.equals(dateTo)) {
            sb.append("\n<div style=\"margin-top:5px;\">Date: " + dateFrom.toString() + "</div>");
        } else {
            String[] content = new String[]
            {  "\n<table class=\"filter\" cellspacing=\"0\" border=\"0\">"
               , "<tr>"
               , "<td>"
               , "Date from"
               , "</td><td>"
               , ": "
               , "</td><td>"
               , dateFrom.toString()
               , "</td>"
               , "</tr><tr>"
               , "<td>"
               , "Date to"
               , "</td><td>"
               , ": "
               , "</td><td>"
               , dateTo.toString()
               , "</td>"
               , "</tr><tr>"
               , "<td>"
               , "Work Days"
               , "</td><td>"
               , ": "
               , "</td><td>"
               , String.valueOf(getWorkDayCount())
               , "</td>"
               , "</tr>"
               , "</table>"
            };
            String result = ApplTools.stringCat("\n", content);
            sb.append(result);
        }
    }
    
    /** Create HTML report */
    public String getReport(String body, String title ) {
        if (title==null) {
            title = JWorkSheet.APPL_NAME + " Report";
        } else {
            title = escape(title);
        }
        
        String[] content = new String[]
        {"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
         , "<html lang=\"en\"><head>"
         , "<base href=\"" + applContext.getConfigDir().toURI() + "\">"
         , "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
         , "<title>" + escape(title) + "</title>"
         , "<meta name=\"Generator\" content=\""+JWorkSheet.APPL_NAME+"\" />"
         , "<link rel=\"stylesheet\" type=\"text/css\" href=\""+Parameters.P_REPORT_CSS.getDefault()                    +"\" />"
         //, "<!-- A style from an application setup -- >"
         , "<link rel=\"stylesheet\" type=\"text/css\" href=\""+Parameters.P_REPORT_CSS.of(applContext.getParameters())+"\" />"
         , "</head>"
         , "<body>"
         , "<h2 style=\"margin-bottom:0px;\">" + escape(title) + "</h2>"
         , "&nbsp;<br />"
         , body
         , "<hr />"
         , "<div class=\"footer\">Powered by <a href=\"" + JWorkSheet.APPL_HOMEPAGE + "\">" + JWorkSheet.APPL_NAME + "</a> version " + JWorkSheet.APPL_VERSION + "</div>"
         , "</body>"
         , "</html>"
        };
        String result = ApplTools.stringCat("\n", content);
        return result;
    }
    
    protected void printTableBeg(StringBuilder sb) throws IOException {
        sb.append("<table border=\"1\" cellspacing=\"1\" class=\"projects\">");
    }
    
    protected void printTableEnd(StringBuilder sb) throws IOException {
        sb.append("</table>");
    }
    
    /** Format the time in minutes. */
    protected String formatTime(int minutes) {
        final String result = applContext.getParameters().formatTime(minutes);
        return result;
    }
    
    /** Format the time in minutes. */
    protected String formatTimeZero(int minutes) {
        final String result
        = minutes!=0
        ? formatTime(minutes)
        : "&nbsp;"
        ;
        return result;
    }
    
    /** Is the day a DayOff? */
    protected boolean isDayOff(YearMonthDay aDay) {
        final ArrayList<WorkDay> days = WorkSpace.P_DAYS.getList(applContext.getWorkSpace());
        for(WorkDay day : days) {
            if (WorkDay.P_DATE.equals(day, aDay)) {
                final boolean result = WorkDay.P_DAYOFF.of(day);
                return result;
            }
        }
        return false;
    }
    
        
}
