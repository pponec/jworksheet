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


package net.ponec.jworksheet.gui.models;

import net.ponec.jworksheet.bo.Project;
import org.ujoframework.UjoProperty;
import org.ujoframework.swing.UjoTableModel;
import org.ujoframework.core.ZeroProvider;

/**
 * Project Table Model
 * @author Pavel Ponec
 */
public class ProjectTableModel extends UjoTableModel<Project> {
    
    /** Properties */
    public final static Project PROPS = null;
    
    /**
     * Creates a new instance of UjoTableModel
     */
    public ProjectTableModel() {
        super(Project.TABLE_COLUMNS);
    }
    
    /** Get next Project id */
    protected Integer nextProjectId() {
        Integer result = ZeroProvider.ZERO_INT;
        for (Project proj : super.rows) {
            Integer id = Project.P_ID.of(proj);
            if (result.compareTo(id)<0) {
                result = id;
            }
        }
        return ++result ;
    }
    
    @Override
    public void addRow(Project proj) {
        Project.P_ID.setValue(proj, nextProjectId());
        super.addRow(proj);
    }
    
    /** Set value to cell. */
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int rowIndex, UjoProperty column) {
        if (column==PROPS.P_DEFAULT && (Boolean) value) {
            Project selected = getRow(rowIndex);
            for (Project proj : rows) {
                column.setValue(proj, Boolean.valueOf(proj==selected));
            }
            super.setValueAt(Boolean.FALSE, rowIndex, PROPS.P_FINISHED);
            super.fireTableColumnUpdated(column);
            return;
        }
        if (column==PROPS.P_FINISHED && (Boolean) value) {
            super.setValueAt(Boolean.FALSE, rowIndex, PROPS.P_DEFAULT);
        }
        
        super.setValueAt(value, rowIndex, column);
    }    
}
