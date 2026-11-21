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

import java.io.File;
import java.util.Locale;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.meta.MetaParameters;
import net.ponec.jworksheet.bo.meta.MetaWorkSpace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The operations above the generated keys.
 * @author Pavel Ponec
 */
public class KeysTest {

    @Test
    @DisplayName("A primitive type is boxed, an object type is kept")
    public void box() {
        assertSame(Integer.class, Keys.box(int.class));
        assertSame(Boolean.class, Keys.box(boolean.class));
        assertSame(Character.class, Keys.box(char.class));
        assertSame(String.class, Keys.box(String.class));
    }

    @Test
    @DisplayName("A null value of a primitive field is written as the zero")
    public void writeNullToPrimitive() {
        final Parameters parameters = new Parameters();
        MetaParameters.workingHours.setValue(parameters, null);
        MetaParameters.windowSizeRestoration.setValue(parameters, null);

        assertEquals(0f, parameters.getWorkingHours());
        assertFalse(parameters.isWindowSizeRestoration());
    }

    @Test
    @DisplayName("A default value is compared by the equals() method")
    public void isDefault() {
        final Parameters parameters = new Parameters();
        assertTrue(Keys.isDefault(MetaParameters.language, parameters));
        assertTrue(Keys.isDefault(MetaParameters.dataFilePath, parameters));

        // A new instance of the same path is still the default one:
        parameters.setDataFilePath(new File(parameters.getDataFilePath().getPath()));
        assertTrue(Keys.isDefault(MetaParameters.dataFilePath, parameters));

        parameters.setLanguage(Locale.FRENCH.equals(Locale.getDefault()) ? Locale.GERMAN : Locale.FRENCH);
        assertFalse(Keys.isDefault(MetaParameters.language, parameters));
    }

    @Test
    @DisplayName("A type of the list item is resolved by the generic type of the field")
    public void itemTypeOf() {
        assertSame(WorkDay.class, Keys.itemTypeOf(MetaWorkSpace.days));
        assertThrows(IllegalStateException.class, () -> Keys.itemTypeOf(MetaWorkSpace.username));
    }
}
