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
import net.ponec.jworksheet.bo.meta.MetaProject;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.JListKey;
import org.ujorm.annotation.Domain;
import org.ujorm.tools.Check;

/**
 * Project
 * @author Pavel Ponec
 * @composed 1 - * TaskType
 */
@Domain
public class Project implements Comparable<Project> {

    /** Project ID */
    public static final JKey<Project,Integer> P_ID       = JKey.of(MetaProject.id);
    /** Is the project default? */
    public static final JKey<Project,Boolean> P_DEFAULT  = JKey.of(MetaProject.defaultProject);
    /** Is the project finished? */
    public static final JKey<Project,Boolean> P_FINISHED = JKey.of(MetaProject.finished);
    /** Is the project non business type? */
    public static final JKey<Project,Boolean> P_PRIVATE  = JKey.of(MetaProject.privateProject);
    /** Project descripton */
    public static final JKey<Project,String>  P_DESCR    = JKey.of(MetaProject.descr);
    /** List of task */
    public static final JListKey<Project,TaskType> P_TASKS = JListKey.of(MetaProject.tasks, TaskType.class);

    /** Table columns. */
    public static final JKey[] TABLE_COLUMNS =
    { P_ID
    , P_DEFAULT
    , P_FINISHED
    , P_PRIVATE
    , P_DESCR
    };

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
            for (TaskType task : P_TASKS.getList(this)) {
                if (task.getId() == taskId.intValue()) {
                    return task;
                }
            }
        }
        return null;
    }

    /** Returns the first "default" TaskType, null. */
    public TaskType findDefaultTask() {
        for (TaskType task : P_TASKS.getList(this)) {
            if (task.get(TaskType.P_DEFAULT)
            && !task.get(TaskType.P_FINISHED)
            ){
                return task;
            }
        }
        return null ;
    }

    /** Returns all open tasks. */
    public ArrayList<TaskType> getOpenTasks() {
        final ArrayList<TaskType> result = new ArrayList<>(P_TASKS.getItemCount(this));
        for (TaskType task : P_TASKS.getList(this)) {
            if (!task.get(TaskType.P_FINISHED)) {
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
        for (JKey<Project,?> key : JKey.keysOf(Project.class)) {
            if (!key.isTypeOf(List.class)) {
                key.copy(otherProject, this);
            }
        }
    }

    /** Sync tasks with other Project. */
    public void syncTasks(final Project otherProject) {
        final ArrayList<TaskType> newTasks = new ArrayList<>();
        for (TaskType otherTask : P_TASKS.getList(otherProject)) {
            boolean found = false;
            for (TaskType thisTask : P_TASKS.getList(this)) {
                if (TaskType.P_ID.equals(otherTask, thisTask.get(TaskType.P_ID))) {
                    if (TaskType.P_DESCR.equals(otherTask, thisTask.get(TaskType.P_DESCR))) {
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
        for (TaskType thisTask : P_TASKS.getList(this)) {
            boolean found = false;
            for (TaskType otherTask : P_TASKS.getList(otherProject)) {
                if (TaskType.P_ID.equals(otherTask, thisTask.get(TaskType.P_ID))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                thisTask.set(TaskType.P_FINISHED, true);
            }
        }
        for (TaskType otherTask : newTasks) {
            final TaskType task = new TaskType();
            task.copyFrom(otherTask);
            Project.P_TASKS.addItem(this, task);
        }
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final JKey<Project,V> key) {
        return key.getValue(this);
    }

    public <V> Project set(final JKey<Project,V> key, final V value) {
        key.setValue(this, value);
        return this;
    }
}
