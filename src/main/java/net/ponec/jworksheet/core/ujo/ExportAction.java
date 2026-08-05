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
 * A context which asks a domain object whether a key is exportable.
 * The enumeration replaces the {@code UjoAction} of the original Ujorm implementation.
 * @author Pavel Ponec
 */
public enum ExportAction {

    /** A serialization to the XML data file. */
    XML,
    /** A serialization to the configuration file of the application. */
    RESOURCE_BUNDLE,
    /** A presentation in a table of the graphical user interface. */
    TABLE_SHOW;
}
