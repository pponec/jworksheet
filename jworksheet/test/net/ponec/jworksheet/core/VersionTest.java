/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.jworksheet.core;

import junit.framework.TestCase;

/**
 *
 * @author pavel
 */
public class VersionTest extends TestCase {
    
    public VersionTest(String testName) {
        super(testName);
    }

    /**
     * Test of compareTo method, of class Version.
     */
    public void testCompareTo() {
        System.out.println("compareTo");
        Version ver1, ver2;

        ver1 = new Version("abc1");
        ver2 = new Version("abc1");
        assertEquals(ver1, ver2);

        ver1 = new Version("Abc1");
        ver2 = new Version("abC1");
        assertEquals(ver1, ver2);

        ver1 = new Version("abc1");
        ver2 = new Version("abc2");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("Abc");
        ver2 = new Version("Bbc");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("abc.001");
        ver2 = new Version("abc.002");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("abc.001.9");
        ver2 = new Version("abc.002.8");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("abc");
        ver2 = new Version("abcd");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("abc.02");
        ver2 = new Version("abcd.01");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("0.98");
        ver2 = new Version("0.98D");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("0.98");
        ver2 = new Version("0.98.D");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("0.98.d");
        ver2 = new Version("0.98.d2");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version("0.98.d3");
        ver2 = new Version("1.98.d2");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version( "0.98.d3");
        ver2 = new Version("10.98.d2");
        assertTrue(ver1.compareTo(ver2)<0);

        ver1 = new Version( "9.98.d3");
        ver2 = new Version("10.98.d2");
        assertTrue(ver1.compareTo(ver2)<0);


    }

}
