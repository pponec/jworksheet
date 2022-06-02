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

import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujorm.implementation.map.MapUjo;
import net.ponec.jworksheet.bo.Event;
import org.ujorm.Key;
import org.ujorm.core.KeyFactory;

/**
 * Task Group
 * @author Pavel Ponec
 */
@SuppressWarnings("unchecked")
public class TaskGroup extends MapUjo {

private static final KeyFactory<TaskGroup> f = KeyFactory.CamelBuilder.get(TaskGroup.class);

    public static final Key<TaskGroup,Project>     P_PROJ = f.newKey(Event.P_PROJ.getName()); // , -3
    public static final Key<TaskGroup,TaskType>    P_TASK = f.newKey(Event.P_TASK.getName()); // , -2
    public static final Key<TaskGroup,YearMonthDay> P_DAY = f.newKey(WorkDay.P_DATE.getName()); // , -1
    public static final Key<TaskGroup,Integer>    P_MONTH = f.newKey("Month");
    public static final Key<TaskGroup,Integer>     P_YEAR = f.newKey("Year");

    static { f.lock(); }

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
        Project proj = get(P_PROJ);
        return (proj!=null && proj.get(Project.P_PRIVATE)) ? 0 : totalTime;
    }


    /** Read a value */
    @Override
    public Object readValue(Key property) {
        if (P_MONTH==property
        ||  P_YEAR==property
        ){
            YearMonthDay day = get(P_DAY);
            int type = P_MONTH==property
            ? YearMonthDay.TYPE_MONTH
            : YearMonthDay.TYPE_YEAR
            ;
            return day!=null ? day.get(type) : null ;
        }

        return super.readValue(property);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends TaskGroup, VALUE> VALUE get(Key<UJO, VALUE> up) {
        return up.of((UJO)this);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends TaskGroup, VALUE> UJO set(Key<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }
}
