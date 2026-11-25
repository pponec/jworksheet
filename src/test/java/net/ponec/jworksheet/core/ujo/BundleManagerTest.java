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


import java.awt.Rectangle;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.core.SysTray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import net.ponec.jworksheet.core.ujo.MetaAliases.PA;

/** The configuration file of the application.
 * @author Pavel Ponec
 */
public class BundleManagerTest {

    @TempDir
    private Path tempDir;

    private File newFile() {
        return tempDir.resolve("config.properties").toFile();
    }

    @Test
    @DisplayName("Only the modified values are stored, the default ones are skipped")
    public void defaultValuesAreSkipped() throws Exception {
        final Parameters params = new Parameters();
        params.setWindowSize(new Rectangle(649, 397, 622, 405));

        final File file = newFile();
        BundleManager.save(file, params, "A test:");
        final String text = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);

        assertTrue(text.contains("WindowSize=649,397,622,405"), text);
        assertFalse(text.contains("WorkingHours"), "A default value is not stored");
        assertFalse(text.contains("Language"), "A default value is not stored");
    }

    @Test
    @DisplayName("The modified values survive the round trip")
    public void roundTrip() throws Exception {
        final Parameters params = new Parameters();
        params.setWorkingHours(7.5f);
        params.setFirstDayOfWeek(3);
        params.setExitEventDescription("Odchod");
        params.setSystemTraySecondClick(SysTray.Action.EVENT);
        params.setHideButtonIcons(true);

        final File file = newFile();
        BundleManager.save(file, params, "A test:");
        final Parameters result = BundleManager.load(file, Parameters.class);

        assertEquals(7.5f, result.getWorkingHours());
        assertEquals(3, result.getFirstDayOfWeek());
        assertEquals("Odchod", result.getExitEventDescription());
        assertEquals(SysTray.Action.EVENT, result.getSystemTraySecondClick());
        assertTrue(result.isHideButtonIcons());
    }

    @Test
    @DisplayName("A key missing in the file keeps its default value")
    public void missingKeyKeepsDefault() throws Exception {
        final File file = newFile();
        Files.writeString(file.toPath(), "HideButtonIcons=true\n");

        final Parameters result = BundleManager.load(file, Parameters.class);

        assertTrue(result.isHideButtonIcons(), "The stored value");
        assertEquals(Keys.defaultOf(PA.workingHours), result.getWorkingHours());
        assertEquals(Keys.defaultOf(PA.exitEventDescription), result.getExitEventDescription());
    }

    @Test
    @DisplayName("An invalid value is skipped, the other values are loaded")
    public void invalidValueKeepsOthers() throws Exception {
        final File file = newFile();
        Files.writeString(file.toPath(), "WorkingHours=abc\nDateMainFormat=bad'\nHideButtonIcons=true\n");

        final Parameters result = BundleManager.load(file, Parameters.class);

        assertTrue(result.isHideButtonIcons(), "The valid value");
        assertEquals(Keys.defaultOf(PA.workingHours), result.getWorkingHours());
        assertEquals(Keys.defaultOf(PA.dateMainFormat), result.getDateMainFormat());
    }

    @Test
    @DisplayName("A saved file is loaded to an equal state")
    public void savingIsIdempotent() throws Exception {
        final Parameters params = new Parameters();
        params.setWorkingHours(6f);
        params.setWindowSize(new Rectangle(1, 2, 3, 4));

        final File first = tempDir.resolve("first.properties").toFile();
        final File second = tempDir.resolve("second.properties").toFile();
        BundleManager.save(first, params, "A test:");
        BundleManager.save(second, BundleManager.load(first, Parameters.class), "A test:");

        assertEquals(bodyOf(first), bodyOf(second));
    }

    /** A content of the file without the generated comments. */
    private static String bodyOf(final File file) throws Exception {
        return Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1).stream()
                .filter(line -> !line.startsWith("#"))
                .sorted()
                .reduce("", (a, b) -> a + b + "\n");
    }
}
