/*
 *  Copyright 2007-2010 Pavel Ponec
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ujorm.swing;

import org.ujorm.Ujo;
import org.ujorm.Key;
import org.ujorm.core.UjoActionImpl;
import org.ujorm.core.UjoManager;
import org.ujorm.UjoAction;
import org.ujorm.core.KeyFactory;
import org.ujorm.extensions.UjoTextable;
import org.ujorm.implementation.map.MapUjoExt;

/**
 * An implementation of TableModel for List of Ujo objects.
 * <br>An typical usage is an preview of Key list of the one Ujo object include values.
 *
 * @author Pavel Ponec
 */
@SuppressWarnings("unchecked")
public class KeyRow extends MapUjoExt<KeyRow> {

    private static final KeyFactory<KeyRow> f = KeyFactory.CamelBuilder.get(KeyRow.class);

    /** Index of property */
    public static final Key<KeyRow,Integer> P_INDEX   = f.newKey("Index");
    /** Name of property */
    public static final Key<KeyRow,String> P_NAME     = f.newKey("Name");
    /** Type of property */
    public static final Key<KeyRow,Class>  P_TYPE     = f.newKey("Class");
    /** Class name without packages */
    public static final Key<KeyRow,String> P_TYPENAME = f.newKey("Type");
    /** Value */
    public static final Key<KeyRow,Object> P_VALUE    = f.newKey("Value");
    /** Text Value */
    public static final Key<KeyRow,String> P_TEXT     = f.newKey("Text");
    /** Default Value */
    public static final Key<KeyRow,Object> P_DEFAULT  = f.newKey("Default");
    /** A user column can be used in table renderer for any purpose */
    public static final Key<KeyRow,Object> P_USER1    = f.newKey("User1");
    /** A user column can be used in table renderer for any purpose */
    public static final Key<KeyRow,Object> P_USER2    = f.newKey("User2");

    static { f.lock(); }

    final protected Ujo content;
    final protected Key property;

    public KeyRow(Ujo content, Key property) {
        this.content = content;
        this.property = property;
    }

    /** Write value */
    @Override
    public void writeValue(Key aKey, Object value) {
        if (aKey==P_VALUE) {
            content.writeValue(property, value);
        } else if (aKey==P_TEXT) {
            UjoManager.getInstance().setText(content, property, (String) value, null, new UjoActionImpl(this));
        } else {
            throw new UnsupportedOperationException("Can't write property " + property);
        }
    }

    /** Write a text value. */
    @Override
    public void writeValueString(Key aKey, String value, Class subtype, UjoAction action) {
        if (aKey==P_VALUE) {
            if (content instanceof UjoTextable) {
                ((UjoTextable) content).writeValueString(property, value, subtype, action);
            } else {
                final Object objValue = readUjoManager().decodeValue(property, value, subtype);
                content.writeValue(property, objValue);
            }
        } else {
            throw new UnsupportedOperationException("Can't write property " + property);
        }
    }

    /** Read Value */
    @Override
    public Object readValue(final Key aKey) {
        if (aKey==P_INDEX)   { return property.getIndex(); }
        if (aKey==P_NAME)    { return property.getName(); }
        if (aKey==P_TYPE)    { return property.getType(); }
        if (aKey==P_DEFAULT) { return property.getDefault(); }
        if (aKey==P_VALUE)   { return property.getValue(content); }
        if (aKey==P_TEXT)    { return UjoManager.getInstance().getText(content, property, new UjoActionImpl(this)); }
        if (aKey==P_TYPENAME){
            final String result = property.getType().getName();
            final int i = 1 + result.lastIndexOf('.');
            return result.substring(i);
        }
        throw new UnsupportedOperationException("Can't read property " + property);
    }

    /** Returns an assigned property (a parameter e.g.) */
    public final Key getKey() {
        return property;
    }

    /** Key name + value */
    @Override
    public String toString() {
       final String result = property.getName() + ":" + property.of(content);
       return result;
    }

}

