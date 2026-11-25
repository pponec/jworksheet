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


import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.core.ujo.DomainTools;
import org.ujorm.core.Key;
import net.ponec.jworksheet.core.ujo.MetaAliases.EV;

/**
 * Event Table Model
 * @author Pavel Ponec
 */
public class EventTableModel extends UjoTableModel<Event> {

    public static final int ACTION_SELECT_EVENT = 1001;

    
    /** Application workSpace */
    protected ApplContext applContext;

    /** Is changed a time of the current day? */
    private boolean timeChange;

    /**
     * Creates a new instance of UjoTableModel
     * @param applContext Application Context
     */
    public EventTableModel(ApplContext applContext) {
        super
        ( EV.time
        , EV.period
        , EV.project
        , EV.task
        , EV.descr
        );
        this.applContext = applContext;
    }
    
    /** Is the cell editable? */
    @Override
    public boolean isCellEditable(int rowIndex, Key column) {
        if (EV.period==column) {
            return rowIndex==(getRowCount()-1);
        } else {
            return applContext.getParameters().isModifyFinishedProject()
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
            lastRow.setTimeFinished(row.getTime());
        }
        super.addRow(row);
    }
    
    /** Insert a row to End of model and set a Default project */
    public void insertRowAndDefault(Event row) {
        
        // Set Default values:
        Project defaultProj = applContext.getWorkSpace().findDefaultProject();
        if (defaultProj!=null) {
            row.setProject(defaultProj);
            TaskType defaultTask = defaultProj.findDefaultTask();
            if (defaultTask!=null) {
                row.setTask(defaultTask);
            }
        }
        insertRow(row);
        
    }
    
    /** Set a time of the last period. */
    public void setTimeOfLastPeriod() {
        if (getRowCount()>0 && applContext.isToday()) {
            final Event lastRow = getRowLast();
            lastRow.setTimeFinished(Time.now());
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
        && (requPeriod = insertedRow.getPeriod()) > 0
        && (index = getRowIndex(insertedRow)) > 0
        && (index < getRowCount()-1)
        ){
            final Time time1 = insertedRow.getTime();
            final Time time2 = getRow(index+1).getTime();
            int realPeriod = time2.substract(time1);
            if (realPeriod > requPeriod) {
                Event nextEvent = DomainTools.deepCopy(getRow(index-1), 2);
                nextEvent.setTime(time1.cloneAdd(requPeriod));
                rows.add(index+1, nextEvent);
            }
        }
    }
    
    /** Sort table and recalculate periods. */
    public void sort(boolean enableInsertAction) {

        timeChange = false;
        Event lastRow = getRowLast();
        super.sort(EV.time);
        
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
                lastEvent.setTimeFinished(newEvent.getTime());
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
                time += event.getPeriod();
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
        return EV.period == column
                ? Object.class
                : super.getColumnClass(column);
    }

    /** Set value to cell. */
    @Override
    public void setValueAt(Object value, int rowIndex, Key column) {
        
        // Convert a decimal value to minutes:
        if (EV.period==column) {
            String data = String.valueOf(value);
            
            if (data.indexOf(':')>=0) {
                // A format of the time: 1:30
                value = (short) new Time(data).getMinutes();
            } else {
                if (data.indexOf(',')>=0) {
                    data = data.replace(',', '.');
                }
                final float f = Float.parseFloat(data);
                final boolean deciFormat = data.indexOf('.')>=0;
                value = (short) Math.round(f * (deciFormat?60:1));
            }
            
            // Test:
            getRow(rowIndex).getTime().cloneAdd((Short) value);
        }
        else if (EV.time==column
        ||       EV.project==column // a case PRIVATE state change
        ){
            // the time change listener
            timeChange=true;
        }
        
        if (column==EV.project
        &&  value!=null
        &&  value!=getValueAt(rowIndex, column)
        ){
            TaskType taskType = ((Project) value).findDefaultTask();
            super.setValueAt(taskType, rowIndex, EV.task);
        }
        super.setValueAt(value, rowIndex, column);

        if (timeChange 
        && applContext.getParameters().isAutomaticSortingByTime()) {
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
