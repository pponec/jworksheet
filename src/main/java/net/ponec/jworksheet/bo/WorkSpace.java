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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.bo.meta.MetaWorkSpace;
import net.ponec.jworksheet.core.Version;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
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
@Getter
@Setter
public class WorkSpace implements Exportable, DomainObject {

    @UColumn(name = "Version")
    private Version version = new Version("0");

    @UColumn(name = "Created")
    private Date created;

    @UColumn(name = "Archived")
    private Date archived;

    @UColumn(name = "Username")
    private String username = "?";

    @UColumn(name = "CheckReleaseDate")
    private Date checkReleaseDate = new Date(0L);

    @UColumn(name = "CheckReleaseVersion")
    private Version checkReleaseVersion = JWorkSheet.APPL_VERSION;

    @UColumn(name = "Day")
    private List<WorkDay> days = new ArrayList<>();

    @UColumn(name = "Project")
    private List<Project> projects = new ArrayList<>();

    // ------------------------ JAVABEAN ------------------------

    public void setDays(final List<WorkDay> days) {
        this.days = days != null ? days : new ArrayList<>();
    }

    public void setProjects(final List<Project> projects) {
        this.projects = projects != null ? projects : new ArrayList<>();
    }

    // ------------------------ BUSINESS ------------------------

    /** Find a workDay or create new. */
    public WorkDay findWorkDay(final YearMonthDay dayId) {
        return days.stream()
                .filter(workDay -> dayId.equals(workDay.getDate()))
                .findFirst()
                .orElseGet(() -> {
                    final WorkDay result = new WorkDay();
                    result.setDate(dayId.cloneDay());
                    days.add(result);
                    return result;
                });
    }

    /** Find a Project by its identifier. An undefined identifier has got no project. */
    public Project findProject(final Integer id) {
        return id == null ? null : projects.stream()
                .filter(project -> project.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /** Returns the first "default" TaskType, null. */
    public Project findDefaultProject() {
        return projects.stream()
                .filter(proj -> proj.isDefaultProject() && !proj.isFinished())
                .findFirst()
                .orElse(null);
    }

    /** Returns all open Projects. */
    public ArrayList<Project> getOpenProjects() {
        return projects.stream()
                .filter(project -> !project.isFinished())
                .collect(Collectors.toCollection(ArrayList::new));
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

    /** An empty day is not exported. An undefined value is skipped by the exporter itself. */
    @Override
    public boolean isExportable(final ExportAction action, final Key<?,?> key, final Object value) {
        return action != ExportAction.XML
            || MetaWorkSpace.days != key
            || !(value instanceof final WorkDay workDay)
            || !workDay.getEvents().isEmpty();
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
}
