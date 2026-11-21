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

        assertEquals("1.00", workSpace.getVersion().toString());
        assertEquals("Pavel", workSpace.getUsername());
        assertNotNull(workSpace.getCreated());
        assertNotNull(workSpace.getArchived());
        assertEquals("1.00", workSpace.getCheckReleaseVersion().toString());
    }

    @Test
    @DisplayName("F1b) The day of the events is loaded including the references")
    public void workDayAndEvents() throws IOException {
        final WorkSpace workSpace = loadXml(readDataFile());

        assertEquals(1, workSpace.getDays().size());
        final WorkDay day = workSpace.getDays().get(0);
        assertEquals("2021-03-14", day.getDate().toString());
        assertFalse(day.isDayOff());
        assertEquals(2, day.getEvents().size());

        final Event first = day.getEvents().get(0);
        assertEquals("08:00", first.getStartTime().toString());
        assertEquals(Short.valueOf((short) 240), first.getPeriod());
        assertEquals("12:00", first.getTimeFinished().toString());
        assertEquals("Water the garden", first.getProject().getDescr());
        assertEquals("Roses", first.getTask().getDescr());
        assertEquals("", first.getDescr());
        assertFalse(first.isPrivate());

        final Event second = day.getEvents().get(1);
        assertEquals("12:00", second.getStartTime().toString());
        assertEquals(Short.valueOf((short) 0), second.getPeriod());
        assertEquals("Dinner", second.getProject().getDescr());
        assertNull(second.getTask(), "The event has no TaskID element");
        assertTrue(second.isPrivate(), "The project 'Dinner' is a private one");
    }

    @Test
    @DisplayName("F1c) The project code-book is loaded")
    public void projectCodeBook() throws IOException {
        final WorkSpace workSpace = loadXml(readDataFile());

        assertEquals(6, workSpace.getProjects().size());
        assertEquals(6, workSpace.getOpenProjects().size(), "No project is finished");
        assertNull(workSpace.findDefaultProject(), "No project is a default one");

        assertEquals(2, workSpace.findProject(0).getTasks().size());
        assertEquals(1, workSpace.findProject(1).getTasks().size());
        assertEquals(0, workSpace.findProject(2).getTasks().size());

        assertTrue(workSpace.findProject(3).isPrivateProject());
        assertTrue(workSpace.findProject(5).isPrivateProject());
        assertFalse(workSpace.findProject(4).isPrivateProject());

        assertEquals("No comment", workSpace.findProject(3).findDefaultTask().getDescr());
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
