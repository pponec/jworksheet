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

import net.ponec.jworksheet.gui.models.meta.MetaUjoKeyRow;

import net.ponec.jworksheet.bo.meta.MetaParameters;

import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ujo.Keys;
import org.ujorm.core.Key;

/**
 * Row of Parameters table.
 * @author Pavel Ponec
 */
public class ParamTableModel extends SingleUjoTabModel {


    private ApplContext applContext;

    /** Creates a new instance of ParamTableModel */
    public ParamTableModel(Parameters params, ApplContext applContext) {
        super
        ( params
        , MetaUjoKeyRow.index
        , MetaUjoKeyRow.name
        , MetaUjoKeyRow.typeName
        , MetaUjoKeyRow.value
        //, MetaUjoKeyRow.value
        , MetaUjoKeyRow.defaultValue
        );
        this.applContext = applContext;
    }

    /** Returns a localized Column Name */
    @Override
    public String getColumnName(Key property) {
        return applContext.getLanguageManager().getTextAllways(property);
    }

    /** Set a value to a cell of table model. */
    @Override
    public void setValueAt(Object value, int rowIndex, Key column) {
        super.setValueAt(value, rowIndex, column);

        final UjoKeyRow row = getRow(rowIndex);
        if (MetaParameters.colorOfEditableArea==row.getKey()) {
            // Repaint color background;
            fireTableColumnUpdated(column);
        } else if (MetaParameters.hideButtonIcons==row.getKey()
        ||         MetaParameters.automaticSortingByTime==row.getKey()) {
            applContext.getTopFrame().initIcons();
        }
    }
}
