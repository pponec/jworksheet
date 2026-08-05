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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.ValueCoder;

/**
 * A table model which presents the keys of a single domain object, one key per a row.
 * The class replaces the {@code org.ujorm.swing.SingleUjoTabModel} of the original
 * Ujorm implementation.
 * <p>The value column is presented and edited as a text.
 *
 * @author Pavel Ponec
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SingleUjoTabModel extends UjoTableModel<UjoKeyRow> {

    /** Creates a model of all the visible keys of the domain object. */
    public SingleUjoTabModel(final Object content) {
        this(content, JKey.keysOf(UjoKeyRow.class).toArray(new JKey[0]));
    }

    /** Creates a model of all the visible keys of the domain object. */
    public SingleUjoTabModel(final Object content, final JKey... columns) {
        super(columns);
        rows = createKeyRows(content);
    }

    /** Creates a row per a key which the domain object allows to show. */
    private static List<UjoKeyRow> createKeyRows(final Object content) {
        final Exportable filter = content instanceof final Exportable e ? e : null;
        final List<UjoKeyRow> result = new ArrayList<>();

        for (JKey key : JKey.keysOf(content.getClass())) {
            if (filter == null
            ||  filter.isExportable(ExportAction.TABLE_SHOW, key, key.getValue(content))) {
                result.add(new UjoKeyRow(content, key));
            }
        }
        return result;
    }

    /** Only the value is editable. */
    @Override
    public boolean isCellEditable(final int rowIndex, final JKey column) {
        return column == UjoKeyRow.P_VALUE;
    }

    /** Set a value to a cell of table model. A text is decoded to the type of the presented key. */
    @Override
    public void setValueAt(final Object value, final int rowIndex, final JKey column) {
        if (column == UjoKeyRow.P_VALUE && value instanceof final String text) {
            final UjoKeyRow row = getRow(rowIndex);
            row.setValue(ValueCoder.decode(row.getKey().getType(), text));
            fireTableCellUpdated(rowIndex, getColumnIndex(column));
        } else {
            super.setValueAt(value, rowIndex, column);
        }
    }

    /** The value and the default value are presented in a text format. */
    @Override
    public Object getValueAt(final int rowIndex, final JKey column) {
        if (column == UjoKeyRow.P_VALUE || column == UjoKeyRow.P_DEFAULT) {
            return ValueCoder.encode(super.getValueAt(rowIndex, column));
        }
        return super.getValueAt(rowIndex, column);
    }

    /** Returns an iterator of the rows. */
    public Iterator<UjoKeyRow> iterator() {
        return rows.iterator();
    }
}
