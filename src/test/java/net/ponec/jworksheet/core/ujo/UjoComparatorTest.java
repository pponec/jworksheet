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

package net.ponec.jworksheet.core.ujo;

import java.util.ArrayList;
import java.util.List;
import net.ponec.jworksheet.bo.TaskType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The comparator of the domain objects used by the tables and the reports.
 * @author Pavel Ponec
 */
public class UjoComparatorTest {

    private static TaskType newTask(final int id, final String descr) {
        return new TaskType().set(TaskType.P_ID, id).set(TaskType.P_DESCR, descr);
    }

    @Test
    @DisplayName("The items are sorted by the keys in the order of their declaration")
    public void sortByMoreKeys() {
        final List<TaskType> list = new ArrayList<>(List.of
                ( newTask(2, "Bravo")
                , newTask(1, "Charlie")
                , newTask(2, "Alfa")));

        UjoComparator.<TaskType>newInstance(TaskType.P_ID, TaskType.P_DESCR).sort(list);

        assertEquals("Charlie", list.get(0).get(TaskType.P_DESCR));
        assertEquals("Alfa", list.get(1).get(TaskType.P_DESCR));
        assertEquals("Bravo", list.get(2).get(TaskType.P_DESCR));
    }

    @Test
    @DisplayName("An undefined value is the first one")
    public void undefinedValueIsFirst() {
        final UjoComparator<TaskType> comparator = UjoComparator.newInstance(TaskType.P_DESCR);
        final TaskType undefined = newTask(1, null); // The setter stores an empty text
        undefined.set(TaskType.P_DESCR, null);

        final TaskType defined = newTask(2, "Alfa");
        assertTrue(comparator.compare(undefined, defined) < 0);
        assertTrue(comparator.compare(defined, undefined) > 0);
    }

    @Test
    @DisplayName("The equals() method compares the selected keys only")
    public void equalsBySelectedKeys() {
        final UjoComparator<TaskType> comparator = UjoComparator.newInstance(TaskType.P_ID);

        assertTrue(comparator.equals(newTask(1, "Alfa"), newTask(1, "Bravo")), "The same identifier");
        assertFalse(comparator.equals(newTask(1, "Alfa"), newTask(2, "Alfa")), "A different identifier");
    }

    @Test
    @DisplayName("A comparison of the equal items provides the zero")
    public void equalItems() {
        final UjoComparator<TaskType> comparator = UjoComparator
                .newInstance(TaskType.P_ID, TaskType.P_DESCR);
        assertEquals(0, comparator.compare(newTask(7, "Alfa"), newTask(7, "Alfa")));
    }
}
