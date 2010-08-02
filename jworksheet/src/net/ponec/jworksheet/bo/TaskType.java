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

import net.ponec.jworksheet.core.ApplTools;
import org.ujoframework.UjoProperty;
import org.ujoframework.implementation.map.MapUjo;

/**
 * A Task of Project
 * @author Pavel Ponec
 */
public class TaskType extends MapUjo implements Comparable {
    
    /** Task ID */
    public static final UjoProperty<TaskType,Integer> P_ID       = newProperty("ID", 0);
    /** Is the task default? */
    public static final UjoProperty<TaskType,Boolean> P_DEFAULT  = newProperty("Default", false);
    /** Is the task finished? */
    public static final UjoProperty<TaskType,Boolean> P_FINISHED = newProperty("Finished", false);
    /** Description of the task. */
    public static final UjoProperty<TaskType,String>  P_DESCR    = newProperty("Description", "");

    /** Show description */
    @Override
    public String toString() {
        String result = P_DESCR.of(this);
        return ApplTools.isValid(result) ? result : String.valueOf(P_ID.of(this)) ;
    }
    
    /** Compare to another TaskType by ID. */
    @Override
    public int compareTo(Object o) {
        int id1 = P_ID.of(this);
        int id2 = o!=null ? P_ID.of((TaskType)o) : Integer.MAX_VALUE;
        final int result = id1<id2 ? -1 : id1>id2 ? +1 : 0 ;
        return result;
    }

   @SuppressWarnings("unchecked")
   public <UJO extends TaskType, VALUE> VALUE get(UjoProperty<UJO, VALUE> up) {
        return up.getValue((UJO)this);
    }

   @SuppressWarnings("unchecked")
    public <UJO extends TaskType, VALUE> UJO set(UjoProperty<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }

    public void copyFrom(TaskType otherTask) {
        for (UjoProperty p : readProperties()) {
            p.copy(otherTask, this);
        }
    }
}
