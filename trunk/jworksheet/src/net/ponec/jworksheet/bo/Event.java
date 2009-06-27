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


package net.ponec.jworksheet.bo;

import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.core.ApplTools;
import org.ujoframework.UjoProperty;
import org.ujoframework.core.UjoManager;
import org.ujoframework.core.UjoService;
import org.ujoframework.core.ZeroProvider;
import org.ujoframework.extensions.UjoAction;
import org.ujoframework.implementation.bean.*;
import static org.ujoframework.extensions.UjoAction.*;

/** An Event of the Work Day.
 * I have selected the BeanUjo implementaton for its small memory footprint, two fields are stored in a memory safe data type in addition:
 * <ul>
 * <li>time - primitive short</li>
 * <li>description - byte array in a UTF format</li>
 * <ul>
 * @composed 1 - * Project
 * @composed 1 - * TaskType
 */
public class Event extends BeanUjo {
    
    /** Start time of event */
    public static final UjoProperty<Event,Time>     P_TIME   = newProperty("Time"     , Time.class);
    /** Period of the event in minutes */
    public static final UjoProperty<Event,Short>    P_PERIOD = newProperty("Period"   , ZeroProvider.ZERO_SHORT); // [min]
    /** Project (object) */
    public static final UjoProperty<Event,Project>  P_PROJ   = newProperty("Project"  , Project.class);
    /** Project ID for the persistence */
    public static final UjoProperty<Event,Integer>  P_PROJID = newProperty("ProjectID", Integer.class);
    /** Task (object) */
    public static final UjoProperty<Event,TaskType> P_TASK   = newProperty("Task"     , TaskType.class);
    /** Task ID for the persistence */
    public static final UjoProperty<Event,Integer>  P_TASKID = newProperty("TaskID"   , Integer.class);
    /** Description of the event */
    public static final UjoProperty<Event,String>   P_DESCR  = newProperty("Description", "");
    
    // --- An optional property unique name test ---
    static { init(Event.class,true); }
    
    // ------------------------ JAVABEAN BEG ------------------------
    
    private short    time;
    private short    period;    // [min]
    private Project  project;
    private Integer  projectID;
    private TaskType task;
    private Integer  taskID;
    private byte[]   descr = ZeroProvider.ZERO_BYTES;
    
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public Time getTime() {
        return new Time(time);
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setTime(Time time) {
        this.time = time.getTimeMinutes();
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public short getPeriod() {
        return period;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setPeriod(short period) {
        this.period = period;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public Project getProject() {
        return project;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setProject(Project project) {
        this.project = project;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public Integer getProjectID() {
        return projectID;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public TaskType getTask() {
        return task;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setTask(TaskType task) {
        this.task = task;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public Integer getTaskID() {
        return taskID;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public String getDescription() {
        return ApplTools.newString(descr, UjoService.UTF_8);
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setDescription(String description) {
        this.descr = description!=null ? ApplTools.getBytes(description, UjoService.UTF_8) : ZeroProvider.ZERO_BYTES;
    }    
    
    // ------------------------ JAVABEAN END ------------------------
    
    /** Init time */
    public Event initTime() {
        P_TIME.setValue(this, new Time(true));
        return this;
    }

    /** Set a time period */
    public void setPeriod(Time time) {
        final short period = time.substract(P_TIME.of(this));
        P_PERIOD.setValue(this, period);
    }
    
    /** Is the event from a private project? */
    public boolean isPrivate() {
        Project project = Event.P_PROJ.of(this);
        final boolean result = project!=null && Project.P_PRIVATE.of(project);
        return result;
    }
    
    /** Read Visibility: */
    @Override
    public boolean readAuthorization(UjoAction action, UjoProperty property, Object value) {
        switch(action.getType()) {
            case ACTION_XML_EXPORT: {
                final boolean enabled
                =  property!=P_PROJ
                && property!=P_TASK
                ;
                return enabled;
            }
        }
        return super.readAuthorization(action, property, value);
    }

    /** Overrided for additional features */
    @Override
    public Object readValue(UjoProperty property) {
        Object result;
        if (property==P_PROJID) {
            Project proj = P_PROJ.of(this);
            result = proj!=null ? Project.P_ID.of(proj) : super.readValue(property);
        } else if (property==P_TASKID) {
            TaskType task = P_TASK.of(this);
            result = task!=null ? TaskType.P_ID.of(task) : super.readValue(property);
        } else {
            result = super.readValue(property);
        }
        return result;
    }
    
    /** Overrided for additional features */
    @Override
    public void writeValue(UjoProperty property, Object value) {
        if (P_PERIOD==property
        && (value==null || ((Short)value)<=0)
        ){
            value = ZeroProvider.ZERO_SHORT;
        }
        super.writeValue(property, value);
    }
    
    /** Is property finished? */
    public boolean isFinished(UjoProperty property) {
        if (P_PROJ==property) {
            Project proj = P_PROJ.of(this);
            return proj!=null && Project.P_FINISHED.of(proj);
        }
        if (P_TASK==property) {
            TaskType task = P_TASK.of(this);
            return isFinished(P_PROJ) || task!=null && TaskType.P_FINISHED.of(task);
        }
        return false;
    }
    
    /** Returns a Finished time */
    public Time getTimeFinished() {
        final Time result = P_TIME.of(this).cloneAdd(P_PERIOD.of(this));
        return result;
    }
    
}
