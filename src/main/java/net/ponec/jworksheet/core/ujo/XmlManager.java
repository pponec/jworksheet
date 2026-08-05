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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.ujorm.annotation.Domain;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A serialization of the domain objects to an XML document and back.
 * <p>The Ujorm 3 does not offer any XML support, so the class reimplements the document
 * format of the original {@code org.ujorm.core.UjoManagerXML}. The format must be kept -
 * the users have got the data files written by the previous releases.
 * <p>The rules of the format:
 * <ul>
 * <li>the root element is the {@value #ROOT_ELEMENT},</li>
 * <li>an element is named by the key name, so by the {@code @Column} label of the field,</li>
 * <li>the elements are written in the order of the key declaration,</li>
 * <li>an undefined value is skipped, an empty text is written as a pair of the tags,</li>
 * <li>a key rejected by the {@link Exportable} interface of the domain object is skipped,</li>
 * <li>the indentation is two spaces per a level.</li>
 * </ul>
 *
 * @author Pavel Ponec
 */
public final class XmlManager {

    /** A name of the root element. */
    public static final String ROOT_ELEMENT = "body";

    /** A default comment of the document header. */
    public static final String DEFAULT_COMMENT = " A data file of the jWorkSheet application ";

    /** An indentation of a single level. */
    private static final String INDENT = "  ";

    /** A new line separator. The value is independent of the operating system
     * to keep the data file portable. */
    private static final String NEW_LINE = "\n";

    /** A cache of the element name to the key mapping. */
    private static final Map<Class<?>, Map<String, JKey<?, ?>>> ELEMENT_MAPS = new HashMap<>();

    private XmlManager() {
    }

    // ------------------------ EXPORT ------------------------

    /** Writes the domain object to an XML document with the default comment. */
    public static void saveXML(final Appendable out, final Object root) throws IOException {
        saveXML(out, root, DEFAULT_COMMENT);
    }

    /** Writes the domain object to an XML document.
     * @param out A target of the document.
     * @param root A domain object to export.
     * @param comment A body of the comment written behind the XML declaration.
     */
    public static void saveXML(final Appendable out, final Object root, final String comment)
            throws IOException {
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NEW_LINE);
        if (comment != null) {
            out.append("<!--").append(comment).append("-->").append(NEW_LINE);
        }
        writeElement(out, ROOT_ELEMENT, root, 0);
    }

    /** Writes the domain object to an XML file. */
    public static void saveXML(final File file, final Object root, final String comment)
            throws IOException {
        try (Writer out = new OutputStreamWriter
                (new BufferedOutputStream(new FileOutputStream(file)), UTF_8)) {
            saveXML(out, root, comment);
        }
    }

    /** Writes a single element including all its children. */
    private static void writeElement
            ( final Appendable out
            , final String name
            , final Object domain
            , final int level) throws IOException {

        indent(out, level).append('<').append(name).append('>').append(NEW_LINE);

        final Exportable filter = domain instanceof final Exportable e ? e : null;
        for (JKey<?, ?> key : keysOf(domain.getClass())) {
            final Object value = readValue(key, domain);
            if (value == null) {
                continue;
            }
            if (key instanceof final JListKey<?, ?> listKey) {
                for (Object item : (List<?>) value) {
                    if (item != null && isExportable(filter, key, item)) {
                        writeElement(out, key.getName(), item, level + 1);
                    }
                }
            } else if (isExportable(filter, key, value)) {
                if (isDomain(value.getClass())) {
                    writeElement(out, key.getName(), value, level + 1);
                } else {
                    indent(out, level + 1)
                            .append('<').append(key.getName()).append('>');
                    escape(out, ValueCoder.encode(value));
                    out.append("</").append(key.getName()).append('>').append(NEW_LINE);
                }
            }
        }

        indent(out, level).append("</").append(name).append('>');
        if (level > 0) {
            // The original implementation writes no new line behind the root element:
            out.append(NEW_LINE);
        }
    }

    private static boolean isExportable(final Exportable filter, final JKey<?, ?> key, final Object value) {
        return filter == null || filter.isExportable(ExportAction.XML, key, value);
    }

    private static Appendable indent(final Appendable out, final int level) throws IOException {
        for (int i = 0; i < level; i++) {
            out.append(INDENT);
        }
        return out;
    }

    /** Writes a text of an element content. */
    private static void escape(final Appendable out, final String text) throws IOException {
        for (int i = 0, max = text.length(); i < max; i++) {
            final char c = text.charAt(i);
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                default -> out.append(c);
            }
        }
    }

    // ------------------------ IMPORT ------------------------

    /** Reads a domain object from an XML document. */
    public static <T> T parseXML(final InputStream is, final Class<T> rootType) {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            final SAXParser parser = factory.newSAXParser();
            final RootHandler<T> handler = new RootHandler<>(rootType);
            parser.parse(new InputSource(is), handler);
            return handler.getRoot();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Can't parse the XML document to "
                    + rootType.getSimpleName(), e);
        }
    }

    /** Reads a domain object from an XML file. */
    public static <T> T parseXML(final File file, final Class<T> rootType) {
        try (InputStream is = new FileInputStream(file)) {
            return parseXML(is, rootType);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read the file: " + file, e);
        }
    }

    /** A SAX handler which builds the tree of the domain objects. */
    private static final class RootHandler<T> extends DefaultHandler {

        /** A type of the root domain object. */
        private final Class<T> rootType;

        /** The domain objects of the currently open elements. */
        private final Deque<Object> domains = new ArrayDeque<>();

        /** A text of the currently open scalar element, or {@code null}. */
        private StringBuilder text;

        /** A depth of the elements which are not recognized and so are skipped. */
        private int skipped;

        private T root;

        RootHandler(final Class<T> rootType) {
            this.rootType = rootType;
        }

        T getRoot() {
            return root != null ? root : newInstance(rootType);
        }

        @Override
        public void startElement(final String uri, final String localName
                , final String name, final Attributes attributes) {
            if (skipped > 0) {
                skipped++;
                return;
            }
            if (domains.isEmpty()) {
                root = newInstance(rootType);
                domains.push(root);
                return;
            }
            final Object parent = domains.peek();
            final JKey<?, ?> key = elementMapOf(parent.getClass()).get(name);
            if (key == null) {
                // An element of an unknown name is skipped including its children:
                skipped = 1;
                return;
            }
            if (key instanceof final JListKey<?, ?> listKey) {
                domains.push(newInstance(listKey.getItemType()));
            } else if (isDomain(key.getType())) {
                domains.push(newInstance(key.getType()));
            } else {
                text = new StringBuilder();
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) {
            if (text != null) {
                text.append(ch, start, length);
            }
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void endElement(final String uri, final String localName, final String name) {
            if (skipped > 0) {
                skipped--;
                return;
            }
            if (text != null) {
                final Object parent = domains.peek();
                final JKey key = elementMapOf(parent.getClass()).get(name);
                key.setValue(parent, ValueCoder.decode(key.getType(), text.toString()));
                text = null;
                return;
            }
            final Object child = domains.pop();
            if (domains.isEmpty()) {
                return; // The root element is closed
            }
            final Object parent = domains.peek();
            final JKey key = elementMapOf(parent.getClass()).get(name);
            if (key instanceof final JListKey listKey) {
                listKey.addItem(parent, child);
            } else {
                key.setValue(parent, child);
            }
        }
    }

    // ------------------------ TOOLS ------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object readValue(final JKey<?, ?> key, final Object domain) {
        return ((JKey) key).getValue(domain);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<JKey<?, ?>> keysOf(final Class<?> domainClass) {
        return (List) JKey.keysOf((Class) domainClass);
    }

    /** Returns a mapping of the element name to the key of a domain class. */
    private static Map<String, JKey<?, ?>> elementMapOf(final Class<?> domainClass) {
        synchronized (ELEMENT_MAPS) {
            Map<String, JKey<?, ?>> result = ELEMENT_MAPS.get(domainClass);
            if (result == null) {
                result = new HashMap<>();
                for (JKey<?, ?> key : keysOf(domainClass)) {
                    result.put(key.getName(), key);
                }
                ELEMENT_MAPS.put(domainClass, result);
            }
            return result;
        }
    }

    /** Is the class a domain object with its own keys? */
    private static boolean isDomain(final Class<?> type) {
        return type.isAnnotationPresent(Domain.class);
    }

    private static <T> T newInstance(final Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("A domain class requires a public constructor"
                    + " without arguments: " + type.getName(), e);
        }
    }
}
