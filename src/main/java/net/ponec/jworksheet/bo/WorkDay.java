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

import jakarta.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.bo.meta.MetaWorkDay;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import net.ponec.jworksheet.core.ujo.Keys;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;

/**
 * One Work Day
 * @author Pavel Ponec
 * @composed 1 - * Event
 */
@Domain
public class WorkDay implements Comparable<WorkDay>, Exportable {

    @Column(name = "Date")
    private YearMonthDay date;

    @Column(name = "DayOff")
    private boolean dayOff = false;

    @Column(name = "Event")
    private List<Event> events = new ArrayList<>();

    // ------------------------ JAVABEAN ------------------------

    public YearMonthDay getDate() {
        return date;
    }

    public void setDate(final YearMonthDay date) {
        this.date = date;
    }

    public boolean isDayOff() {
        return dayOff;
    }

    public void setDayOff(final boolean dayOff) {
        this.dayOff = dayOff;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(final List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();
    }

    // ------------------------ BUSINESS ------------------------

    /** A day off flag is exported for a true value only. */
    @Override
    public boolean isExportable(final ExportAction action, final Key<?,?> key, final Object value) {
        if (action == ExportAction.XML && key == MetaWorkDay.dayOff) {
            return Boolean.TRUE.equals(value);
        }
        return true;
    }

    /** Compare to another WorkDay by its date. */
    @Override
    public int compareTo(final WorkDay o) {
        return date.compareTo(o.getDate());
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final Key<WorkDay,V> key) {
        return key.getValue(this);
    }

    public <V> WorkDay set(final Key<WorkDay,V> key, final V value) {
        Keys.write(key, this, value);
        return this;
    }
}
