/*
 * TextCellEditor.java
 *
 * Created on 17. bĹ™ezen 2008, 19:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.ponec.jworksheet.gui.models;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Special text cell editor.
 * @author Pavel Ponec
 */
public class TextCellEditor extends DefaultCellEditor {

    /** Basic constructor */
    public TextCellEditor() {
        super(new JTextField());
    }

    /** Alternative constructor */
    public TextCellEditor(JTextField textField) {
        super(textField);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        final Component result = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (result instanceof JTextField) {
            ((JTextField) result).selectAll();
        }
        return result;
    }



}
