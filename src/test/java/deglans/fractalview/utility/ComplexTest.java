/*
 * Copyright (c) 2016 Deglans Dalpasso.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Deglans Dalpasso - initial API and implementation and initial documentation
 */
package deglans.fractalview.utility;

import deglans.fractalview.utility.Complex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests class for Complex number.
 *
 * @version 0.10
 * @author Deglans Dalpasso
 */
public class ComplexTest {

    /**
     * File with the data for test plus function.
     */
    public static final String TESTS_PLUS_FILE = "complex_plus.txt";

    /**
     * File with the data for test minus function.
     */
    public static final String TESTS_MINUS_FILE = "complex_minus.txt";

    /**
     * File with the data for test times function.
     */
    public static final String TESTS_TIMES_FILE = "complex_times.txt";

    /**
     * File with the data for test div function.
     */
    public static final String TESTS_DIV_FILE = "complex_div.txt";

    /**
     * File with the data for test pow(Complex) function.
     */
    public static final String TESTS_POW_COMPLEX_FILE = "complex_pow_complex.txt";

    /**
     * Maximum delta between data.
     */
    public static final double DELTA = 0.001;

    /**
     * Called at the start of each test.
     */
    public ComplexTest() {
        //System.out.println("ComplexTest()");
    }

    /**
     * Called before everything.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("setUpClass");
    }

    /**
     * Called after everything.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("tearDownClass");
    }

    /**
     * Called after the constructor, but before each test.
     */
    @Before
    public void setUp() {
        System.out.println("setUp");
    }

    /**
     * Called at the end of each test.
     */
    @After
    public void tearDown() {
        System.out.println("tearDown");
    }

    /**
     * Test of parseComplex method, of class Complex.
     */
    @Test
    public void testParseComplex() {
        System.out.println("parseComplex");
        String s = "(2.0, 2.0)";
        Complex expResult = new Complex(2.0, 2.0);
        Complex result = Complex.parseComplex(s);
        assertEquals(expResult.getReal(), result.getReal(), DELTA);
        assertEquals(expResult.getImag(), result.getImag(), DELTA);
    }

    /**
     * Test of toString method, of class Complex.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Complex instance = new Complex(2.0, 2.0);
        String expResult = "(2.0, 2.0)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

//    /**
//     * Test of equals method, of class Complex.
//     */
//    @Test
//    public void testEquals_Object() {
//        System.out.println("equals");
//        Object obj = new Complex(2.0, 2.0);
//        Complex instance = new Complex(2.0, 2.0);
//        boolean expResult = true;
//        boolean result = instance.equals(obj);
//        assertEquals(expResult, result);
//    }

    /**
     * Test of equals method, of class Complex.
     */
    @Test
    public void testEquals_Complex_double() {
        System.out.println("equals");
        Complex z = new Complex(2.0, 2.0);
        Complex instance = new Complex(2.0, 2.0);
        boolean expResult = true;
        boolean result = instance.equals(z, DELTA);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of hashCode method, of class Complex.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        Complex instance = null;
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of clone method, of class Complex.
//     */
//    @Test
//    public void testClone() throws Exception {
//        System.out.println("clone");
//        Complex instance = null;
//        Object expResult = null;
//        Object result = instance.clone();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of getReal method, of class Complex.
//     */
//    @Test
//    public void testGetReal() {
//        System.out.println("getReal");
//        Complex instance = null;
//        double expResult = 0.0;
//        double result = instance.getReal();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of getImag method, of class Complex.
//     */
//    @Test
//    public void testGetImag() {
//        System.out.println("getImag");
//        Complex instance = null;
//        double expResult = 0.0;
//        double result = instance.getImag();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of plus method, of class Complex.
     */
    @Test
    public void testPlus() {
        System.out.println("plus");
        List<ComplexLine> list = readFile(TESTS_PLUS_FILE);
        Complex result;
        for (ComplexLine elem : list) {
            result = elem.op1.plus(elem.op2);
            assertTrue(result.equals(elem.res, DELTA));
        }
    }

    /**
     * Test of minus method, of class Complex.
     */
    @Test
    public void testMinus() {
        System.out.println("minus");
        List<ComplexLine> list = readFile(TESTS_MINUS_FILE);
        Complex result;
        for (ComplexLine elem : list) {
            result = elem.op1.minus(elem.op2);
            assertTrue(result.equals(elem.res, DELTA));
        }
    }

    /**
     * Test of times method, of class Complex.
     */
    @Test
    public void testTimes() {
        System.out.println("times");
        List<ComplexLine> list = readFile(TESTS_TIMES_FILE);
        Complex result;
        for (ComplexLine elem : list) {
            result = elem.op1.times(elem.op2);
            assertTrue(result.equals(elem.res, DELTA));
        }
    }

    /**
     * Test of div method, of class Complex.
     */
    @Test
    public void testDiv() {
        System.out.println("div");
        List<ComplexLine> list = readFile(TESTS_DIV_FILE);
        Complex result;
        for (ComplexLine elem : list) {
            result = elem.op1.div(elem.op2);
            assertTrue(result.equals(elem.res, DELTA));
        }
    }

//    /**
//     * Test of mod method, of class Complex.
//     */
//    @Test
//    public void testMod() {
//        System.out.println("mod");
//        Complex instance = null;
//        double expResult = 0.0;
//        double result = instance.mod();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of arg method, of class Complex.
//     */
//    @Test
//    public void testArg() {
//        System.out.println("arg");
//        Complex instance = null;
//        double expResult = 0.0;
//        double result = instance.arg();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of pow method, of class Complex.
//     */
//    @Test
//    public void testPow_int() {
//        System.out.println("pow");
//        int n = 0;
//        Complex instance = null;
//        Complex expResult = null;
//        Complex result = instance.pow(n);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of pow method, of class Complex.
     */
    @Test
    public void testPow_Complex() {
        System.out.println("pow");
        List<ComplexLine> list = readFile(TESTS_POW_COMPLEX_FILE);
        Complex result;
        for (ComplexLine elem : list) {
            result = elem.op1.pow(elem.op2);
            assertTrue(result.equals(elem.res, DELTA));
        }
    }

//    /**
//     * Test of interpolate method, of class Complex.
//     */
//    @Test
//    public void testInterpolate() {
//        System.out.println("interpolate");
//        Complex endValue = null;
//        double t = 0.0;
//        Complex instance = null;
//        Complex expResult = null;
//        Complex result = instance.interpolate(endValue, t);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Read test file and put the lines in a List.
     *
     * @param fileName the file to read.
     * @return the List.
     */
    private List<ComplexLine> readFile(String fileName) {
        BufferedReader inputStream = null;
        List<ComplexLine> list = new ArrayList<>();
        String aline;

        try {
            // open the file
            //URL path = getClass().getResource("../../../datatestfile/" + fileName);
            URL path = getClass().getResource("/datatestfile/" + fileName);
            //System.out.println(path.getPath());
            inputStream = new BufferedReader(new FileReader(path.getPath()));

            // read the file line by line
            while ((aline = inputStream.readLine()) != null) {
                //System.out.println(aline);
                list.add(new ComplexLine(aline));
            }
        }
        catch (IOException ex) {
            System.err.println(ex.toString());
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex) {
                    System.err.println(ex.toString());
                }
            }
        }

        return list;
    }

    /**
     * Utility for read tests files.
     */
    private class ComplexLine {

        /**
         * First operand.
         */
        final Complex op1;

        /**
         * Second operand.
         */
        final Complex op2;

        /**
         * Result.
         */
        final Complex res;

        /**
         * Decode one line.
         *
         * @param line the line to decode.
         */
        ComplexLine(String line) {
            //(6.80375,-2.11234) + (5.66198,5.9688) = (12.4657,3.85646)

            Pattern pattern = Pattern.compile(
                    "\\(                # parentesi aperta\n" +
                    "\\-?\\d+\\.?\\d*   # numero con segno\n" +
                    ",\\s               # una virgola seguita da uno spazio\n" +
                    "\\-?\\d+\\.?\\d*   # numero con segno\n" +
                    "\\)                # parentesi chiusa\n"
                    , Pattern.COMMENTS);

            Matcher matcher = pattern.matcher(line);

            matcher.find();
            op1 = Complex.parseComplex(matcher.group());

            matcher.find();
            op2 = Complex.parseComplex(matcher.group());

            matcher.find();
            res = Complex.parseComplex(matcher.group());
        }

        @Override
        public String toString() {
            return op1.toString() + " " + op2.toString() + " " + res.toString();
        }

    }

}
