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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A serialization of a domain object to a {@link Properties} file and back.
 * <p>The Ujorm 3 does not offer any resource bundle support, so the class reimplements
 * the behaviour of the original {@code org.ujorm.core.UjoManagerRBundle}. The format must
 * be kept - the users have got the configuration files written by the previous releases.
 * <p>Only the keys rejected by the {@link Exportable} interface are skipped, so the file
 * of the application contains the modified values only.
 *
 * @author Pavel Ponec
 */
public final class BundleManager {

    private BundleManager() {
    }

    /** Writes the domain object to a properties file.
     * @param file A target file.
     * @param domain A domain object to export.
     * @param comment A header comment of the file.
     */
    public static <D> void save(final File file, final D domain, final String comment)
            throws IOException {
        final Properties properties = new Properties();
        final Exportable filter = domain instanceof final Exportable e ? e : null;

        for (JKey<D, ?> key : keysOf(domain)) {
            final Object value = key.getValue(domain);
            if (value == null) {
                continue;
            }
            if (filter == null || filter.isExportable(ExportAction.RESOURCE_BUNDLE, key, value)) {
                properties.setProperty(key.getName(), ValueCoder.encode(value));
            }
        }
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            properties.store(os, comment);
        }
    }

    /** Reads a domain object from a properties file. The keys missing in the file keep
     * their default values. */
    public static <D> D load(final File file, final Class<D> domainClass) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return load(is, domainClass);
        }
    }

    /** Reads a domain object from a properties stream. The keys missing in the stream keep
     * their default values. */
    public static <D> D load(final InputStream is, final Class<D> domainClass) throws IOException {
        final Properties properties = new Properties();
        properties.load(is);

        final D result = newInstance(domainClass);
        for (JKey<D, ?> key : JKey.keysOf(domainClass)) {
            final String text = properties.getProperty(key.getName());
            if (text != null) {
                setValue(key, result, ValueCoder.decode(key.getType(), text));
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <D> void setValue(final JKey<D, ?> key, final D domain, final Object value) {
        ((JKey) key).setValue(domain, value);
    }

    @SuppressWarnings("unchecked")
    private static <D> Iterable<JKey<D, ?>> keysOf(final D domain) {
        return JKey.keysOf((Class<D>) domain.getClass());
    }

    private static <D> D newInstance(final Class<D> domainClass) {
        try {
            return domainClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("A domain class requires a public constructor"
                    + " without arguments: " + domainClass.getName(), e);
        }
    }
}
