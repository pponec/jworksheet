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
import java.util.ArrayList;
import java.util.List;
import net.ponec.jworksheet.core.ujo.Keys;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;
import org.ujorm.tools.Check;

/**
 * Project
 * @author Pavel Ponec
 * @composed 1 - * TaskType
 */
@Domain
public class Project implements Comparable<Project> {

    @Column(name = "ID")
    private int id = 0;

    @Column(name = "Default")
    private boolean defaultProject = false;

    @Column(name = "Finished")
    private boolean finished = false;

    @Column(name = "Private")
    private boolean privateProject = false;

    @Column(name = "Description")
    private String descr = "";

    @Column(name = "Task")
    private List<TaskType> tasks = new ArrayList<>();

    // ------------------------ JAVABEAN ------------------------

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public boolean isDefaultProject() {
        return defaultProject;
    }

    public void setDefaultProject(final boolean defaultProject) {
        this.defaultProject = defaultProject;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public boolean isPrivateProject() {
        return privateProject;
    }

    public void setPrivateProject(final boolean privateProject) {
        this.privateProject = privateProject;
    }

    public String getDescr() {
        return descr;
    }

    /** An undefined description is replaced by an empty text. */
    public void setDescr(final String descr) {
        this.descr = descr != null ? descr : "";
    }

    public List<TaskType> getTasks() {
        return tasks;
    }

    public void setTasks(final List<TaskType> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    // ------------------------ BUSINESS ------------------------

    /** Show description */
    @Override
    public String toString() {
        final String result = getDescr();
        return Check.hasLength(result) ? result : String.valueOf(getId());
    }

    /** Find a TaskType by its id. An undefined identifier has got no task. */
    public TaskType findTaskType(final Integer taskId) {
        if (taskId != null) {
            for (TaskType task : tasks) {
                if (task.getId() == taskId.intValue()) {
                    return task;
                }
            }
        }
        return null;
    }

    /** Returns the first "default" TaskType, null. */
    public TaskType findDefaultTask() {
        for (TaskType task : tasks) {
            if (task.isDefaultTask() && !task.isFinished()) {
                return task;
            }
        }
        return null ;
    }

    /** Returns all open tasks. */
    public ArrayList<TaskType> getOpenTasks() {
        final ArrayList<TaskType> result = new ArrayList<>(tasks.size());
        for (TaskType task : tasks) {
            if (!task.isFinished()) {
                result.add(task);
            }
        }
        return result;
    }

    /** Compare to another Project by ID. */
    @Override
    public int compareTo(final Project o) {
        final int id1 = getId();
        final int id2 = o != null ? o.getId() : Integer.MAX_VALUE;
        return Integer.compare(id1, id2);
    }

    /** Copy all the scalar attributes from another project. The list of the tasks is skipped. */
    public void copyFrom(final Project otherProject) {
        for (Key<Project,?> key : Keys.keysOf(Project.class)) {
            if (!Keys.isList(key)) {
                Keys.copy(key, otherProject, this);
            }
        }
    }

    /** Sync tasks with other Project. */
    public void syncTasks(final Project otherProject) {
        final ArrayList<TaskType> newTasks = new ArrayList<>();
        for (TaskType otherTask : otherProject.getTasks()) {
            boolean found = false;
            for (TaskType thisTask : tasks) {
                if (otherTask.getId() == thisTask.getId()) {
                    if (otherTask.getDescr().equals(thisTask.getDescr())) {
                        // only update tasks with same description, otherwise
                        // old non-synced tasks may change description
                        thisTask.copyFrom(otherTask);
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                newTasks.add(otherTask);
            }
        }
        for (TaskType thisTask : tasks) {
            boolean found = false;
            for (TaskType otherTask : otherProject.getTasks()) {
                if (otherTask.getId() == thisTask.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                thisTask.setFinished(true);
            }
        }
        for (TaskType otherTask : newTasks) {
            final TaskType task = new TaskType();
            task.copyFrom(otherTask);
            tasks.add(task);
        }
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final Key<Project,V> key) {
        return key.getValue(this);
    }

    public <V> Project set(final Key<Project,V> key, final V value) {
        Keys.write(key, this, value);
        return this;
    }
}
