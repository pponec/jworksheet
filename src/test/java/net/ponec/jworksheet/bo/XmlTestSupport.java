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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujorm.core.UjoManagerXML;
import org.ujorm.core.XmlHeader;

import static java.nio.charset.StandardCharsets.UTF_8;

/** Common tools for the XML persistence tests.
 * <p>The class emulates the data serialization of the
 * {@link net.ponec.jworksheet.core.ApplContext} class,
 * see its methods {@code saveData()} and {@code loadData()}.
 */
public abstract class XmlTestSupport {

    /** A context of the UjoAction. The production code sends an ApplContext instance,
     * however the object is used as an action marker only. */
    protected static final String CONTEXT = "jUnit";

    // ------------------------ SERIALIZATION ------------------------

    /** Export the WorkSpace to an XML document. */
    protected static String toXml(final WorkSpace workSpace) {
        final StringBuilder result = new StringBuilder(256);
        try {
            UjoManagerXML.getInstance().saveXML(result, workSpace, new XmlHeader(), CONTEXT);
        } catch (IOException e) {
            throw new IllegalStateException("Can't export the WorkSpace", e);
        }
        return result.toString();
    }

    /** Import a WorkSpace from an XML document. No reference is assigned. */
    protected static WorkSpace fromXml(final String xml) {
        final InputStream is = new ByteArrayInputStream(xml.getBytes(UTF_8));
        return UjoManagerXML.getInstance().parseXML(is, WorkSpace.class, CONTEXT);
    }

    /** Import a WorkSpace from an XML document including the reference assignment.
     * The method emulates the {@code ApplContext.loadData()} behaviour. */
    protected static WorkSpace loadXml(final String xml) {
        final WorkSpace result = fromXml(xml);
        result.assingTasks();
        return result;
    }

    /** Export the WorkSpace to XML and import it back including the reference assignment. */
    protected static WorkSpace reload(final WorkSpace workSpace) {
        return loadXml(toXml(workSpace));
    }

    // ------------------------ XML DOCUMENT TOOLS ------------------------

    /** Count of the XML elements of the required name. */
    protected static int countElements(final String xml, final String element) {
        final String begTag = "<" + element + ">";
        final String emptyTag = "<" + element + "/>";
        int result = 0;
        for (int i = 0; (i = xml.indexOf(begTag, i)) >= 0; i += begTag.length()) {
            result++;
        }
        for (int i = 0; (i = xml.indexOf(emptyTag, i)) >= 0; i += emptyTag.length()) {
            result++;
        }
        return result;
    }

    /** Is the XML element available in the document? */
    protected static boolean hasElement(final String xml, final String element) {
        return countElements(xml, element) > 0;
    }

    /** Returns a body of the first XML element of the required name. */
    protected static String elementBody(final String xml, final String element) {
        final String begTag = "<" + element + ">";
        final int beg = xml.indexOf(begTag);
        if (beg < 0) {
            return null;
        }
        final int end = xml.indexOf("</" + element + ">", beg);
        return xml.substring(beg + begTag.length(), end);
    }

    // ------------------------ DATA BUILDERS ------------------------

    protected static TaskType newTask(final int id, final String description) {
        return newTask(id, description, false, false);
    }

    protected static TaskType newTask
            ( final int id
            , final String description
            , final boolean defaultTask
            , final boolean finished) {
        return new TaskType()
                .set(TaskType.P_ID, id)
                .set(TaskType.P_DESCR, description)
                .set(TaskType.P_DEFAULT, defaultTask)
                .set(TaskType.P_FINISHED, finished);
    }

    protected static Project newProject(final int id, final String description, final TaskType... tasks) {
        final Project result = new Project()
                .set(Project.P_ID, id)
                .set(Project.P_DESCR, description);
        for (TaskType task : tasks) {
            Project.P_TASKS.addItem(result, task);
        }
        return result;
    }

    protected static Event newEvent
            ( final String time
            , final int period
            , final Project project
            , final TaskType task
            , final String description) {
        return new Event()
                .set(Event.P_TIME, new Time(time))
                .set(Event.P_PERIOD, (short) period)
                .set(Event.P_PROJ, project)
                .set(Event.P_TASK, task)
                .set(Event.P_DESCR, description);
    }

    protected static WorkDay newDay(final String date, final Event... events) {
        final WorkDay result = new WorkDay().set(WorkDay.P_DATE, new YearMonthDay(date));
        for (Event event : events) {
            WorkDay.P_EVENTS.addItem(result, event);
        }
        return result;
    }

    /** A minimal WorkSpace with the mandatory attributes assigned. */
    protected static WorkSpace newWorkSpace() {
        return new WorkSpace().set(WorkSpace.P_ARCHIVED, newDate(2021, Calendar.MARCH, 14, 11, 42, 58));
    }

    /** A time zone independent date without milliseconds. */
    protected static Date newDate
            ( final int year
            , final int month
            , final int day
            , final int hour
            , final int minute
            , final int second) {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, day, hour, minute, second);
        return cal.getTime();
    }
}
