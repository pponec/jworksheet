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
import java.util.Date;
import java.util.List;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.bo.meta.MetaWorkSpace;
import net.ponec.jworksheet.core.Version;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.JListKey;
import net.ponec.jworksheet.gui.JWorkSheet;
import org.ujorm.annotation.Domain;

/**
 * This is a <strong>root</strong> of all persistent business objects.
 * @author Pavel Ponec
 * @composed 1 - * WorkDay
 * @composed 1 - * Project
 */
@Domain
public class WorkSpace implements Exportable {

    /** Version of the persistence file format. */
    public static final JKey<WorkSpace,Version> P_VERSN     = JKey.of(MetaWorkSpace.version);
    /** Date of the last file saving. */
    public static final JKey<WorkSpace,Date> P_CREATED      = JKey.of(MetaWorkSpace.created);
    /** Date of the last file archivation. */
    public static final JKey<WorkSpace,Date> P_ARCHIVED     = JKey.of(MetaWorkSpace.archived);
    /** Name of user of this workspace. */
    public static final JKey<WorkSpace,String> P_USERNAME   = JKey.of(MetaWorkSpace.username);
    /** Minimal date to check new release */
    public static final JKey<WorkSpace,Date> P_RELEASE_CHECK_DATE = JKey.of(MetaWorkSpace.checkReleaseDate);
    /** Minimal version to new release notification */
    public static final JKey<WorkSpace,Version> P_RELEASE_CHECK_VERSION = JKey.of(MetaWorkSpace.checkReleaseVersion);

    /** Work day list */
    public static final JListKey<WorkSpace,WorkDay> P_DAYS  = JListKey.of(MetaWorkSpace.days, WorkDay.class);
    /** Code-book of all projects. */
    public static final JListKey<WorkSpace,Project> P_PROJS = JListKey.of(MetaWorkSpace.projects, Project.class);

    @Column(name = "Version")
    private Version version = new Version("0");

    @Column(name = "Created")
    private Date created;

    @Column(name = "Archived")
    private Date archived;

    @Column(name = "Username")
    private String username = "?";

    @Column(name = "CheckReleaseDate")
    private Date checkReleaseDate = new Date(0L);

    @Column(name = "CheckReleaseVersion")
    private Version checkReleaseVersion = JWorkSheet.APPL_VERSION;

    @Column(name = "Day")
    private List<WorkDay> days = new ArrayList<>();

    @Column(name = "Project")
    private List<Project> projects = new ArrayList<>();

    // ------------------------ JAVABEAN ------------------------

    public Version getVersion() {
        return version;
    }

    public void setVersion(final Version version) {
        this.version = version;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public Date getArchived() {
        return archived;
    }

    public void setArchived(final Date archived) {
        this.archived = archived;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Date getCheckReleaseDate() {
        return checkReleaseDate;
    }

    public void setCheckReleaseDate(final Date checkReleaseDate) {
        this.checkReleaseDate = checkReleaseDate;
    }

    public Version getCheckReleaseVersion() {
        return checkReleaseVersion;
    }

    public void setCheckReleaseVersion(final Version checkReleaseVersion) {
        this.checkReleaseVersion = checkReleaseVersion;
    }

    public List<WorkDay> getDays() {
        return days;
    }

    public void setDays(final List<WorkDay> days) {
        this.days = days != null ? days : new ArrayList<>();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(final List<Project> projects) {
        this.projects = projects != null ? projects : new ArrayList<>();
    }

    // ------------------------ BUSINESS ------------------------

    /** Find a workDay or create new. */
    public WorkDay findWorkDay(final YearMonthDay dayId) {
        for (WorkDay workDay : P_DAYS.getList(this)) {
            if (WorkDay.P_DATE.equals(workDay, dayId)) {
                return workDay;
            }
        }

        final WorkDay result = new WorkDay();
        WorkDay.P_DATE.setValue(result, dayId.cloneDay());
        WorkSpace.P_DAYS.addItem(this, result);
        return result;
    }

    /** Find a Project by its identifier. An undefined identifier has got no project. */
    public Project findProject(final Integer id) {
        if (id != null) {
            for (Project project : P_PROJS.getList(this)) {
                if (project.getId() == id.intValue()) {
                    return project;
                }
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
        final ArrayList<Project> result = new ArrayList<>(P_PROJS.getItemCount(this));
        for (Project task : P_PROJS.getList(this)) {
            if (!task.get(Project.P_FINISHED)) {
                result.add(task);
            }
        }
        return result;
    }

    /** Assing tasks and projects. Call the method after a data loading.
     * <p>The setters of the Event keep the identifiers in sync with the assigned objects,
     * so a broken reference clears the identifier too. */
    public void assingTasks() {
        for (WorkDay day : P_DAYS.getList(this)) {
            for (Event event : WorkDay.P_EVENTS.getList(day)) {
                final Integer projectId = event.get(Event.P_PROJID);
                final Integer taskId    = event.get(Event.P_TASKID);
                //
                final Project project = this.findProject(projectId);
                final TaskType task   = project != null ? project.findTaskType(taskId) : null ;
                //
                Event.P_PROJ.setValue(event, project);
                Event.P_TASK.setValue(event, task);
            }
        }
    }

    /**Sync projects with other WorkSpace. */
    public void syncProjects(final WorkSpace otherSpace) {
        final ArrayList<Project> newProjects = new ArrayList<>();
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
            final Project project = new Project();
            project.copyFrom(otherProject);
            project.syncTasks(otherProject);
            WorkSpace.P_PROJS.addItem(this, project);
        }
    }

    /** Create a DemoData */
    public void createDemoData() {
        final TaskType task = new TaskType();
        TaskType.P_DESCR.setValue(task, "Task of the 1st Project");
        final Project project = new Project();
        Project.P_DESCR.setValue(project, "1st Project");
        Project.P_TASKS.addItem(project, task);
        WorkSpace.P_PROJS.addItem(this, project);
    }

    /** Sort Days by a YearMonthDay */
    public void sortDays() {
        P_DAYS.sort(this, WorkDay.P_DATE);
    }

    /** An undefined archivation date is not exported and an empty day is skipped. */
    @Override
    public boolean isExportable(final ExportAction action, final JKey<?,?> key, final Object value) {
        if (action == ExportAction.XML) {
            if (P_ARCHIVED == key) {
                return value != null;
            }
            if (P_DAYS == key && value instanceof final WorkDay workDay) {
                return WorkDay.P_EVENTS.getItemCount(workDay) > 0;
            }
        }
        return true;
    }

    /** Show release notification */
    public boolean isReleaseNotification(final Parameters params) {
        boolean result = false;

        final Date currentDate = new Date();
        final Date requiredDate = get(P_RELEASE_CHECK_DATE);

        if (currentDate.compareTo(requiredDate) >= 0) {

            final Version currentVersion = JWorkSheet.APPL_VERSION;
            final Version webVersion = params.getWebRelease();
            final Version requiredVersion = get(P_RELEASE_CHECK_VERSION);

            if (webVersion.compareTo(currentVersion) > 0
            &&  webVersion.compareTo(requiredVersion) > 0
            ){
                result = true;
            }
        }
        return result;
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final JKey<WorkSpace,V> key) {
        return key.getValue(this);
    }

    public <V> WorkSpace set(final JKey<WorkSpace,V> key, final V value) {
        key.setValue(this, value);
        return this;
    }
}
