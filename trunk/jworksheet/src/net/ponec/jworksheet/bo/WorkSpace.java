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
import java.util.Date;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujoframework.UjoProperty;
import org.ujoframework.extensions.ListProperty;
import org.ujoframework.UjoAction;
import org.ujoframework.implementation.map.MapUjo;
import static org.ujoframework.UjoAction.*;

/**
 * This is a <strong>root</strong> of all persistent business objects.
 * @author Pavel Ponec
 * @composed 1 - * WorkDay
 * @composed 1 - * Project
 */
public class WorkSpace extends MapUjo {
    
    /** Version of the persistence file format. */
    public static final UjoProperty<WorkSpace,String> P_VERSN  = newProperty("Version", "");
    /** Date of the last file saving. */
    public static final UjoProperty<WorkSpace,Date> P_CREATED  = newProperty("Created", Date.class);
    /** Date of the last file archivation. */
    public static final UjoProperty<WorkSpace,Date> P_ARCHIVED = newProperty("Archived", Date.class);
    
    /** Work day list */
    public static final ListProperty<WorkSpace,WorkDay> P_DAYS  = newPropertyList("Day", WorkDay.class);
    /** Code-book of all projects. */
    public static final ListProperty<WorkSpace,Project> P_PROJS = newPropertyList("Project", Project.class);
    
    /** Find a workDay or create new. */
    public WorkDay findWorkDay(YearMonthDay dayId) {
        for (WorkDay workDay : P_DAYS.getList(this)) {
            if (WorkDay.P_DATE.equals(workDay, dayId)) {
                return workDay;
            }
        }
        
        WorkDay result = new WorkDay();
        WorkDay.P_DATE.setValue(result, dayId.cloneDay());
        WorkSpace.P_DAYS.addItem(this, result);
        return result;
    }
    
    /** Find a workDay. */
    public Project findProject(Integer id) {
        for (Project project : P_PROJS.getList(this)) {
            if (Project.P_ID.equals(project, id)) {
                return project;
            }
        }
        return null;
    }
    
    /** Returns the first "default" TaskType, null. */
    public Project findDefaultProject() {
        for (Project proj : P_PROJS.getList(this)) {
            if (proj.get(Project.P_DEFAULT)
            && !proj.get(Project.P_FINISHED)
            ){
                return proj;
            }
        }
        return null ;
    }
    
    /** Returns all open Projects. */
    public ArrayList<Project> getOpenProjects() {
        ArrayList<Project> result = new ArrayList<Project>(P_PROJS.getItemCount(this));
        for (Project task : P_PROJS.getList(this)) {
            if (!task.get(Project.P_FINISHED)) {
                result.add(task);
            }
        }
        return result;
    }
    
    /** Assing tasks and projects. Call the method after a data loading. */
    public void assingTasks() {
        for (WorkDay day : P_DAYS.getList(this)) {
            for (Event event : WorkDay.P_EVENTS.getList(day)) {
                Integer projectId = event.get(Event.P_PROJID);
                Integer taskId    = event.get(Event.P_TASKID);
                //
                Project project = this.findProject(projectId);
                TaskType task   = project!=null ? project.findTaskType(taskId) : null ;
                //
                Event.P_PROJ  .setValue(event, project);
                Event.P_PROJID.setValue(event, null);
                Event.P_TASK  .setValue(event, task);
                Event.P_TASKID.setValue(event, null);
            }
        }
    }
    
    /** Create a DemoData */
    public void createDemoData() {
        TaskType task = new TaskType();
        TaskType.P_DESCR.setValue(task, "Task of the 1st Project");
        Project project = new Project();
        Project.P_DESCR.setValue(project, "1st Project");
        Project.P_TASKS.addItem(project, task);
        WorkSpace.P_PROJS.addItem(this, project);
    }
    
    /** Sort Days by a YearMonthDay */
    @SuppressWarnings("unchecked")
    public void sortDays() {
        ((ListProperty)P_DAYS).sort(this, WorkDay.P_DATE);
    }
    
    /** An authorization of ACTION_XML_EXPORT */
    @Override
    public boolean readAuthorization(UjoAction action, UjoProperty property, Object value) {
        
        switch (action.getType()) {
            case ACTION_XML_EXPORT:
                if (P_ARCHIVED==property) {
                    return value!=null;
                    
                } else if (P_DAYS==property
                &&     value instanceof WorkDay
                ){
                    final WorkDay workDay = (WorkDay) value;
                    return WorkDay.P_EVENTS.getItemCount(workDay)>0;
                    
                }
        }
        return super.readAuthorization(action, property, value);
    }
    
   @SuppressWarnings("unchecked")
   public <UJO extends WorkSpace, VALUE> VALUE get(UjoProperty<UJO, VALUE> up) {
        return up.getValue((UJO)this);
    }

   @SuppressWarnings("unchecked")
    public <UJO extends WorkSpace, VALUE> UJO set(UjoProperty<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }
}
