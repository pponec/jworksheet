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
import net.ponec.jworksheet.core.ujo.MetaAliases.PA;
import net.ponec.jworksheet.core.ujo.MetaAliases.PR;
import net.ponec.jworksheet.core.ujo.MetaAliases.WS;

/** The operations above the generated keys.
 * @author Pavel Ponec
 */
public class KeysTest {

    @Test
    @DisplayName("A primitive type of the key is boxed, an object type is kept")
    public void typeOf() {
        assertSame(Integer.class, Keys.typeOf(PR.id));
        assertSame(Boolean.class, Keys.typeOf(PR.finished));
        assertSame(String.class, Keys.typeOf(PR.descr));
    }

    @Test
    @DisplayName("A null value of a primitive field is written as the zero")
    public void writeNullToPrimitive() {
        final Parameters parameters = new Parameters();
        PA.workingHours.setValue(parameters, null);
        PA.windowSizeRestoration.setValue(parameters, null);

        assertEquals(0f, parameters.getWorkingHours());
        assertFalse(parameters.isWindowSizeRestoration());
    }

    @Test
    @DisplayName("A default value is compared by the equals() method")
    public void isDefault() {
        final Parameters parameters = new Parameters();
        assertTrue(Keys.isDefault(PA.language, parameters));
        assertTrue(Keys.isDefault(PA.dataFilePath, parameters));

        // A new instance of the same path is still the default one:
        parameters.setDataFilePath(new File(parameters.getDataFilePath().getPath()));
        assertTrue(Keys.isDefault(PA.dataFilePath, parameters));

        parameters.setLanguage(Locale.FRENCH.equals(Locale.getDefault()) ? Locale.GERMAN : Locale.FRENCH);
        assertFalse(Keys.isDefault(PA.language, parameters));
    }

    @Test
    @DisplayName("A type of the list item is resolved by the generic type of the field")
    public void itemTypeOf() {
        assertSame(WorkDay.class, Keys.itemTypeOf(WS.days));
        assertThrows(IllegalStateException.class, () -> Keys.itemTypeOf(WS.username));
    }
}
