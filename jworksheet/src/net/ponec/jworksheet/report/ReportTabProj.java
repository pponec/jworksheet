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

import org.ujoframework.core.UjoComparator;

/**
 * Day Report with a project resolution.
 * @author Ponec
 */
public class ReportTabProj extends ReportTab {

    /** Create Comparator */
    @Override
    protected UjoComparator createUjoComparator() {
        UjoComparator comparator = UjoComparator.newInstance(TaskGroup.P_PROJ);
        return comparator;
    }

}
