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

/**
 * A domain object which filters its own keys on an export.
 * The interface replaces the {@code Ujo.readAuthorization()} method of the original
 * Ujorm implementation.
 * @author Pavel Ponec
 */
public interface Exportable {

    /** Is the value of the key exportable in the required context?
     * <p>An item of a list is offered by a repeated call where the {@code key} is the list key
     * and the {@code value} is the item, so a whole item can be skipped.
     *
     * @param action A context of the export.
     * @param key A key of this domain object.
     * @param value A value of the key, or an item of a list.
     * @return The {@code false} value skips the key (or the list item) on the export.
     */
    default boolean isExportable(final ExportAction action, final JKey<?, ?> key, final Object value) {
        return true;
    }
}
