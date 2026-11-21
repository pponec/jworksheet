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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/** The installers run on a JRE with no Java compiler, so the handlers of all the domain
 * classes must be generated at build time. The tests run on a JDK, where a missing handler
 * would be generated silently at run time, so its class file is looked up explicitly.
 * @author Pavel Ponec
 */
public class PrecompiledHandlerTest {

    /** An index of the domain classes written by the Ujorm annotation processor. */
    private static final String ENTITY_INDEX = "META-INF/ujorm/entities.lst";

    @Test
    @DisplayName("Each domain class has got its pre-compiled handler")
    public void handlersArePrecompiled() throws IOException {
        final List<String> domains = readDomains();
        assertFalse(domains.isEmpty(), "No domain class in " + ENTITY_INDEX);

        for (String domain : domains) {
            final String handler = "org/ujorm/gen_/" + domain.replace('.', '/') + "_.class";
            assertNotNull(getClass().getClassLoader().getResource(handler), "Missing: " + handler);
        }
    }

    private List<String> readDomains() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ENTITY_INDEX)) {
            assertNotNull(is, "Missing: " + ENTITY_INDEX);
            return new BufferedReader(new InputStreamReader(is, UTF_8)).lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .toList();
        }
    }
}
