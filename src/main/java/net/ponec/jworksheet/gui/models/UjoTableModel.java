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
import java.util.Comparator;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import net.ponec.jworksheet.core.ujo.DomainTools;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.UjoComparator;

/**
 * A table model of the domain objects where a column is described by a {@link JKey}.
 * The class replaces the {@code org.ujorm.swing.UjoTableModel} of the original
 * Ujorm implementation.
 *
 * @param <ROW> a type of the table row
 * @author Pavel Ponec
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class UjoTableModel<ROW> extends AbstractTableModel {

    /** An identifier of the event which repaints the table header. */
    public static final int EVENT_REPAINT_HEADER = 1000;

    /** Columns of the table. */
    protected JKey[] columns;

    /** Rows of the table. */
    protected List<ROW> rows;

    /** Creates a new instance for the required columns. */
    public UjoTableModel(final JKey... columns) {
        this.columns = columns;
        initData();
    }

    /** Creates a new instance for all the keys of the domain class. */
    public UjoTableModel(final Class<ROW> domainClass) {
        this(JKey.keysOf(domainClass).toArray(new JKey[0]));
    }

    /** An Initialization */
    protected void initData() {
        rows = new ArrayList<>();
    }

    /** Assign a table rows */
    public void setRows(final List<ROW> rows) {
        if (rows == null) {
            throw new IllegalArgumentException("rows are required");
        }
        this.rows = rows;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /** Convert a column index to a key. */
    public JKey getColumn(final int columnIndex) {
        return columns[columnIndex];
    }

    /** Set columns into table */
    public void setColumns(final JKey... columns) {
        this.columns = columns;
        fireTableStructureChanged();
    }

    // ------------------------ THE INDEXED API OF THE SWING ------------------------

    @Override
    public final Object getValueAt(final int rowIndex, final int columnIndex) {
        return getValueAt(rowIndex, columns[columnIndex]);
    }

    @Override
    public final void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        setValueAt(value, rowIndex, columns[columnIndex]);
    }

    @Override
    public final String getColumnName(final int columnIndex) {
        return getColumnName(columns[columnIndex]);
    }

    @Override
    public final boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return isCellEditable(rowIndex, columns[columnIndex]);
    }

    @Override
    public final Class getColumnClass(final int columnIndex) {
        return getColumnClass(columns[columnIndex]);
    }

    // ------------------------ THE KEY API ------------------------

    /** Returns a row of the index. The method can throw an IndexOutOfBoundsException. */
    public ROW getRow(final int rowIndex) throws IndexOutOfBoundsException {
        return rows.get(rowIndex);
    }

    /** Returns a row of the index, or null if the index is out of the range. */
    public ROW getRowNullable(final int rowIndex) {
        return rowIndex >= 0 && rowIndex < rows.size() ? rows.get(rowIndex) : null;
    }

    /** Returns the last row of the model, or null if the model has got no rows. */
    public ROW getRowLast() {
        return getRowNullable(rows.size() - 1);
    }

    /** Returns a value from the cell. */
    public Object getValueAt(final int rowIndex, final JKey column) {
        return column.getValue(rows.get(rowIndex));
    }

    /** Set a value to a cell of table model. */
    public void setValueAt(final Object value, final int rowIndex, final JKey column) {
        column.setValue(getRow(rowIndex), value);
        fireTableCellUpdated(rowIndex, getColumnIndex(column));
    }

    /** Column Name */
    public String getColumnName(final JKey column) {
        return column.getName();
    }

    /** Is the cell editable? */
    public boolean isCellEditable(final int rowIndex, final JKey column) {
        return true;
    }

    /** Returns the most specific superclass for all the cell values in the column. */
    public Class getColumnClass(final JKey column) {
        return column.getType();
    }

    /** Returns a table column index. */
    public int getColumnIndex(final JKey column) {
        for (int i = columns.length - 1; i >= 0; i--) {
            if (columns[i] == column) {
                return i;
            }
        }
        throw new IllegalArgumentException("Bad column: " + column);
    }

    /** Returns a table row index or -1 if the row was not found. */
    public int getRowIndex(final ROW row) {
        for (int i = 0, max = rows.size(); i < max; i++) {
            if (rows.get(i) == row) {
                return i;
            }
        }
        return -1;
    }

    /** Delete row. */
    public ROW deleteRow(final int rowIndex) {
        final ROW result = rows.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
        return result;
    }

    /** Add a row to the end of the model. */
    public void addRow(final ROW row) {
        final int lastRow = rows.size();
        rows.add(row);
        fireTableRowsInserted(lastRow, lastRow);
    }

    /** Append a copy of the row to the end of the model. */
    public ROW cloneRow(final int rowIndex, final int depth) {
        final ROW result = DomainTools.deepCopy(getRow(rowIndex), depth);
        addRow(result);
        return result;
    }

    /** Repaint header */
    public void fireTableHeaderRepainted() {
        fireTableChanged(new TableModelEvent
                ( this
                , 0
                , 0
                , TableModelEvent.ALL_COLUMNS
                , EVENT_REPAINT_HEADER
                ));
    }

    // ------------------------ UTILS ------------------------

    /** Sort data by a key list. A key value must be comparable. */
    public void sort(final JKey... keys) {
        sort(UjoComparator.<ROW>newInstance(keys));
    }

    /** Sort data by a Comparator object. */
    public void sort(final Comparator<ROW> comparator) {
        rows.sort(comparator);
        fireTableAllRowUpdated();
    }

    /** Fire a sign, that all rows were updated. */
    public void fireTableAllRowUpdated() {
        fireTableRowsUpdated(0, getRowCount() - 1);
    }

    /** Fire a sign, that the column was updated. */
    public void fireTableColumnUpdated(final JKey column) {
        final int columnIndex = getColumnIndex(column);
        for (int i = getRowCount() - 1; i >= 0; i--) {
            fireTableCellUpdated(i, columnIndex);
        }
    }
}
