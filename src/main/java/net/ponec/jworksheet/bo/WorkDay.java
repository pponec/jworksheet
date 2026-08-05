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
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.core.ujo.JListKey;
import org.ujorm.annotation.Domain;

/**
 * One Work Day
 * @author Pavel Ponec
 * @composed 1 - * Event
 */
@Domain
public class WorkDay implements Comparable<WorkDay>, Exportable {

    /** A work day */
    public static final JKey<WorkDay,YearMonthDay> P_DATE   = JKey.of(MetaWorkDay.date);
    /** A day off work */
    public static final JKey<WorkDay,Boolean>      P_DAYOFF = JKey.of(MetaWorkDay.dayOff);
    /** List of events */
    public static final JListKey<WorkDay,Event>    P_EVENTS = JListKey.of(MetaWorkDay.events, Event.class);

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
    public boolean isExportable(final ExportAction action, final JKey<?,?> key, final Object value) {
        if (action == ExportAction.XML && key == P_DAYOFF) {
            return Boolean.TRUE.equals(value);
        }
        return true;
    }

    /** Compare to another WorkDay by a P_DATE. */
    @Override
    public int compareTo(final WorkDay o) {
        return get(P_DATE).compareTo(o.get(P_DATE));
    }

    // ------------------------ KEY ACCESS ------------------------

    public <V> V get(final JKey<WorkDay,V> key) {
        return key.getValue(this);
    }

    public <V> WorkDay set(final JKey<WorkDay,V> key, final V value) {
        key.setValue(this, value);
        return this;
    }
}
