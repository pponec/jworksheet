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

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Code version - an immutable value.
 * <p>The items are compared case insensitive, the first item by its length too,
 * so the {@link #equals(Object)} and the {@link #hashCode()} follow the same rule.
 * @author Pavel Ponec
 */
public final class Version implements Comparable<Version> {

    /** The items of the version, the array is never exposed. */
    private final String[] version;

    public Version(final String code) {
        StringTokenizer st = new StringTokenizer(code, ".");
        version = new String[st.countTokens()];
        int i = 0;

        while (st.hasMoreTokens()) {
            version[i++] = st.nextToken();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (String s : version) {
            if (result.length()>0) {
                result.append('.');
            }
            result.append(s);
        }
        return result.toString();
    }

    /** Compare two integers */
    private int compare(int i1, int i2) {
        return i1 < i2
             ? -1
             : i1 > i2
             ? 1
             : 0
             ;
    }

    /** Compare two String elements */
    private int compare(String s1, String s2, boolean lengthComparation) {
        int result = 0;
        if (lengthComparation) {
           result = compare( s1.length()
                           , s2.length()
                           );
        }
        return result==0
             ? s1.compareTo(s2)
             : result
             ;
    }

    /** Compare to antother version. */
    @Override
    public int compareTo(final Version parm) {
        int max = Math.min
                ( this.version.length
                , parm.version.length
                )
                ;
        for (int i=0; i<max; i++) {
            String s1 = this.version[i].toUpperCase(Locale.ENGLISH);
            String s2 = parm.version[i].toUpperCase(Locale.ENGLISH);
            int result = compare(s1, s2, i==0);
            if (result!=0) return result;
        }

        return compare(version.length, parm.version.length);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof final Version other && compareTo(other) == 0;
    }

    /** The hash is consistent with the case insensitive {@link #compareTo(Version)}. */
    @Override
    public int hashCode() {
        return toString().toUpperCase(Locale.ENGLISH).hashCode();
    }

}
