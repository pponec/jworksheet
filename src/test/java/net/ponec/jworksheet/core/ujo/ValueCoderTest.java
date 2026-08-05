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

package net.ponec.jworksheet.core.ujo;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.core.SysTray;
import net.ponec.jworksheet.core.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The text format of the values must stay compatible with the original Ujorm implementation -
 * the users have got the data files and the configuration files written by the previous releases.
 * @author Pavel Ponec
 */
public class ValueCoderTest {

    // ------------------------ THE LEGACY FORMAT ------------------------

    @Test
    @DisplayName("A color is a six digit upper case hexadecimal number")
    public void colorFormat() {
        assertEquals("5DA158", ValueCoder.encode(new Color(0x5DA158)));
        assertEquals("FFFACD", ValueCoder.encode(new Color(0xFFFACD)));
        assertEquals("000000", ValueCoder.encode(new Color(0x000000)));
        assertEquals(new Color(0x5DA158), ValueCoder.decode(Color.class, "5DA158"));
    }

    @Test
    @DisplayName("A locale items are separated by a hyphen")
    public void localeFormat() {
        assertEquals("cs-CZ", ValueCoder.encode(new Locale("cs", "CZ")));
        assertEquals("en", ValueCoder.encode(new Locale("en")));
        assertEquals(new Locale("cs", "CZ"), ValueCoder.decode(Locale.class, "cs-CZ"));
        assertEquals(new Locale("en"), ValueCoder.decode(Locale.class, "en"));
    }

    @Test
    @DisplayName("A rectangle is a comma separated list of the x, y, width and height")
    public void rectangleFormat() {
        assertEquals("649,397,622,405", ValueCoder.encode(new Rectangle(649, 397, 622, 405)));
        assertEquals(new Rectangle(649, 397, 622, 405)
                , ValueCoder.decode(Rectangle.class, "649,397,622,405"));
    }

    @Test
    @DisplayName("A date keeps the milliseconds")
    public void dateFormat() {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2021, Calendar.MARCH, 14, 11, 51, 31);
        cal.set(Calendar.MILLISECOND, 634);
        final Date date = cal.getTime();

        assertEquals("2021-03-14T11:51:31.634", ValueCoder.encode(date));
        assertEquals(date, ValueCoder.decode(Date.class, "2021-03-14T11:51:31.634"));
    }

    @Test
    @DisplayName("A file is stored by its path and an enum by its name")
    public void fileAndEnumFormat() {
        assertEquals("<default>", ValueCoder.encode(new File("<default>")));
        assertEquals(new File("/tmp/data.xml"), ValueCoder.decode(File.class, "/tmp/data.xml"));

        assertEquals("HIDE", ValueCoder.encode(SysTray.Action.HIDE));
        assertEquals(SysTray.Action.HIDE, ValueCoder.decode(SysTray.Action.class, "HIDE"));
    }

    @Test
    @DisplayName("A value type is encoded by its toString() and decoded by its text constructor")
    public void valueTypeFormat() {
        assertEquals("08:00", ValueCoder.encode(new Time("08:00")));
        assertEquals(new Time("08:00"), ValueCoder.decode(Time.class, "08:00"));

        assertEquals("2021-03-14", ValueCoder.encode(new YearMonthDay("2021-03-14")));
        assertEquals(new YearMonthDay("2021-03-14"), ValueCoder.decode(YearMonthDay.class, "2021-03-14"));

        assertEquals("1.02", ValueCoder.encode(new Version("1.02")));
        assertEquals("1.02", ValueCoder.decode(Version.class, "1.02").toString());
    }

    // ------------------------ THE EDGE CASES ------------------------

    @Test
    @DisplayName("An undefined value is encoded and decoded to the null")
    public void undefinedValue() {
        assertNull(ValueCoder.encode(null));
        assertNull(ValueCoder.decode(String.class, null));
        assertNull(ValueCoder.decode(Integer.class, null));
    }

    @Test
    @DisplayName("An empty text is a null value for all the types but the String")
    public void emptyText() {
        assertEquals("", ValueCoder.decode(String.class, ""));
        assertNull(ValueCoder.decode(Integer.class, ""));
        assertNull(ValueCoder.decode(Color.class, ""));
        assertNull(ValueCoder.decode(Time.class, ""));
    }

    @Test
    @DisplayName("A boolean accepts the digit one as a true value")
    public void booleanLeniency() {
        assertEquals(Boolean.TRUE, ValueCoder.decode(Boolean.class, "true"));
        assertEquals(Boolean.TRUE, ValueCoder.decode(Boolean.class, "1"));
        assertEquals(Boolean.FALSE, ValueCoder.decode(Boolean.class, "false"));
        assertEquals(Boolean.FALSE, ValueCoder.decode(Boolean.class, "0"));
    }

    @Test
    @DisplayName("An enum accepts a lower case text, the original implementation did it too")
    public void enumLeniency() {
        assertEquals(SysTray.Action.HIDE, ValueCoder.decode(SysTray.Action.class, "hide"));
        assertEquals(SysTray.Action.EVENT, ValueCoder.decode(SysTray.Action.class, "Event"));
    }

    @Test
    @DisplayName("A primitive type is decoded to its wrapper")
    public void primitiveTypes() {
        assertEquals((short) 240, ValueCoder.decode(short.class, "240"));
        assertEquals(7, ValueCoder.decode(int.class, "7"));
        assertEquals(8f, ValueCoder.decode(float.class, "8.0"));
        assertEquals(Boolean.TRUE, ValueCoder.decode(boolean.class, "true"));
    }

    @Test
    @DisplayName("An undecodable text throws a described exception")
    public void invalidText() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class
                , () -> ValueCoder.decode(Integer.class, "seven"));
        assertTrue(e.getMessage().contains("seven"), e.getMessage());
    }
}
