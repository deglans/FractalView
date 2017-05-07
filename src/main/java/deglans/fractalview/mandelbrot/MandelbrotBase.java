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

import deglans.fractalview.utility.CartesianPlane;
import deglans.fractalview.utility.Complex;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javafx.concurrent.Task;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * MandelbrotBase is the root for every fractals that needs a cartesian plane.
 * This class extends Task and implements the Callable interface for support
 * multi-threading.
 * The instances create by this class are "one shot" and can't be reused.
 *
 * @version 0.12
 * @author Deglans Dalpasso
 */
public abstract class MandelbrotBase extends Task<Long> implements Callable<Long> {

    /**
     * The CartesianPlane for conversion between CanvasPoint and Complex coordinate.
     */
    private final CartesianPlane plane;

    /**
     * The result of the calculus.
     */
    private final WritableImage image;

    /**
     * The Callback function for take an action when the calculus is finished.
     */
    private Callback onFinish = null;

    /**
     * Create a new instance for one fractal calculus.
     *
     * @param plane the CartesianPlane for conversion between CanvasPoint and Complex coordinate.
     * @param image the result of the calculus.
     */
    public MandelbrotBase(CartesianPlane plane, WritableImage image) {
        this.plane = plane;
        this.image = image;
    }

    /**
     * Set the Callback function for take an action when the calculus is finished.
     * Substitute of setOnSucceeded(), because i thing in the Animation class
     * the thread is start by calling directly the function call() from
     * the executor.
     *
     * @param onFinish the function to be call when the calculus is finished.
     */
    public void setOnFinish(Callback onFinish) {
        this.onFinish = onFinish;
    }

    /**
     * Function for calculate the fractal.
     *
     * @return the time taken for the calculus in milliseconds.
     * @throws Exception
     */
    @Override
    public Long call() throws Exception {
        long startTime = System.currentTimeMillis();

        // Use a stream for the y loop
        IntStream yStream = IntStream.range(0, (int)plane.getHeight()).parallel();

        // Counter for the progress bar
        final AtomicInteger progress = new AtomicInteger(0);
        PixelWriter pixelWriter = image.getPixelWriter();

        yStream.forEach((int y) -> {
            Color c;
            for (int x = 0; x < plane.getWidth(); x++) {
                if (isCancelled()) {
                    return;
                }
                // Calculate the point and draw it
                c = calcPoint(plane.toComplex(x, y));
                synchronized (pixelWriter) {
                    pixelWriter.setColor(x, y, c);
                }
            }
            if (isCancelled()) {
                return;
            }
            updateProgress(progress.incrementAndGet(), plane.getHeight());
        });

        // if set use the onFinish callback function
        if (onFinish != null) {
            onFinish.call(null);
        }

        return System.currentTimeMillis() - startTime;
    }

    /**
     * The function used to calculate a point of the fractal.
     *
     * @param c the point to be calculate.
     * @return the color of this point.
     */
    protected abstract Color calcPoint(Complex c);

}
