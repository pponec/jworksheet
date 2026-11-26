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


import net.ponec.jworksheet.core.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import net.ponec.jworksheet.core.ujo.MetaAliases._EV;

/** The XML export rules implemented by the methods
 * {@code readAuthorization()}, {@code readValue()} and {@code writeValue()}
 * of the business objects. The tests check the XML document itself.
 * @author Pavel Ponec
 */
public class WorkSpaceXmlFilterTest extends XmlTestSupport {

    private WorkSpace workSpace;
    private Project project;
    private TaskType task;

    @BeforeEach
    public void initWorkSpace() {
        task = newTask(1, "Roses");
        project = newProject(3, "Water the garden", task);

        workSpace = newWorkSpace();
        workSpace.setVersion(new Version("1.00"));
        workSpace.setUsername("Pavel");
        workSpace.getProjects().add(project);
        workSpace.getDays().add(newDay("2021-03-14"
                , newEvent("08:00", 240, project, task, "Watering")));
    }

    @Test
    @DisplayName("The Event exports the identifiers instead of the Project and the Task objects")
    public void eventExportsIdentifiersOnly() {
        final String xml = toXml(workSpace);

        assertEquals(1, countElements(xml, "ProjectID"));
        assertEquals(1, countElements(xml, "TaskID"));
        // The 'Project' element belongs to the code-book only, the 'Task' one to the project:
        assertEquals(1, countElements(xml, "Project"));
        assertEquals(1, countElements(xml, "Task"));
        assertEquals("3", elementBody(xml, "ProjectID"));
        assertEquals("1", elementBody(xml, "TaskID"));
    }

    @Test
    @DisplayName("The identifiers are kept in sync with the assigned objects")
    public void identifiersFollowTheObjects() {
        final Event event = workSpace.getDays().get(0).getEvents().get(0);

        // Assigning a project or a task assigns its identifier too:
        assertEquals(Integer.valueOf(3), event.getProjectId(), "The identifier of the project");
        assertEquals(Integer.valueOf(1), event.getTaskId(), "The identifier of the task");

        // ... and the keys provide the same values:
        assertEquals(Integer.valueOf(3), _EV.projectId.getValue(event));
        assertEquals(Integer.valueOf(1), _EV.taskId.getValue(event));

        // A cleared reference clears its identifier as well:
        event.setTask(null);
        assertNull(event.getTaskId());
    }

    @Test
    @DisplayName("The assingTasks() method restores the references after a loading")
    public void identifiersAreSyncedAfterLoading() {
        final WorkSpace result = reload(workSpace);
        final Event event = result.getDays().get(0).getEvents().get(0);

        assertNotNull(event.getProject());
        assertNotNull(event.getTask());

        // The identifiers of the assigned objects are available again:
        assertEquals(Integer.valueOf(3), event.getProjectId());
        assertEquals(Integer.valueOf(1), event.getTaskId());
        assertEquals(Integer.valueOf(3), event.getProjectId());
        assertEquals(Integer.valueOf(1), event.getTaskId());
    }

    @Test
    @DisplayName("A negative period is normalized to the zero before the export")
    public void negativePeriodIsNormalized() {
        final Event event = workSpace.getDays().get(0).getEvents().get(0);
        event.setPeriod((short) -30);

        assertEquals(Event.ZERO_SHORT, event.getPeriod());
        assertEquals("0", elementBody(toXml(workSpace), "Period"));
    }

    @Test
    @DisplayName("The Archived date is exported for a defined value only")
    public void archivedDateFilter() {
        assertTrue(hasElement(toXml(workSpace), "Archived"));

        workSpace.setArchived(null);
        assertFalse(hasElement(toXml(workSpace), "Archived"));
    }

    @Test
    @DisplayName("An empty day is not exported")
    public void emptyDayFilter() {
        workSpace.getDays().add(newDay("2021-03-15"));
        assertEquals(2, workSpace.getDays().size());
        assertEquals(1, countElements(toXml(workSpace), "Day"));
    }

    @Test
    @DisplayName("The DayOff flag is exported for a true value only")
    public void dayOffFilter() {
        final WorkDay day = workSpace.getDays().get(0);

        assertFalse(hasElement(toXml(workSpace), "DayOff"));
        day.setDayOff(true);
        assertTrue(hasElement(toXml(workSpace), "DayOff"));
    }

    @Test
    @DisplayName("The export is idempotent - a repeated saving provides the same document")
    public void exportIsIdempotent() {
        final String xml1 = toXml(workSpace);
        final String xml2 = toXml(loadXml(xml1));
        final String xml3 = toXml(loadXml(xml2));

        assertEquals(xml1, xml2);
        assertEquals(xml2, xml3);
    }
}
