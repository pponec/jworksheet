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
import java.util.Comparator;
import java.util.List;
import org.ujorm.core.Key;

/**
 * A key of a list of the domain objects.
 * <p>The Ujorm 3 describes a collection field by a raw {@code Key<D,List>} only, so the type
 * of the item is carried by this class - the XML serialization needs it to create the items
 * while parsing a document.
 *
 * @param <D> a type of the domain class
 * @param <I> a type of the list item
 * @author Pavel Ponec
 */
public class JListKey<D, I> extends JKey<D, List<I>> {

    /** A type of the list item. */
    private final Class<I> itemType;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected JListKey(final Key<D, ?> key, final Class<I> itemType) {
        super(key, (Class) List.class, null, null);
        this.itemType = itemType;
    }

    /** Creates a key of a list of the domain objects.
     * @param key A key generated from the collection field.
     * @param itemType A type of the list item.
     */
    @SuppressWarnings("rawtypes")
    public static <D, I> JListKey<D, I> of(final Key<D, List> key, final Class<I> itemType) {
        return new JListKey<>(key, itemType);
    }

    /** Returns a type of the list item. */
    public Class<I> getItemType() {
        return itemType;
    }

    /** Returns a list of the items. The result is never {@code null} - an undefined list
     * is created and assigned to the domain object. */
    public List<I> getList(final D domain) {
        List<I> result = getValue(domain);
        if (result == null) {
            result = new ArrayList<>();
            setValue(domain, result);
        }
        return result;
    }

    /** Returns a count of the items. */
    public int getItemCount(final D domain) {
        final List<I> result = getValue(domain);
        return result != null ? result.size() : 0;
    }

    /** Returns an item of the required index. */
    public I getItem(final D domain, final int index) {
        return getList(domain).get(index);
    }

    /** Appends an item to the end of the list. */
    public void addItem(final D domain, final I item) {
        getList(domain).add(item);
    }

    /** Removes the item from the list.
     * @return Was the item available in the list? */
    public boolean removeItem(final D domain, final I item) {
        return getList(domain).remove(item);
    }

    /** Sorts the items by the values of the required key. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sort(final D domain, final JKey<? super I, ?> sortKey) {
        getList(domain).sort(Comparator.comparing
                ( item -> (Comparable) sortKey.getValue(item)
                , Comparator.nullsFirst(Comparator.naturalOrder())));
    }
}
