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
package deglans.fractalview.mandelbrot;

import deglans.fractalview.utility.Complex;
import deglans.fractalview.utility.ColorPalette;
import javafx.scene.paint.Color;

/**
 * JuliaSimple is the most simple way to calculate the Julia fractals.
 * JuliaSimple use the escape-time algorithm and a simple color palette.
 *
 * http://mcgoodwin.net/julia/juliajewels.html
 *
 * @version 0.12
 * @author Deglans Dalpasso
 */
public class JuliaSimple extends MandelbrotBase {

    /**
     * The data of this fractal instance.
     */
    protected final DataBox dataBox;

    /**
     * The color palette of this fractal instance.
     */
    protected final ColorPalette colorPalette;

    /**
     * Create a JuliaSimple instance with the given data
     * and the default colors palette or HUE palette.
     *
     * @param dataBox the data of this fractal instance.
     * @param hue if true, use a HUE palette, else use the default color palette.
     */
    public JuliaSimple(DataBox dataBox, boolean hue) {
        super(dataBox.getCartesianPlane(), dataBox.getImage());
        this.dataBox = dataBox;
        this.colorPalette = new ColorPalette(this.dataBox.getMaxIterations(), hue);
    }

    /**
     * Create a JuliaSimple instance with the given data.
     *
     * @param dataBox the data of this fractal instance.
     * @param colorPalette the palette of this fractal instance.
     */
    public JuliaSimple(DataBox dataBox, ColorPalette colorPalette) {
        super(dataBox.getCartesianPlane(), dataBox.getImage());
        this.dataBox = dataBox;
        this.colorPalette = colorPalette;
    }

    /**
     * Calculate the color of c through the escape-time algorithm.
     *
     * @param c the point to be calculated.
     * @return the color of c.
     */
    @Override
    protected Color calcPoint(Complex c) {
        Complex z = new Complex(c);
        int count = 0;

        while ((count < dataBox.getMaxIterations()) && (z.mod() < Math.max(2, c.mod()))) {
            z = z.pow(dataBox.getPower()).plus(dataBox.getConstant());
            count++;
        }

        return colorPalette.getColor(count);
    }

}
