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

package net.ponec.jworksheet.report;

import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The time of a task group.
 * @author Pavel Ponec
 */
public class TaskGroupTest {

    @Test
    @DisplayName("The time of a private project is no business time")
    public void businessTime() {
        final Project project = new Project();
        final Event event = new Event();
        event.setProject(project);
        final TaskGroup group = new TaskGroup(event);
        group.addTime((short) 90);

        assertEquals(90, group.getTime());
        assertEquals(90, group.getBusinessTime());

        project.setPrivateProject(true);
        assertEquals(90, group.getTime());
        assertEquals(0, group.getBusinessTime());
    }
}
