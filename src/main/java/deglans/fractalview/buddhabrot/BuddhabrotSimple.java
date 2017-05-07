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

import deglans.fractalview.utility.CartesianPlane;
import deglans.fractalview.utility.Complex;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Implementation of Buddhabrot.
 * https://en.wikipedia.org/wiki/Buddhabrot
 * https://it.wikipedia.org/wiki/Buddhabrot
 *
 * @version 0.1
 * @author Deglans Dalpasso
 */
public class BuddhabrotSimple extends BuddhabrotBase {

    /**
     * Number of iterations.
     */
    private final int maxIterations;

    private final Complex power;

    private final Color zero;

    private final Color max;

    private final WritableImage image;

    public BuddhabrotSimple(CartesianPlane plane, WritableImage image,
            int maxIterations, Complex power, int supersampling,
            Color zero, Color max) {

        super(plane, supersampling);

        this.maxIterations = maxIterations;
        this.power = power;
        this.zero = zero;
        this.max = max;
        this.image = image;
    }

    @Override
    protected void drawImage() {
        double maxMap = getMax();

        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                Color c = zero.interpolate(max, (double)getValue(y, x) / maxMap);
                pixelWriter.setColor(x, y, c);
            }
        }
    }

    @Override
    protected List<Complex> calculatePath(Complex c) {
        List<Complex> path = new ArrayList<>();
        int count = 0;
        Complex z = new Complex(0, 0);

        while ((count < maxIterations) && (z.mod() < 2)) {
            z = z.pow(power).plus(c);
            path.add(new Complex(z));
            count++;
        }

        if (z.mod() < 2) {
            return new ArrayList<>();
        }
        else {
            return path;
        }
    }

}
