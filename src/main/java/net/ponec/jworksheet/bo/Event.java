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

import net.ponec.jworksheet.core.ujo.DomainObject;
import org.ujorm.annotation.UColumn;
import lombok.Getter;
import lombok.Setter;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;
import net.ponec.jworksheet.core.ujo.MetaAliases.EV;

/** An Event of the Work Day.
 * <p>The identifiers {@code projectId} and {@code taskId} are persistent references
 * to the code-book. They are kept in sync by the {@link #setProject(net.ponec.jworksheet.bo.Project)}
 * and the {@link #setTask(net.ponec.jworksheet.bo.TaskType)} setters, so a saved document
 * always contains the identifiers of the currently assigned objects.
 */
@Domain
@Getter
@Setter
public class Event implements Exportable, DomainObject {

    public static final Short ZERO_SHORT = (short) 0;

    /** The start time of the event. */
    @UColumn(name = "Time")
    private Time time = Time.MIDNIGHT;

    @UColumn(name = "Period")
    private short period; // [min]

    @UColumn(name = "Project")
    private Project project;

    @UColumn(name = "ProjectID")
    private Integer projectId;

    @UColumn(name = "Task")
    private TaskType task;

    @UColumn(name = "TaskID")
    private Integer taskId;

    @UColumn(name = "Description")
    private String descr = "";

    // ------------------------ JAVABEAN ------------------------

    /** A negative period is normalized to the zero. */
    public void setPeriod(final short period) {
        this.period = period > 0 ? period : 0;
    }

    /** Assigning a project keeps its identifier in sync. */
    public void setProject(final Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    /** The identifier is read from the assigned project, because the user can change it
     * in the project table, otherwise the stored value of a document is provided. */
    public Integer getProjectId() {
        return project != null ? Integer.valueOf(project.getId()) : projectId;
    }

    /** Assigning a task keeps its identifier in sync. */
    public void setTask(final TaskType task) {
        this.task = task;
        this.taskId = task != null ? task.getId() : null;
    }

    /** The identifier is read from the assigned task, see the {@link #getProjectId()}. */
    public Integer getTaskId() {
        return task != null ? Integer.valueOf(task.getId()) : taskId;
    }

    /** An undefined description is replaced by an empty text. */
    public void setDescr(final String descr) {
        this.descr = descr != null ? descr : "";
    }

    // ------------------------ THE START TIME ------------------------

    /** An undefined start time is replaced by the midnight. */
    public void setTime(final Time time) {
        this.time = time != null ? time : Time.MIDNIGHT;
    }

    /** Init time */
    public Event initTime() {
        setTime(Time.now());
        return this;
    }

    /** Returns a Finished time */
    public Time getTimeFinished() {
        return time.cloneAdd(period);
    }

    /** Assigns the finished time, so the period is calculated from the start time.
     * A finished time before the start time provides the zero period. */
    public void setTimeFinished(final Time finishedTime) {
        setPeriod(finishedTime.substract(time));
    }

    // ------------------------ BUSINESS ------------------------

    /** Is the event from a private project? */
    public boolean isPrivate() {
        return project != null && project.isPrivateProject();
    }

    /** Is the project or the task of the key finished? */
    public boolean isFinished(final Key<Event,?> key) {
        if (EV.project == key) {
            return project != null && project.isFinished();
        }
        if (EV.task == key) {
            return isFinished(EV.project) || task != null && task.isFinished();
        }
        return false;
    }

    /** The related objects are not exported, the document keeps their identifiers only. */
    @Override
    public boolean isExportable(final ExportAction action, final Key<?,?> key, final Object value) {
        if (action == ExportAction.XML) {
            return key != EV.project
                && key != EV.task;
        }
        return true;
    }
}
