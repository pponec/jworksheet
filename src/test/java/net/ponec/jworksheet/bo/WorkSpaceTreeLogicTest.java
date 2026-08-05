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

import net.ponec.jworksheet.bo.meta.MetaEvent;
import net.ponec.jworksheet.bo.meta.MetaProject;
import net.ponec.jworksheet.bo.meta.MetaTaskType;
import net.ponec.jworksheet.bo.meta.MetaWorkDay;

import java.util.List;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The data tree operations which follow the XML loading.
 * @author Pavel Ponec
 */
public class WorkSpaceTreeLogicTest extends XmlTestSupport {

    /** A code-book of two projects where the second one is finished. */
    private static WorkSpace newCodeBook() {
        final Project garden = newProject(0, "Water the garden"
                , newTask(1, "Roses")
                , newTask(2, "Tulips", true, false)
                , newTask(3, "Cactus", false, true));
        garden.set(MetaProject.defaultProject, true);

        final Project washing = newProject(1, "Washing dishes", newTask(1, "Plates"));
        washing.set(MetaProject.finished, true);

        final WorkSpace result = newWorkSpace();
        result.getProjects().add(garden);
        result.getProjects().add(washing);
        return result;
    }

    // ------------------------ E1) THE REFERENCE ASSIGNMENT ------------------------

    @Test
    @DisplayName("E1) The assingTasks() assigns the references and syncs the identifiers")
    public void assingTasks() {
        final WorkSpace workSpace = newCodeBook();
        final Event event = new Event()
                .set(MetaEvent.period, (short) 60)
                .set(MetaEvent.projectId, 0)
                .set(MetaEvent.taskId, 2);
        event.setStartTime(new Time("08:00"));
        final WorkDay day = newDay("2021-03-14");
        day.getEvents().add(event);
        workSpace.getDays().add(day);

        assertNull(event.get(MetaEvent.project), "No reference before the assignment");

        workSpace.assingTasks();

        assertSame(workSpace.findProject(0), event.get(MetaEvent.project));
        assertSame(workSpace.findProject(0).findTaskType(2), event.get(MetaEvent.task));

        // The identifier attributes are kept in sync with the assigned objects:
        assertEquals(Integer.valueOf(0), event.getProjectId());
        assertEquals(Integer.valueOf(2), event.getTaskId());
    }

    // ------------------------ E2) THE WORK DAY LOOKUP ------------------------

    @Nested
    @DisplayName("E2) The findWorkDay() method")
    class FindWorkDay {

        @Test
        @DisplayName("Returns an existing day")
        public void existingDay() {
            final WorkSpace workSpace = newWorkSpace();
            final WorkDay day = newDay("2021-03-14");
            workSpace.getDays().add(day);

            assertSame(day, workSpace.findWorkDay(new YearMonthDay("2021-03-14")));
            assertEquals(1, workSpace.getDays().size());
        }

        @Test
        @DisplayName("Creates and appends a missing day")
        public void missingDay() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"));

            final WorkDay result = workSpace.findWorkDay(new YearMonthDay("2021-03-15"));

            assertEquals("2021-03-15", result.get(MetaWorkDay.date).toString());
            assertEquals(2, workSpace.getDays().size());
            assertSame(result, workSpace.getDays().get(1));
        }
    }

    // ------------------------ E3) THE CODE-BOOK LOOKUP ------------------------

    @Nested
    @DisplayName("E3) The code-book lookup")
    class CodeBookLookup {

        private final WorkSpace workSpace = reload(newCodeBook());

        @Test
        @DisplayName("The findProject() returns null for an unknown identifier")
        public void findProject() {
            assertEquals("Water the garden", workSpace.findProject(0).get(MetaProject.descr));
            assertEquals("Washing dishes", workSpace.findProject(1).get(MetaProject.descr));
            assertNull(workSpace.findProject(99));
        }

        @Test
        @DisplayName("The findDefaultProject() skips the finished projects")
        public void findDefaultProject() {
            assertEquals(Integer.valueOf(0), workSpace.findDefaultProject().get(MetaProject.id));

            workSpace.findProject(0).set(MetaProject.finished, true);
            assertNull(workSpace.findDefaultProject());
        }

        @Test
        @DisplayName("The getOpenProjects() skips the finished projects")
        public void getOpenProjects() {
            final List<Project> result = workSpace.getOpenProjects();
            assertEquals(1, result.size());
            assertEquals(Integer.valueOf(0), result.get(0).get(MetaProject.id));
        }

        @Test
        @DisplayName("The findTaskType() returns null for an unknown identifier")
        public void findTaskType() {
            final Project project = workSpace.findProject(0);
            assertEquals("Roses", project.findTaskType(1).get(MetaTaskType.descr));
            assertNull(project.findTaskType(99));
        }

        @Test
        @DisplayName("The findDefaultTask() and the getOpenTasks() skip the finished tasks")
        public void taskFilters() {
            final Project project = workSpace.findProject(0);

            assertEquals("Tulips", project.findDefaultTask().get(MetaTaskType.descr));
            assertEquals(2, project.getOpenTasks().size());

            project.findTaskType(2).set(MetaTaskType.finished, true);
            assertNull(project.findDefaultTask());
            assertEquals(1, project.getOpenTasks().size());
        }
    }

    // ------------------------ E5) THE SHARED PROJECT SYNCHRONIZATION ------------------------

    @Nested
    @DisplayName("E5) The syncProjects() method")
    class SyncProjects {

        /** A shared code-book loaded from the second XML file. */
        private WorkSpace newSharedSpace() {
            final WorkSpace result = newWorkSpace();
            result.getProjects().add(newProject(0, "Water the garden"
                    , newTask(1, "Roses")
                    , newTask(9, "Orchids")));
            result.getProjects().add(newProject(7, "A brand new project"
                    , newTask(1, "The first task")));
            return result;
        }

        @Test
        @DisplayName("A new shared project is copied to the local code-book")
        public void newProjectIsCopied() {
            final WorkSpace workSpace = newCodeBook();
            final WorkSpace sharedSpace = newSharedSpace();
            workSpace.syncProjects(sharedSpace);

            final Project result = workSpace.findProject(7);
            assertNotNull(result);
            assertEquals("A brand new project", result.get(MetaProject.descr));
            assertEquals(1, result.getTasks().size());
            assertNotSame(sharedSpace.findProject(7), result, "A deep copy is expected");
            assertNotSame(sharedSpace.findProject(7).findTaskType(1), result.findTaskType(1));
        }

        @Test
        @DisplayName("A new shared task is copied to an existing project")
        public void newTaskIsCopied() {
            final WorkSpace workSpace = newCodeBook();
            workSpace.syncProjects(newSharedSpace());

            final Project result = workSpace.findProject(0);
            assertNotNull(result.findTaskType(9), "The new task");
            assertEquals("Orchids", result.findTaskType(9).get(MetaTaskType.descr));
        }

        @Test
        @DisplayName("A local project missing in the shared code-book is marked as finished")
        public void missingProjectIsFinished() {
            final WorkSpace workSpace = newCodeBook();
            assertFalse(workSpace.findProject(0).get(MetaProject.finished));

            workSpace.syncProjects(newSharedSpace());

            assertFalse(workSpace.findProject(0).get(MetaProject.finished), "A shared project");
            assertTrue(workSpace.findProject(1).get(MetaProject.finished), "A local project only");
        }

        @Test
        @DisplayName("A local task missing in the shared project is marked as finished")
        public void missingTaskIsFinished() {
            final WorkSpace workSpace = newCodeBook();
            workSpace.syncProjects(newSharedSpace());

            final Project result = workSpace.findProject(0);
            assertFalse(result.findTaskType(1).get(MetaTaskType.finished), "A shared task");
            assertTrue(result.findTaskType(2).get(MetaTaskType.finished), "A local task only");
        }

        @Test
        @DisplayName("A project of the same identifier but of a different description is not overwritten")
        public void renamedProjectIsNotOverwritten() {
            final WorkSpace workSpace = newCodeBook();
            workSpace.findProject(0).set(MetaProject.descr, "My own description");

            workSpace.syncProjects(newSharedSpace());

            final Project result = workSpace.findProject(0);
            assertEquals("My own description", result.get(MetaProject.descr));
            assertNull(result.findTaskType(9), "No task is synchronized");
            assertFalse(result.get(MetaProject.finished), "The project is not closed");
        }
    }
}
