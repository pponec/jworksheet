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
import net.ponec.jworksheet.core.ujo.Keys;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.core.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/** A round trip of the data tree: WorkSpace &rarr; XML &rarr; WorkSpace.
 * @author Pavel Ponec
 */
public class WorkSpaceXmlRoundTripTest extends XmlTestSupport {

    // ------------------------ A) THE WORKSPACE ROOT ------------------------

    @Nested
    @DisplayName("A) The WorkSpace root")
    class WorkSpaceRoot {

        @Test
        @DisplayName("A1) An empty WorkSpace keeps its default values")
        public void emptyWorkSpace() {
            final WorkSpace result = reload(new WorkSpace());

            assertEquals("0", result.getVersion().toString());
            assertEquals("?", result.getUsername());
            assertNull(result.getCreated());
            assertNull(result.getArchived());
            assertEquals(0, result.getDays().size());
            assertEquals(0, result.getProjects().size());
        }

        @Test
        @DisplayName("A2) All the scalar attributes survive the round trip")
        public void allScalarAttributes() {
            final LocalDateTime created = LocalDateTime.of(2021, 3, 14, 11, 51, 31, 634_000_000);
            final LocalDateTime archived = LocalDateTime.of(2021, 3, 13, 0, 0);
            final LocalDateTime checkDate = LocalDateTime.of(2022, 1, 1, 1, 0);

            final WorkSpace workSpace = new WorkSpace();
            workSpace.setVersion(new Version("1.00"));
            workSpace.setCreated(created);
            workSpace.setArchived(archived);
            workSpace.setUsername("Příliš žluťoučký kůň");
            workSpace.setCheckReleaseDate(checkDate);
            workSpace.setCheckReleaseVersion(new Version("1.02"));

            final WorkSpace result = reload(workSpace);

            assertEquals("1.00", result.getVersion().toString());
            assertEquals(created, result.getCreated());
            assertEquals(archived, result.getArchived());
            assertEquals("Příliš žluťoučký kůň", result.getUsername());
            assertEquals(checkDate, result.getCheckReleaseDate());
            assertEquals("1.02", result.getCheckReleaseVersion().toString());
        }

        @Test
        @DisplayName("A3) An undefined Archived date is not exported at all")
        public void undefinedArchivedDate() {
            final String xml = toXml(new WorkSpace());

            assertFalse(hasElement(xml, "Archived"));
            assertNull(loadXml(xml).getArchived());
        }
    }

    // ------------------------ B) THE WORK DAY ------------------------

    @Nested
    @DisplayName("B) The WorkDay level")
    class WorkDayLevel {

        @Test
        @DisplayName("B1) A day with events is exported")
        public void dayWithEvents() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 240, null, null, "Morning")));

            final WorkSpace result = reload(workSpace);
            final List<WorkDay> days = result.getDays();

            assertEquals(1, days.size());
            assertEquals("2021-03-14", days.get(0).getDate().toString());
            assertEquals(1, days.get(0).getEvents().size());
        }

        @Test
        @DisplayName("B2) A day without any event is skipped by the export filter")
        public void dayWithoutEvents() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 60, null, null, "Work")));
            workSpace.getDays().add(newDay("2021-03-15"));
            workSpace.getDays().add(newDay("2021-03-16"));

            final String xml = toXml(workSpace);

            assertEquals(1, countElements(xml, "Day"));
            assertEquals(1, loadXml(xml).getDays().size());
        }

        @Test
        @DisplayName("B3a) The DayOff flag is exported for a true value only")
        public void dayOffTrue() {
            final WorkSpace workSpace = newWorkSpace();
            final WorkDay day = newDay("2021-03-14", newEvent("08:00", 60, null, null, ""));
            day.setDayOff(true);
            workSpace.getDays().add(day);

            final String xml = toXml(workSpace);
            assertTrue(hasElement(xml, "DayOff"));

            final WorkDay result = loadXml(xml).getDays().get(0);
            assertTrue(result.isDayOff());
        }

        @Test
        @DisplayName("B3b) A false DayOff flag is not exported, the default value is restored")
        public void dayOffFalse() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 60, null, null, "")));

            final String xml = toXml(workSpace);
            assertFalse(hasElement(xml, "DayOff"));

            final WorkDay result = loadXml(xml).getDays().get(0);
            assertFalse(result.isDayOff());
        }

        @Test
        @DisplayName("B3c) A day off without any event is lost - the event filter has a higher priority")
        public void dayOffWithoutEvents() {
            final WorkSpace workSpace = newWorkSpace();
            final WorkDay day = newDay("2021-03-14");
            day.setDayOff(true);
            workSpace.getDays().add(day);

            final String xml = toXml(workSpace);

            assertEquals(0, countElements(xml, "Day"));
            assertEquals(0, loadXml(xml).getDays().size());
        }

        @Test
        @DisplayName("B4) The order of days is kept, the sortDays() orders them by a date")
        public void dayOrder() {
            final WorkSpace workSpace = newWorkSpace();
            for (String date : new String[] {"2021-03-16", "2021-03-14", "2021-03-15"}) {
                workSpace.getDays().add(newDay(date
                        , newEvent("08:00", 60, null, null, date)));
            }

            final WorkSpace result = reload(workSpace);
            assertEquals("2021-03-16", result.getDays().get(0).getDate().toString());

            result.sortDays();
            assertEquals("2021-03-14", result.getDays().get(0).getDate().toString());
            assertEquals("2021-03-15", result.getDays().get(1).getDate().toString());
            assertEquals("2021-03-16", result.getDays().get(2).getDate().toString());
        }

        @ParameterizedTest(name = "B5) The date {0} survives the round trip")
        @ValueSource(strings = {"2021-01-01", "2021-02-28", "2020-02-29", "2021-06-30", "2021-12-31"})
        public void dateBoundaries(final String date) {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay(date
                    , newEvent("08:00", 60, null, null, "")));

            final WorkDay result = reload(workSpace).getDays().get(0);
            assertEquals(date, result.getDate().toString());
            assertEquals(LocalDate.parse(date), result.getDate());
        }
    }

    // ------------------------ C) THE EVENT ------------------------

    @Nested
    @DisplayName("C) The Event level")
    class EventLevel {

        @Test
        @DisplayName("C1) A project and a task are restored as the code-book instances")
        public void projectAndTaskReference() {
            final TaskType task = newTask(7, "Roses");
            final Project project = newProject(3, "Water the garden", task);

            final WorkSpace workSpace = newWorkSpace();
            workSpace.getProjects().add(project);
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 240, project, task, "Watering")));

            final WorkSpace result = reload(workSpace);
            final Event event = result.getDays().get(0).getEvents().get(0);

            assertSame(result.findProject(3), event.getProject());
            assertSame(result.findProject(3).findTaskType(7), event.getTask());
            assertEquals("Water the garden", event.getProject().getDescr());
            assertEquals("Roses", event.getTask().getDescr());
        }

        @Test
        @DisplayName("C2) An event with a project but without any task")
        public void projectWithoutTask() {
            final Project project = newProject(3, "Shopping");

            final WorkSpace workSpace = newWorkSpace();
            workSpace.getProjects().add(project);
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("12:00", 30, project, null, "")));

            final String xml = toXml(workSpace);
            assertTrue(hasElement(xml, "ProjectID"));
            assertFalse(hasElement(xml, "TaskID"));

            final Event event = loadXml(xml).getDays().get(0).getEvents().get(0);
            assertNotNull(event.getProject());
            assertNull(event.getTask());
        }

        @Test
        @DisplayName("C3) An event without a project and a task")
        public void noReferences() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("12:00", 30, null, null, "Free time")));

            final String xml = toXml(workSpace);
            assertFalse(hasElement(xml, "ProjectID"));
            assertFalse(hasElement(xml, "TaskID"));

            final Event event = loadXml(xml).getDays().get(0).getEvents().get(0);
            assertNull(event.getProject());
            assertNull(event.getTask());
            assertEquals("Free time", event.getDescr());
        }

        @Test
        @DisplayName("C4) A broken reference is resolved to the null value")
        public void brokenReference() {
            final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<body>\n"
                    + "  <Day>\n"
                    + "    <Date>2021-03-14</Date>\n"
                    + "    <Event>\n"
                    + "      <Time>08:00</Time>\n"
                    + "      <Period>60</Period>\n"
                    + "      <ProjectID>99</ProjectID>\n"
                    + "      <TaskID>77</TaskID>\n"
                    + "    </Event>\n"
                    + "    <Event>\n"
                    + "      <Time>09:00</Time>\n"
                    + "      <Period>60</Period>\n"
                    + "      <ProjectID>0</ProjectID>\n"
                    + "      <TaskID>77</TaskID>\n"
                    + "    </Event>\n"
                    + "  </Day>\n"
                    + "  <Project>\n"
                    + "    <ID>0</ID>\n"
                    + "    <Description>The single project</Description>\n"
                    + "  </Project>\n"
                    + "</body>";

            final WorkSpace result = loadXml(xml);
            final WorkDay day = result.getDays().get(0);

            final Event unknownProject = day.getEvents().get(0);
            assertNull(unknownProject.getProject(), "An unknown project");
            assertNull(unknownProject.getTask(), "A task of an unknown project");

            final Event unknownTask = day.getEvents().get(1);
            assertNotNull(unknownTask.getProject());
            assertNull(unknownTask.getTask(), "An unknown task of a known project");
        }

        @ParameterizedTest(name = "C5) The period {0} is stored as {1}")
        @CsvSource({"0, 0", "1, 1", "240, 240", "-1, 0", "-1440, 0"})
        public void periodValues(final short period, final short expected) {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", period, null, null, "")));

            final Event event = reload(workSpace).getDays().get(0).getEvents().get(0);
            assertEquals(expected, event.getPeriod());
        }

        @Test
        @DisplayName("C5b) An undefined period is normalized to the zero")
        public void undefinedPeriod() {
            final Event event = new Event();
            MetaEvent.period.setValue(event, null);
            event.setTime(new Time("08:00"));
            assertEquals(Event.ZERO_SHORT, event.getPeriod());
        }

        @Test
        @DisplayName("C5c) An undefined start time is normalized to the midnight")
        public void undefinedTime() {
            final Event event = new Event();
            MetaEvent.time.setValue(event, null);
            assertEquals("00:00", event.getTime().toString());
        }

        @ParameterizedTest(name = "C6) The time {0} plus {1} minutes is finished at {2}")
        @CsvSource({"00:00, 0, 00:00", "08:30, 90, 10:00", "23:59, 1, 24:00", "24:00, 0, 24:00"})
        public void timeValues(final String time, final short period, final String finished) {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent(time, period, null, null, "")));

            final String xml = toXml(workSpace);
            assertEquals(time, elementBody(xml, "Time"));

            final Event event = loadXml(xml).getDays().get(0).getEvents().get(0);
            assertEquals(new Time(time).getTimeMinutes(), event.getTime().getTimeMinutes());
            assertEquals(finished, event.getTimeFinished().toString());
        }

        @ParameterizedTest(name = "C7) The description [{0}] survives the round trip")
        @ValueSource(strings =
                { ""
                , "A plain text"
                , "Příliš žluťoučký kůň úpěl ďábelské ódy"
                , "Tags <b>&amp;</b> \"quotes\" 'apostrophes'"
                , "The first line\nThe second line\tTabulator"
                , "Emoji: ☀ © €"
                })
        public void descriptionValues(final String description) {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 60, null, null, description)));

            final Event event = reload(workSpace).getDays().get(0).getEvents().get(0);
            assertEquals(description, event.getDescr());
        }

        @Test
        @DisplayName("C7b) An undefined description is restored as an empty text")
        public void undefinedDescription() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 60, null, null, null)));

            final Event event = reload(workSpace).getDays().get(0).getEvents().get(0);
            assertEquals("", event.getDescr());
        }

        @Test
        @DisplayName("C8) The order of the events of one day is kept")
        public void eventOrder() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("12:00", 60, null, null, "C")
                    , newEvent("08:00", 60, null, null, "A")
                    , newEvent("10:00", 60, null, null, "B")));

            final WorkDay day = reload(workSpace).getDays().get(0);
            assertEquals(3, day.getEvents().size());
            assertEquals("C", day.getEvents().get(0).getDescr());
            assertEquals("A", day.getEvents().get(1).getDescr());
            assertEquals("B", day.getEvents().get(2).getDescr());
        }
    }

    // ------------------------ D) THE PROJECT CODE-BOOK ------------------------

    @Nested
    @DisplayName("D) The Project and TaskType code-book")
    class CodeBook {

        @Test
        @DisplayName("D1) A project without any task")
        public void projectWithoutTasks() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getProjects().add(newProject(0, "An empty project"));

            final WorkSpace result = reload(workSpace);
            assertEquals(1, result.getProjects().size());
            assertEquals(0, result.findProject(0).getTasks().size());
        }

        @Test
        @DisplayName("D2) All the flags of a project and its tasks survive the round trip")
        public void projectFlags() {
            final Project project = newProject(5, "A private project"
                    , newTask(1, "A default task", true, false)
                    , newTask(2, "A finished task", false, true)
                    , newTask(3, "A common task", false, false));
            project.setDefaultProject(true);
            project.setFinished(true);
            project.setPrivateProject(true);

            final WorkSpace workSpace = newWorkSpace();
            workSpace.getProjects().add(project);

            final Project result = reload(workSpace).findProject(5);

            assertTrue(result.isDefaultProject());
            assertTrue(result.isFinished());
            assertTrue(result.isPrivateProject());
            assertEquals(3, result.getTasks().size());
            assertTrue(result.findTaskType(1).isDefaultTask());
            assertTrue(result.findTaskType(2).isFinished());
            assertFalse(result.findTaskType(3).isDefaultTask());
            assertFalse(result.findTaskType(3).isFinished());
        }

        @Test
        @DisplayName("D3) A task identifier is unique inside its project only")
        public void duplicatedTaskId() {
            final TaskType roses = newTask(1, "Roses");
            final TaskType dishes = newTask(1, "Dishes");
            final Project garden = newProject(0, "Water the garden", roses);
            final Project kitchen = newProject(1, "Washing", dishes);

            final WorkSpace workSpace = newWorkSpace();
            workSpace.getProjects().add(garden);
            workSpace.getProjects().add(kitchen);
            workSpace.getDays().add(newDay("2021-03-14"
                    , newEvent("08:00", 60, garden, roses, "")
                    , newEvent("09:00", 60, kitchen, dishes, "")));

            final WorkDay day = reload(workSpace).getDays().get(0);

            assertEquals("Roses", day.getEvents().get(0).getTask().getDescr());
            assertEquals("Dishes", day.getEvents().get(1).getTask().getDescr());
        }

        @Test
        @DisplayName("D4) The descriptions with the special characters")
        public void specialCharacters() {
            final WorkSpace workSpace = newWorkSpace();
            workSpace.getProjects().add(newProject(0, "R&D <Šílený> \"projekt\""
                    , newTask(1, "Úkol č. 1 & 2")));

            final Project result = reload(workSpace).findProject(0);
            assertEquals("R&D <Šílený> \"projekt\"", result.getDescr());
            assertEquals("Úkol č. 1 & 2", result.findTaskType(1).getDescr());
        }
    }
}
