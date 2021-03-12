package net.ponec.jworksheet.core;

import junit.framework.TestCase;

/**
 *
 * @author Ponec
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
        String  txt1, txt2;

        txt1 = "abc1";
        txt2 = txt1;
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertEquals(ver1, ver2);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "Abc1";
        txt2 = "abC1";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertEquals(ver1, ver2);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "abc1";
        txt2 = "abc2";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "Abc";
        txt2 = "Bbc";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "abc.001";
        txt2 = "abc.002";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "abc.001.9";
        txt2 = "abc.002.8";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "abc";
        txt2 = "abcd";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "abc.02";
        txt2 = "abcd.01";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "0.98";
        txt2 = "0.98D";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "0.98";
        txt2 = "0.98.D";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "0.98.d";
        txt2 = "0.98.d2";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "0.98.d3";
        txt2 = "1.98.d2";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 =  "0.98.d3";
        txt2 = "10.98.d2";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 =  "9.98.d3";
        txt2 = "10.98.d2";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());

        txt1 = "0.98a.d3";
        txt2 = "0.99";
        ver1 = new Version(txt1);
        ver2 = new Version(txt2);
        assertTrue(ver1.compareTo(ver2)<0);
        assertEquals(txt1, ver1.toString());
        assertEquals(txt2, ver2.toString());


    }

}
