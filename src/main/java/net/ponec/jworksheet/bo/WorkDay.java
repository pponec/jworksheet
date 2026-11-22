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

import net.ponec.jworksheet.core.ujo.DomainObject;
import org.ujorm.annotation.UColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.bo.meta.MetaWorkDay;
import net.ponec.jworksheet.core.ujo.ExportAction;
import net.ponec.jworksheet.core.ujo.Exportable;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;

/**
 * One Work Day
 * @author Pavel Ponec
 * @composed 1 - * Event
 */
@Domain
@Getter
@Setter
public class WorkDay implements Comparable<WorkDay>, Exportable, DomainObject {

    @UColumn(name = "Date")
    private YearMonthDay date;

    @UColumn(name = "DayOff")
    private boolean dayOff = false;

    @UColumn(name = "Event")
    private List<Event> events = new ArrayList<>();

    // ------------------------ JAVABEAN ------------------------

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
}
