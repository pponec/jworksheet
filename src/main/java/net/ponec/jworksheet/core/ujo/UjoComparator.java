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

import java.util.Comparator;
import java.util.List;
import org.ujorm.core.Key;

/**
 * A comparator of the domain objects by a list of the keys.
 * The class replaces the {@code org.ujorm.core.UjoComparator} of the original
 * Ujorm implementation. The values are compared in an ascending order
 * and an undefined value is the first one.
 *
 * @param <T> a type of the compared domain object
 * @author Pavel Ponec
 */
public class UjoComparator<T> implements Comparator<T> {

    /** The keys are compared in this order. */
    private final Key<T,?>[] keys;

    @SafeVarargs
    protected UjoComparator(final Key<T,?>... keys) {
        this.keys = keys;
    }

    /** Creates a comparator by a list of the keys. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> UjoComparator<T> newInstance(final Key... keys) {
        return new UjoComparator<>(keys);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int compare(final T o1, final T o2) {
        for (Key<T,?> key : keys) {
            final Object v1 = key.getValue(o1);
            final Object v2 = key.getValue(o2);

            if (v1 == v2) {
                continue;
            }
            if (v1 == null) {
                return -1;
            }
            if (v2 == null) {
                return 1;
            }
            final int result = ((Comparable) v1).compareTo(v2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    /** Are the objects equal by all the compared keys? */
    public boolean equals(final T o1, final T o2) {
        return compare(o1, o2) == 0;
    }

    /** Sorts the list by this comparator. */
    public void sort(final List<T> list) {
        list.sort(this);
    }
}
