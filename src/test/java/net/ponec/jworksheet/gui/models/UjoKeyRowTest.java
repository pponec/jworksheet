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

package net.ponec.jworksheet.gui.models;

import java.awt.Color;
import java.util.Locale;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.core.ujo.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ujorm.core.Key;

import static org.junit.jupiter.api.Assertions.*;

/** A row of the parameter table.
 * @author Pavel Ponec
 */
public class UjoKeyRowTest {

    @Test
    @DisplayName("A reset of all the rows restores the default parameters")
    public void resetValue() {
        final Parameters parameters = new Parameters();
        parameters.setWorkingHours(5f);
        parameters.setDecimalTimeFormat(true);
        parameters.setLanguage(Locale.FRENCH.equals(Locale.getDefault()) ? Locale.GERMAN : Locale.FRENCH);
        parameters.setColorOfEditableArea(Color.RED);

        for (Key<Parameters, ?> key : Keys.keysOf(Parameters.class)) {
            new UjoKeyRow(parameters, key).resetValue();
        }

        for (Key<Parameters, ?> key : Keys.keysOf(Parameters.class)) {
            assertTrue(Keys.isDefault(key, parameters), "Not a default: " + Keys.fullNameOf(key));
        }
    }
}
