/**
  * Copyright (C) 2007-2021, Pavel Ponec, contact: http://ponec.net/
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
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import org.ujorm.core.UjoComparator;

/**
 * Standard report
 * @author Pavel Ponec
 */
public class ReportB extends ReportA {
    
    /** Create Comparator */
    @Override
    protected UjoComparator createUjoComparator() {
        return UjoComparator.<TaskGroup>newInstance(TaskGroup.P_PROJ);
    }
    
    /** Print the result. */
    @Override
    public int printDetail(StringBuilder sb) throws IOException {
        int total = 0;
        
        sb.append("<tr>");
        sb.append("<th>").append(getText(Event.P_PROJ)).append("</th>");
        sb.append("<th align=\"right\">").append(getText("Time[hours]")).append("</th>");
        sb.append("</tr>");
        
        for(TaskGroup group : groupSet.getGroups()) {
            Project  proj = group.get(TaskGroup.P_PROJ);
            TaskType task = group.get(TaskGroup.P_TASK);
            
            if (proj==null || !proj.get(Project.P_PRIVATE)) {
                sb.append("<tr>");
                sb.append("<td>").append(escape(proj)).append("</td>");
                sb.append("<td class=\"num\">").append(formatTime(group.getTime())).append("</td>");
                sb.append("</tr>");
                total += group.getTime();
            }
        }
        return total;
    }
}
