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

import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import lombok.EqualsAndHashCode;
import net.ponec.jworksheet.core.MessageException;
import org.jetbrains.annotations.NotNull;

/**
 * Time - an immutable value, so the instance can be shared by more events.
 * <p>The value is serialized by the {@code toString()} method and restored by the
 * constructor with a single {@link String} argument, see the {@code ValueCoder} class.
 * @author Pavel Ponec
 */
@EqualsAndHashCode
public final class Time implements Comparable<Time>, Serializable {

    /** Separator */
    public static final String SEPARATOR = ":";

    public static final int VALUE_MIN = 0;
    public static final int VALUE_MAX = 24*60;

    /** The midnight 00:00 */
    public static final Time MIDNIGHT = new Time(0);

    /** minutes from 0 to 60*24 */
    private final short minutes;

    /**
     * Creates a new instance of Time.
     * @param time Accepted formats: {@code 23:56}, {@code 23.56}, {@code 2356} or hours only {@code 8}.
     * @throws MessageException An invalid text or a time out of the range.
     */
    public Time(final String time) {
        this(parse(time));
    }

    /**
     * Creates a new instance of Time.
     * @param minutes Minutes from the midnight.
     */
    public Time(final int minutes) {
        this.minutes = valid(minutes);
    }

    /** Returns the current system time. */
    public static Time now() {
        return new Time(LocalTime.now().get(ChronoField.MINUTE_OF_DAY));
    }

    /** Converts a text to minutes, see the {@link #Time(String)} constructor. */
    private static int parse(final String time) {
        final String text = time.trim().replace('.', ':');
        final int separator = text.indexOf(SEPARATOR);
        try {
            if (separator >= 0) {
                return minutesOf(time,
                        Integer.parseInt(text.substring(0, separator)),
                        Integer.parseInt(text.substring(separator + 1)));
            }
            final int value = Integer.parseInt(text);
            return text.length() <= 2
                 ? minutesOf(time, value, 0)               // Hours only: 8
                 : minutesOf(time, value / 100, value % 100); // Hours and minutes: 830
        } catch (NumberFormatException e) {
            throw new MessageException("Invalid time: " + time, e);
        }
    }

    /** Returns minutes of the day, the minute must be in the range 0 to 59. */
    private static int minutesOf(final String time, final int hour, final int minute) {
        if (minute < 0 || minute > 59) {
            throw new MessageException("Invalid minutes of the time: " + time);
        }
        return hour * 60 + minute;
    }

    /** Returns the valid minutes, or throws an exception. */
    private static short valid(final int minutes) {
        if (minutes < VALUE_MIN || minutes > VALUE_MAX) {
            throw new MessageException("Time is out of range: " + (minutes / 60) + SEPARATOR + (minutes % 60));
        }
        return (short) minutes;
    }

    /** Result [minutes]: this - time. */
    public short substract(final Time time) {
        return (short) (this.minutes - time.minutes);
    }

    /** Return minutes */
    public int getMinutes() {
        return minutes;
    }

    /** Format: {@code 08:05} */
    @Override
    public String toString() {
        return "%02d%s%02d".formatted(minutes / 60, SEPARATOR, minutes % 60);
    }

    @Override
    public int compareTo(@NotNull final Time obj) {
        return Short.compare(minutes, obj.minutes);
    }

    /**
     * Add some minutes to a new Time instance.
     */
    public Time cloneAdd(final int period) {
        return new Time(minutes + period);
    }
}
