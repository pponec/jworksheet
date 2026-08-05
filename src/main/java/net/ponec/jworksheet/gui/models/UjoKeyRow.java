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

import jakarta.persistence.Column;
import net.ponec.jworksheet.core.ujo.JKey;
import net.ponec.jworksheet.gui.models.meta.MetaUjoKeyRow;
import org.ujorm.annotation.Domain;

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
public class UjoKeyRow {

    /** Index of key */
    public static final JKey<UjoKeyRow,Integer> P_INDEX   = JKey.of(MetaUjoKeyRow.index);
    /** Name of key */
    public static final JKey<UjoKeyRow,String> P_NAME     = JKey.of(MetaUjoKeyRow.name);
    /** Type of key */
    public static final JKey<UjoKeyRow,Class> P_TYPE      = JKey.of(MetaUjoKeyRow.type);
    /** Class name without packages */
    public static final JKey<UjoKeyRow,String> P_TYPENAME = JKey.of(MetaUjoKeyRow.typeName);
    /** Value */
    public static final JKey<UjoKeyRow,Object> P_VALUE    = JKey.of(MetaUjoKeyRow.value);
    /** Default Value */
    public static final JKey<UjoKeyRow,Object> P_DEFAULT  = JKey.of(MetaUjoKeyRow.defaultValue);

    @Column(name = "Index")
    private Integer index;

    @Column(name = "Name")
    private String name;

    @Column(name = "Class")
    private Class type;

    @Column(name = "Type")
    private String typeName;

    @Column(name = "Value")
    private Object value;

    @Column(name = "Default")
    private Object defaultValue;

    /** A domain object of the presented key. */
    private final transient Object content;

    /** A presented key of the domain object. */
    private final transient JKey key;

    /** A constructor required by the metamodel. The instance presents no key. */
    public UjoKeyRow() {
        this(null, null);
    }

    public UjoKeyRow(final Object content, final JKey key) {
        this.content = content;
        this.key = key;
    }

    // ------------------------ THE COMPUTED ACCESSORS ------------------------

    public Integer getIndex() {
        return key != null ? key.getIndex() : null;
    }

    public void setIndex(final Integer index) {
        throw new UnsupportedOperationException("Read only: " + P_INDEX);
    }

    public String getName() {
        return key != null ? key.getName() : null;
    }

    public void setName(final String name) {
        throw new UnsupportedOperationException("Read only: " + P_NAME);
    }

    public Class getType() {
        return key != null ? key.getType() : null;
    }

    public void setType(final Class type) {
        throw new UnsupportedOperationException("Read only: " + P_TYPE);
    }

    /** A simple name of the value type. */
    public String getTypeName() {
        if (key == null) {
            return null;
        }
        final String result = key.getType().getName();
        return result.substring(1 + result.lastIndexOf('.'));
    }

    public void setTypeName(final String typeName) {
        throw new UnsupportedOperationException("Read only: " + P_TYPENAME);
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
        return key != null ? key.getDefault() : null;
    }

    public void setDefaultValue(final Object defaultValue) {
        throw new UnsupportedOperationException("Read only: " + P_DEFAULT);
    }

    // ------------------------ TOOLS ------------------------

    /** Returns an assigned key (a parameter e.g.) */
    public JKey getKey() {
        return key;
    }

    /** Returns the domain object of the presented key. */
    public Object getContent() {
        return content;
    }

    @Override
    public String toString() {
        return key != null ? key.getName() + ":" + getValue() : "?";
    }
}
