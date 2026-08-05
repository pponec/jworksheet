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

import jakarta.persistence.Column;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.core.ujo.Keys;
import net.ponec.jworksheet.report.meta.MetaTaskGroup;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;

/**
 * Task Group
 * @author Pavel Ponec
 */
@Domain
public class TaskGroup {

    @Column(name = "Project")
    private Project project;

    @Column(name = "Task")
    private TaskType task;

    @Column(name = "Date")
    private YearMonthDay day;

    /** A declaration of the derived column, see the {@link #getMonth()} method. */
    @Column(name = "Month")
    private Integer month;

    /** A declaration of the derived column, see the {@link #getYear()} method. */
    @Column(name = "Year")
    private Integer year;

    /** A total time in minutes. */
    private transient int totalTime = 0;

    /** A constructor required by the metamodel. */
    public TaskGroup() {
    }

    /** Creates a new instance of TaskGroup */
    public TaskGroup(final Event event) {
        if (event != null) {
            init(event);
        }
    }

    /** Initialize the TaskGroup by an Event */
    void init(final Event event) {
        this.project = event.getProject();
        this.task = event.getTask();
    }

    // ------------------------ JAVABEAN ------------------------

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public TaskType getTask() {
        return task;
    }

    public void setTask(final TaskType task) {
        this.task = task;
    }

    public YearMonthDay getDay() {
        return day;
    }

    public void setDay(final YearMonthDay day) {
        this.day = day;
    }

    /** A month of the day. The value is derived, so it has got no setter behaviour. */
    public Integer getMonth() {
        return day != null ? day.get(YearMonthDay.TYPE_MONTH) : null;
    }

    public void setMonth(final Integer month) {
        throw new UnsupportedOperationException("Read only: " + MetaTaskGroup.month);
    }

    /** A year of the day. The value is derived, so it has got no setter behaviour. */
    public Integer getYear() {
        return day != null ? day.get(YearMonthDay.TYPE_YEAR) : null;
    }

    public void setYear(final Integer year) {
        throw new UnsupportedOperationException("Read only: " + MetaTaskGroup.year);
    }

    // ------------------------ BUSINESS ------------------------

    /** Add a new time. */
    public void addTime(final short time) {
        totalTime += time;
    }

    /** Get total time */
    public int getTime() {
        return totalTime;
    }

    /** Get business time. */
    public int getBusinessTime() {
        return project != null && project.isPrivateProject() ? 0 : totalTime;
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final Key<TaskGroup,V> key) {
        return key.getValue(this);
    }

    public <V> TaskGroup set(final Key<TaskGroup,V> key, final V value) {
        Keys.write(key, this, value);
        return this;
    }
}
