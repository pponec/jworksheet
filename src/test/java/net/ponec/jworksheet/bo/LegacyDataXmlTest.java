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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** A compatibility of the persistent format checked on the real data file
 * {@code src/test/resources/data.xml}.
 * @author Pavel Ponec
 */
public class LegacyDataXmlTest extends XmlTestSupport {

    /** A sample of the production data file. */
    private static final String DATA_FILE = "/data.xml";

    private static String readDataFile() throws IOException {
        try (InputStream is = LegacyDataXmlTest.class.getResourceAsStream(DATA_FILE)) {
            assertNotNull(is, "The resource is required: " + DATA_FILE);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    @DisplayName("F1a) The WorkSpace attributes are loaded")
    public void workSpaceAttributes() throws IOException {
        final WorkSpace workSpace = loadXml(readDataFile());

        assertEquals("1.00", workSpace.get(WorkSpace.P_VERSN).toString());
        assertEquals("Pavel", workSpace.get(WorkSpace.P_USERNAME));
        assertNotNull(workSpace.get(WorkSpace.P_CREATED));
        assertNotNull(workSpace.get(WorkSpace.P_ARCHIVED));
        assertEquals("1.00", workSpace.get(WorkSpace.P_RELEASE_CHECK_VERSION).toString());
    }

    @Test
    @DisplayName("F1b) The day of the events is loaded including the references")
    public void workDayAndEvents() throws IOException {
        final WorkSpace workSpace = loadXml(readDataFile());

        assertEquals(1, WorkSpace.P_DAYS.getItemCount(workSpace));
        final WorkDay day = WorkSpace.P_DAYS.getItem(workSpace, 0);
        assertEquals("2021-03-14", day.get(WorkDay.P_DATE).toString());
        assertFalse(day.get(WorkDay.P_DAYOFF));
        assertEquals(2, WorkDay.P_EVENTS.getItemCount(day));

        final Event first = WorkDay.P_EVENTS.getItem(day, 0);
        assertEquals("08:00", first.get(Event.P_TIME).toString());
        assertEquals(Short.valueOf((short) 240), first.get(Event.P_PERIOD));
        assertEquals("12:00", first.getTimeFinished().toString());
        assertEquals("Water the garden", first.get(Event.P_PROJ).get(Project.P_DESCR));
        assertEquals("Roses", first.get(Event.P_TASK).get(TaskType.P_DESCR));
        assertEquals("", first.get(Event.P_DESCR));
        assertFalse(first.isPrivate());

        final Event second = WorkDay.P_EVENTS.getItem(day, 1);
        assertEquals("12:00", second.get(Event.P_TIME).toString());
        assertEquals(Short.valueOf((short) 0), second.get(Event.P_PERIOD));
        assertEquals("Dinner", second.get(Event.P_PROJ).get(Project.P_DESCR));
        assertNull(second.get(Event.P_TASK), "The event has no TaskID element");
        assertTrue(second.isPrivate(), "The project 'Dinner' is a private one");
    }

    @Test
    @DisplayName("F1c) The project code-book is loaded")
    public void projectCodeBook() throws IOException {
        final WorkSpace workSpace = loadXml(readDataFile());

        assertEquals(6, WorkSpace.P_PROJS.getItemCount(workSpace));
        assertEquals(6, workSpace.getOpenProjects().size(), "No project is finished");
        assertNull(workSpace.findDefaultProject(), "No project is a default one");

        assertEquals(2, Project.P_TASKS.getItemCount(workSpace.findProject(0)));
        assertEquals(1, Project.P_TASKS.getItemCount(workSpace.findProject(1)));
        assertEquals(0, Project.P_TASKS.getItemCount(workSpace.findProject(2)));

        assertTrue(workSpace.findProject(3).get(Project.P_PRIVATE));
        assertTrue(workSpace.findProject(5).get(Project.P_PRIVATE));
        assertFalse(workSpace.findProject(4).get(Project.P_PRIVATE));

        assertEquals("No comment", workSpace.findProject(3).findDefaultTask().get(TaskType.P_DESCR));
    }

    @Test
    @DisplayName("F2) A repeated saving of the loaded file provides the same document")
    public void savingIsIdempotent() throws IOException {
        final WorkSpace workSpace = loadXml(readDataFile());
        final String xml1 = toXml(workSpace);
        final String xml2 = toXml(loadXml(xml1));

        assertEquals(xml1, xml2);
        assertTrue(xml1.contains("<ProjectID>0</ProjectID>"));
        assertTrue(xml1.contains("<TaskID>1</TaskID>"));
        assertEquals(6, countElements(xml1, "Project"));
    }
}
