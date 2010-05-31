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

package net.ponec.jworksheet.core;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Code version.
 * @author Pavel Ponec
 */
final public class Version implements Comparable<Version>{

    final private String[] version;

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
    public int compareTo(Version parm) {
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
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return compareTo((Version)obj)==0;
    }

}
