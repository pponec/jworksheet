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


package net.ponec.jworksheet.core;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The Version is an immutable value object compared case insensitive.
 * @author Pavel Ponec
 */
public class VersionTest {

    @Test
    @DisplayName("The equals() and the hashCode() follow the case insensitive compareTo()")
    public void equalsAndHashCode() {
        final Version version = new Version("2.00a");
        final Version upper = new Version("2.00A");

        assertEquals(0, version.compareTo(upper));
        assertEquals(version, upper);
        assertEquals(version.hashCode(), upper.hashCode());
        assertTrue(Set.of(version).contains(upper));
        assertNotEquals(new Version("2.01"), version);
        assertNotEquals(new Version("2.00a.1"), version);
    }

    @Test
    @DisplayName("A comparison with the null or with another type provides the false value")
    public void equalsOfAnotherType() {
        assertFalse(new Version("1.00").equals(null));
        assertFalse(new Version("1.00").equals("1.00"));
    }

    @Test
    @DisplayName("The first item is compared by its length, the next ones as a text")
    public void order() {
        assertTrue(new Version("10.00").compareTo(new Version("9.00")) > 0);
        assertTrue(new Version("2.00").compareTo(new Version("1.10")) > 0);
        assertTrue(new Version("1.10").compareTo(new Version("1.10.1")) < 0);
    }

    @Test
    @DisplayName("The text is kept including the case, an empty item is skipped")
    public void textFormat() {
        assertEquals("2.00a", new Version("2.00a").toString());
        assertEquals("1.2", new Version("1..2").toString());
        assertEquals(new Version("1.2"), new Version("1..2"));
    }
}
