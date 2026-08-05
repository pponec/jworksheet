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

import java.util.ArrayList;
import java.util.List;

/**
 * Common operations above the domain objects.
 * The class replaces a part of the {@code org.ujorm.core.UjoManager} of the original
 * Ujorm implementation.
 * @author Pavel Ponec
 */
public final class DomainTools {

    private DomainTools() {
    }

    /** Creates a copy of the domain object.
     * <p>The scalar values are shared, the lists are copied to a new collection and their
     * items are copied by a recursive call with a decreased depth.
     *
     * @param source An object to copy.
     * @param depth A depth of the copy. A value less than one provides the original object.
     * @return A new instance of the domain object.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T deepCopy(final T source, final int depth) {
        if (source == null || depth <= 0) {
            return source;
        }
        final Class<T> domainClass = (Class<T>) source.getClass();
        final T result = newInstance(domainClass);

        for (JKey<T,?> key : JKey.keysOf(domainClass)) {
            if (key instanceof final JListKey listKey) {
                final List<Object> items = new ArrayList<>();
                for (Object item : listKey.getList(source)) {
                    items.add(deepCopy(item, depth - 1));
                }
                listKey.setValue(result, items);
            } else {
                ((JKey) key).setValue(result, key.getValue(source));
            }
        }
        return result;
    }

    private static <T> T newInstance(final Class<T> domainClass) {
        try {
            return domainClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("A domain class requires a public constructor"
                    + " without arguments: " + domainClass.getName(), e);
        }
    }
}
