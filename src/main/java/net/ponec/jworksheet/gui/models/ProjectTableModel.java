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


import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.core.ApplContext;
import org.ujorm.core.Key;
import net.ponec.jworksheet.core.ujo.MetaAliases.PR;

/**
 * Project Table Model
 * @author Pavel Ponec
 */
public class ProjectTableModel extends UjoTableModel<Project> {

    /** Table columns. */
    private static final Key[] TABLE_COLUMNS =
    { PR.id
    , PR.defaultProject
    , PR.finished
    , PR.privateProject
    , PR.descr
    };
    

    /** Application context */
    private final ApplContext applContext;
    
    /**
     * Creates a new instance of UjoTableModel
     */
    public ProjectTableModel(final ApplContext applContext) {
        super(TABLE_COLUMNS);
        this.applContext = applContext;
    }

    /** Returns a localized Column Name */
    @Override
    public String getColumnName(Key property) {
        return applContext.getLanguageManager().getTextAllways(property);
    }

    /** Get next Project id */
    protected int nextProjectId() {
        int result = 0;
        for (Project proj : super.rows) {
            result = Math.max(result, proj.getId());
        }
        return result + 1;
    }
    
    @Override
    public void addRow(Project proj) {
        proj.setId(nextProjectId());
        super.addRow(proj);
    }
    
    /** Set value to cell. */
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int rowIndex, Key column) {
        if (column==PR.defaultProject && (Boolean) value) {
            Project selected = getRow(rowIndex);
            for (Project proj : rows) {
                column.setValue(proj, Boolean.valueOf(proj==selected));
            }
            super.setValueAt(Boolean.FALSE, rowIndex, PR.finished);
            super.fireTableColumnUpdated(column);
            return;
        }
        if (column==PR.finished && (Boolean) value) {
            super.setValueAt(Boolean.FALSE, rowIndex, PR.defaultProject);
        }
        
        super.setValueAt(value, rowIndex, column);
    }    
}
