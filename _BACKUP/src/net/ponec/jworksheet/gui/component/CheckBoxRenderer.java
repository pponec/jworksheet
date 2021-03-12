/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.jworksheet.gui.component;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 *  Nimbus L&F Renderer
 * @author Pavel Ponec
 */
public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		setOpaque(true);
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
		    setBackground( (row%2==1) ? Color.WHITE : table.getBackground());
        } 
               
		this.setHorizontalAlignment(SwingConstants.CENTER);
		setSelected((Boolean) value);
		return this;
	}
}

