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

import org.ujorm.Key;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujorm.Key;
import org.ujorm.extensions.ListProperty;
import org.ujorm.UjoAction;
import org.ujorm.implementation.map.MapUjo;
import static org.ujorm.UjoAction.*;
import org.ujorm.core.KeyFactory;
import org.ujorm.extensions.AbstractUjo;

/**
 * One Work Day
 * @author Pavel Ponec
 * @composed 1 - * Event
 */
public class WorkDay extends AbstractUjo implements Comparable {
    
    /** KeyFactory */
    private static final KeyFactory f = KeyFactory.CamelBuilder.get(WorkDay.class);
    
    /** A work day */
    public static final Key<WorkDay,YearMonthDay> P_DATE = f.newKey("Date");
    /** A day off work */
    public static final Key<WorkDay,Boolean>    P_DAYOFF = f.newKey("DayOff", false);
    /** List of events */
    public static final ListProperty<WorkDay,Event> P_EVENTS = f.newListKey("Event");
    
    static {
        f.lock();
    }
    
    /** Overrided for additional features */
    @Override
    public boolean readAuthorization(UjoAction action, Key property, Object value) {
        switch(action.getType()) {
            case ACTION_XML_EXPORT:
                return property==P_DAYOFF ? (Boolean) value : true ;
            default:
                return super.readAuthorization(action, property, value);
        }
    }

    /** Compare to another WorkDay by a P_DATE. */
    @Override
    public int compareTo(Object o) {
        final YearMonthDay d1 = get(P_DATE);
        final YearMonthDay d2 = ((WorkDay) o).get(P_DATE);
        final int result = d1.compareTo(d2);
        return result;
    }

   @SuppressWarnings("unchecked")
   public <UJO extends WorkDay, VALUE> VALUE get(Key<UJO, VALUE> up) {
        return up.of((UJO)this);
    }

    @SuppressWarnings("unchecked")
    public <UJO extends WorkDay, VALUE> UJO set(Key<UJO, VALUE> up, VALUE value) {
        up.setValue((UJO)this, value);
        return (UJO) this;
    }
}
