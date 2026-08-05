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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.ujorm.core.Key;
import org.ujorm.core.KeyInfo;

/**
 * An application key of a domain object. The class wraps a {@link Key} generated
 * by the Ujorm annotation processor and adds the features the application needs
 * but the Ujorm 3 does not provide:
 * <ul>
 * <li>a <strong>default value</strong> resolved from a prototype instance of the domain class,</li>
 * <li>a <strong>legacy name</strong> shared by the XML documents, the configuration file
 *     and the localization bundles (the {@code @Column} label),</li>
 * <li>an optional <strong>converter</strong> between the exposed value type and the stored one,
 *     so a memory optimized field can be published under a rich type
 *     (see {@code Event.P_TIME}: a {@code short} field exposed as a {@code Time}),</li>
 * <li>a <strong>null normalization</strong> of the keys backed by a primitive field.</li>
 * </ul>
 * The keys are singletons, so they can be compared by the {@code ==} operator.
 *
 * @param <D> a type of the domain class
 * @param <V> a type of the published value
 * @author Pavel Ponec
 */
public class JKey<D, V> implements Key<D, V> {

    /** A cache of the prototypes to resolve the default values. */
    private static final Map<Class<?>, Object> PROTOTYPES = new HashMap<>();

    /** All the keys of a domain class in the order of their declaration. */
    private static final Map<Class<?>, List<JKey<?, ?>>> REGISTRY = new HashMap<>();

    /** The generated key. Its value type differs from the {@code V} when a converter is assigned. */
    private final Key<D, Object> key;

    /** A type of the published value. A primitive type of the key is published as its wrapper. */
    private final Class<V> type;

    /** Is the wrapped key backed by a field of a primitive type? */
    private final boolean primitive;

    /** A converter of the published value to the stored one. The {@code null} means an identity. */
    private final Function<V, Object> encoder;

    /** A converter of the stored value to the published one. The {@code null} means an identity. */
    private final Function<Object, V> decoder;

    /** A default value resolved lazily from a prototype of the domain class. */
    private V defaultValue;

    /** Was the {@link #defaultValue} resolved yet? */
    private boolean defaultResolved;

    @SuppressWarnings("unchecked")
    protected JKey
            ( final Key<D, ?> key
            , final Class<V> type
            , final Function<V, Object> encoder
            , final Function<Object, V> decoder) {
        this.key = (Key<D, Object>) key;
        this.primitive = key.type().isPrimitive();
        this.type = type != null ? type : (Class<V>) box(key.type());
        this.encoder = encoder;
        this.decoder = decoder;
        register(this);
    }

    /** Registers the key to its domain class. The static initializer of a domain class runs
     * the key declarations in their source order, so the registry keeps that order too. */
    private static void register(final JKey<?, ?> key) {
        synchronized (REGISTRY) {
            REGISTRY.computeIfAbsent(key.domainClass(), c -> new ArrayList<>()).add(key);
        }
    }

    /** Returns all the keys of a domain class in the order of their declaration. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <D> List<JKey<D, ?>> keysOf(final Class<D> domainClass) {
        try {
            // The keys are registered by the static initializer of the domain class:
            Class.forName(domainClass.getName(), true, domainClass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't initialize the class: " + domainClass.getName(), e);
        }
        synchronized (REGISTRY) {
            final List<JKey<?, ?>> result = REGISTRY.get(domainClass);
            if (result == null) {
                throw new IllegalStateException("No key was declared by the class: " + domainClass.getName());
            }
            return (List) Collections.unmodifiableList(new ArrayList<>(result));
        }
    }

    // ------------------------ FACTORY ------------------------

    /** A key published under the type of the domain field. */
    public static <D, V> JKey<D, V> of(final Key<D, V> key) {
        return new JKey<>(key, null, null, null);
    }

    /** A key published under a different type than the domain field uses.
     * @param key A key generated from the domain field.
     * @param type A type of the published value.
     * @param encoder Converts the published value to the stored one.
     * @param decoder Converts the stored value to the published one.
     */
    @SuppressWarnings("unchecked")
    public static <D, V, S> JKey<D, V> of
            ( final Key<D, S> key
            , final Class<V> type
            , final Function<V, S> encoder
            , final Function<S, V> decoder) {
        return new JKey<>(key, type, (Function<V, Object>) encoder, (Function<Object, V>) decoder);
    }

    // ------------------------ VALUE ACCESS ------------------------

    /** Returns a value of the domain object. */
    @Override
    public V getValue(final D domain) {
        final Object result = key.getValue(domain);
        return result != null && decoder != null ? decoder.apply(result) : cast(result);
    }

    /** Returns a value of the domain object. An alias for the {@link #getValue(java.lang.Object)}
     * kept for a compatibility with the original API. */
    public V of(final D domain) {
        return getValue(domain);
    }

    /** Assigns a value to the domain object.
     * A {@code null} is normalized to a zero value for the keys backed by a primitive field. */
    @Override
    public void setValue(final D domain, final V value) {
        Object result = value != null && encoder != null ? encoder.apply(value) : value;
        if (result == null && primitive) {
            result = zeroOf(key.type());
        }
        key.setValue(domain, result);
    }

    /** Copies a value of the key from one domain object to another one. */
    public void copy(final D from, final D to) {
        setValue(to, getValue(from));
    }

    /** Has the domain object the default value of this key? */
    public boolean isDefault(final D domain) {
        return Objects.equals(getValue(domain), getDefault());
    }

    /** Has the domain object the required value of this key? */
    @Override
    public boolean equals(final D domain, final V value) {
        return Objects.equals(getValue(domain), value);
    }

    // ------------------------ METADATA ------------------------

    /** Returns a name of the key. The name is used by the XML documents, the configuration
     * file and the localization bundles, so it is the {@code @Column} label of the domain
     * field rather than the Java field name. */
    @Override
    public String name() {
        return key.info().columnLabel();
    }

    /** Returns a name of the key. An alias for the {@link #name()} kept for a compatibility
     * with the original API. */
    public String getName() {
        return name();
    }

    /** Returns a type of the published value. */
    @Override
    public Class<V> type() {
        return type;
    }

    /** Returns a type of the published value. An alias for the {@link #type()} kept for
     * a compatibility with the original API. */
    public Class<V> getType() {
        return type;
    }

    /** Is the published type assignable to the required class? */
    @Override
    public boolean isTypeOf(final Class<?> requiredType) {
        return requiredType.isAssignableFrom(type);
    }

    @Override
    public Class<D> domainClass() {
        return key.domainClass();
    }

    @Override
    public short index() {
        return key.index();
    }

    /** An alias for the {@link #index()} kept for a compatibility with the original API. */
    public int getIndex() {
        return key.index();
    }

    @Override
    public KeyInfo info() {
        return key.info();
    }

    /** Returns the wrapped key generated by the Ujorm annotation processor. */
    public Key<D, ?> ujoKey() {
        return key;
    }

    /** Is the key backed by a field of a primitive type? */
    public boolean isPrimitive() {
        return primitive;
    }

    // ------------------------ DEFAULT VALUE ------------------------

    /** Returns a default value of the key.
     * <p>The Ujorm 3 does not know the default values, so the value is read from a prototype
     * instance of the domain class - the field initializers are the single source of the truth.
     * The prototype is created lazily and the result is cached, so a dynamic default
     * (a system locale or a system login) is evaluated exactly once. */
    @Override
    public V getDefault() {
        if (!defaultResolved) {
            defaultValue = getValue(prototypeOf(domainClass()));
            defaultResolved = true;
        }
        return defaultValue;
    }

    /** An alias for the {@link #getDefault()} required by the {@link Key} interface. */
    @Override
    public V getDefaultValue() {
        return getDefault();
    }

    /** Returns a shared prototype of the domain class to read the default values from. */
    @SuppressWarnings("unchecked")
    private static <D> D prototypeOf(final Class<D> domainClass) {
        synchronized (PROTOTYPES) {
            Object result = PROTOTYPES.get(domainClass);
            if (result == null) {
                try {
                    result = domainClass.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("A domain class requires a public constructor"
                            + " without arguments: " + domainClass.getName(), e);
                }
                PROTOTYPES.put(domainClass, result);
            }
            return (D) result;
        }
    }

    // ------------------------ TOOLS ------------------------

    @Override
    public Key<D, V> self() {
        return this;
    }

    @Override
    public String toString() {
        return name();
    }

    @SuppressWarnings("unchecked")
    private V cast(final Object value) {
        return (V) value;
    }

    /** Returns a wrapper of the primitive type, or the original class. */
    private static Class<?> box(final Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == boolean.class) { return Boolean.class; }
        if (type == byte.class) { return Byte.class; }
        if (type == char.class) { return Character.class; }
        if (type == short.class) { return Short.class; }
        if (type == int.class) { return Integer.class; }
        if (type == long.class) { return Long.class; }
        if (type == float.class) { return Float.class; }
        if (type == double.class) { return Double.class; }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    /** Returns a zero value of the primitive type. */
    private static Object zeroOf(final Class<?> type) {
        if (type == boolean.class) { return Boolean.FALSE; }
        if (type == byte.class) { return (byte) 0; }
        if (type == char.class) { return (char) 0; }
        if (type == short.class) { return (short) 0; }
        if (type == int.class) { return 0; }
        if (type == long.class) { return 0L; }
        if (type == float.class) { return 0f; }
        if (type == double.class) { return 0d; }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
