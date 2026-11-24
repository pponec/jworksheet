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
import net.ponec.jworksheet.core.MessageException;
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
    @DisplayName("The supported text formats")
    public void parse() {
        assertEquals("08:30", new Time("8:30").toString());
        assertEquals("08:30", new Time("8.30").toString());
        assertEquals("08:30", new Time("830").toString());
        assertEquals("12:30", new Time("1230").toString());
        assertEquals("08:00", new Time("8").toString(), "Hours only");
        assertEquals("12:00", new Time("12").toString(), "Hours only");
        assertEquals("08:05", new Time(" 8:5 ").toString());
        assertEquals("00:00", Time.MIDNIGHT.toString());
    }

    @Test
    @DisplayName("An invalid text is reported by the MessageException")
    public void parseInvalidText() {
        assertThrows(MessageException.class, () -> new Time(""));
        assertThrows(MessageException.class, () -> new Time("8:"));
        assertThrows(MessageException.class, () -> new Time("abc"));
        assertThrows(MessageException.class, () -> new Time("24:01"));
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
