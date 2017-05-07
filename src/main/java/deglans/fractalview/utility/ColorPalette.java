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

import javafx.scene.paint.Color;

/**
 * ColorPalette contains all the information about how to color fractals.
 *
 * @version 0.1
 * @author Deglans Dalpasso
 */
public class ColorPalette {

    /**
     * Default colors for the color palette.
     */
    public static final Color[] DEFAULT_COLORS = {
        Color.rgb(40, 0, 0),
        Color.RED,
        Color.WHITE,
        Color.RED,
        Color.rgb(100, 0, 0),
        Color.RED,
        Color.rgb(50, 0, 0)
    };

    /**
     * Default stops for the color palette.
     */
    public static final double[] DEFAULT_STOPS = {
        0.0, 0.17, 0.25, 0.30, 0.5, 0.75, 1.0
    };

    /**
     * Default color for the points that are in the set.
     */
    public static final Color DEFAULT_COLOR_SET = Color.BLACK;

    /**
     * Default color for the points that are in the set (HUE palette).
     */
    public static final Color DEFAULT_COLOR_SET_HUE = Color.BLACK;

    /**
     * The color palette.
     */
    private final Color[] colorPalette;

    /**
     * The color for the points that are in the set.
     */
    private final Color colorSet;

    /**
     * The length of the palette.
     */
    private final int paletteLength;

    /**
     * Create a ColorPalette with default colors or HUE colors.
     *
     * @param paletteLength the length of the palette.
     * @param hue if true, make a HUE palette.
     */
    public ColorPalette(int paletteLength, boolean hue) {
        this.paletteLength = paletteLength;

        if (hue) {
            colorPalette = makeHUEPalette();
            colorSet = DEFAULT_COLOR_SET_HUE;
        }
        else {
            colorPalette = makePalette(DEFAULT_COLORS, DEFAULT_STOPS);
            colorSet = DEFAULT_COLOR_SET;
        }
    }

    /**
     * Create a ColorPalette with the given data.
     *
     * @param paletteLength the length of the palette.
     * @param colors the colors for the color palette.
     * @param stops the stops for the color palette.
     * @param colorSet the color for the points that are in the set.
     */
    public ColorPalette(int paletteLength, Color[] colors, double[] stops, Color colorSet) {
        this.paletteLength = paletteLength;
        this.colorSet = colorSet;
        colorPalette = makePalette(colors, stops);
    }

    /**
     * Create the palette.
     *
     * @param colors the colors for the color palette.
     * @param stops the stops for the color palette.
     * @return the palette.
     */
    private Color[] makePalette(Color[] colors, double[] stops) {
        Color[] rv = new Color[paletteLength];
        int j = 0;
        for (int i = 0; i < paletteLength; i++) {
            double p = (double) i / (paletteLength - 1);
            if (p > stops[j + 1]) {
                j++;
            }
            double val = (p - stops[j]) / (stops[j + 1] - stops[j]);
            rv[i] = colors[j].interpolate(colors[j + 1], val);
        }
        return rv;
    }

    /**
     * Create an HUE palette.
     *
     * @return the HUE palette.
     */
    private Color[] makeHUEPalette() {
        Color[] rv = new Color[paletteLength];
        double index;

        for (int k = 0; k < paletteLength; k++) {
            index = ((double)k / (paletteLength - 1)) * 360;
            rv[k] = Color.hsb(index, 1, 1, 1);
        }

        return rv;
    }

    /**
     * Get the colors from the palette.
     *
     * @param index the index in the palette.
     * @return the color associated with index.
     */
    public Color getColor(int index) {
        if (index >= paletteLength) {
            return colorSet;
        }
        else {
            return colorPalette[index];
        }
    }

}
