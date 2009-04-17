/**
 * Copyright (C) 2007-8, Paul Ponec, contact: http://ponec.net/
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


package net.ponec.jworksheet.gui.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.gui.models.EventTableModel;
import net.ponec.jworksheet.resources.ResourceProvider;
import org.ujoframework.Ujo;
import org.ujoframework.UjoProperty;
import org.ujoframework.swing.UjoTableModel;

/**
 * UjoTable for an Ujo objects.
 * @author Pavel Ponec
 */
public class UjoTable extends JTable implements TableCellRenderer {
    
    private TableCellRenderer superRenderer = null;
    private ImageIcon sortIcon = null;
    private UjoProperty sortedColumn = null;
    
    /** Creates a new instance of UjoTable */
    public UjoTable() {
        init();
    }
    
    /** A basic initialization. */
    protected void init() {
    }
    
    /** Converts a view column index to UjoProperty. */
    public UjoProperty convertColumnIndexToProperty(int viewColumnIndex) {
        final int modelColumnIndex = convertColumnIndexToModel(viewColumnIndex);
        final UjoProperty result = getModel().getColumn(modelColumnIndex);
        return result;
    }
    
    /**
     * Return an Ujo column of table model.
     */
    public UjoProperty getColumn(int tableColumnIndex) {
        final UjoProperty result = getModel().getColumn(convertColumnIndexToModel(tableColumnIndex));
        return result;
    }
    
    /** Return a TableColumn */
    public TableColumn getTableColumn(UjoProperty column) {
        final int index = getModel().getColumnIndex(column);
        final TableColumn result = getColumnModel().getColumn(index);
        return result;
    }
    
    /** Select a row. If row is out of range, the row can be corrected. */
    public void selectRow(int rowIndex) {
        int rowCount = getModel().getRowCount();
        // Select row:
        if (rowIndex>=rowCount) {
            rowIndex = rowCount - 1;
        }
        if (rowIndex>=0) {
            setRowSelectionInterval(rowIndex, rowIndex);
            scrollRectToVisible(getCellRect(rowIndex, 0, true));
        }
    }
    
    /**
     * Select an Ujo
     */
    public int selectRow(Ujo row) {
        UjoTableModel model = getModel();
        if (row!=null) for (int i=0; i<model.getRowCount(); i++) {
            if (row==model.getRow(i)) {
                selectRow(i);
                return i;
            }
        }
        clearSelection();
        return -1;
    }
    
    /** Enable an edit mode by ENTER key. */
    public void enableEnterEditMode() {
        final String insertAction = "InsetrAction";
        getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), insertAction);
        getActionMap().put(insertAction, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                final int row    = getSelectedRow();
                final int column = getSelectedColumn();
                if ( editCellAt(row, column, e) ) {
                    getEditorComponent().requestFocusInWindow();
                }
            }
        });
    }
    
    /** Assign a renderer to all columns. */
    public void setRenderer(TableCellRenderer renderer) {
        TableColumnModel columns = getColumnModel();
        for (int i=getColumnCount()-1; i>=0; i--) {
            columns.getColumn(i).setCellRenderer(renderer);
        }
    }
    
    /** Returns a model in a String format. */
    public String getStringModel() {
        StringBuilder result = new StringBuilder(256);
        List<List<String>> data = getStringModelArray(false);
        
        for (List<String> row : data) {
            if (result.length()>0) { result.append('\n'); }
            for (String cell : row) {
                result.append(cell);
                result.append('\t');
                System.err.println("cell:"  + cell);
            }
        }
        return result.toString();
    }
    
    /** Returns a model in an array String format. */
    public List<List<String>> getStringModelArray(boolean header) {
        List<List<String>> result = new ArrayList<List<String>>(getRowCount());
        List<String> arrayRow;
        
        UjoTableModel model = getModel();
        
        // If Header
        if (header && getTableHeader()!=null) {
            arrayRow = new ArrayList<String>();
            result.add(arrayRow);
            for (int col=0; col<model.getColumnCount(); col++) {
                UjoProperty column = getColumn(col);
                TableCellRenderer renderer = getTableColumn(column).getHeaderRenderer();
                String cell = model.getColumnName(column);
                if (renderer!=null) {
                    Component comp = renderer.getTableCellRendererComponent(this, cell, false, false, -1, col);
                    if (comp instanceof JLabel) {
                        cell = ((JLabel)comp).getText();
                    }
                }
                arrayRow.add(cell);
            }
        }
        
        for (int row=0; row<model.getRowCount(); row++) {
            arrayRow = new ArrayList<String>();
            result.add(arrayRow);
            for (int col=0; col<model.getColumnCount(); col++) {
                UjoProperty column = getColumn(col);
                TableCellRenderer renderer = getTableColumn(column).getCellRenderer();
                Object value = model.getValueAt(row, column);
                String cell = String.valueOf(value);
                if (renderer!=null) {
                    Component comp = renderer.getTableCellRendererComponent(this, value, false, false, row, col);
                    if (comp instanceof JLabel) {
                        cell = ((JLabel)comp).getText();
                    }
                }
                arrayRow.add(cell);
            }
        }
        return result;
    }
    
    
    /**
     * Submit/Leave an Edit Mode (if any).
     * @param submit true - submit / false = leave
     */
    public void submitEditMode(boolean submit) {
        Component component = getEditorComponent();
        if (component!=null) {
            if (submit
            && getRowCount()>0
            && getColumnCount()>0
            ){
                editCellAt(0,0);
            }
            editingCanceled(new ChangeEvent(this));
        }
    }
    
    /** Assing an UjoTableModel only. */
    @Override
    public void setModel(TableModel dataModel) {
        if (dataModel instanceof UjoTableModel) {
            super.setModel(dataModel);
            // Add A Model Change Listener.
            dataModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (e.getColumn()==TableModelEvent.ALL_COLUMNS){
                        submitEditMode(e.getType()!=TableModelEvent.DELETE);
                    }
                }
            });
        }
    }
    
    /** Returns an Ujo model. */
    @Override
    public UjoTableModel getModel() {
        return (UjoTableModel) super.getModel();
    }
    
    // -------------------- SORTING --------------------------------------
    
    /** Set a sorter for change columns order. */
    public void enableSorting(/*cz.ponec.tools.gui.ISorterGet aSorter*/ ApplContext context) {
        // setColumnSelectionAllowed(false);
        if (superRenderer!=null) { return; }
        
        // New Header renderer:
        this.superRenderer = getTableHeader().getDefaultRenderer();
        this.sortIcon = new ResourceProvider().getIcon(ResourceProvider.SORT);
        getTableHeader().setDefaultRenderer(this);
        getTableHeader().setToolTipText(context.getLanguageManager().getText("SortedTableColumn.TIP"));
        sortedColumn = getModel().getColumn(0);
        
        // Add MouseListener;
        getTableHeader().addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final int viewColumn  = getColumnModel().getColumnIndexAtX(e.getX());
                final int modelColumn = convertColumnIndexToModel(viewColumn);
                sortedColumn = getModel().getColumn(modelColumn);
                
                if (e.getClickCount()==1
                &&  e.getButton()==MouseEvent.BUTTON1
                &&  UjoTable.this.isEnabled()
                ){
                    int row = getSelectedRow();
                    Ujo selected = getRowSelectionAllowed() && row>=0
                    ? getModel().getRow(row) : null ;
                    
                    getModel().sort(sortedColumn);
                    
                    if (selected!=null) {   // Restore selection:                        
                        selectRow(selected);
                    }
                    getTableHeader().repaint();
                }
            }
        });
    }    
    
    /** Render table header  */
    public Component getTableCellRendererComponent
    ( JTable table
    , Object value
    , boolean isSelected
    , boolean hasFocus
    , int row
    , int column
    ) {
        
        Component result = superRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (result instanceof JLabel) {
            final JLabel label = (JLabel) result;
            label.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
            
            final int columnModel = table.convertColumnIndexToModel(column);
            final UjoProperty colProperty = ((UjoTable)table).getModel().getColumn(columnModel);
            label.setIcon(colProperty==sortedColumn ? sortIcon : null );
        }

        return result;
    }
    
    /** Show selected column */
    public void showSortedColumn(final UjoProperty property) {
        sortedColumn = property;
        getTableHeader().repaint();
    } 

    /** Show selected column */
    public void showSortedColumn(final String property) {
        final UjoProperty pro = getColumn(Math.max(0, getModel().findColumn(property)));
        showSortedColumn(pro);
    }

    /** Get Sorted Column */
    public UjoProperty getSortedColumn() {
        return sortedColumn;
    }

    /** Table change listener */
    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType()==EventTableModel.ACTION_SELECT_EVENT ) {
            selectRow(e.getFirstRow());
        } else {
            super.tableChanged(e);
        }
    }

}
