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

import java.io.ByteArrayInputStream;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.meta.MetaWorkSpace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/** The parsing rules of the XML documents, which are not covered by the round trip tests.
 * @author Pavel Ponec
 */
public class XmlManagerTest {

    private static WorkSpace parse(final String xml) {
        return XmlManager.parseXML(new ByteArrayInputStream(xml.getBytes(UTF_8)), WorkSpace.class);
    }

    @Test
    @DisplayName("An unknown element is skipped including its children")
    public void unknownElementIsSkipped() {
        final String name = Keys.nameOf(MetaWorkSpace.username);
        final WorkSpace result = parse("<body>"
                + "<Unknown><" + name + ">Wrong</" + name + "></Unknown>"
                + "<" + name + ">Pavel</" + name + ">"
                + "</body>");

        assertEquals("Pavel", result.getUsername());
    }

    @Test
    @DisplayName("A document type is rejected, so no external entity is resolved")
    public void doctypeIsRejected() {
        final String name = Keys.nameOf(MetaWorkSpace.username);
        final String xml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE body [<!ENTITY secret SYSTEM \"file:///etc/hostname\">]>"
                + "<body><" + name + ">&secret;</" + name + "></body>";

        assertThrows(IllegalStateException.class, () -> parse(xml));
    }
}
