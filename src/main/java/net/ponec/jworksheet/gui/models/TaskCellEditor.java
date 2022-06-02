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

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import org.ujorm.swing.UjoTableModel;

/**
 * A JComboBox cell editor
 * @author Pavel Ponec
 */
public class TaskCellEditor extends DefaultCellEditor implements TableCellEditor  {

    private JComboBox comboBox;

    /** Creates a new instance of TaskCellEditor */
    public TaskCellEditor() {
        super(new JComboBox());
        comboBox = (JComboBox) super.getComponent();
    }

    /** Set Model of ComboBox */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        UjoTableModel tableModel = (UjoTableModel) table.getModel();
        final Event event = (Event) tableModel.getRow(row);
        final Project project = event.get(Event.P_PROJ);
        final ArrayList<TaskType> tasks = project!=null ? project.getOpenTasks() : null;

        if (tasks!=null && tasks.size()>0) {
            comboBox.setModel(new UjoComboBoxModel(tasks));
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        } else {
            comboBox.setModel(new UjoComboBoxModel());
            return new JLabel("<no option>");
        }
    }

}
