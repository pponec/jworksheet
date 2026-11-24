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


package net.ponec.jworksheet.report;

import java.io.File;
import net.ponec.jworksheet.core.Calculator;
import org.ujorm.tools.Check;

/**
 * Report Description
 * @author Pavel Ponec
 */
public final class MetaReport implements Comparable<MetaReport> {

    /** Name or key */
    private final String title;

    /** Report class */
    private final Class<? extends Calculator> typeClass;

    /** XSL transformer */
    private final File xsl;

    /** XML data source */
    private final String dataFile;

    /** Creates a new instance of MetaReport */
    public MetaReport(String title, Class<? extends Calculator> type) {
        this(title, type, null, null);
    }

    /** Creates a new instance of MetaReport */
    public MetaReport(String title, File xsl, String dataType) {
        this(Check.isEmpty(title) ? xsl.getName() : title, null, xsl, dataType);
    }

    /** Creates a new instance with all the attributes. */
    private MetaReport(String title, Class<? extends Calculator> typeClass, File xsl, String dataFile) {
        this.title = title;
        this.typeClass = typeClass;
        this.xsl = xsl;
        this.dataFile = dataFile;
    }

    /** Returns a copy with a new title, this instance is unchanged. */
    public MetaReport withTitle(String title) {
        return new MetaReport(title, typeClass, xsl, dataFile);
    }

    /** Report Title */
    public String getTitle() {
        return title;
    }

    public Class<? extends Calculator> getTypeClass() {
        return typeClass;
    }

    public File getXSL() {
        return xsl;
    }

    /** Returns a not null value. */
    private String getXslName() {
        return xsl!=null ? xsl.getName() : "" ;
    }

    public String getDataFile() {
        return dataFile;
    }

    /** Returns title */
    @Override
    public String toString() {
        return title;
    }

    /** Compare objects by the name of the XSL file.
     * Note: the order is not consistent with the equals(), two reports of the same file are different objects. */
    @Override
    public int compareTo(MetaReport param) {
        final int result = this.getXslName().compareToIgnoreCase(param.getXslName());
        return result;
    }
}
