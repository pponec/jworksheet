/**
  * Copyright (C) 2007-9, Paul Ponec, contact: http://ponec.net/
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

package net.ponec.jworksheet.module;

import java.util.Date;
import java.util.Locale;


/**
 * An module interface
 * @author Ponec
 */
public interface ModuleApi {

    /** The jWorkSheet context */
    public JwsContext getJwsContext();

    /** The jWorkSheet context */
    public void setJwsContext(JwsContext jwsContext);

    /** A listen in start or finish the jWorkSheet application */
    public void eventListener(boolean start);

    /** A listener for an application language change. */
    public void localeListener(Locale locale);

    /** Returns a Module Name */
    public String getName();

    /** Returns a Module Description */
    public String getDescription();

    /** Returns a Release */
    public String getRelease();

    /** Returns a Date of Creation */
    public Date getCreated();

}
