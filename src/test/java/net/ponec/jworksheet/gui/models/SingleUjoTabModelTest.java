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

import java.util.Locale;
import net.ponec.jworksheet.bo.Parameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import net.ponec.jworksheet.core.ujo.MetaAliases._PA;
import net.ponec.jworksheet.core.ujo.MetaAliases._UR;

/** The parameter table edits a value by its text representation.
 * @author Pavel Ponec
 */
public class SingleUjoTabModelTest {

    /** Returns an index of the row of the parameter. */
    private static int rowOf(final SingleUjoTabModel model, final Object key) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getRow(i).getKey() == key) {
                return i;
            }
        }
        throw new IllegalArgumentException("No row: " + key);
    }

    @Test
    @DisplayName("A value is presented and edited as a text")
    public void textValue() {
        final Parameters parameters = new Parameters();
        final SingleUjoTabModel model = new SingleUjoTabModel(parameters);

        final int hours = rowOf(model, _PA.workingHours);
        model.setValueAt("7.5", hours, _UR.value);
        assertEquals(7.5f, parameters.getWorkingHours());
        assertEquals("7.5", model.getValueAt(hours, _UR.value));

        final int language = rowOf(model, _PA.language);
        model.setValueAt("cs-CZ", language, _UR.value);
        assertEquals(new Locale("cs", "CZ"), parameters.getLanguage());
    }

    @Test
    @DisplayName("The hidden parameters have got no row")
    public void hiddenParameters() {
        final SingleUjoTabModel model = new SingleUjoTabModel(new Parameters());
        assertThrows(IllegalArgumentException.class, () -> rowOf(model, _PA.windowSize));
        assertThrows(IllegalArgumentException.class, () -> rowOf(model, _PA.sortProjColumn));
    }
}
