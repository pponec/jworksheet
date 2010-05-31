/**
  * Copyright (C) 2007-2010, Paul Ponec, contact: http://ponec.net/
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


/**
 * An ring buffer implementation. The object have got the initialization length allways.
 * @author Ponec
 */
final public class RingBuffer implements CharSequence {

    /** Obsah tøídy (buffer)*/
    private final char[] b;

    /** Délka */
    public final int length;

    /** Poèátek øetìzce */
    private int pos = 0;

    /** Creates new PStRing */
    public RingBuffer(int length) {
        this.b = new char[length];
        this.length = length;
    }

    final public void add(char c) {
       b[pos] = c;
       pos = ++pos % this.length;
    }

    /** Test na shodu */
    public boolean equals(String s) {
        return equals(s.toCharArray());
    }

    /** Test na shodu */
    public boolean equals(char[] s) {
       int i;
       for(i=0; i<this.length && s[i]==b[(pos + i) % length]; i++) {}
       return (i==length);
    }

    /** Returns character from position  >i<: */
    public char charAt(int i) {
       return b[(pos + i) % length];
    }

    /** Export do Stringu. TODO: CodePage */
    @Override
    public String toString() {
       char[] t = new char[length];
       for(int i=0; i<this.length; i++) t[i] = b[(pos + i) % length];
       return (new String(t));
    }

    /** Export do Stringu */
    public String substring(int begIndex, int endIndex) {
       int i;
       if (endIndex<=begIndex) return "" ;
       char[] t = new char[endIndex - begIndex];
       for(i=begIndex; i<endIndex; i++) {
           t[i-begIndex] = b[(pos + i) % length];
       }
       return (new String(t));
    }

    /** Length of the String */
    final public int length() {
        return length;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

}