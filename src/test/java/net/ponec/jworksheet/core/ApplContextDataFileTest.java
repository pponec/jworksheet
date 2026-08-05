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
import net.ponec.jworksheet.bo.Parameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/** The data file and the shared projects file are resolved from the parameters, where
 * an undefined path means a file of the configuration directory. The default value is
 * read from a prototype instance of the Parameters, so it must be compared by the
 * equals() method - the original comparison by the identity failed on a loaded
 * configuration file and the application saved the data to a file named {@code <default>}.
 * @author Pavel Ponec
 */
public class ApplContextDataFileTest {

    /** A configuration directory of the test. */
    @TempDir
    private File configDir;

    /** A context of the temporary configuration directory. */
    private ApplContext applContext() {
        final ApplContext result = new ApplContext();
        result.setUserConfigDir(configDir.getPath());
        return result;
    }

    @Test
    @DisplayName("A new configuration points to the configuration directory")
    public void defaultParameters() {
        final ApplContext context = applContext();

        assertEquals(new File(configDir, ApplContext.FILE_DATA), context.getDataFile());
        assertEquals(new File(configDir, ApplContext.PROJECTS_DATA), context.getProjectsFile());
    }

    @Test
    @DisplayName("A default path loaded from a configuration file is not a data file")
    public void defaultPathOfLoadedConfiguration() {
        final ApplContext context = applContext();
        final Parameters parameters = context.getParameters();
        // A loaded configuration assigns a new instance of the same value:
        parameters.setDataFilePath(new File(parameters.getDataFilePath().getPath()));
        parameters.setSharedProjectsFilePath(new File(parameters.getSharedProjectsFilePath().getPath()));

        assertEquals(new File(configDir, ApplContext.FILE_DATA), context.getDataFile());
        assertEquals(new File(configDir, ApplContext.PROJECTS_DATA), context.getProjectsFile());
    }

    @Test
    @DisplayName("An assigned path is used as it is")
    public void assignedPath() {
        final ApplContext context = applContext();
        final File dataFile = new File(configDir, "my-data.xml");
        final File projectsFile = new File(configDir, "my-projects.xml");
        context.getParameters().setDataFilePath(dataFile);
        context.getParameters().setSharedProjectsFilePath(projectsFile);

        assertEquals(dataFile, context.getDataFile());
        assertEquals(projectsFile, context.getProjectsFile());
    }
}
