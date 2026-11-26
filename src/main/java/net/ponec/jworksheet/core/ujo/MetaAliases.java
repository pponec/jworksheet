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

import net.ponec.jworksheet.bo.meta.MetaEvent;
import net.ponec.jworksheet.bo.meta.MetaParameters;
import net.ponec.jworksheet.bo.meta.MetaProject;
import net.ponec.jworksheet.report.meta.MetaTaskGroup;
import net.ponec.jworksheet.bo.meta.MetaTaskType;
import net.ponec.jworksheet.gui.models.meta.MetaUjoKeyRow;
import net.ponec.jworksheet.bo.meta.MetaWorkDay;
import net.ponec.jworksheet.bo.meta.MetaWorkSpace;

/**
 * Short aliases of the generated key interfaces made of the capital letters of the entity name
 * (two letters), so that a key can be written short but still qualified by its domain,
 * e.g. {@code _WS.username}. Import the nested type, e.g. {@code import net.ponec.jworksheet.core.ujo.MetaAliases._WS;}
 */
public final class MetaAliases {

    private MetaAliases() {
    }

    /** {@link MetaEvent} */
    public interface _EV extends MetaEvent {}
    /** {@link MetaParameters} */
    public interface _PA extends MetaParameters {}
    /** {@link MetaProject} */
    public interface _PR extends MetaProject {}
    /** {@link MetaTaskGroup} */
    public interface _TG extends MetaTaskGroup {}
    /** {@link MetaTaskType} */
    public interface _TT extends MetaTaskType {}
    /** {@link MetaUjoKeyRow} */
    public interface _UR extends MetaUjoKeyRow {}
    /** {@link MetaWorkDay} */
    public interface _WD extends MetaWorkDay {}
    /** {@link MetaWorkSpace} */
    public interface _WS extends MetaWorkSpace {}
}
