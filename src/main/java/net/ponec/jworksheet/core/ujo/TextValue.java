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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A text representation of the field value in the persistent documents.
 * <p>The annotation is required by a memory optimized field whose stored type differs from
 * the type of the document - see the start time of an {@code Event}, which is stored
 * by a {@code short} field but written as a {@code Time} text:
 * <pre class="pre">
 *  {@code @Column}(name = "Time")
 *  {@code @TextValue}(TimeCodec.class)
 *  private short time;
 * </pre>
 *
 * @author Pavel Ponec
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TextValue {

    /** A codec which converts the field value to its text representation.
     * The class requires a public constructor without arguments. */
    Class<? extends TextCodec<?>> value();
}
