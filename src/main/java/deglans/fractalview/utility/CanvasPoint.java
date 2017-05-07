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

/**
 * CanvasPoint implements a point on a canvas.
 * This class is used as a box for coordinates x,y on a canvas.
 *
 * @version 0.10
 * @author Deglans Dalpasso
 */
public class CanvasPoint {

    /**
     * The x coordinate.
     */
    private final double x;

    /**
     * The y coordinate.
     */
    private final double y;

    /**
     * Create a new CanvasPoint with the given coordinate.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    public CanvasPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new CanvasPoint from another CanvasPoint.
     *
     * @param p the CanvasPoint to copy.
     */
    public CanvasPoint(CanvasPoint p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Return the x coordinate.
     *
     * @return the x coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Return the y coordinate.
     *
     * @return the y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Return a string that represent this CanvasPoint.
     *
     * @return a string in the format (x, y).
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
