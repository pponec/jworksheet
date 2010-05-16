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


package net.ponec.jworksheet.bo;

import java.util.ArrayList;
import net.ponec.jworksheet.core.ApplTools;
import org.ujoframework.UjoProperty;
import org.ujoframework.extensions.ListProperty;
import org.ujoframework.implementation.map.MapUjo;

/**
 * Project
 * @author Pavel Ponec
 * @composed 1 - * TaskType
 */
public class Project extends MapUjo implements Comparable {
    
    /** Project ID */
    public static final UjoProperty<Project,Integer> P_ID       = newProperty("ID", 0);
    /** Is the project default? */
    public static final UjoProperty<Project,Boolean> P_DEFAULT  = newProperty("Default", false);
    /** Is the project finished? */
    public static final UjoProperty<Project,Boolean> P_FINISHED = newProperty("Finished", false);
    /** Is the project non business type? */
    public static final UjoProperty<Project,Boolean> P_PRIVATE  = newProperty("Private", false);
    /** Project descripton */
    public static final UjoProperty<Project,String>  P_DESCR    = newProperty("Description", "");
    /** List of task */
    public static final ListProperty<Project,TaskType> P_TASKS = newPropertyList("Task", TaskType.class);
    
    /** Table columns. */
    public static final UjoProperty[] TABLE_COLUMNS
    ={P_ID
    , P_DEFAULT
    , P_FINISHED
    , P_PRIVATE
    , P_DESCR
    };

    /** Show description */
    @Override
    public String toString() {
        String result = P_DESCR.of(this);
        return ApplTools.isValid(result) ? result : String.valueOf(P_ID.of(this)) ;
    }

    /** Find a TaskType by its id */
    public TaskType findTaskType(Integer taskId) {
        for (TaskType task : P_TASKS.getList(this)) {
            if (TaskType.P_ID.equals(task, taskId)) {
                return task;
            }
        }
        return null;
    }
    
    /** Returns the first "default" TaskType, null. */
    public TaskType findDefaultTask() {
        for (TaskType task : P_TASKS.getList(this)) {
            if (TaskType.P_DEFAULT.of(task)
            && !TaskType.P_FINISHED.of(task)
            ){
                return task;
            }
        }
        return null ;
    }
    
    /** Returns all open tasks. */
    public ArrayList<TaskType> getOpenTasks() {
        ArrayList<TaskType> result = new ArrayList<TaskType>(P_TASKS.getItemCount(this));
        for (TaskType task : P_TASKS.getList(this)) {
            if (!TaskType.P_FINISHED.of(task)) {
                result.add(task);
            }
        }
        return result;
    }

    /** Compare to another Project by ID. */
    @Override
    public int compareTo(Object o) {
          int id1 = P_ID.of(this);
          int id2 = o!=null ? P_ID.of((Project)o) : Integer.MAX_VALUE;
          final int result = id1<id2 ? -1 : id1>id2 ? +1 : 0 ;
          return result;
    }

    @SuppressWarnings("unchecked")
    public <UJO extends Project, VALUE> VALUE get(UjoProperty<UJO, VALUE> up) {
        return up.getValue((UJO)this);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends Project, VALUE> UJO set(UjoProperty<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }


    
}
