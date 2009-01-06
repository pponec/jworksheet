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

import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Parameters;
import org.ujoframework.UjoProperty;
import org.ujoframework.swing.UjoPropertyRow;
import org.ujoframework.swing.SingleUjoTabModel;

/**
 * Row of Parameters table.
 * @author Pavel Ponec
 */
public class ParamTableModel extends SingleUjoTabModel {
    
    /** Properties */
    public final static UjoPropertyRow PROPS = null;
    
    private ApplContext applContext;
    
    /** Creates a new instance of ParamTableModel */
    @SuppressWarnings("static-access")
    public ParamTableModel(Parameters params, ApplContext applContext) {
        super
        ( params
        , PROPS.P_NAME
        , PROPS.P_CLASSNAME
        , PROPS.P_VALUE
        //, PROPS.P_TEXT
        , PROPS.P_DEFAULT
        );
        this.applContext = applContext;
    }
    
    /** Set a value to a cell of table model. */
    @Override
    public void setValueAt(Object value, int rowIndex, UjoProperty column) {
        super.setValueAt(value, rowIndex, column);
        
        UjoPropertyRow row = getRow(rowIndex);
        if (Parameters.P_COLOR_EDITABLE==row.getProperty()) {
            // Repaint color background;
            fireTableColumnUpdated(column);
        } else if (Parameters.P_HIDE_ICONS==row.getProperty()) {
            applContext.getTopFrame().initIcons();
        }
    }
}
