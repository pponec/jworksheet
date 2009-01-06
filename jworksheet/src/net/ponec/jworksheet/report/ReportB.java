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
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import org.ujoframework.Ujo;
import org.ujoframework.core.UjoComparator;

/**
 * Standard report
 * @author Pavel Ponec
 */
public class ReportB extends ReportA {
    
    /** Create Comparator */
    @Override
    protected UjoComparator createUjoComparator() {
        UjoComparator comparator = UjoComparator.create(true, TaskGroup.P_PROJ);
        return comparator;
    }
    
    /** Print the result. */
    @Override
    public int printDetail(StringBuilder sb) throws IOException {
        int total = 0;
        
        sb.append("<tr>");
        sb.append("<th>").append("Project").append("</th>");
        sb.append("<th align=\"right\">").append("Time [hours]").append("</th>");
        sb.append("</tr>");
        
        for(TaskGroup group : groupSet.getGroups()) {
            Project  proj = TaskGroup.P_PROJ.of(group);
            TaskType task = TaskGroup.P_TASK.of(group);
            
            if (proj==null || !Project.P_PRIVATE.of(proj)) {
                sb.append("<tr>");
                sb.append("<td>").append(escape(proj)).append("</td>");
                sb.append("<td align=\"right\">").append(formatTime(group.getTime())).append("</td>");
                sb.append("</tr>");
                total += group.getTime();
            }
        }
        return total;
    }
}
