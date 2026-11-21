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

package net.ponec.jworksheet.bo;

import org.ujorm.annotation.UColumn;
import lombok.Getter;
import lombok.Setter;
import net.ponec.jworksheet.core.ujo.Keys;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;
import org.ujorm.tools.Check;

/**
 * A Task of Project
 * @author Pavel Ponec
 */
@Domain
@Getter
@Setter
public class TaskType implements Comparable<TaskType> {

    @UColumn(name = "ID")
    private int id = 0;

    @UColumn(name = "Default")
    private boolean defaultTask = false;

    @UColumn(name = "Finished")
    private boolean finished = false;

    @UColumn(name = "Description")
    private String descr = "";

    // ------------------------ JAVABEAN ------------------------

    /** An undefined description is replaced by an empty text. */
    public void setDescr(final String descr) {
        this.descr = descr != null ? descr : "";
    }

    // ------------------------ BUSINESS ------------------------

    /** Show description */
    @Override
    public String toString() {
        final String result = getDescr();
        return Check.hasLength(result) ? result : String.valueOf(getId());
    }

    /** Compare to another TaskType by ID. */
    @Override
    public int compareTo(final TaskType o) {
        final int id1 = getId();
        final int id2 = o != null ? o.getId() : Integer.MAX_VALUE;
        return Integer.compare(id1, id2);
    }

    /** Copy all the attributes from another task. */
    public void copyFrom(final TaskType otherTask) {
        for (Key<TaskType,?> key : Keys.keysOf(TaskType.class)) {
            Keys.copy(key, otherTask, this);
        }
    }
}
