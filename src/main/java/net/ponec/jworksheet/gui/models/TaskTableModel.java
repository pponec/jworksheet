/**
  * Copyright (C) 2007-2022, Pavel Ponec, contact: http://ponec.net/
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

import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.core.ApplContext;
import org.ujorm.Key;
import org.ujorm.swing.UjoTableModel;



/**
 * Task Table Model
 * @author Pavel Ponec
 */
public class TaskTableModel extends UjoTableModel<TaskType> {

    /** Properties */
    public final static TaskType PROPS = null;

    /** Application context */
    private final ApplContext applContext;

    /** Creates a new instance of TaskTableModel */
    public TaskTableModel(final ApplContext applContext) {
        super( TaskType.class );
        this.applContext = applContext;
    }

    /** Returns a localized Column Name */
    @Override
    public String getColumnName(Key property) {
        return applContext.getLanguageManager().getTextAllways(property);
    }

    /** Get next Task id */
    protected Integer nextTaskId() {
        Integer result = 0;
        for (TaskType task : super.rows) {
            Integer id = task.get(TaskType.P_ID);
            if (result.compareTo(id)<0) {
                result = id;
            }
        }
        return ++result ;
    }

    @Override
    public void addRow(TaskType row) {
        TaskType.P_ID.setValue(row, nextTaskId());
        super.addRow(row);
    }

    /** Set value to cell. */
    @Override
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int rowIndex, Key column) {
        if (column==PROPS.P_DEFAULT && (Boolean) value) {
            TaskType selected = getRow(rowIndex);
            for (TaskType task : rows) {
                column.setValue(task, Boolean.valueOf(task==selected));
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
