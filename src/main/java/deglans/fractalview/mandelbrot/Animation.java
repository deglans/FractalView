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

import com.icafe4j.image.gif.GIFTweaker;
import deglans.fractalview.utility.CartesianPlane;
import deglans.fractalview.utility.ColorPalette;
import deglans.fractalview.utility.Complex;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

/**
 * Create gif animations of fractals.
 *
 * @version 0.6
 * @author Deglans Dalpasso
 */
public class Animation extends Task<Long> {

    /**
     * Default total number of frames for the animation.
     */
    public static final int DEFAULT_TOTAL_FRAMES = 10;

    /**
     * Fractal to render.
     */
    private final String selectedFractal;

    /**
     * Number of frames.
     */
    private final int frameNumber;

    /**
     * Start point of the animation.
     */
    private final DataBox start;

    /**
     * End point of the animation.
     */
    private final DataBox end;

    /**
     * ColorPalette for the animation.
     */
    private final ColorPalette colorPalette;

    /**
     * Buffer for frames.
     */
    private WritableImage[] frameBuffer;

    /**
     * List of thread, one for frame.
     */
    private List<Callable<Long>> frameThread;

    /**
     * List of Future, one for frame.
     */
    private List<Future<Long>> frameFuture;

    /**
     * Counter for the progress bar.
     */
    private final AtomicInteger count;

    /**
     * Maximum for the progress bar.
     */
    private final int maxCount;

    /**
     * Create the Animation instance.
     *
     * @param selectedFractal fractal to render.
     * @param frameNumber number of frames.
     * @param start start point of the animation.
     * @param end end point of the animation.
     * @param colorPalette ColorPalette for the animation.
     */
    public Animation(String selectedFractal, int frameNumber,
            DataBox start, DataBox end,
            ColorPalette colorPalette) {

        this.selectedFractal = selectedFractal;
        this.frameNumber = frameNumber;
        this.start = start;
        this.end = end;
        this.colorPalette = colorPalette;
        count = new AtomicInteger(0);
        maxCount = frameNumber + 5; // see *
    }

    /**
     * Function for calculate the fractal animation.
     * Creates one thread for frame.
     *
     * @return the time taken for the calculus in milliseconds.
     * @throws Exception
     */
    @Override
    protected Long call() throws Exception {
        long startTime = System.currentTimeMillis();
        long calcTime = 0;
        ExecutorService executor = null;

        try {
            frameBuffer = new WritableImage[frameNumber];
            updateProgress(count.incrementAndGet(), maxCount); // * 1
            if (isCancelled()) {
                return System.currentTimeMillis() - startTime;
            }

            executor = Executors.newWorkStealingPool();
            //executor = Executors.newFixedThreadPool(4);
            updateProgress(count.incrementAndGet(), maxCount); // * 2
            if (isCancelled()) {
                return System.currentTimeMillis() - startTime;
            }

            frameThread = initTasks();
            updateProgress(count.incrementAndGet(), maxCount); // * 3
            if (isCancelled()) {
                return System.currentTimeMillis() - startTime;
            }

            frameFuture = executor.invokeAll(frameThread);
            for (Future<Long> f : frameFuture) {
                calcTime += f.get();
            }
            updateProgress(count.incrementAndGet(), maxCount); // * 4

            makeAnimation();
            updateProgress(count.incrementAndGet(), maxCount); // * 5
        }
        catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
        finally {
            // Turnoff the executor
            if (executor != null) {
                executor.shutdown();
            }
        }

        return System.currentTimeMillis() - startTime + calcTime;
    }

    /**
     * Cancel threads that are creating frames.
     */
    public void cancelFrameThreads() {
        frameThread.stream().forEach((t) -> {
            ((MandelbrotBase)t).cancel();
        });
    }

    /**
     * Initialize threads for making frames.
     *
     * @return threads for making frames.
     */
    private List<Callable<Long>> initTasks() {
        List<Callable<Long>> list = new ArrayList<>();

        for (int k = 0; k < frameNumber; k++) {
            frameBuffer[k] = new WritableImage((int) start.getCartesianPlane().getWidth(),
                    (int) start.getCartesianPlane().getHeight());

            MandelbrotBase tmp =
                    FractalFactory.bulidFractal(selectedFractal, nextDataBox(k), colorPalette);

            tmp.setOnFinish(t -> {
                updateProgress(count.incrementAndGet(), maxCount);
                return null;
            });

            list.add((Callable<Long>) tmp);
        }

        return list;
    }

    /**
     * Interpolate the start point with the end point.
     *
     * @param n actual frame.
     * @return information for making the actual frame.
     */
    private DataBox nextDataBox(int n) {
        double f = (double)n / (frameNumber-1);

//        int nxt_maxIterations = (int) (start.getMaxIterations() +
//                ((end.getMaxIterations() - start.getMaxIterations()) * f));

        Complex nxt_power = start.getPower().interpolate(end.getPower(), f);

        Complex nxt_constant = start.getConstant().interpolate(end.getConstant(), f);

        Complex nxt_upLeft = start.getCartesianPlane().getUpLeft().
                interpolate(end.getCartesianPlane().getUpLeft(), f);

        Complex nxt_downRight = start.getCartesianPlane().getDownRight().
                interpolate(end.getCartesianPlane().getDownRight(), f);

        CartesianPlane new_cp = new CartesianPlane(start.getCartesianPlane().getWidth(),
                start.getCartesianPlane().getHeight(), nxt_upLeft, nxt_downRight);

        return new DataBox(start.getMaxIterations(), nxt_power, nxt_constant, new_cp, frameBuffer[n]);
    }

    /**
     * Put all together the frames into a gif animation.
     */
    private void makeAnimation() {
        try {
            FileOutputStream fout = new FileOutputStream("anime.gif");

            BufferedImage[] images = new BufferedImage[frameNumber];
            int[] delays = new int[frameNumber];

            for (int k = 0; k < frameNumber; k++) {
                images[k] = SwingFXUtils.fromFXImage(frameBuffer[k], null);
                delays[k] = 1000;
            }

            GIFTweaker.writeAnimatedGIF(images, delays, fout);
            fout.close();
        }
        catch (FileNotFoundException ex) {
            System.err.println(ex.toString());
        }
        catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

}
