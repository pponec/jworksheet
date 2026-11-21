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

package net.ponec.jworksheet.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/** The file operations of the {@link ApplTools}, which save and back up the data file.
 * @author Pavel Ponec
 */
public class ApplToolsFileTest {

    @TempDir
    private Path dir;

    private File write(final String name, final String content) throws IOException {
        return Files.writeString(dir.resolve(name), content, UTF_8).toFile();
    }

    private static String read(final File file) throws IOException {
        return Files.readString(file.toPath(), UTF_8);
    }

    @Test
    @DisplayName("A rename replaces an existing target and removes the source")
    public void renameReplacesTarget() throws IOException {
        final File source = write("data.tmp", "new");
        final File target = write("data.xml", "old");

        ApplTools.rename(source, target);

        assertEquals("new", read(target));
        assertFalse(source.exists());
        assertFalse(new File(dir.toFile(), "data.xml$").exists(), "No auxiliary file is left");
    }

    @Test
    @DisplayName("A rename creates a missing target")
    public void renameToNewTarget() throws IOException {
        final File source = write("data.tmp", "new");
        final File target = dir.resolve("data.xml").toFile();

        ApplTools.rename(source, target);

        assertEquals("new", read(target));
    }

    @Test
    @DisplayName("A failed rename keeps the original target")
    public void failedRenameKeepsTarget() throws IOException {
        final File source = dir.resolve("missing.tmp").toFile();
        final File target = write("data.xml", "old");

        assertThrows(IOException.class, () -> ApplTools.rename(source, target));
        assertEquals("old", read(target));
    }

    @Test
    @DisplayName("A copy replaces the target and keeps the time of the last modification")
    public void copyFile() throws IOException {
        final File source = write("data.xml", "data");
        final File target = write("data.backup.xml", "old backup");
        final FileTime time = FileTime.fromMillis(1_600_000_000_000L);
        Files.setLastModifiedTime(source.toPath(), time);

        ApplTools.copy(source, target);

        assertEquals("data", read(target));
        assertEquals(time, Files.getLastModifiedTime(target.toPath()));
    }

    @Test
    @DisplayName("A stream is copied to a file, an existing file is replaced")
    public void copyStream() throws IOException {
        final File target = write("style.css", "old style");

        ApplTools.copy(new ByteArrayInputStream("body {}".getBytes(UTF_8)), target);

        assertEquals("body {}", read(target));
    }

    @Test
    @DisplayName("A content of the file is limited by the number of the characters")
    public void fileContent() throws IOException {
        final File file = write("data.xml", "Žluťoučký kůň");

        assertEquals("Žluťoučký kůň", ApplTools.getFileContent(file, UTF_8, Integer.MAX_VALUE));
        assertEquals("Žluť", ApplTools.getFileContent(file, UTF_8, 4));
    }

    @Test
    @DisplayName("An empty or a missing file provides an empty text")
    public void emptyFileContent() throws IOException {
        assertEquals("", ApplTools.getFileContent(write("empty.xml", ""), UTF_8, 300));
        assertEquals("", ApplTools.getFileContent(dir.resolve("missing.xml").toFile(), UTF_8, 300));
        assertEquals("", ApplTools.getFileContent(null, UTF_8, 300));
    }
}
