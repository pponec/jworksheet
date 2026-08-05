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

import net.ponec.jworksheet.bo.meta.MetaEvent;
import net.ponec.jworksheet.bo.meta.MetaParameters;
import net.ponec.jworksheet.bo.meta.MetaProject;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.gui.component.*;
import net.ponec.jworksheet.core.ujo.Keys;
import org.ujorm.core.Key;

/**
 * EventTableRenderer.
 * @author Pavel Ponec
 */
public class EventTableRenderer extends DefaultTableCellRenderer {
    
    /** Context */
    private final ApplContext context;
    
    protected Color colorAlt = null;
    
    final protected boolean nimbus = ApplTools.isNimbusLAF();
    
    
    /**
     * Creates a new instance of EventTableRenderer
     */
    public EventTableRenderer(ApplContext context) {
        this.context = context;
    }
    
    @Override
    public Component getTableCellRendererComponent
    ( JTable aTable
    , Object value
    , boolean isSelected
    , boolean hasFocus
    , int rowIndex
    , int viewColumn
    ){
        JLabel result = (JLabel) super.getTableCellRendererComponent
        ( aTable
        , value
        , isSelected
        , hasFocus
        , rowIndex
        , viewColumn
        );
        
        final UjoTable table = (UjoTable) aTable;
        final UjoTableModel model = table.getModel();
        Key column = table.getColumn(viewColumn);
        Event event = (Event) model.getRow(rowIndex);
        //
        boolean projOrTask = column==MetaEvent.project || column==MetaEvent.task ;
        boolean editable = (nimbus
            ? (column==MetaEvent.time || column==MetaEvent.period)
            : !projOrTask  )
            && model.isCellEditable(rowIndex, column)
            ;
        
        Project project = event.get(MetaEvent.project);
        boolean finishedProj = project!=null && projOrTask && event.isFinished(column);
        boolean privateProj  = project!=null && project.get(MetaProject.privateProject);
        
        result.setForeground
        ( isSelected   ? aTable.getSelectionForeground()
        : finishedProj ? getColorOfFihishedProject()
        : privateProj  ? getColorOfPrivateProject()
        : table.getForeground()
        );
        result.setBackground
        ( isSelected             ? aTable.getSelectionBackground()
        : (column==MetaEvent.time) ? getColorOfEditableArea(rowIndex)
        : editable               ? getColorOfEditableArea()
        : table.getBackground()
        );
        result.setHorizontalAlignment(isNumericalType(column) ? JLabel.RIGHT : JLabel.LEFT );
        
        if (column==MetaEvent.period) {
            /** Num Formatter */
            final String time = context.getParameters().formatTime((Short)value);
            result.setText(time);
        }
        
        return result;
    }
    
    /** Return true, it column is nuberical type. */
    public boolean isNumericalType(Key property) {
        if (property==MetaEvent.time  ) return true;
        if (property==MetaEvent.period) return true;
        return false;
    }
    
    /** Get Color of a private project. */
    protected Color getColorOfPrivateProject() {
        return context.getParameters().get(MetaParameters.colorOfPrivateProject);
    }
    
    /** Get a Color of an editable area. */
    protected Color getColorOfFihishedProject() {
        final Color result = context.getParameters().get(MetaParameters.colorOfFinishedProject);
        return result;
    }

    
    /** Get a Color of an editable area. */
    protected Color getColorOfEditableArea() {
        return context.getParameters().get(MetaParameters.colorOfEditableArea);
    }
    
    /** Get a Color of an editable area. */
    protected Color getColorOfEditableArea(int row) {
        
        Color result;
        if (row%2==0) {
            result = getColorOfEditableArea();
        } else {
            if (colorAlt==null) {
                colorAlt = getColorOfEditableArea();
                if (ApplTools.isNimbusLAF()) {
                    colorAlt = ApplTools.modify(colorAlt, 30);
                }
            }
            result = colorAlt;
        }
        return result;
    }    
    
}
