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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.ujorm.core.Key;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.helpers.DefaultHandler;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A serialization of the domain objects to an XML document and back.
 * <p>The Ujorm 3 does not offer any XML support, so the class reimplements the document
 * format of the original {@code org.ujorm.core.UjoManagerXML}. The data must be kept -
 * the users have got the data files written by the previous releases.
 * <p>The rules of the format:
 * <ul>
 * <li>the root element is the {@value #ROOT_ELEMENT},</li>
 * <li>an element is named by the key name, so by the {@code @UColumn} label of the field,</li>
 * <li>the elements are written in the order of the key declaration,</li>
 * <li>an undefined value is skipped, an empty text is written as a pair of the tags,</li>
 * <li>a key rejected by the {@link Exportable} interface of the domain object is skipped,</li>
 * <li>the elements are indented by a tab per a level.</li>
 * </ul>
 *
 * @author Pavel Ponec
 */
public final class XmlManager {

    /** A name of the root element. */
    public static final String ROOT_ELEMENT = "body";

    /** A default comment of the document header. */
    public static final String DEFAULT_COMMENT = " A data file of the jWorkSheet application ";

    private XmlManager() {
    }

    // ------------------------ EXPORT ------------------------

    /** Writes the domain object to an XML document with the default comment. */
    public static void saveXML(final Appendable out, final DomainObject root) throws IOException {
        saveXML(out, root, DEFAULT_COMMENT);
    }

    /** Writes the domain object to an XML document.
     * @param out A target of the document.
     * @param root A domain object to export.
     * @param comment An optional body of the comment written behind the XML declaration.
     */
    public static void saveXML(final Appendable out, final DomainObject root, final String comment)
            throws IOException {
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (comment != null) {
            out.append("<!--").append(comment).append("-->\n");
        }
        write(out, ROOT_ELEMENT, root, "");
    }

    /** Writes the domain object to an XML file. */
    public static void saveXML(final File file, final DomainObject root, final String comment)
            throws IOException {
        try (Writer out = Files.newBufferedWriter(file.toPath(), UTF_8)) {
            saveXML(out, root, comment);
        }
    }

    /** Writes a single element including all its children. */
    private static void write(final Appendable out, final String name, final DomainObject domain,
            final String indent) throws IOException {
        out.append(indent).append('<').append(name).append(">\n");
        for (Key<Object, Object> key : Keys.keysOf(domain)) {
            final Object value = key.getValue(domain);
            final String element = Keys.nameOf(key);
            for (Object item : value == null ? List.of() : Keys.isList(key) ? (List<?>) value : List.of(value)) {
                if (item == null || !Exportable.isExportable(domain, ExportAction.XML, key, item)) {
                    continue;
                }
                if (item instanceof final DomainObject child) {
                    write(out, element, child, indent + '\t');
                } else {
                    out.append(indent).append("\t<").append(element).append('>')
                       .append(escape(ValueCoder.encode(item)))
                       .append("</").append(element).append(">\n");
                }
            }
        }
        out.append(indent).append("</").append(name).append(">\n");
    }

    /** Escapes a text of an element. The carriage return is escaped too,
     * because a parser normalizes the line ends of the text otherwise. */
    private static String escape(final String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\r", "&#13;");
    }

    // ------------------------ IMPORT ------------------------

    /** Reads a domain object from an XML document. The name of the root element is not checked,
     * an element of an unknown name (the names are case sensitive) is skipped including its children. */
    public static <T extends DomainObject> T parseXML(final InputStream is, final Class<T> rootType) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // No document type is expected, so the external entities can not be abused (XXE):
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            // The default handler would print a fatal error to the console, the exception is enough:
            builder.setErrorHandler(new DefaultHandler());
            return read(builder.parse(is).getDocumentElement(), rootType);
        } catch (Exception e) {
            throw new IllegalStateException("Can't parse the XML document to " + rootType.getSimpleName(), e);
        }
    }

    /** Reads a domain object from an XML file. */
    public static <T extends DomainObject> T parseXML(final File file, final Class<T> rootType) {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            return parseXML(is, rootType);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read the file: " + file, e);
        }
    }

    /** Creates a domain object of the type and fills it by the child elements. */
    @SuppressWarnings("unchecked")
    private static <T extends DomainObject> T read(final Element element, final Class<T> type) {
        final T result = Keys.newInstance(type);
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof final Element child) {
                Keys.findKey(result, child.getTagName()).ifPresent(key -> {
                    if (Keys.isList(key)) {
                        ((List<Object>) key.getValue(result)).add(read(child, Keys.itemTypeOf(key).asSubclass(DomainObject.class)));
                    } else if (Keys.isDomain(key.type())) {
                        key.setValue(result, read(child, key.type().asSubclass(DomainObject.class)));
                    } else {
                        key.setValue(result, ValueCoder.decode(key, child.getTextContent()));
                    }
                });
            }
        }
        return result;
    }
}
