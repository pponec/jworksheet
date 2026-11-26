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

package net.ponec.jworksheet.gui.models;

import net.ponec.jworksheet.core.ujo.DomainObject;
import org.ujorm.annotation.UColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.ponec.jworksheet.core.ujo.Keys;
import org.ujorm.annotation.Domain;
import org.ujorm.core.Key;
import net.ponec.jworksheet.core.ujo.MetaAliases._UR;

/**
 * A single row of a table which presents the keys of one domain object including their values.
 * The class replaces the {@code org.ujorm.swing.UjoKeyRow} of the original Ujorm implementation.
 * <p>The fields below are declarations of the columns only - all the accessors are computed
 * from the wrapped domain object and its key.
 *
 * @author Pavel Ponec
 */
@Domain
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class UjoKeyRow implements DomainObject {

    @UColumn(name = "Index")
    private Integer index;

    @UColumn(name = "Name")
    private String name;

    @UColumn(name = "Class")
    private Class type;

    @UColumn(name = "Type")
    private String typeName;

    @UColumn(name = "Value")
    private Object value;

    @UColumn(name = "Default")
    private Object defaultValue;

    /** A domain object of the presented key. */
    @Getter
    private final transient Object content;

    /** A presented key of the domain object. */
    @Getter
    private final transient Key key;

    // ------------------------ THE COMPUTED ACCESSORS ------------------------

    public Integer getIndex() {
        return key != null ? (int) key.index() : null;
    }

    public void setIndex(final Integer index) {
        throw new UnsupportedOperationException("Read only: " + _UR.index);
    }

    public String getName() {
        return key != null ? Keys.nameOf(key) : null;
    }

    public void setName(final String name) {
        throw new UnsupportedOperationException("Read only: " + _UR.name);
    }

    public Class getType() {
        return key != null ? Keys.typeOf(key) : null;
    }

    public void setType(final Class type) {
        throw new UnsupportedOperationException("Read only: " + _UR.type);
    }

    /** A simple name of the value type. */
    public String getTypeName() {
        if (key == null) {
            return null;
        }
        final String result = Keys.typeOf(key).getName();
        return result.substring(1 + result.lastIndexOf('.'));
    }

    public void setTypeName(final String typeName) {
        throw new UnsupportedOperationException("Read only: " + _UR.typeName);
    }

    /** Reads the value of the key from the domain object. */
    public Object getValue() {
        return key != null ? key.getValue(content) : null;
    }

    /** Writes the value of the key to the domain object. */
    public void setValue(final Object value) {
        key.setValue(content, value);
    }

    public Object getDefaultValue() {
        return key != null ? Keys.defaultOf(key) : null;
    }

    /** Restores the default value of the key. The {@code Key.getDefault()} method
     * of the Ujorm 3 always provides the {@code null}, so it must not be used here. */
    public void resetValue() {
        setValue(getDefaultValue());
    }

    public void setDefaultValue(final Object defaultValue) {
        throw new UnsupportedOperationException("Read only: " + _UR.defaultValue);
    }

    // ------------------------ TOOLS ------------------------

    @Override
    public String toString() {
        return key != null ? Keys.nameOf(key) + ":" + getValue() : "?";
    }
}
