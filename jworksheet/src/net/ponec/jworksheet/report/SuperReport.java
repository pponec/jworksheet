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
import java.util.List;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.core.Calculator;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.core.LanguageManager;
import net.ponec.jworksheet.gui.JWorkSheet;
import net.ponec.jworksheet.resources.ResourceProvider;
import org.ujoframework.UjoProperty;

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

    /** Translate key pro UjoProperty */
    protected String getText(CharSequence key) {
        LanguageManager lm = applContext.getLanguageManager();
        if (key instanceof UjoProperty) {
            return lm.getTextAllways((UjoProperty) key);
        } else {
            return lm.getTextAllways(key.toString());
        }
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
        for(WorkDay workDay : applContext.getWorkSpace().get(WorkSpace.P_DAYS)) {
            YearMonthDay ymd = workDay.get(WorkDay.P_DATE);
            if (ymd.compareTo(dateFrom)>=0
            &&  ymd.compareTo(dateTo  )<=0
            &&  WorkDay.P_EVENTS.getItemCount(workDay)>0
            ){
                if (!workDay.get(WorkDay.P_DAYOFF)) {
                    ++workDayCount;
                }
                for (Event event : workDay.get(WorkDay.P_EVENTS)) {
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
        String text = aText!=null ? aText.toString() : "";
        if (text.length()==0) {
            return "&nbsp;" ;
        }
        StringBuilder sb = new StringBuilder(text.length() + 8);
        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            switch(c) {
                case '<' : sb.append("&lt;")  ; break;
                case '>' : sb.append("&gt;")  ; break;
                case '&' : sb.append("&amp;") ; break;
                case '"' : sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case ' ' : sb.append(' ')     ; break;
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
            sb.append("\n<div style=\"margin-top:5px;\">");
            sb.append(getText("Date"));
            sb.append(": ");
            sb.append(dateFrom.toString());
            sb.append("</div>");
        } else {
            String[] content = new String[]
            {  "\n<table class=\"filter\" cellspacing=\"0\">"
               , "<tr>"
               , "<td>"
               , getText("DateFrom")
               , "</td><td>"
               , ": "
               , "</td><td>"
               , dateFrom.toString()
               , "</td>"
               , "</tr><tr>"
               , "<td>"
               , getText("DateTo")
               , "</td><td>"
               , ": "
               , "</td><td>"
               , dateTo.toString()
               , "</td>"
               , "</tr><tr>"
               , "<td>"
               , getText("WorkDays")
               , "</td><td>"
               , ": "
               , "</td><td>"
               , String.valueOf(getWorkDayCount())
               , "</td>"
               , "</tr>"
               , "</table>"
            };
            String result = ApplTools.stringCat("", content);
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
         , "<link rel=\"stylesheet\" type=\"text/css\" href=\""+applContext.getParameters().get(Parameters.P_REPORT_CSS)+"\" />"
         , "</head>"
         , "<body>"
         , "<h2 style=\"margin-bottom:0px;\">" + escape(title) + "</h2>"
         , "&nbsp;<br />"
         , body
         , "<hr />"
         , "<div class=\"footer\">Powered by <a href=\"" + JWorkSheet.APPL_HOMEPAGE + "\">" + JWorkSheet.APPL_NAME + "</a> version " + JWorkSheet.APPL_VERSION + "<img src=\"styles/"+ResourceProvider.LOGO16+"\"/></div>"
         , "</body>"
         , "</html>"
        };
        String result = ApplTools.stringCat("\n", content);
        return result;
    }
    
    protected void printTableBeg(StringBuilder sb) throws IOException {
        sb.append("<table cellspacing=\"0\" class=\"projects border\">");
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
        final List<WorkDay> days = WorkSpace.P_DAYS.getList(applContext.getWorkSpace());
        for(WorkDay day : days) {
            if (WorkDay.P_DATE.equals(day, aDay)) {
                final boolean result = day.get(WorkDay.P_DAYOFF);
                return result;
            }
        }
        return false;
    }
    
        
}
