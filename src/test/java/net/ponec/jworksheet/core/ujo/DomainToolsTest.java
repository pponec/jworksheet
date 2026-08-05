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

import net.ponec.jworksheet.bo.meta.MetaProject;
import net.ponec.jworksheet.bo.meta.MetaTaskType;

import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The copy of a domain object used by the "clone row" action of the tables.
 * @author Pavel Ponec
 */
public class DomainToolsTest {

    private static Project newProject() {
        final Project result = new Project()
                .set(MetaProject.id, 3)
                .set(MetaProject.descr, "Water the garden")
                .set(MetaProject.privateProject, true);
        result.getTasks().add(new TaskType()
                .set(MetaTaskType.id, 1)
                .set(MetaTaskType.descr, "Roses"));
        return result;
    }

    @Test
    @DisplayName("A copy carries all the scalar values")
    public void scalarValues() {
        final Project source = newProject();
        final Project result = DomainTools.deepCopy(source, 2);

        assertNotSame(source, result);
        assertEquals(3, result.get(MetaProject.id));
        assertEquals("Water the garden", result.get(MetaProject.descr));
        assertTrue(result.get(MetaProject.privateProject));
    }

    @Test
    @DisplayName("A copy of the depth 2 shares no item of the list")
    public void deepCopyOfTheItems() {
        final Project source = newProject();
        final Project result = DomainTools.deepCopy(source, 2);

        assertEquals(1, result.getTasks().size());
        final TaskType sourceTask = source.getTasks().get(0);
        final TaskType resultTask = result.getTasks().get(0);

        assertNotSame(sourceTask, resultTask, "The item is copied");
        assertEquals("Roses", resultTask.get(MetaTaskType.descr));

        resultTask.set(MetaTaskType.descr, "Tulips");
        assertEquals("Roses", sourceTask.get(MetaTaskType.descr), "The source is not affected");
    }

    @Test
    @DisplayName("A copy of the depth 1 shares the items of the list")
    public void shallowCopyOfTheItems() {
        final Project source = newProject();
        final Project result = DomainTools.deepCopy(source, 1);

        assertNotSame(source, result, "The object itself is always copied");
        assertSame(source.getTasks().get(0), result.getTasks().get(0));
    }

    @Test
    @DisplayName("A copy of the list is a new collection in any depth")
    public void listIsNeverShared() {
        final Project source = newProject();
        final Project result = DomainTools.deepCopy(source, 1);

        result.getTasks().add(new TaskType().set(MetaTaskType.id, 2));
        assertEquals(1, source.getTasks().size(), "The source list is not affected");
        assertEquals(2, result.getTasks().size());
    }

    @Test
    @DisplayName("A zero depth provides the original object")
    public void zeroDepth() {
        final Project source = newProject();
        assertSame(source, DomainTools.deepCopy(source, 0));
        assertNull(DomainTools.deepCopy(null, 2));
    }
}
