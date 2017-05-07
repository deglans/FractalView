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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.Interpolatable;

/**
 * Complex implements a complex number.
 * Defines complex arithmetic and mathematical functions.
 *
 * https://en.wikipedia.org/wiki/Complex_number
 * http://mathworld.wolfram.com/ComplexExponentiation.html
 *
 * @version 0.10
 * @author Deglans Dalpasso
 */
public class Complex implements Interpolatable<Complex> {

    /**
     * The real part of Complex number.
     */
    private final double re;

    /**
     * The imaginary part of Complex number.
     */
    private final double im;

    /**
     * Create a new Complex number with the given real and imaginary parts.
     *
     * @param real the Complex number real part.
     * @param imag the Complex number imaginary part.
     */
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    /**
     * Create a new Complex number from another Complex number.
     *
     * @param z the Complex number to copy.
     */
    public Complex(Complex z) {
        re = z.re;
        im = z.im;
    }

    /**
     * Create a new Complex number from a string.
     *
     * @param s a string in the format (real, imaginary).
     * @return a Complex number.
     * @throws NumberFormatException
     */
    public static Complex parseComplex(String s) throws NumberFormatException {
        /*
            \\-?  # segno meno opzionale
            \\d+  # una o piu cifre
            \\.?  # segno decimale opzionale
            \\d*  # zero o piu cifre
        */
        Pattern pattern = Pattern.compile("\\-?\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(s);

        matcher.find();
        double double_re = Double.parseDouble(matcher.group());
        matcher.find();
        double double_im = Double.parseDouble(matcher.group());

        return new Complex(double_re, double_im);
    }

    /**
     * Return a string that represent this Complex number.
     *
     * @return a string in the format (real, imaginary).
     */
    @Override
    public String toString() {
        return "(" + re + ", " + im + ")";
    }

    /**
     * Check if two Complex number are equal to within a positive delta.
     * The check is done by take the difference between real and imaginary part separately.
     *
     * @param z the Complex number to check.
     * @param delta the maximum delta between this Complex number and z.
     * @return true if the difference between numbers are strictly less than delta; false otherwise.
     */
    public boolean equals(Complex z, double delta) {
        double delta_re = Math.abs(re - z.re);
        double delta_im = Math.abs(im - z.im);
        return (delta_re < delta) && (delta_im < delta);
    }

    /**
     * Return the real part of this Complex number.
     *
     * @return the real part of this Complex number.
     */
    public double getReal() {
        return re;
    }

    /**
     * Return the imaginary part of this Complex number.
     *
     * @return the imaginary part of this Complex number.
     */
    public double getImag() {
        return im;
    }

    /**
     * Addition of Complex numbers.
     * (a+i*b) + (c+i*d) = (a+c) + i*(b+d)
     *
     * @param w is the number to add.
     * @return z + w where z is this Complex number.
     */
    public Complex plus(Complex w) {
        return new Complex(re + w.re, im + w.im);
    }

    /**
     * Subtraction of Complex numbers.
     * (a+i*b) - (c+i*d) = (a-c) + i*(b-d)
     *
     * @param w is the number to subtract.
     * @return z - w where z is this Complex number.
     */
    public Complex minus(Complex w) {
        return new Complex(re - w.re, im - w.im);
    }

    /**
     * Complex multiplication.
     * (a+i*b) * (c+i*d) = (a*c - b*d) + i*(b*c + a*d)
     *
     * @param w is the number to multiply by.
     * @return z * w where z is this Complex number.
     */
    public Complex times(Complex w) {
        double new_re = re * w.re - im * w.im;
        double new_im = im * w.re + re * w.im;
        return new Complex(new_re, new_im);
    }

    /**
     * Division of Complex numbers.
     * (a+i*b) / (c+i*d) = (a*c+b*d)/den + i*((b*c-a*d)/den)
     * den = c*c+d*d
     *
     * @param w is the number to divide by.
     * @return z / w where z is this Complex number.
     */
    public Complex div(Complex w) {
        double den = w.re * w.re + w.im * w.im;
        double new_re = (re * w.re + im * w.im) / den;
        double new_im = (im * w.re - re * w.im) / den;
        return new Complex(new_re, new_im);
    }

    /**
     * Returns the modulus of this Complex number.
     *
     * @return the modulus of this Complex number.
     */
    public double mod() {
        return Math.sqrt(re * re + im * im);
    }

    /**
     * Returns the argument in radians of this Complex number.
     *
     * @return the argument in radians of this Complex number.
     */
    public double arg() {
        return Math.atan2(im, re);
    }

    /**
     * Returns this Complex number raised to the power of n using De Moivre's formula.
     *
     * @param n the exponent.
     * @return this Complex number raised to the power of n.
     */
    public Complex pow(int n) {
        double new_mod = Math.pow(mod(), n);
        double new_arg = arg() * n;
        double new_real = new_mod * Math.cos(new_arg);
        double new_imag = new_mod * Math.sin(new_arg);
        return new Complex(new_real, new_imag);
    }

    /**
     * Returns this Complex number raised to the power of another Complex number.
     *
     * @param z the exponent.
     * @return this Complex number raised to the power of z.
     */
    public Complex pow(Complex z) {
        if ((re == 0) && (im == 0)) {
            return new Complex(0, 0);
        }
        else {
            double new_mod = Math.pow((re*re + im*im), z.re/2) * Math.exp(-z.im * arg());
            double new_arg = (z.re * arg()) + (0.5 * z.im * Math.log(re*re + im*im));
            double new_real = new_mod * Math.cos(new_arg);
            double new_imag = new_mod * Math.sin(new_arg);
            return new Complex(new_real, new_imag);
        }
    }

    /**
     * The function calculates an interpolated value along the fraction t between 0.0 and 1.0.
     * When t = 1.0, endVal is returned.
     *
     * @param endValue target value.
     * @param t fraction between 0.0 and 1.0.
     * @return the interpolated value.
     */
    @Override
    public Complex interpolate(Complex endValue, double t) {
        Complex ct = new Complex(t, 0);
        Complex delta = endValue.minus(this).times(ct);
        return this.plus(delta);
    }

}
