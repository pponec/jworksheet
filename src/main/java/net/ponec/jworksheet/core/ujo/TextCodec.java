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

/**
 * A converter between a value of a domain field and its text representation.
 * <p>The codec is required by a field whose stored type differs from the type published
 * by the persistent documents - see the {@link TextValue} annotation.
 *
 * @param <R> a type of the value stored by the domain field
 * @author Pavel Ponec
 */
public interface TextCodec<R> {

    /** Converts a value of the domain field to its text representation. */
    String encode(R value);

    /** Converts a text to a value of the domain field. */
    R decode(String text);
}
