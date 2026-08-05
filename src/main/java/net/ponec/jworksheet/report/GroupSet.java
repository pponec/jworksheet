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


package net.ponec.jworksheet.report;

import java.util.ArrayList;
import java.util.Collections;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.UjoComparator;

/**
 * Report Set
 * @author Pavel Ponec
 */
public class GroupSet {

    /** Task Group Collection */
    protected ArrayList<TaskGroup> tasks = new ArrayList<TaskGroup>();

    /** Task Group Comparator */
    protected UjoComparator<TaskGroup> comparator;

    protected JKey<Event,Short> propertyTime;

    protected boolean showEmptyProject = false;

    /** Look-up KEY. */
    protected TaskGroup key = new TaskGroup(null);

    public GroupSet(UjoComparator<TaskGroup> comparator, JKey<Event,Short> propertyTime) {
        this.comparator   = comparator;
        this.propertyTime = propertyTime;
    }

    /** Insert or create an task group. */
    public TaskGroup addTime(Event event) {
        short period = event.get(propertyTime);
        if (showEmptyProject || period>0) {
            TaskGroup taskGroup = findTaskGroup(event);
            taskGroup.addTime( period );
            return taskGroup;
        } else {
            return null;
        }
    }

    /** Insert or create an task group. */
    protected TaskGroup findTaskGroup(Event event) {
        key.init(event);
        int pointer = Collections.binarySearch(tasks, key, comparator);
        if (pointer>=0) {
            return (TaskGroup) tasks.get(pointer);
        } else {
            TaskGroup taskGroup = new TaskGroup(event);
            tasks.add(-pointer - 1, taskGroup);
            return taskGroup;
        }
    }

    /** Get all sorted groups. */
    protected TaskGroup[] getGroups() {
        TaskGroup[] result = tasks.toArray(new TaskGroup[tasks.size()] );
        return result;
    }

    /** Returns count of items */
    public int size() {
        return tasks.size();
    }

    /** String value */
    @Override
    public String toString() {
        return "[" + size() + "]" ;
    }

    /** Returns a time */
    public JKey<Event,Short> getPropertyTime() {
        return propertyTime;
    }

    public boolean isShowEmptyProject() {
        return showEmptyProject;
    }

    public void setShowEmptyProject(boolean showEmptyProject) {
        this.showEmptyProject = showEmptyProject;
    }



}
