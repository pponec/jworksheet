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
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.ujorm.core.Key;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
 * <li>an element is named by the key name, so by the {@code @UColumn} label of the field,</li>
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
     * @param comment A body of the comment written behind the XML declaration.
     */
    public static void saveXML(final Appendable out, final DomainObject root, final String comment)
            throws IOException {
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NEW_LINE);
        if (comment != null) {
            out.append("<!--").append(comment).append("-->").append(NEW_LINE);
        }
        writeElement(out, ROOT_ELEMENT, root, 0);
    }

    /** Writes the domain object to an XML file. */
    public static void saveXML(final File file, final DomainObject root, final String comment)
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
            , final DomainObject domain
            , final int level) throws IOException {

        indent(out, level).append('<').append(name).append('>').append(NEW_LINE);

        for (Key<Object, Object> key : Keys.keysOf(domain)) {
            final Object value = key.getValue(domain);
            if (value == null) {
                continue;
            }
            final String element = Keys.nameOf(key);
            if (Keys.isList(key)) {
                for (Object item : (List<?>) value) {
                    if (item != null && Exportable.isExportable(domain, ExportAction.XML, key, item)) {
                        writeElement(out, element, (DomainObject) item, level + 1);
                    }
                }
            } else if (Exportable.isExportable(domain, ExportAction.XML, key, value)) {
                if (value instanceof final DomainObject child) {
                    writeElement(out, element, child, level + 1);
                } else {
                    indent(out, level + 1).append('<').append(element).append('>');
                    escape(out, ValueCoder.encode(key, value));
                    out.append("</").append(element).append('>').append(NEW_LINE);
                }
            }
        }

        indent(out, level).append("</").append(name).append('>');
        if (level > 0) {
            // The original implementation writes no new line behind the root element:
            out.append(NEW_LINE);
        }
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
            final Element root = builder.parse(new InputSource(is)).getDocumentElement();
            return read(root, rootType);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Can't parse the XML document to "
                    + rootType.getSimpleName(), e);
        }
    }

    /** Reads a domain object from an XML file. */
    public static <T extends DomainObject> T parseXML(final File file, final Class<T> rootType) {
        try (InputStream is = new FileInputStream(file)) {
            return parseXML(is, rootType);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read the file: " + file, e);
        }
    }

    /** Creates a domain object of the type and fills it by the child elements.
     * An element of an unknown name is skipped including its children. */
    private static <T extends DomainObject> T read(final Element element, final Class<T> type) {
        final T result = Keys.newInstance(type);
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof final Element child) {
                Keys.findKey(result, child.getTagName()).ifPresent(key -> assign(result, key, child));
            }
        }
        return result;
    }

    /** Writes the content of the element to the key of the domain object. */
    @SuppressWarnings("unchecked")
    private static void assign(final DomainObject domain, final Key<Object, Object> key, final Element element) {
        if (Keys.isList(key)) {
            final Class<? extends DomainObject> itemType = Keys.itemTypeOf(key).asSubclass(DomainObject.class);
            ((List<Object>) key.getValue(domain)).add(read(element, itemType));
        } else if (Keys.isDomain(key.type())) {
            key.setValue(domain, read(element, key.type().asSubclass(DomainObject.class)));
        } else {
            key.setValue(domain, ValueCoder.decode(key, element.getTextContent()));
        }
    }
}
