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
package deglans.fractalview.buddhabrot;

import deglans.fractalview.utility.CanvasPoint;
import deglans.fractalview.utility.CartesianPlane;
import deglans.fractalview.utility.Complex;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javafx.concurrent.Task;

/**
 * Base implementation of Buddhabrot.
 * https://en.wikipedia.org/wiki/Buddhabrot
 * https://it.wikipedia.org/wiki/Buddhabrot
 *
 * @version 0.1
 * @author Deglans Dalpasso
 */
public abstract class BuddhabrotBase extends Task<Long> {

    /**
     * The CartesianPlane for conversion between CanvasPoint and Complex coordinate.
     */
    private final CartesianPlane plane;

    /**
     * The matrix where save the points.
     */
    private final Map map;

    private final int supersampling;

    /**
     * Create a new instance for one fractal calculus.
     *
     * @param plane the CartesianPlane for conversion between CanvasPoint and Complex coordinate.
     * @param supersampling
     */
    public BuddhabrotBase(CartesianPlane plane, int supersampling) {
        this.plane = plane;
        this.supersampling = supersampling;

        map = new Map((int) plane.getHeight(), (int) plane.getWidth());
    }

    /**
     * Function for calculate the fractal.
     *
     * @return the time taken for the calculus in milliseconds.
     * @throws Exception
     */
    @Override
    protected Long call() throws Exception {
        long startTime = System.currentTimeMillis();

        // Counter for the progress bar
        final AtomicInteger progress = new AtomicInteger(0);

        // Use a stream for the y loop
        IntStream yStream = IntStream.range(0, map.rows).parallel();


        //for (int y = 0; y < map.rows; y++) {
        yStream.forEach((int y) -> {
            for (int x = 0; x < map.columns; x++) {
                if (isCancelled()) {
                    return;
                }
                List<List<Complex>> multipath = calculatePathSS(plane.toComplex(x, y));
                multipath.stream().forEach((l) -> {
                    l.stream().forEach((z) -> {
                        incrementAtPosition(z);
                    });
                });
            }
            if (isCancelled()) {
                return;
            }
            updateProgress(progress.incrementAndGet(), map.rows);
        });

        drawImage();

        return System.currentTimeMillis() - startTime;
    }

    protected List<List<Complex>> calculatePathSS(Complex c) {
        List<List<Complex>> multipath = new ArrayList<>();
        double delta = 1 / plane.getScale();
        double inc = delta / (supersampling + 1);

        double max_re = c.getReal() + delta;
        double max_im = c.getImag() - delta;

        for (double im = c.getImag(); im > max_im; im -= inc) {
            for (double re = c.getReal(); re < max_re; re += inc) {
                multipath.add(calculatePath(new Complex(re, im)));
            }
        }

        return multipath;
    }

    /**
     * The function used to calculate a point of the fractal.
     *
     * @param c the point to be calculate.
     * @return a list of points to be added at the map (eventually empty).
     */
    protected abstract List<Complex> calculatePath(Complex c);

    protected abstract void drawImage();

    /**
     * Increment the map at a given location.
     *
     * @param z the point to add at the map.
     */
    private synchronized void incrementAtPosition(Complex z) {
        CanvasPoint p = plane.toCanvasPoint(z);
        int r = (int)p.getY();
        int c = (int)p.getX();

        if (((0 <= r) && (r < map.rows)) && ((0 <= c) && (c < map.columns))) {
            map.incrementAndGet(r, c);
        }
    }

    /**
     * Get the value of the map at a given position.
     *
     * @param r the row.
     * @param c the column.
     * @return the value at (r, c).
     */
    protected int getValue(int r, int c) {
        return map.get(r, c);
    }

    protected int getMax() {
        return map.getMax();
    }

    /**
     * Get the number of rows of the map.
     *
     * @return the number of rows of the map.
     */
    protected int getRows() {
        return map.rows;
    }

    /**
     * Get the number of columns of the map.
     *
     * @return the number of columns of the map.
     */
    protected int getColumns() {
        return map.columns;
    }

    private final class Map {

        private final int[][] map;

        final int rows;

        final int columns;

        Map(int rows, int columns) {
            map = new int[rows][columns];
            this.rows = rows;
            this.columns = columns;
            resetMap();
        }

        void resetMap() {
            IntStream.range(0, rows).parallel().forEach((int r) -> {
                for (int c = 0; c < columns; c++) {
                    synchronized (map) {
                        map[r][c] = 0;
                    }
                }
            });
        }

        synchronized int incrementAndGet(int r, int c) {
            map[r][c]++;
            return map[r][c];
        }

        int getMax() {
            AtomicInteger max = new AtomicInteger(-1);
            IntStream.range(0, rows).parallel().forEach((int r) -> {
                for (int c = 0; c < columns; c++) {
                    max.accumulateAndGet(map[r][c], (int a, int b) -> {
                        return Math.max(a, b);
                    });
                }
            });
            return max.get();
        }

        synchronized int get(int r, int c) {
            return map[r][c];
        }

    }

}
