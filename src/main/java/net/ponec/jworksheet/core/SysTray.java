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

package net.ponec.jworksheet.core;

import javax.annotation.Nonnull;

/**
 * Dummu System Tray management.
 * @author Pavel Ponec
 */
public class SysTray {

    /** SysTray Acton on a second mouse click. */
    public enum Action {
        NONE, EVENT, HIDE;
    }

    protected SysTray() {
    }

    public void init(ApplContext applContext) {
    }

    public boolean isSupported() {
        return false;
    }

    /** Close the Systray */
    public void close() {
    }

    /** Set a tooltip to a main icon. */
    public void setTooltip(String message) {
    }

    /** Returns an instance or null. */
    @Nonnull
    public static SysTray getInstance(ApplContext applContext) {
        SysTray result;
        try {
            result = (SysTray) Class.forName("net.ponec.jworksheet.core.SysTray6").newInstance();
        } catch (Throwable e) {
            result = new SysTray();
        }
        result.init(applContext);
        return result;
    }

}
