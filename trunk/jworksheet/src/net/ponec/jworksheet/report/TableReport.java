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

import java.io.IOException;
import java.util.List;
import net.ponec.jworksheet.core.ApplContext;

/**
 * Standard report
 * @author Pavel Ponec
 */
public class TableReport extends ReportA {
    
    public TableReport(ApplContext context) {
        super.applContext = context;
    }
    
    /** Print the data result */
    public String printTable(List<List<String>> table, String title, boolean header) throws IOException {
        StringBuilder body = new StringBuilder(256);
        
        printTableBeg(body);
        for(List<String> row : table) {
            body.append("<tr>");
            for(String value : row) {
                body.append(header ? "<th>" : "<td>");
                body.append(escape(value));
                body.append(header ? "</th>" : "</td>");
            }
            body.append("</tr>");
            header = false;
        }
        printTableEnd(body);
        
        final String result = getReport(body.toString(), title);
        return result;
    }
    
    
    
}
