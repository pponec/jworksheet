/**
  * Copyright (C) 2007-2021, Pavel Ponec, contact: http://ponec.net/
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

import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.item.Time;
import org.ujorm.Key;
import org.ujorm.swing.UjoTableModel;

/**
 * Event Table Model
 * @author Pavel Ponec
 */
public class EventTableModel extends UjoTableModel<Event> {

    public static final int ACTION_SELECT_EVENT = 1001;

    /** Properties */
    public final static Event PROPS = null;
    
    /** Application workSpace */
    protected ApplContext applContext;

    /** Is changed a time of the current day? */
    private boolean timeChange;

    /**
     * Creates a new instance of UjoTableModel
     * @param applContext Application Context
     */
    @SuppressWarnings("static-access")
    public EventTableModel(ApplContext applContext) {
        super
        ( PROPS.P_TIME
        , PROPS.P_PERIOD
        , PROPS.P_PROJ
        , PROPS.P_TASK
        , PROPS.P_DESCR
        );
        this.applContext = applContext;
    }
    
    /** Is the cell editable? */
    @Override
    public boolean isCellEditable(int rowIndex, Key column) {
        if (Event.P_PERIOD==column) {
            return rowIndex==(getRowCount()-1);
        } else {
            return applContext.getParameters().get(Parameters.P_MODIFY_FINESHED_PROJ)
            ||   ! getRow(rowIndex).isFinished(column)
            ;
        }
    }

    /** Returns a localized Column Name */
    @Override
    public String getColumnName(Key property) {
        return applContext.getLanguageManager().getTextAllways(property);
    }
    
    /** Insert a row to End of model. */
    public void insertRow(Event row) {
        Event lastRow = getRowLast();
        if (lastRow!=null) {
            // Set the last period:
            lastRow.setPeriod(row.get(Event.P_TIME));
        }
        super.addRow(row);
    }
    
    /** Insert a row to End of model and set a Default project */
    public void insertRowAndDefault(Event row) {
        
        // Set Default values:
        Project defaultProj = applContext.getWorkSpace().findDefaultProject();
        if (defaultProj!=null) {
            Event.P_PROJ.setValue(row, defaultProj);
            TaskType defaultTask = defaultProj.findDefaultTask();
            if (defaultTask!=null) {
                Event.P_TASK.setValue(row, defaultTask);
            }
        }
        insertRow(row);
        
    }
    
    /** Set a time of the last period. */
    public void setTimeOfLastPeriod() {
        if (getRowCount()>0 && applContext.isToday()) {
            final Event lastRow = getRowLast();
            lastRow.setPeriod(new Time(true));
        }
    }
    
    /**
     * Splitting of Interval is supported.
     * Set a time of the last realPeriod.
     * @param insertedRow Send inserted file
     */
    protected void performAfterInsertAction(Event insertedRow) {
        int index;      // row index
        int requPeriod; // Required period
        
        if (insertedRow!=null
        && (requPeriod = insertedRow.get(Event.P_PERIOD)) > 0
        && (index = getRowIndex(insertedRow)) > 0
        && (index < getRowCount()-1)
        ){
            final Time time1 = insertedRow.get(Event.P_TIME);
            final Time time2 = getRow(index+1).get(Event.P_TIME);
            int realPeriod = time2.substract(time1);
            if (realPeriod > requPeriod) {
                Event nextEvent = (Event) getRow(index-1).clone(2, this);
                Event.P_TIME.setValue(nextEvent, time1.cloneAdd(requPeriod));
                rows.add(index+1, nextEvent);
            }
        }
    }
    
    /** Sort table and recalculate periods. */
    public void sort(boolean enableInsertAction) {

        timeChange = false;
        Event lastRow = getRowLast();
        super.sort(Event.P_TIME);
        
        if (enableInsertAction) {
            performAfterInsertAction(lastRow);
        }
        
        // Set Time the last Period:
        setTimeOfLastPeriod();
        
        // Recalculate times of all periods:
        Event lastEvent = null;
        for (int i=0; i<getRowCount(); i++) {
            Event newEvent = getRow(i);
            if (lastEvent!=null) {
                lastEvent.setPeriod(newEvent.get(Event.P_TIME));
            }
            lastEvent = newEvent;
        }
        fireTableDataChanged();
    }
    
    /** Returns a total time */
    public int getTotalTime() {
        int time = 0 ;
        for (Event event : rows) {
            if (!event.isPrivate()) {
                time += event.get(Event.P_PERIOD);
            }
        }
        return time;
    }
    
    /** Returns a total time */
    public String getTotalTimeHours() {
        final String result = applContext.getParameters().formatTime(getTotalTime());
        return result;
    }
    
    
    @Override
    public Class getColumnClass(Key column) {
        @SuppressWarnings("static-access")
        final Class result
        = PROPS.P_PERIOD==column
        ? Object.class
        : super.getColumnClass(column)
        ;
        return result;
    }
    
    /** Set value to cell. */
    @Override
    @SuppressWarnings("static-access")
    public void setValueAt(Object value, int rowIndex, Key column) {
        
        // Convert a decimal value to minutes:
        if (PROPS.P_PERIOD==column) {
            String data = String.valueOf(value);
            
            final int timeSeparator = data.indexOf(':');
            if (timeSeparator>=0) {
                int mins =   Integer.parseInt(data.substring(timeSeparator+1)); 
                mins += 60 * Integer.parseInt(data.substring(0, timeSeparator)); 
                value = new Short((short) mins);
            } else {
                if (data.indexOf(',')>=0) {
                    data = data.replace(',', '.');
                }
                final float f = Float.parseFloat(data);
                final boolean deciFormat = data.indexOf('.')>=0;
                value = new Short((short)Math.round(f * (deciFormat?60:1)));      
            }
            
            // Test:
            getRow(rowIndex).get(PROPS.P_TIME).cloneAdd((Short)value);
        }
        else if (PROPS.P_TIME==column
        ||       PROPS.P_PROJ==column // a case PRIVATE state change
        ){
            // the time change listener
            timeChange=true;
        }
        
        if (column==Event.P_PROJ
        &&  value!=null
        &&  value!=getValueAt(rowIndex, column)
        ){
            TaskType taskType = ((Project) value).findDefaultTask();
            super.setValueAt(taskType, rowIndex, Event.P_TASK);
        }
        super.setValueAt(value, rowIndex, column);

        if (timeChange 
        && applContext.getParameters().get(Parameters.P_AUTOMATIC_SORTING_BY_TIME)) {
            final Event e = getRow(rowIndex);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Thread.yield();
                    sort(true);
                    fireTableChanged(new TableModelEvent
                        ( EventTableModel.this
                        , getRowIndex(e)
                        , 0
                        , 0
                        , ACTION_SELECT_EVENT)
                        );
                }
            });
        }
    }

    @Override
    public void setRows(List<Event> events) {
        if (timeChange && super.rows!=null) {
            sort(false);
        }
        super.setRows(events);
    }
}
