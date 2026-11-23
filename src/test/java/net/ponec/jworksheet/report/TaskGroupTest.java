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

import java.util.Calendar;
import java.time.LocalDate;
import net.ponec.jworksheet.report.meta.MetaTaskGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The month and the year of a task group are derived from its day.
 * @author Pavel Ponec
 */
public class TaskGroupTest {

    @Test
    @DisplayName("The month and the year are read from the day, the keys provide them too")
    public void derivedValues() {
        final TaskGroup group = new TaskGroup();
        assertNull(group.getMonth());
        assertNull(group.getYear());

        group.setDay(LocalDate.parse("2021-03-14"));
        assertEquals(Calendar.MARCH, group.getMonth(), "The month is zero based like the Calendar");
        assertEquals(2021, MetaTaskGroup.year.getValue(group));
        assertThrows(UnsupportedOperationException.class, () -> group.setMonth(4));
    }
}
