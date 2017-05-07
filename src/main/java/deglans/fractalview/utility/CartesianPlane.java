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
 * CaresianPlane is used to convert coordinate between CanvasPoint and Complex.
 * TODO: check well this class for non-square plane!
 *
 * @version 0.10
 * @author Deglans Dalpasso
 */
public class CartesianPlane {

    /**
     * The height of the canvas.
     */
    private final double height;

    /**
     * The width of the canvas.
     */
    private final double width;

    /**
     * The point at up left of the cartesian plane.
     */
    private Complex upLeft;

    /**
     * The point at down right of the cartesian plane.
     */
    private Complex downRight;

    /**
     * Relationship between pixel and plane unit (pixel/unit).
     */
    private double scale;

    /**
     * Create a new CartesianPlane with the given data.
     *
     * @param width the width of the canvas.
     * @param height the height of the canvas.
     * @param upLeft the point at up left of the cartesian plane.
     * @param downRight the point at down right of the cartesian plane.
     */
    public CartesianPlane(double width, double height, Complex upLeft, Complex downRight) {
        this.width = width;
        this.height = height;

        // Calculate the side of the plane
        double sideX = (downRight.getReal() - upLeft.getReal());
        double sideY = (upLeft.getImag() - downRight.getImag());
        // and use it to calculate the center of the plane
        Complex center = new Complex(upLeft.getReal() + sideX/2, upLeft.getImag() - sideY/2);

        // Calculate the scale (pixel/unit)
        scale = width / sideX;
        // and recalculate the y side
        double newSideY = height / scale;

        // Use this data for calculate the correct upLeft and downRight
        // these are different from the function parameter only if the area is not a square
        // TODO this situascion need to be well tested
        this.upLeft = new Complex(upLeft.getReal(), center.getImag()+(newSideY/2));
        this.downRight = new Complex(downRight.getReal(), center.getImag()-(newSideY/2));
        //this.upLeft = new Complex(upLeft.getReal(), upLeft.getImag()+newSideY);
        //this.downRight = new Complex(downRight.getReal(), downRight.getImag()-newSideY);
    }

    /**
     * Move the CatesianPlane (used by drag-and-drop function).
     *
     * @param start the initial position of drag-and-drop.
     * @param stop the final position of drag-and-drop.
     */
    public void move(Complex start, Complex stop) {
        Complex delta = start.minus(stop);
        upLeft = upLeft.plus(delta);
        downRight = downRight.plus(delta);
    }

    /**
     * Zoom the CartesianPlane at a given point (old double-click function).
     *
     * @param center the point to be zoomed.
     * @param zoom the factor of the zoom.
     */
    public void zoomCenter(Complex center, double zoom) {
        // Calculate like the constructor
        double newSideX = (downRight.getReal() - upLeft.getReal()) * zoom;

        scale = width / newSideX;
        double newSideY = height / scale;

        //center = new Complex(center);
        //upLeft = new Complex(center.getReal()-(newSideX/2), center.getImag()+(newSideY/2));
        //downRight = new Complex(center.getReal()+(newSideX/2), center.getImag()-(newSideY/2));

        upLeft = new Complex(center.getReal()-(newSideX/2), center.getImag()+(newSideY/2));
        downRight = new Complex(center.getReal()+(newSideX/2), center.getImag()-(newSideY/2));
    }

    /**
     * Zoom the CartesianPlane at the mouse position (used by scroll function).
     *
     * @param mouse the position of the mouse.
     * @param zoom the factor of the zoom.
     */
    public void zoomAtMousePos(Complex mouse, double zoom) {
        // Calculate like the constructor
        double deltaX = (mouse.getReal() - upLeft.getReal()) * zoom;
        double deltaY = (upLeft.getImag() - mouse.getImag()) * zoom;
        double newScale = scale / zoom;
        //double newScale = width / deltaX;

        Complex newUpLeft = new Complex(mouse.getReal()-deltaX, mouse.getImag()+deltaY);
        //Complex newDownRight = new Complex(newUpLeft.getReal()+(mouse.getX()/newScale), newUpLeft.getImag()-(mouse.getY()/newScale));
        //Complex newDownRight = new Complex(newUpLeft.getReal()+deltaX, newUpLeft.getImag()-deltaY);

        //double sideX = (newDownRight.getReal() - newUpLeft.getReal());
        //double sideY = (newUpLeft.getImag() - newDownRight.getImag());
        double sideX = width / newScale;
        double sideY = height / newScale;
        Complex newCenter = new Complex(newUpLeft.getReal() + sideX/2, newUpLeft.getImag() - sideY/2);
        zoomCenter(newCenter, zoom);
    }

    /**
     * Return the height of the canvas.
     *
     * @return the height of the canvas.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Return the width of the canvas.
     *
     * @return the width of the canvas.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Return the point at up left of the cartesian plane.
     *
     * @return the point at up left of the cartesian plane.
     */
    public Complex getUpLeft() {
        return new Complex(upLeft);
    }

    /**
     * Return the point at down right of the cartesian plane.
     *
     * @return the point at down right of the cartesian plane.
     */
    public Complex getDownRight() {
        return new Complex(downRight);
    }

    /**
     * Return the point at center of the cartesian plane.
     *
     * @return the point at center of the cartesian plane.
     */
    public Complex getCenter() {
        double sideX = (downRight.getReal() - upLeft.getReal());
        double sideY = (upLeft.getImag() - downRight.getImag());
        return new Complex(upLeft.getReal() + sideX/2, upLeft.getImag() - sideY/2);
    }

    /**
     * Return the relationship between pixel and plane unit (pixel/unit).
     *
     * @return the relationship between pixel and plane unit (pixel/unit).
     */
    public double getScale() {
        return scale;
    }

    /**
     * Convert the canvas (x, y) coordinates to CartesianPlane coordinates.
     *
     * @param x the x coordinate on the canvas.
     * @param y the y coordinate on the canvas.
     * @return the CartesianPlane coordinates.
     */
    public Complex toComplex(double x, double y) {
        double tx = (x - (-upLeft.getReal()*scale)) / scale;
        double ty = (y - (upLeft.getImag()*scale)) / -scale;
        return new Complex(tx, ty);
    }

    /**
     * Convert the CanvasPoint coordinates to CartesianPlane coordinates.
     *
     * @param p the coordinate on the canvas.
     * @return the CartesianPlane coordinates.
     */
    public Complex toComplex(CanvasPoint p) {
        return toComplex(p.getX(), p.getY());
    }

    /**
     * Convert the CartesianPlane coordinates to CanvasPoint coordinates.
     *
     * @param x the x coordinate on the CartesianPlane.
     * @param y the y coordinate on the CartesianPlane.
     * @return the CanvasPoint coordinates.
     */
    public CanvasPoint toCanvasPoint(double x, double y) {
        //double tx = (x * (-upLeft.getReal()*scale) * scale);
        double tx = (x * scale) + (-upLeft.getReal()*scale);
        //double ty = (y * (upLeft.getImag()*scale) * scale);
        double ty = (y * -scale) + (upLeft.getImag()*scale);
        return new CanvasPoint(tx, ty);
    }

    /**
     * Convert the CartesianPlane coordinates to CanvasPoint coordinates.
     *
     * @param z the coordinate on the CartesianPlane.
     * @return the CanvasPoint coordinates.
     */
    public CanvasPoint toCanvasPoint(Complex z) {
        return CartesianPlane.this.toCanvasPoint(z.getReal(), z.getImag());
    }

}
