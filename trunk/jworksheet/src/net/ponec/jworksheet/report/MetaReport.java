/**
  * Copyright (C) 2007-8, Paul Ponec, contact: http://ponec.net/
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
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.core.Calculator;

/**
 * Report Description
 * @author Pavel Ponec
 */
public class MetaReport implements Comparable<MetaReport> {
    
    /** Name or key */
    private String title;
    
    /** Report class */
    private Class<? extends Calculator>  typeClass;
    
    /** XSL transformer */
    private File xsl;
    
    /** XML data source */
    private String dataFile;
    
    /** Creates a new instance of MetaReport */
    public MetaReport(String title, Class<? extends Calculator> type) {
        setTitle(title);
        setTypeClass(type);
    }
    
    /** Creates a new instance of MetaReport */
    public MetaReport(String title, File xsl, String dataType) {
        if (!ApplTools.isValid(title)) {
            title = xsl.getName();
        }
        
        setTitle(title);
        setXSL(xsl);
        setDataType(dataType);
    }
    
    /** Report Title */
    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public Class<? extends Calculator> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<? extends Calculator> typeClass) {
        this.typeClass = typeClass;
    }

    public File getXSL() {
        return xsl;
    }
    
    /** Returns a not null value. */
    private String getXslName() {
        return xsl!=null ? xsl.getName() : "" ;
    }

    public void setXSL(File xsl) {
        this.xsl = xsl;
    }

    public String getDataFile() {
        return dataFile;
    }

    /** Plaing file name. */
    public void setDataType(String dataFile) {
        this.dataFile = dataFile;
    }
    
    /** Returns title */
    public String toString() {
        return title;
    }

    /** Compara object by title. */
    public int compareTo(MetaReport param) {
        final int result = this.getXslName().compareToIgnoreCase(param.getXslName());
        return result;
    }
}
