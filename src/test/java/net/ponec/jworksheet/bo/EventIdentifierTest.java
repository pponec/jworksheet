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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The identifiers of an event follow the assigned project and task, because the user can
 * change an identifier in the project table. The original Ujorm implementation read them
 * from the assigned objects too.
 * @author Pavel Ponec
 */
public class EventIdentifierTest extends XmlTestSupport {

    @Test
    @DisplayName("A changed identifier of the project and the task is saved")
    public void changedIdentifierIsSaved() {
        final TaskType task = newTask(1, "Roses");
        final Project project = newProject(3, "Garden", task);
        final WorkSpace workSpace = newWorkSpace();
        workSpace.getProjects().add(project);
        workSpace.getDays().add(newDay("2021-03-14", newEvent("08:00", 60, project, task, "")));

        project.setId(7);
        task.setId(5);

        final String xml = toXml(workSpace);
        assertEquals("7", elementBody(xml, "ProjectID"));
        assertEquals("5", elementBody(xml, "TaskID"));

        final Event event = reload(workSpace).getDays().get(0).getEvents().get(0);
        assertEquals(7, event.getProject().getId());
        assertEquals(5, event.getTask().getId());
    }

    @Test
    @DisplayName("An event without any reference provides the stored identifiers")
    public void storedIdentifiers() {
        final Event event = new Event();
        assertNull(event.getProjectId());

        event.setProjectId(4);
        event.setTaskId(2);
        assertEquals(4, event.getProjectId());
        assertEquals(2, event.getTaskId());
    }
}
