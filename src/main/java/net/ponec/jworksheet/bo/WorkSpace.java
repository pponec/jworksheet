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
import net.ponec.jworksheet.core.ujo.Keys;
import net.ponec.jworksheet.gui.JWorkSheet;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;

/**
 * This is a <strong>root</strong> of all persistent business objects.
 * @author Pavel Ponec
 * @composed 1 - * WorkDay
 * @composed 1 - * Project
 */
@Domain
public class WorkSpace implements Exportable {

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
        for (WorkDay workDay : days) {
            if (dayId.equals(workDay.getDate())) {
                return workDay;
            }
        }

        final WorkDay result = new WorkDay();
        result.setDate(dayId.cloneDay());
        days.add(result);
        return result;
    }

    /** Find a Project by its identifier. An undefined identifier has got no project. */
    public Project findProject(final Integer id) {
        if (id != null) {
            for (Project project : projects) {
                if (project.getId() == id.intValue()) {
                    return project;
                }
            }
        }
        return null;
    }

    /** Returns the first "default" TaskType, null. */
    public Project findDefaultProject() {
        for (Project proj : projects) {
            if (proj.isDefaultProject() && !proj.isFinished()) {
                return proj;
            }
        }
        return null ;
    }

    /** Returns all open Projects. */
    public ArrayList<Project> getOpenProjects() {
        final ArrayList<Project> result = new ArrayList<>(projects.size());
        for (Project project : projects) {
            if (!project.isFinished()) {
                result.add(project);
            }
        }
        return result;
    }

    /** Assing tasks and projects. Call the method after a data loading.
     * <p>The setters of the Event keep the identifiers in sync with the assigned objects,
     * so a broken reference clears the identifier too. */
    public void assingTasks() {
        for (WorkDay day : days) {
            for (Event event : day.getEvents()) {
                final Project project = findProject(event.getProjectId());
                final TaskType task = project != null ? project.findTaskType(event.getTaskId()) : null;
                //
                event.setProject(project);
                event.setTask(task);
            }
        }
    }

    /**Sync projects with other WorkSpace. */
    public void syncProjects(final WorkSpace otherSpace) {
        final ArrayList<Project> newProjects = new ArrayList<>();
        for (Project otherProject : otherSpace.getProjects()) {
            boolean found = false;
            for (Project thisProject : projects) {
                if (otherProject.getId() == thisProject.getId()) {
                    if (otherProject.getDescr().equals(thisProject.getDescr())) {
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
        for (Project thisProject : projects) {
            boolean found = false;
            for (Project otherProject : otherSpace.getProjects()) {
                if (otherProject.getId() == thisProject.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                thisProject.setFinished(true);
            }
        }
        for (Project otherProject : newProjects) {
            final Project project = new Project();
            project.copyFrom(otherProject);
            project.syncTasks(otherProject);
            projects.add(project);
        }
    }

    /** Create a DemoData */
    public void createDemoData() {
        final TaskType task = new TaskType();
        task.setDescr("Task of the 1st Project");
        final Project project = new Project();
        project.setDescr("1st Project");
        project.getTasks().add(task);
        projects.add(project);
    }

    /** Sort Days by a YearMonthDay */
    public void sortDays() {
        days.sort(null);
    }

    /** An undefined archivation date is not exported and an empty day is skipped. */
    @Override
    public boolean isExportable(final ExportAction action, final Key<?,?> key, final Object value) {
        if (action == ExportAction.XML) {
            if (MetaWorkSpace.archived == key) {
                return value != null;
            }
            if (MetaWorkSpace.days == key && value instanceof final WorkDay workDay) {
                return !workDay.getEvents().isEmpty();
            }
        }
        return true;
    }

    /** Show release notification */
    public boolean isReleaseNotification(final Parameters params) {
        boolean result = false;

        final Date currentDate = new Date();
        final Date requiredDate = checkReleaseDate;

        if (currentDate.compareTo(requiredDate) >= 0) {

            final Version currentVersion = JWorkSheet.APPL_VERSION;
            final Version webVersion = params.getWebRelease();
            final Version requiredVersion = checkReleaseVersion;

            if (webVersion.compareTo(currentVersion) > 0
            &&  webVersion.compareTo(requiredVersion) > 0
            ){
                result = true;
            }
        }
        return result;
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final Key<WorkSpace,V> key) {
        return key.getValue(this);
    }

    public <V> WorkSpace set(final Key<WorkSpace,V> key, final V value) {
        Keys.write(key, this, value);
        return this;
    }
}
