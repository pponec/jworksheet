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

import net.ponec.jworksheet.core.ujo.TextCodec;

/**
 * A codec of a time stored by a memory optimized {@code short} field of the minutes,
 * but published as a {@code Time} text in a format of the {@code 08:00} sample.
 * @author Pavel Ponec
 */
public class TimeCodec implements TextCodec<Short> {

    @Override
    public String encode(final Short minutes) {
        return new Time(minutes.intValue()).toString();
    }

    @Override
    public Short decode(final String text) {
        return new Time(text).getTimeMinutes();
    }
}
