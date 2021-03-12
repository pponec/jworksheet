/**
  * Copyright (C) 2007-2021, Pavel Ponec, contact: http://ponec.net/
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
import net.ponec.jworksheet.core.Version;
import net.ponec.jworksheet.gui.JWorkSheet;
import org.ujorm.Key;
import org.ujorm.extensions.ListProperty;
import org.ujorm.UjoAction;
import org.ujorm.implementation.map.MapUjo;
import static org.ujorm.UjoAction.*;
import org.ujorm.extensions.UjoTextable;

/**
 * This is a <strong>root</strong> of all persistent business objects.
 * @author Pavel Ponec
 * @composed 1 - * WorkDay
 * @composed 1 - * Project
 */
public class WorkSpace extends MapUjo implements UjoTextable {

    /** Version of the persistence file format. */
    public static final Key<WorkSpace,Version> P_VERSN  = newKey("Version", new Version("0"));
    /** Date of the last file saving. */
    public static final Key<WorkSpace,Date> P_CREATED  = newKey("Created");
    /** Date of the last file archivation. */
    public static final Key<WorkSpace,Date> P_ARCHIVED = newKey("Archived");
    /** Name of user of this workspace. */
    public static final Key<WorkSpace,String> P_USERNAME = newKey("Username", "?");
    /** Minimal date to check new release */
    public static final Key<WorkSpace,Date> P_RELEASE_CHECK_DATE = newKey("CheckReleaseDate", new Date(0L));
    /** Minimal version to new release notification */
    public static final Key<WorkSpace,Version> P_RELEASE_CHECK_VERSION = newKey("CheckReleaseVersion", JWorkSheet.APPL_VERSION);

    /** Work day list */
    public static final ListProperty<WorkSpace,WorkDay> P_DAYS  = newListKey("Day");
    /** Code-book of all projects. */
    public static final ListProperty<WorkSpace,Project> P_PROJS = newListKey("Project");

    static {
        init(WorkSpace.class, true);
    }


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

    /**Sync projects with other WorkSpace. */
    public void syncProjects(WorkSpace otherSpace) {
        ArrayList<Project> newProjects = new ArrayList<Project>();
        for (Project otherProject : P_PROJS.getList(otherSpace)) {
            boolean found = false;
            for (Project thisProject : P_PROJS.getList(this)) {
                if (Project.P_ID.equals(otherProject, thisProject.get(Project.P_ID))) {
                    if (Project.P_DESCR.equals(otherProject, thisProject.get(Project.P_DESCR))) {
                        // only update projects with same description, otherwise
                        // old non-synced projects may change description
                        thisProject.copyFrom(otherProject);
                        thisProject.syncTasks(otherProject);
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                newProjects.add(otherProject);
            }
        }
        for (Project thisProject : P_PROJS.getList(this)) {
            boolean found = false;
            for (Project otherProject : P_PROJS.getList(otherSpace)) {
                if (Project.P_ID.equals(otherProject, thisProject.get(Project.P_ID))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                thisProject.set(Project.P_FINISHED, true);
            }
        }
        for (Project otherProject : newProjects) {
            Project project = new Project();
            project.copyFrom(otherProject);
            project.syncTasks(otherProject);
            WorkSpace.P_PROJS.addItem(this, project);
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
    public boolean readAuthorization(UjoAction action, Key property, Object value) {

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
   public <UJO extends WorkSpace, VALUE> VALUE get(Key<UJO, VALUE> up) {
        return up.of((UJO)this);
    }

   @SuppressWarnings("unchecked")
   public <UJO extends WorkSpace, VALUE> UJO set(Key<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
   }

   /** Show release notification */
   public boolean isReleaseNotification(Parameters params) {
        boolean result = false;

        Date currentDate = new Date();
        Date requiredDate = get(P_RELEASE_CHECK_DATE);

        if (currentDate.compareTo(requiredDate)>=0) {

            Version currentVersion = JWorkSheet.APPL_VERSION;
            Version webVersion = params.getWebRelease();
            Version requiredVersion = get(P_RELEASE_CHECK_VERSION);

            if (webVersion.compareTo(currentVersion)>0
            &&  webVersion.compareTo(requiredVersion)>0
            ){
                result = true;
            }
        }
        return result;
    }
}
