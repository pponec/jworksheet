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

import net.ponec.jworksheet.bo.meta.MetaTaskType;

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
        return new TaskType().set(MetaTaskType.id, id).set(MetaTaskType.descr, descr);
    }

    @Test
    @DisplayName("The items are sorted by the keys in the order of their declaration")
    public void sortByMoreKeys() {
        final List<TaskType> list = new ArrayList<>(List.of
                ( newTask(2, "Bravo")
                , newTask(1, "Charlie")
                , newTask(2, "Alfa")));

        UjoComparator.<TaskType>newInstance(MetaTaskType.id, MetaTaskType.descr).sort(list);

        assertEquals("Charlie", list.get(0).get(MetaTaskType.descr));
        assertEquals("Alfa", list.get(1).get(MetaTaskType.descr));
        assertEquals("Bravo", list.get(2).get(MetaTaskType.descr));
    }

    @Test
    @DisplayName("An undefined value is the first one")
    public void undefinedValueIsFirst() {
        final UjoComparator<TaskType> comparator = UjoComparator.newInstance(MetaTaskType.descr);
        final TaskType undefined = newTask(1, null); // The setter stores an empty text
        undefined.set(MetaTaskType.descr, null);

        final TaskType defined = newTask(2, "Alfa");
        assertTrue(comparator.compare(undefined, defined) < 0);
        assertTrue(comparator.compare(defined, undefined) > 0);
    }

    @Test
    @DisplayName("The equals() method compares the selected keys only")
    public void equalsBySelectedKeys() {
        final UjoComparator<TaskType> comparator = UjoComparator.newInstance(MetaTaskType.id);

        assertTrue(comparator.equals(newTask(1, "Alfa"), newTask(1, "Bravo")), "The same identifier");
        assertFalse(comparator.equals(newTask(1, "Alfa"), newTask(2, "Alfa")), "A different identifier");
    }

    @Test
    @DisplayName("A comparison of the equal items provides the zero")
    public void equalItems() {
        final UjoComparator<TaskType> comparator = UjoComparator
                .newInstance(MetaTaskType.id, MetaTaskType.descr);
        assertEquals(0, comparator.compare(newTask(7, "Alfa"), newTask(7, "Alfa")));
    }
}
