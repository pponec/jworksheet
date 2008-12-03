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

import net.ponec.jworksheet.bo.item.YearMonthDay;
import org.ujoframework.UjoProperty;
import org.ujoframework.extensions.ListProperty;
import org.ujoframework.extensions.UjoAction;
import org.ujoframework.implementation.map.MapUjo;
import static org.ujoframework.extensions.UjoAction.*;

/**
 * One Work Day
 * @author Pavel Ponec
 * @composed 1 - * Event
 */
public class WorkDay extends MapUjo implements Comparable {
    
    /** A work day */
    public static final UjoProperty<WorkDay,YearMonthDay> P_DATE = newProperty("Date", YearMonthDay.class);
    /** A day off work */
    public static final UjoProperty<WorkDay,Boolean>    P_DAYOFF = newProperty("DayOff", false);
    /** List of events */
    public static final ListProperty<WorkDay,Event>     P_EVENTS = newPropertyList("Event", Event.class);
    
    @Override
    public boolean readAuthorization(UjoAction action, UjoProperty property, Object value) {
        switch(action.getType()) {
            case ACTION_XML_EXPORT:
                return property==P_DAYOFF ? (Boolean) value : true ;
            default:
                return super.readAuthorization(action, property, value);
        }
    }

    @Override
    public int compareTo(Object o) {
        final YearMonthDay d1 = P_DATE.of(this);
        final YearMonthDay d2 = P_DATE.of((WorkDay) o);
        final int result = d1.compareTo(d2);
        return result;        
    }
    
}
