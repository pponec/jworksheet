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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import net.ponec.jworksheet.core.ujo.MetaAliases._PA;
import net.ponec.jworksheet.core.ujo.MetaAliases._PR;
import net.ponec.jworksheet.core.ujo.MetaAliases._WS;

/** The operations above the generated keys.
 * @author Pavel Ponec
 */
public class KeysTest {

    @Test
    @DisplayName("A primitive type of the key is boxed, an object type is kept")
    public void typeOf() {
        assertSame(Integer.class, Keys.typeOf(_PR.id));
        assertSame(Boolean.class, Keys.typeOf(_PR.finished));
        assertSame(String.class, Keys.typeOf(_PR.descr));
    }

    @Test
    @DisplayName("A null value of a primitive field is written as the zero")
    public void writeNullToPrimitive() {
        final Parameters parameters = new Parameters();
        _PA.workingHours.setValue(parameters, null);
        _PA.windowSizeRestoration.setValue(parameters, null);

        assertEquals(0f, parameters.getWorkingHours());
        assertFalse(parameters.isWindowSizeRestoration());
    }

    @Test
    @DisplayName("A default value is compared by the equals() method")
    public void isDefault() {
        final Parameters parameters = new Parameters();
        assertTrue(Keys.isDefault(_PA.language, parameters));
        assertTrue(Keys.isDefault(_PA.dataFilePath, parameters));

        // A new instance of the same path is still the default one:
        parameters.setDataFilePath(new File(parameters.getDataFilePath().getPath()));
        assertTrue(Keys.isDefault(_PA.dataFilePath, parameters));

        parameters.setLanguage(Locale.FRENCH.equals(Locale.getDefault()) ? Locale.GERMAN : Locale.FRENCH);
        assertFalse(Keys.isDefault(_PA.language, parameters));
    }

    @Test
    @DisplayName("A type of the list item is resolved by the generic type of the field")
    public void itemTypeOf() {
        assertSame(WorkDay.class, Keys.itemTypeOf(_WS.days));
        assertThrows(IllegalStateException.class, () -> Keys.itemTypeOf(_WS.username));
    }
}
