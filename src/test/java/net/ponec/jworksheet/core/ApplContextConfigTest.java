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

import java.io.File;
import java.util.Objects;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.core.ujo.BundleManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/** The configuration file is written by a temporary file, which is renamed,
 * so a failure of the writing never damages the original file.
 * @author Pavel Ponec
 */
public class ApplContextConfigTest {

    @TempDir
    private File configDir;

    @Test
    @DisplayName("The parameters are saved and no temporary file is left")
    public void saveParameters() throws Exception {
        final ApplContext context = new ApplContext();
        context.setUserConfigDir(configDir.getPath());
        context.getParameters().setWorkingHours(6.5f);

        context.saveParameters();
        context.saveParameters(); // An existing file is replaced

        final Parameters loaded = BundleManager.load(context.getConfigFile(), Parameters.class);
        assertEquals(6.5f, loaded.getWorkingHours());
        assertEquals(0, Objects.requireNonNull(configDir.list((dir, name) -> name.endsWith(".tmp"))).length);
    }
}
