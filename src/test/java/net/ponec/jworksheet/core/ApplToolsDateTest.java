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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The date formatter of the user patterns.
 * @author Pavel Ponec
 */
public class ApplToolsDateTest {

    /** The Sunday 2026-01-04: the first week starts by the Monday 2025-12-29,
     * or by the Sunday 2025-12-28 so the day is in the second week. */
    private static final LocalDate SUNDAY = LocalDate.of(2026, 1, 4);

    @Test
    @DisplayName("The week of the year follows the first day of the week, not the language")
    public void weekOfYear() {
        assertEquals("1", ApplTools.dateFormatter("w", Locale.US, Calendar.MONDAY).apply(SUNDAY));
        assertEquals("2", ApplTools.dateFormatter("w", Locale.US, Calendar.SUNDAY).apply(SUNDAY));
    }

    @Test
    @DisplayName("The formatter is reusable and it uses the language")
    public void reusableFormatter() {
        final var formatter = ApplTools.dateFormatter("EEE yyyy-MM-dd", Locale.US, Calendar.MONDAY);
        assertEquals("Sun 2026-01-04", formatter.apply(SUNDAY));
        assertEquals("Mon 2026-01-05", formatter.apply(SUNDAY.plusDays(1)));
    }

    @Test
    @DisplayName("A local date survives the conversion to the Date of the Swing components")
    public void dateConversion() {
        assertEquals(SUNDAY, ApplTools.toLocalDate(ApplTools.toDate(SUNDAY)));
    }
}
