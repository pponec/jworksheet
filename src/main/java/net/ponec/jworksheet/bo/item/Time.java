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
import java.util.Calendar;
import java.util.StringTokenizer;
import net.ponec.jworksheet.core.*;
import org.jetbrains.annotations.NotNull;

/**
 * Time - an immutable value, so the instance can be shared by more events.
 * <p>The value is serialized by the {@code toString()} method and restored by the
 * constructor with a single {@link String} argument, see the {@code ValueCoder} class.
 * @author Pavel Ponec
 */
public class Time implements Comparable<Time>, Serializable {

    /** Separator */
    public static final String SEPARATOR = ":";


    public static int VALUE_MIN = 0;
    public static int VALUE_MAX = 24*60;

    /** minutes from 0 to 60*24-1 */
    private final short minutes;

    /**
     * Creates a new instance of Time
     * @param time Format 23:56
     */
    public Time(String time) {
        if (time.indexOf('.')>=0) { time = time.replace('.', ':'); }

        int timeLength = time.length();
        int separIndex = time.indexOf(SEPARATOR);
        if (separIndex<0 && timeLength>=2) {
            StringBuilder sb = new StringBuilder(5);
            sb.append('0');
            sb.append(time, 0, timeLength-2);
            sb.append(SEPARATOR);
            sb.append(time.substring(timeLength-2));
            time = sb.toString();
        }

        StringTokenizer st = new StringTokenizer(time, SEPARATOR);
        int hourse = Integer.parseInt(st.nextToken());
        int minute = Integer.parseInt(st.nextToken());
        this.minutes = valid(hourse * 60 + minute);
    }

    /**
     * Creates a new instance of a System Time.
     */
    public Time(int minutes) {
        this.minutes = valid(minutes);
    }


    /**
     * Creates a new instance of a System Time.
     */
    public Time(boolean systemTime) {
        this(systemTime ? systemMinutes() : 0);
    }

    /** Returns minutes of the current system time. */
    private static int systemMinutes() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
    }

    /** Returns Time in minutes. */
    public short getTimeMinutes() {
        return minutes;
    }

    /** Returns the valid minutes, or throws an exception. */
    protected static short valid(int minutes) {
        if (minutes<VALUE_MIN
        ||  minutes>VALUE_MAX
        ){
            throw new MessageException("Time is out of range: " + (minutes/60) + SEPARATOR + (minutes % 60)  );
        }
        return (short) minutes;
    }

    /** Result [minutes]: this - time. */
    public short substract(Time time) {
        final int result = this.minutes - time.minutes;
        return (short) result;
    }

    /** Return minutes */
    public int getMinutes() {
        return minutes;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(5);
        int hourse = minutes / 60 ;
        int minute = minutes % 60 ;

        if (hourse<10) { result.append('0'); }
        result.append(hourse);
        //
        result.append(SEPARATOR);
        if (minute<10) { result.append('0'); }
        result.append(minute);

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.minutes == ((Time)obj).minutes;
    }

    @Override
    public int compareTo(@NotNull Time obj) {
        final int result
        = this.minutes < obj.minutes ? -1
        : this.minutes > obj.minutes ? +1
        : 0
        ;
        return result;
    }

    /**
     * Add some minutes to a new Time instance.
     */
    public Time cloneAdd(int period) {
        return new Time(minutes + period);
    }

}
