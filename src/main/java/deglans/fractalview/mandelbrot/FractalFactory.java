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

import deglans.fractalview.utility.ColorPalette;

/**
 * FractalFactory is the factory for all fractal.
 *
 * @version 0.3
 * @author Deglans Dalpasso
 */
public class FractalFactory {

    /**
     * The list of all fractal.
     */
    public static final String[] FRACTAL_LIST = {
        "Mandelbrot Simple",
        "Mandelbrot Simple (default color)",
        "Mandelbrot Simple (HUE color)",
        "Julia Simple",
        "Julia Simple (default color)",
        "Julia Simple (HUE color)",
        "Mandelbrot Periodic",
        "Mandelbrot Periodic (default color)",
        "Mandelbrot Periodic (HUE color)",
        "Burning Ship Simple",
        "Burning Ship Simple (default color)",
        "Burning Ship Simple (HUE color)",
        "Burning Julia Simple",
        "Burning Julia Simple (default color)",
        "Burning Julia Simple (HUE color)",
        "Mandelbrot Lyapunov"
    };

    /**
     * Factory for create fractal instances.
     *
     * @param selectedFractal the selected fractal.
     * @param dataBox the data for calculate the fractal.
     * @param colorPalette the colors for the fractal.
     * @return the fractal ready to calculate.
     */
    public static MandelbrotBase bulidFractal(String selectedFractal,
            DataBox dataBox, ColorPalette colorPalette) {

        switch (selectedFractal) {
            case "Mandelbrot Simple":
                return new MandelbrotSimple(dataBox, colorPalette);

            case "Mandelbrot Simple (default color)":
                return new MandelbrotSimple(dataBox, false);

            case "Mandelbrot Simple (HUE color)":
                return new MandelbrotSimple(dataBox, true);

            case "Julia Simple":
                return new JuliaSimple(dataBox, colorPalette);

            case "Julia Simple (default color)":
                return new JuliaSimple(dataBox, false);

            case "Julia Simple (HUE color)":
                return new JuliaSimple(dataBox, true);

            case "Mandelbrot Periodic":
                return new MandelbrotPeriodic(dataBox, colorPalette);

            case "Mandelbrot Periodic (default color)":
                return new MandelbrotPeriodic(dataBox, false);

            case "Mandelbrot Periodic (HUE color)":
                return new MandelbrotPeriodic(dataBox, true);

            case "Burning Ship Simple":
                return new BurningShipSimple(dataBox, colorPalette);

            case "Burning Ship Simple (default color)":
                return new BurningShipSimple(dataBox, false);

            case "Burning Ship Simple (HUE color)":
                return new BurningShipSimple(dataBox, true);

            case "Burning Julia Simple":
                return new BurningJuliaSimple(dataBox, colorPalette);

            case "Burning Julia Simple (default color)":
                return new BurningJuliaSimple(dataBox, false);

            case "Burning Julia Simple (HUE color)":
                return new BurningJuliaSimple(dataBox, true);

            case "Mandelbrot Lyapunov":
                return new MandelbrotLyapunov(dataBox, false);

            default:
                return null;
        }
    }

}
