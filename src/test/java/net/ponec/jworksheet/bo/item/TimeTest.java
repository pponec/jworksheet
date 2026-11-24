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


package net.ponec.jworksheet.bo.item;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The Time is an immutable value object.
 * @author Pavel Ponec
 */
public class TimeTest {

    @Test
    @DisplayName("Equal times have the same hash, so they work as keys of a hash collection")
    public void equalsAndHashCode() {
        final Time time = new Time("08:30");

        assertEquals(new Time(8 * 60 + 30), time);
        assertEquals(new Time(8 * 60 + 30).hashCode(), time.hashCode());
        assertEquals(1, Set.of(time).size());
        assertTrue(Set.of(time).contains(new Time("8.30")));
        assertNotEquals(new Time("08:31"), time);
    }

    @Test
    @DisplayName("A comparison with the null or with another type provides the false value")
    public void equalsOfAnotherType() {
        assertFalse(new Time(0).equals(null));
        assertFalse(new Time(0).equals("00:00"));
    }

    @Test
    @DisplayName("A shifted time is a new instance, the original stays unchanged")
    public void cloneAddIsImmutable() {
        final Time time = new Time("08:00");
        final Time result = time.cloneAdd(90);

        assertEquals("09:30", result.toString());
        assertEquals("08:00", time.toString());
    }
}
