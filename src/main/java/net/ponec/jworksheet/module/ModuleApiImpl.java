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


package net.ponec.jworksheet.module;

import java.util.Locale;

/**
 * An extended module interface
 * @author Ponec
 * @since 0.85
 */
public abstract class ModuleApiImpl implements ModuleApi {

    private JwsContext jwsContext;

    @Override
    public JwsContext getJwsContext() {
        return jwsContext;
    }

    @Override
    public void setJwsContext(JwsContext jwsContext) {
        this.jwsContext = jwsContext;
    }


    /** Listener can be overriden to registration the event. */
    @Override
    public void localeListener(Locale locale) {}

}
