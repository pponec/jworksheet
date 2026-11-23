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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.ujorm.core.Key;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A serialization of the domain objects to an XML document and back by the StAX API of the JDK.
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

    /** The default JDK implementations are used, so a StAX provider of the class path
     * can not change the document format. */
    private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newDefaultFactory();
    private static final XMLInputFactory INPUT_FACTORY = createInputFactory();

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
        if (out instanceof final Writer writer) {
            saveXML(writer, root, comment);
        } else {
            final StringWriter writer = new StringWriter(1024);
            saveXML(writer, root, comment);
            out.append(writer.getBuffer());
        }
    }

    /** Writes the domain object to an XML file. */
    public static void saveXML(final File file, final DomainObject root, final String comment)
            throws IOException {
        try (Writer out = new BufferedWriter(new OutputStreamWriter
                (new BufferedOutputStream(new FileOutputStream(file)), UTF_8))) {
            saveXML(out, root, comment);
        }
    }

    /** Writes the domain object to a character stream. */
    private static void saveXML(final Writer out, final DomainObject root, final String comment)
            throws IOException {
        try {
            final XMLStreamWriter writer = OUTPUT_FACTORY.createXMLStreamWriter(out);
            writer.writeStartDocument(UTF_8.name(), "1.0");
            writer.writeCharacters(NEW_LINE);
            if (comment != null) {
                writer.writeComment(comment);
                writer.writeCharacters(NEW_LINE);
            }
            writeElement(writer, ROOT_ELEMENT, root, 0);
            writer.writeEndDocument();
            writer.flush(); // The writer does not close the underlying stream
        } catch (XMLStreamException e) {
            throw new IOException("Can't write the " + root.getClass().getSimpleName(), e);
        }
    }

    /** Writes a single element including all its children. */
    private static void writeElement
            ( final XMLStreamWriter out
            , final String name
            , final DomainObject domain
            , final int level) throws XMLStreamException {

        indent(out, level);
        out.writeStartElement(name);
        out.writeCharacters(NEW_LINE);

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
                    indent(out, level + 1);
                    out.writeStartElement(element);
                    // An empty text closes the start tag, so the element is written as a pair of tags:
                    out.writeCharacters(ValueCoder.encode(key, value));
                    out.writeEndElement();
                    out.writeCharacters(NEW_LINE);
                }
            }
        }

        indent(out, level);
        out.writeEndElement();
        if (level > 0) {
            // The original implementation writes no new line behind the root element:
            out.writeCharacters(NEW_LINE);
        }
    }

    /** Writes an indentation of the element at the level. The method must be called
     * before the start tag of the element, the text is written by the parent element. */
    private static void indent(final XMLStreamWriter out, final int level) throws XMLStreamException {
        if (level > 0) {
            out.writeCharacters(INDENT.repeat(level));
        }
    }

    // ------------------------ IMPORT ------------------------

    /** Creates a factory of the XML readers. No document type is expected,
     * so the DTD support is off and the external entities can not be abused (XXE). */
    private static XMLInputFactory createInputFactory() {
        final XMLInputFactory result = XMLInputFactory.newDefaultFactory();
        result.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        result.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        result.setProperty(XMLInputFactory.IS_COALESCING, true);
        return result;
    }

    /** Reads a domain object from an XML document. The name of the root element is not checked,
     * an element of an unknown name (the names are case sensitive) is skipped including its children. */
    public static <T extends DomainObject> T parseXML(final InputStream is, final Class<T> rootType) {
        XMLStreamReader reader = null;
        try {
            reader = INPUT_FACTORY.createXMLStreamReader(is);
            nextElement(reader);
            final T result = read(reader, rootType);
            // Check the rest of the document:
            while (reader.hasNext()) {
                checkEvent(reader.next());
            }
            return result;
        } catch (XMLStreamException e) {
            throw new IllegalStateException("Can't parse the XML document to "
                    + rootType.getSimpleName(), e);
        } finally {
            close(reader);
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
     * The reader must be located on the start tag, the method ends on the related end tag.
     * An element of an unknown name is skipped including its children. */
    private static <T extends DomainObject> T read(final XMLStreamReader reader, final Class<T> type)
            throws XMLStreamException {
        final T result = Keys.newInstance(type);
        while (nextElement(reader)) {
            final var key = Keys.findKey(result, reader.getLocalName());
            if (key.isPresent()) {
                assign(result, key.get(), reader);
            } else {
                skipElement(reader);
            }
        }
        return result;
    }

    /** Writes the content of the element to the key of the domain object. */
    @SuppressWarnings("unchecked")
    private static void assign(final DomainObject domain, final Key<Object, Object> key, final XMLStreamReader reader)
            throws XMLStreamException {
        if (Keys.isList(key)) {
            final Class<? extends DomainObject> itemType = Keys.itemTypeOf(key).asSubclass(DomainObject.class);
            ((List<Object>) key.getValue(domain)).add(read(reader, itemType));
        } else if (Keys.isDomain(key.type())) {
            key.setValue(domain, read(reader, key.type().asSubclass(DomainObject.class)));
        } else {
            key.setValue(domain, ValueCoder.decode(key, readText(reader)));
        }
    }

    /** Moves the reader to the next start tag of a child element and returns {@code true},
     * or to the end tag of the current element and returns {@code false}. */
    private static boolean nextElement(final XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            switch (checkEvent(reader.next())) {
                case XMLStreamConstants.START_ELEMENT:
                    return true;
                case XMLStreamConstants.END_ELEMENT:
                    return false;
                default:
                    // A text, a comment and so on are ignored
            }
        }
        throw new XMLStreamException("Unexpected end of the document", reader.getLocation());
    }

    /** Returns the text of the current element including the text of all nested elements,
     * the reader ends on the related end tag. */
    private static String readText(final XMLStreamReader reader) throws XMLStreamException {
        final StringBuilder result = new StringBuilder();
        for (int depth = 1; depth > 0;) {
            switch (checkEvent(reader.next())) {
                case XMLStreamConstants.START_ELEMENT -> depth++;
                case XMLStreamConstants.END_ELEMENT -> depth--;
                case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA, XMLStreamConstants.SPACE ->
                        result.append(reader.getText());
                default -> { /* A comment and so on are ignored */ }
            }
        }
        return result.toString();
    }

    /** Skips the current element including its children, the reader ends on the related end tag. */
    private static void skipElement(final XMLStreamReader reader) throws XMLStreamException {
        for (int depth = 1; depth > 0;) {
            switch (checkEvent(reader.next())) {
                case XMLStreamConstants.START_ELEMENT -> depth++;
                case XMLStreamConstants.END_ELEMENT -> depth--;
                default -> { /* The content is ignored */ }
            }
        }
    }

    /** A document type is rejected, the data file does not use it. */
    private static int checkEvent(final int event) throws XMLStreamException {
        if (event == XMLStreamConstants.DTD || event == XMLStreamConstants.ENTITY_REFERENCE) {
            throw new XMLStreamException("A document type is not supported");
        }
        return event;
    }

    private static void close(final XMLStreamReader reader) {
        if (reader != null) try {
            reader.close();
        } catch (XMLStreamException e) {
            // The parsing result or exception is more important
        }
    }
}
