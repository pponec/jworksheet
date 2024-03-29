/**
  * Copyright (C) 2007-2022, Pavel Ponec, contact: http://ponec.net/
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

import org.ujorm.Key;
import org.ujorm.core.KeyFactory;
import org.ujorm.implementation.map.MapUjo;
import org.ujorm.tools.Check;

/**
 * A Task of Project
 * @author Pavel Ponec
 */
public class TaskType extends MapUjo implements Comparable<TaskType> {

    private static final KeyFactory<TaskType> f = KeyFactory.CamelBuilder.get(TaskType.class);

    /** Task ID */
    public static final Key<TaskType,Integer> P_ID       = f.newKey("ID", 0);
    /** Is the task default? */
    public static final Key<TaskType,Boolean> P_DEFAULT  = f.newKey("Default", false);
    /** Is the task finished? */
    public static final Key<TaskType,Boolean> P_FINISHED = f.newKey("Finished", false);
    /** Description of the task. */
    public static final Key<TaskType,String>  P_DESCR    = f.newKey("Description", "");

    static { f.lock(); }

    /** Show description */
    @Override
    public String toString() {
        String result = P_DESCR.of(this);
        return Check.hasLength(result) ? result : String.valueOf(P_ID.of(this)) ;
    }

    /** Compare to another TaskType by ID. */
    @Override
    public int compareTo(TaskType o) {
        int id1 = P_ID.of(this);
        int id2 = o!=null ? P_ID.of(o) : Integer.MAX_VALUE;
        final int result = id1<id2 ? -1 : id1>id2 ? +1 : 0 ;
        return result;
    }

   @SuppressWarnings("unchecked")
   public <UJO extends TaskType, VALUE> VALUE get(Key<UJO, VALUE> up) {
        return up.of((UJO)this);
    }

   @SuppressWarnings("unchecked")
    public <UJO extends TaskType, VALUE> UJO set(Key<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }

    @SuppressWarnings("unchecked")
    public void copyFrom(TaskType otherTask) {
        for (Key p : readKeys()) {
            p.copy(otherTask, this);
        }
    }
}
