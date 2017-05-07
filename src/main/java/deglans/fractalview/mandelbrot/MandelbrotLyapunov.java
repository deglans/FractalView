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
 * MandelbrotLyapunov is a more advanced way to calculate the Mandelbrot fractals.
 * This class did not work!
 *
 * http://math.stackexchange.com/questions/1257555/how-to-compute-a-negative-multibrot-set
 *
 * @version 0.2
 * @author Deglans Dalpasso
 */
public class MandelbrotLyapunov extends MandelbrotBase {

    /**
     * The data of this fractal instance.
     */
    protected final DataBox dataBox;

    /**
     * The color palette of this fractal instance.
     */
    protected final ColorPalette colorPalette;

    /**
     * Create a MandelbrotLyapunov instance with the given data
     * and the default colors palette or HUE palette.
     *
     * @param dataBox the data of this fractal instance.
     * @param hue if true, use a HUE palette, else use the default color palette.
     */
    public MandelbrotLyapunov(DataBox dataBox, boolean hue) {
        super(dataBox.getCartesianPlane(), dataBox.getImage());
        this.dataBox = dataBox;
        this.colorPalette = new ColorPalette(this.dataBox.getMaxIterations(), hue);
    }

    /**
     * Create a MandelbrotLyapunov instance with the given data.
     *
     * @param dataBox the data of this fractal instance.
     * @param colorPalette the palette of this fractal instance.
     */
    public MandelbrotLyapunov(DataBox dataBox, ColorPalette colorPalette) {
        super(dataBox.getCartesianPlane(), dataBox.getImage());
        this.dataBox = dataBox;
        this.colorPalette = colorPalette;
    }

    @Override
    protected Color calcPoint(Complex c) {
        // http://math.stackexchange.com/questions/1257555/how-to-compute-a-negative-multibrot-set
        Complex z = new Complex(0, 0);
        Complex derivative;
        double lyapunov = 0;

        for (int count = 0; count < dataBox.getMaxIterations(); count++) {
            z = z.pow(dataBox.getPower()).plus(c);
            derivative = new Complex(z);
            lyapunov += Math.log(derivative.mod());
        }

        lyapunov /= dataBox.getMaxIterations();

        if (lyapunov <= 0) {
            // If it is equal to or less than zero, then it is part of the set.
            return Color.BLACK;
        }
        else {
            return Color.WHITE;
        }
    }

}
