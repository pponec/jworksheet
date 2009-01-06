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

import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujoframework.UjoProperty;
import org.ujoframework.implementation.map.MapUjo;
import net.ponec.jworksheet.bo.Event;
import org.ujoframework.implementation.map.MapProperty;

/**
 * Task Group
 * @author Pavel Ponec
 */
@SuppressWarnings("unchecked")
public class TaskGroup extends MapUjo {
    
    public static final UjoProperty<TaskGroup,Project>     P_PROJ = new MapProperty(Event.P_PROJ);
    public static final UjoProperty<TaskGroup,TaskType>    P_TASK = new MapProperty(Event.P_TASK);
    public static final UjoProperty<TaskGroup,YearMonthDay> P_DAY = new MapProperty(WorkDay.P_DATE);
    public static final UjoProperty<TaskGroup,Integer>    P_MONTH = newProperty("Month", Integer.class);
    public static final UjoProperty<TaskGroup,Integer>    P_YEAR  = newProperty("Year" , Integer.class);
    
    /** A total time in minutes. */
    private int totalTime = 0;
    
    /** Creates a new instance of TaskGroup */
    public TaskGroup(Event event) {
        if (event!=null) {
            init(event);
        }
    }
    
    /** Initializa TaskGroup by Event */
    /*public*/ void init(Event event) {
        writeValue(P_PROJ, event.readValue(Event.P_PROJ));
        writeValue(P_TASK, event.readValue(Event.P_TASK));
    }
    
    /** Add a new time. */
    public void addTime(short time) {
        totalTime += time;
    }
    
    /** Get total time */
    public int getTime() {
        return totalTime;
    }
    
    /** Get business time. */
    public int getBusinessTime() {
        Project proj = P_PROJ.of(this);
        return (proj!=null && Project.P_PRIVATE.of(proj)) ? 0 : totalTime;
    }
    
    
    /** Read a value */
    @Override
    public Object readValue(UjoProperty property) {
        if (P_MONTH==property
        ||  P_YEAR==property
        ){
            YearMonthDay day = P_DAY.of(this);
            int type = P_MONTH==property
            ? YearMonthDay.TYPE_MONTH
            : YearMonthDay.TYPE_YEAR
            ;
            return day!=null ? day.get(type) : null ;
        }
        
        return super.readValue(property);
    }
}
