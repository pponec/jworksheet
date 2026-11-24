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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Code version - an immutable value.
 * <p>The items are compared case insensitive, the first item by its length too,
 * so the {@link #equals(Object)} and the {@link #hashCode()} follow the same rule.
 * @author Pavel Ponec
 */
public final class Version implements Comparable<Version> {

    /** The first item is compared by its length, so the {@code 10} is greater than the {@code 9}. */
    private static final Comparator<String> FIRST_ITEM = Comparator
            .comparingInt(String::length)
            .thenComparing(String.CASE_INSENSITIVE_ORDER);

    /** The items of the version, the array is never exposed. */
    private final String[] version;

    /** Creates a version from the text, the empty items are skipped. Sample: {@code 2.00} */
    public Version(final String code) {
        version = Arrays.stream(code.split("\\."))
                .filter(Predicate.not(String::isEmpty))
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return String.join(".", version);
    }

    /** Compare to antother version. */
    @Override
    public int compareTo(final Version parm) {
        final int max = Math.min(version.length, parm.version.length);
        for (int i = 0; i < max; i++) {
            final Comparator<String> comparator = i == 0 ? FIRST_ITEM : String.CASE_INSENSITIVE_ORDER;
            final int result = comparator.compare(version[i], parm.version[i]);
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(version.length, parm.version.length);
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
