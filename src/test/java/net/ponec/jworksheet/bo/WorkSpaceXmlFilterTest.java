/**
  * Copyright (C) 2007-2022, Pavel Ponec, contact: http://ponec.net/
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

        workSpace = newWorkSpace()
                .set(WorkSpace.P_VERSN, new Version("1.00"))
                .set(WorkSpace.P_USERNAME, "Pavel");
        WorkSpace.P_PROJS.addItem(workSpace, project);
        WorkSpace.P_DAYS.addItem(workSpace, newDay("2021-03-14"
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
    @DisplayName("The identifiers are read from the assigned objects, not from the ID attributes")
    @SuppressWarnings("deprecation")
    public void identifiersAreReadFromTheObjects() {
        final Event event = WorkDay.P_EVENTS.getItem(WorkSpace.P_DAYS.getItem(workSpace, 0), 0);

        // The plain JavaBean attributes are empty (the getters read the fields directly):
        assertNull(event.getProjectID(), "The identifier attribute is empty");
        assertNull(event.getTaskID(), "The identifier attribute is empty");

        // ... however the keys are virtual ones, they read the related objects:
        assertEquals(Integer.valueOf(3), event.get(Event.P_PROJID));
        assertEquals(Integer.valueOf(1), event.get(Event.P_TASKID));
    }

    @Test
    @DisplayName("The assingTasks() method clears the identifier attributes after a loading")
    @SuppressWarnings("deprecation")
    public void identifiersAreClearedAfterLoading() {
        final WorkSpace result = reload(workSpace);
        final Event event = WorkDay.P_EVENTS.getItem(WorkSpace.P_DAYS.getItem(result, 0), 0);

        assertNull(event.getProjectID(), "The temporary attribute is cleared");
        assertNull(event.getTaskID(), "The temporary attribute is cleared");
        assertNotNull(event.get(Event.P_PROJ));
        assertNotNull(event.get(Event.P_TASK));

        // The virtual keys provide the identifiers of the assigned objects again:
        assertEquals(Integer.valueOf(3), event.get(Event.P_PROJID));
        assertEquals(Integer.valueOf(1), event.get(Event.P_TASKID));
    }

    @Test
    @DisplayName("A negative period is normalized to the zero before the export")
    public void negativePeriodIsNormalized() {
        final Event event = WorkDay.P_EVENTS.getItem(WorkSpace.P_DAYS.getItem(workSpace, 0), 0);
        event.set(Event.P_PERIOD, (short) -30);

        assertEquals(Event.ZERO_SHORT, event.get(Event.P_PERIOD));
        assertEquals("0", elementBody(toXml(workSpace), "Period"));
    }

    @Test
    @DisplayName("The Archived date is exported for a defined value only")
    public void archivedDateFilter() {
        assertTrue(hasElement(toXml(workSpace), "Archived"));

        workSpace.set(WorkSpace.P_ARCHIVED, null);
        assertFalse(hasElement(toXml(workSpace), "Archived"));
    }

    @Test
    @DisplayName("An empty day is not exported")
    public void emptyDayFilter() {
        WorkSpace.P_DAYS.addItem(workSpace, newDay("2021-03-15"));
        assertEquals(2, WorkSpace.P_DAYS.getItemCount(workSpace));
        assertEquals(1, countElements(toXml(workSpace), "Day"));
    }

    @Test
    @DisplayName("The DayOff flag is exported for a true value only")
    public void dayOffFilter() {
        final WorkDay day = WorkSpace.P_DAYS.getItem(workSpace, 0);

        assertFalse(hasElement(toXml(workSpace), "DayOff"));
        day.set(WorkDay.P_DAYOFF, true);
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
