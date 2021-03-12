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


package net.ponec.jworksheet.bo;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.ujorm.Key;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.core.ApplTools;
import org.ujorm.UjoAction;
import org.ujorm.Key;
import org.ujorm.core.UjoService;
import org.ujorm.implementation.bean.*;
import static org.ujorm.UjoAction.*;
import org.ujorm.core.KeyFactory;

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

    public static final byte[] ZERO_BYTES = new byte[0];
    public static final Short  ZERO_SHORT = (short)0;

    private static final KeyFactory<Event> f = KeyFactory.CamelBuilder.get(Event.class);

    /** Start time of event */
    public static final Key<Event,Time>     P_TIME   = f.newKey("Time");
    /** Period of the event in minutes */
    public static final Key<Event,Short>    P_PERIOD = f.newKey("Period"   , ZERO_SHORT); // [min]
    /** Project (object) */
    public static final Key<Event,Project>  P_PROJ   = f.newKey("Project");
    /** Project ID for the persistence */
    public static final Key<Event,Integer>  P_PROJID = f.newKey("ProjectID");
    /** Task (object) */
    public static final Key<Event,TaskType> P_TASK   = f.newKey("Task");
    /** Task ID for the persistence */
    public static final Key<Event,Integer>  P_TASKID = f.newKey("TaskID");
    /** Description of the event */
    public static final Key<Event,String>   P_DESCR  = f.newKey("Description", "");

    // --- An optional property unique name test ---
    static { f.lock(); }

    // ------------------------ JAVABEAN BEG ------------------------

    private short    time;
    private short    period;    // [min]
    private Project  project;
    private Integer  projectID;
    private TaskType task;
    private Integer  taskID;
    private byte[]   descr = ZERO_BYTES;

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
        return ApplTools.newString(descr, UTF_8);
    }
    /** @deprecated This is a JavaBean method however the method is deprecated because the jWorkSheet application want to use an UJO access only. */ @Deprecated
    public void setDescription(String description) {
        this.descr = description!=null ? ApplTools.getBytes(description, UTF_8) : ZERO_BYTES;
    }

    // ------------------------ JAVABEAN END ------------------------

    /** Init time */
    public Event initTime() {
        P_TIME.setValue(this, new Time(true));
        return this;
    }

    /** Set a time period */
    public void setPeriod(Time time) {
        final short period = time.substract(get(P_TIME));
        P_PERIOD.setValue(this, period);
    }

    /** Is the event from a private project? */
    public boolean isPrivate() {
        Project project = get(Event.P_PROJ);
        final boolean result = project!=null && project.get(Project.P_PRIVATE);
        return result;
    }

    /** Read Visibility: */
    @Override
    public boolean readAuthorization(UjoAction action, Key property, Object value) {
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
    public Object readValue(Key property) {
        Object result;
        if (property==P_PROJID) {
            Project proj = get(P_PROJ);
            result = proj!=null ? proj.get(Project.P_ID) : super.readValue(property);
        } else if (property==P_TASKID) {
            TaskType task = get(P_TASK);
            result = task!=null ? task.get(TaskType.P_ID) : super.readValue(property);
        } else {
            result = super.readValue(property);
        }
        return result;
    }

    /** Overrided for additional features */
    @Override
    public void writeValue(Key property, Object value) {
        if (P_PERIOD==property
        && (value==null || ((Short)value)<=0)
        ){
            value = ZERO_SHORT;
        }
        super.writeValue(property, value);
    }

    /** Is property finished? */
    public boolean isFinished(Key property) {
        if (P_PROJ==property) {
            Project proj = get(P_PROJ);
            return proj!=null && proj.get(Project.P_FINISHED);
        }
        if (P_TASK==property) {
            TaskType task = get(P_TASK);
            return isFinished(P_PROJ) || task!=null && task.get(TaskType.P_FINISHED);
        }
        return false;
    }

    /** Returns a Finished time */
    public Time getTimeFinished() {
        final Time result = get(P_TIME).cloneAdd(get(P_PERIOD));
        return result;
    }

    @SuppressWarnings("unchecked")
    public <UJO extends Event, VALUE> VALUE get(Key<UJO, VALUE> up) {
        return up.of((UJO)this);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends Event, VALUE> UJO set(Key<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }

}
