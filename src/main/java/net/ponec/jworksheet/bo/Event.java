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
import net.ponec.jworksheet.bo.meta.MetaEvent;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.JKey;
import org.ujorm.annotation.Domain;

/** An Event of the Work Day.
 * <p>The start time is stored in a memory safe {@code short} field, however the key
 * publishes it as a {@link Time} object - see the converter of the {@link #P_TIME} key.
 * <p>The identifiers {@link #P_PROJID} and {@link #P_TASKID} are persistent references
 * to the code-book. They are kept in sync by the {@link #setProject(net.ponec.jworksheet.bo.Project)}
 * and the {@link #setTask(net.ponec.jworksheet.bo.TaskType)} setters, so a saved document
 * always contains the identifiers of the currently assigned objects.
 */
@Domain
public class Event implements Exportable {

    public static final Short ZERO_SHORT = (short) 0;

    /** Start time of event */
    public static final JKey<Event,Time>     P_TIME
            = JKey.of(MetaEvent.time, Time.class, Time::getTimeMinutes, minutes -> new Time(minutes.intValue()));
    /** Period of the event in minutes */
    public static final JKey<Event,Short>    P_PERIOD = JKey.of(MetaEvent.period); // [min]
    /** Project (object) */
    public static final JKey<Event,Project>  P_PROJ   = JKey.of(MetaEvent.project);
    /** Project ID for the persistence */
    public static final JKey<Event,Integer>  P_PROJID = JKey.of(MetaEvent.projectId);
    /** Task (object) */
    public static final JKey<Event,TaskType> P_TASK   = JKey.of(MetaEvent.task);
    /** Task ID for the persistence */
    public static final JKey<Event,Integer>  P_TASKID = JKey.of(MetaEvent.taskId);
    /** Description of the event */
    public static final JKey<Event,String>   P_DESCR  = JKey.of(MetaEvent.descr);

    @Column(name = "Time")
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

    // ------------------------ BUSINESS ------------------------

    /** Init time */
    public Event initTime() {
        P_TIME.setValue(this, new Time(true));
        return this;
    }

    /** Set a time period */
    public void setPeriod(final Time time) {
        final short newPeriod = time.substract(get(P_TIME));
        P_PERIOD.setValue(this, newPeriod);
    }

    /** Is the event from a private project? */
    public boolean isPrivate() {
        final Project proj = get(P_PROJ);
        return proj != null && proj.get(Project.P_PRIVATE);
    }

    /** Is the project or the task of the key finished? */
    public boolean isFinished(final JKey<Event,?> key) {
        if (P_PROJ == key) {
            final Project proj = get(P_PROJ);
            return proj != null && proj.get(Project.P_FINISHED);
        }
        if (P_TASK == key) {
            final TaskType tsk = get(P_TASK);
            return isFinished(P_PROJ) || tsk != null && tsk.get(TaskType.P_FINISHED);
        }
        return false;
    }

    /** Returns a Finished time */
    public Time getTimeFinished() {
        return get(P_TIME).cloneAdd(get(P_PERIOD));
    }

    /** The related objects are not exported, the document keeps their identifiers only. */
    @Override
    public boolean isExportable(final ExportAction action, final JKey<?,?> key, final Object value) {
        if (action == ExportAction.XML) {
            return key != P_PROJ
                && key != P_TASK;
        }
        return true;
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final JKey<Event,V> key) {
        return key.getValue(this);
    }

    public <V> Event set(final JKey<Event,V> key, final V value) {
        key.setValue(this, value);
        return this;
    }
}
