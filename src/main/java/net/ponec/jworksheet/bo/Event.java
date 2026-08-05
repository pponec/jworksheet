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


package net.ponec.jworksheet.bo;

import jakarta.persistence.Column;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.bo.item.TimeCodec;
import net.ponec.jworksheet.bo.meta.MetaEvent;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.Keys;
import net.ponec.jworksheet.core.ujo.TextValue;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;

/** An Event of the Work Day.
 * <p>The start time is stored in a memory safe {@code short} field of the minutes, however
 * the documents publish it as a {@code Time} text - see the {@link TimeCodec} class.
 * <p>The identifiers {@code projectId} and {@code taskId} are persistent references
 * to the code-book. They are kept in sync by the {@link #setProject(net.ponec.jworksheet.bo.Project)}
 * and the {@link #setTask(net.ponec.jworksheet.bo.TaskType)} setters, so a saved document
 * always contains the identifiers of the currently assigned objects.
 */
@Domain
public class Event implements Exportable {

    public static final Short ZERO_SHORT = (short) 0;

    @Column(name = "Time")
    @TextValue(TimeCodec.class)
    private short time;

    @Column(name = "Period")
    private short period; // [min]

    @Column(name = "Project")
    private Project project;

    @Column(name = "ProjectID")
    private Integer projectId;

    @Column(name = "Task")
    private TaskType task;

    @Column(name = "TaskID")
    private Integer taskId;

    @Column(name = "Description")
    private String descr = "";

    // ------------------------ JAVABEAN ------------------------

    public short getTime() {
        return time;
    }

    public void setTime(final short time) {
        this.time = time;
    }

    public short getPeriod() {
        return period;
    }

    /** A negative period is normalized to the zero. */
    public void setPeriod(final short period) {
        this.period = period > 0 ? period : 0;
    }

    public Project getProject() {
        return project;
    }

    /** Assigning a project keeps its identifier in sync. */
    public void setProject(final Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

    public TaskType getTask() {
        return task;
    }

    /** Assigning a task keeps its identifier in sync. */
    public void setTask(final TaskType task) {
        this.task = task;
        this.taskId = task != null ? task.getId() : null;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }

    public String getDescr() {
        return descr;
    }

    /** An undefined description is replaced by an empty text. */
    public void setDescr(final String descr) {
        this.descr = descr != null ? descr : "";
    }

    // ------------------------ THE START TIME ------------------------

    /** Returns the start time of the event. */
    public Time getStartTime() {
        return new Time(time);
    }

    /** Assigns the start time of the event. */
    public void setStartTime(final Time startTime) {
        this.time = startTime.getTimeMinutes();
    }

    /** Init time */
    public Event initTime() {
        setStartTime(new Time(true));
        return this;
    }

    /** Returns a Finished time */
    public Time getTimeFinished() {
        return getStartTime().cloneAdd(period);
    }

    /** Set a time period */
    public void setPeriod(final Time finishedTime) {
        setPeriod(finishedTime.substract(getStartTime()));
    }

    // ------------------------ BUSINESS ------------------------

    /** Is the event from a private project? */
    public boolean isPrivate() {
        return project != null && project.isPrivateProject();
    }

    /** Is the project or the task of the key finished? */
    public boolean isFinished(final Key<Event,?> key) {
        if (MetaEvent.project == key) {
            return project != null && project.isFinished();
        }
        if (MetaEvent.task == key) {
            return isFinished(MetaEvent.project) || task != null && task.isFinished();
        }
        return false;
    }

    /** The related objects are not exported, the document keeps their identifiers only. */
    @Override
    public boolean isExportable(final ExportAction action, final Key<?,?> key, final Object value) {
        if (action == ExportAction.XML) {
            return key != MetaEvent.project
                && key != MetaEvent.task;
        }
        return true;
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final Key<Event,V> key) {
        return key.getValue(this);
    }

    public <V> Event set(final Key<Event,V> key, final V value) {
        Keys.write(key, this, value);
        return this;
    }
}
