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

package net.ponec.jworksheet.core;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The {@code findWord()} method replaces the {@code RingBuffer} of the original Ujorm
 * implementation. It is used to read a release number from the home page, so a defect
 * would stay hidden until the "check a new release" action.
 * @author Pavel Ponec
 */
public class ApplToolsTest {

    /** The tested method is a private one. */
    private static String findWord(final String source, final String beg, final String end)
            throws Exception {
        final Method method = ApplTools.class
                .getDeclaredMethod("findWord", Reader.class, String.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, new StringReader(source), beg, end);
    }

    @Test
    @DisplayName("A word between the tags is found and trimmed")
    public void findWordBetweenTags() throws Exception {
        assertEquals("abc", findWord("xxx ${abc} def", "${", "}"));
        assertEquals("1.02", findWord("<description>Release: 1.02 </description>"
                , "<description>Release:", "</description>"));
    }

    @Test
    @DisplayName("An empty start tag reads from the beginning")
    public void emptyStartTag() throws Exception {
        assertEquals("abc", findWord("abc</end>def", "", "</end>"));
    }

    @Test
    @DisplayName("The first occurrence is used")
    public void firstOccurrence() throws Exception {
        assertEquals("one", findWord("[one][two]", "[", "]"));
    }

    @Test
    @DisplayName("A missing tag provides an empty text, never a null")
    public void missingTag() throws Exception {
        assertEquals("", findWord("no tag here", "${", "}"));
        assertEquals("", findWord("${unfinished", "${", "}"));
        assertEquals("", findWord("", "${", "}"));
    }

    @Test
    @DisplayName("A repeated character of the tag does not break the search")
    public void repeatedCharacterOfTag() throws Exception {
        // A naive matching would fail here: the third 'a' must restart the match
        assertEquals("X", findWord("aaabXb", "aab", "b"));
        assertEquals("value", findWord(">>>value<<<", ">>>", "<<<"));
    }

    @Test
    @DisplayName("An empty body provides an empty text")
    public void emptyBody() throws Exception {
        assertEquals("", findWord("${}", "${", "}"));
    }
}
