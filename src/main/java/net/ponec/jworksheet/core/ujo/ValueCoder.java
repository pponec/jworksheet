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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * A converter between a value and its text representation.
 * <p>The Ujorm 3 does not offer any text serialization, so the class reimplements the format
 * of the original {@code org.ujorm.core.UjoCoder} for the types used by this application.
 * The format must be kept - the users have got the data files and the configuration files
 * written by the previous releases.
 * <p>A type which is not listed explicitly is encoded by its {@code toString()} method
 * and decoded by its constructor with a single {@link String} argument. The rule covers
 * the {@code Time}, the {@code YearMonthDay} and the {@code Version} value types.
 *
 * @author Pavel Ponec
 */
public final class ValueCoder {

    /** A separator of the {@link Locale} items. */
    private static final String LOCALE_SEPARATOR = "-";

    /** A separator of the {@link Rectangle} and the {@link Dimension} items. */
    private static final String SHAPE_SEPARATOR = ",";

    /** A format of the {@link Date} values. The {@link SimpleDateFormat} is not thread safe. */
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial
            (() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH));

    private ValueCoder() {
    }

    // ------------------------ ENCODING ------------------------

    /** Converts a value to its text representation.
     * @return The {@code null} value for an undefined argument. */
    public static String encode(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof final String result) {
            return result;
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        if (value instanceof final Character result) {
            return Integer.toString(result);
        }
        if (value instanceof final Locale result) {
            return encodeLocale(result);
        }
        if (value instanceof final Date result) {
            return DATE_FORMAT.get().format(result);
        }
        if (value instanceof final Color result) {
            return Integer.toHexString(result.getRGB() & 0xffffff | 0x1000000).substring(1).toUpperCase();
        }
        if (value instanceof final File result) {
            return result.getPath();
        }
        if (value instanceof final Enum<?> result) {
            return result.name();
        }
        if (value instanceof final Rectangle r) {
            return r.x + SHAPE_SEPARATOR + r.y + SHAPE_SEPARATOR + r.width + SHAPE_SEPARATOR + r.height;
        }
        if (value instanceof final Dimension d) {
            return d.width + SHAPE_SEPARATOR + d.height;
        }
        return value.toString();
    }

    /** Sample: {@code cs-CZ} */
    private static String encodeLocale(final Locale locale) {
        final StringBuilder result = new StringBuilder(10);
        result.append(locale.getLanguage());
        if (!locale.getCountry().isEmpty()) {
            result.append(LOCALE_SEPARATOR).append(locale.getCountry());
            if (!locale.getVariant().isEmpty()) {
                result.append(LOCALE_SEPARATOR).append(locale.getVariant());
            }
        }
        return result.toString();
    }

    // ------------------------ DECODING ------------------------

    /** Converts a text to a value of the required type.
     * @return The {@code null} value for an undefined text, or for an empty text
     *         where the required type is not a {@link String}. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object decode(final Class<?> requiredType, final String text) {
        final Class<?> type = box(requiredType);
        if (text == null || String.class == type) {
            return text;
        }
        if (text.isEmpty()) {
            return null;
        }
        try {
            if (Integer.class == type) { return Integer.valueOf(text); }
            if (Short.class == type) { return Short.valueOf(text); }
            if (Long.class == type) { return Long.valueOf(text); }
            if (Float.class == type) { return Float.valueOf(text); }
            if (Double.class == type) { return Double.valueOf(text); }
            if (Byte.class == type) { return Byte.valueOf(text); }
            if (BigDecimal.class == type) { return new BigDecimal(text); }
            if (Boolean.class == type) {
                return Boolean.parseBoolean(text) || "1".equals(text);
            }
            if (Character.class == type) {
                return (char) Integer.parseInt(text);
            }
            if (Locale.class == type) {
                return decodeLocale(text);
            }
            if (Date.class.isAssignableFrom(type)) {
                return DATE_FORMAT.get().parse(text);
            }
            if (Color.class.isAssignableFrom(type)) {
                return new Color(Integer.parseInt(text, 16));
            }
            if (File.class.isAssignableFrom(type)) {
                return new File(text);
            }
            if (Rectangle.class.isAssignableFrom(type)) {
                final int[] i = decodeShape(text, 4);
                return new Rectangle(i[0], i[1], i[2], i[3]);
            }
            if (Dimension.class.isAssignableFrom(type)) {
                final int[] i = decodeShape(text, 2);
                return new Dimension(i[0], i[1]);
            }
            if (type.isEnum()) {
                // The original implementation accepted a lower case text too:
                return Enum.valueOf((Class<Enum>) type, text.toUpperCase(Locale.ENGLISH));
            }
            final Constructor<?> constructor = type.getConstructor(String.class);
            return constructor.newInstance(text);
        } catch (ParseException | ReflectiveOperationException | RuntimeException e) {
            throw new IllegalArgumentException("Can't decode \"" + text + "\" to " + type.getName(), e);
        }
    }

    /** Sample: {@code cs-CZ} */
    private static Locale decodeLocale(final String text) {
        final StringTokenizer tokenizer = new StringTokenizer(text, LOCALE_SEPARATOR);
        final String language = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
        final String country = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
        final String variant = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
        return new Locale(language, country, variant);
    }

    /** Sample: {@code 649,397,622,405} */
    private static int[] decodeShape(final String text, final int itemCount) {
        final StringTokenizer tokenizer = new StringTokenizer(text, SHAPE_SEPARATOR);
        final int[] result = new int[itemCount];
        for (int i = 0; i < itemCount; i++) {
            result[i] = Integer.parseInt(tokenizer.nextToken().trim());
        }
        return result;
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
}
