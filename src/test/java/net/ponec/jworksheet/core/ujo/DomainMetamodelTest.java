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

import java.util.List;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.gui.models.UjoKeyRow;
import net.ponec.jworksheet.report.TaskGroup;
import org.junit.jupiter.api.DisplayName;
import org.ujorm.core.Key;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/** The Ujorm builds a metamodel of a domain class at the run time by a Java compiler.
 * <p>The generator fails on a property which has got a getter but no setter, so every
 * domain class of the application is checked here - the graphical user interface is not
 * covered by the tests and such a failure would appear at the application start only.
 * @author Pavel Ponec
 */
public class DomainMetamodelTest {

    @ParameterizedTest(name = "The metamodel of the {0} is available")
    @ValueSource(classes =
            { TaskType.class
            , Project.class
            , Event.class
            , WorkDay.class
            , WorkSpace.class
            , Parameters.class
            , TaskGroup.class
            , UjoKeyRow.class
            })
    @DisplayName("A metamodel of every domain class can be created")
    public void metamodelIsAvailable(final Class<?> domainClass) {
        final List<? extends Key<?,?>> keys = assertDoesNotThrow
                (() -> Keys.keysOf(domainClass)
                , "The metamodel generator failed for " + domainClass.getName());

        assertFalse(keys.isEmpty(), "No key was declared");
        for (Key<?,?> key : keys) {
            assertNotNull(Keys.nameOf(key), "A key name is required");
            assertNotNull(Keys.typeOf(key), "A key type is required");
        }
    }

    @ParameterizedTest(name = "The default values of the {0} are readable")
    @ValueSource(classes =
            { TaskType.class
            , Project.class
            , Event.class
            , WorkDay.class
            , WorkSpace.class
            , Parameters.class
            })
    @DisplayName("A default value of every persistent key is readable from a prototype")
    public void defaultValuesAreReadable(final Class<?> domainClass) {
        for (Key<?,?> key : Keys.keysOf(domainClass)) {
            assertDoesNotThrow(() -> Keys.defaultOf(key), "Can't read a default of " + key);
        }
    }
}
