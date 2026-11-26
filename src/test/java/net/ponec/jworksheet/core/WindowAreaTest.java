/*
 * Copyright (C) 2007-2026, Pavel Ponec, contact: http://ponec.net/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ponec.jworksheet.core;

import java.awt.Rectangle;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** An area of the main window must never be stored as an area, where the window can't be found. */
class WindowAreaTest {

    private final List<Rectangle> screens = List.of(new Rectangle(0, 0, 1920, 1080));

    @Test
    @DisplayName("A normal window and the default centered window are usable")
    void usable() {
        assertTrue(ApplTools.isWindowAreaUsable(new Rectangle(100, 50, 622, 405), screens));
        assertTrue(ApplTools.isWindowAreaUsable(new Rectangle(-1, -1, 622, 405), screens));
    }

    @Test
    @DisplayName("A minimized window is not usable (the Windows place it to -32000)")
    void minimized() {
        assertFalse(ApplTools.isWindowAreaUsable(new Rectangle(-32000, -32000, 160, 28), screens));
        assertFalse(ApplTools.isWindowAreaUsable(new Rectangle(5, 5, 0, 0), screens));
    }

    @Test
    @DisplayName("A tiny window is not usable")
    void tiny() {
        assertFalse(ApplTools.isWindowAreaUsable(new Rectangle(100, 100, 199, 405), screens));
        assertFalse(ApplTools.isWindowAreaUsable(new Rectangle(100, 100, 622, 99), screens));
        assertFalse(ApplTools.isWindowAreaUsable(null, screens));
    }

    @Test
    @DisplayName("A window out of all screens is not usable, a partly visible one is")
    void outOfScreen() {
        assertFalse(ApplTools.isWindowAreaUsable(new Rectangle(5000, 100, 622, 405), screens));
        assertFalse(ApplTools.isWindowAreaUsable(new Rectangle(100, 3000, 622, 405), screens));
        assertTrue(ApplTools.isWindowAreaUsable(new Rectangle(1800, 100, 622, 405), screens));
    }

    @Test
    @DisplayName("Without a known screen the size is checked only")
    void unknownScreen() {
        assertTrue(ApplTools.isWindowAreaUsable(new Rectangle(5000, 100, 622, 405), List.of()));
    }
}
