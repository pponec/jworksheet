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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.core.ApplTools;

/**
 * EventTableRenderer.
 * @author Pavel Ponec
 */
public class CommonTableRenderer extends DefaultTableCellRenderer {
    
    /** Context */
    protected final ApplContext context;
    
    protected Color colorAlt = null;
    
    /**
     * Creates a new instance of EventTableRenderer
     */
    public CommonTableRenderer(ApplContext context) {
        this.context = context;
    }
    
    @Override
    public Component getTableCellRendererComponent
    ( final JTable aTable
    , final Object value
    , final boolean isSelected
    , final boolean hasFocus
    , final int rowIndex
    , final int viewColumn
    ){
        JLabel result = (JLabel) super.getTableCellRendererComponent
        ( aTable
        , value
        , isSelected
        , hasFocus
        , rowIndex
        , viewColumn
        );
        
        boolean editable = aTable.getModel().isCellEditable(rowIndex, aTable.convertColumnIndexToModel(viewColumn));
        result.setBackground(isSelected 
            ? aTable.getSelectionBackground() 
            : editable 
            ? getColorOfEditableArea(rowIndex) 
            : aTable.getBackground()
            );
        
        return result;
    }
    
    /** Get a Color of an editable area. */
    protected Color getColorOfEditableArea(int row) {
        
        Color result;
        if (row%2==0) {
            result = context.getParameters().get(Parameters.P_COLOR_EDITABLE);
        } else {
            if (colorAlt==null) {
                colorAlt = context.getParameters().get(Parameters.P_COLOR_EDITABLE);
                colorAlt = ApplTools.modify(colorAlt, 30);
            }
            result = colorAlt;
        }
        
        return result;
    }

}
