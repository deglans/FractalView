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
package deglans.fractalview.GUI;

import deglans.fractalview.buddhabrot.BuddhabrotBase;
import deglans.fractalview.buddhabrot.BuddhabrotSimple;
import deglans.fractalview.utility.Complex;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * BuddhabrotGUI manage the interaction between user and BuddhabrotSimple.
 *
 * @version 0.1
 * @author Deglans Dalpasso
 */
public class BuddhabrotGUI extends BorderPane implements Initializable {

    /**
     * The fractal maker.
     */
    private BuddhabrotBase fractalMaker = null;

    /**
     * CartesianCanvasGUI.
     */
    @FXML
    private CartesianCanvasGUI canvas;

    /**
     * ProgressBar for the calculus.
     */
    @FXML
    private ProgressBar pbRendering;

    /**
     * TextField for the power.
     */
    @FXML
    private TextField tfPower;

    /**
     * TextField for the number of max iterations.
     */
    @FXML
    private TextField tfMaxIterations;

    /**
     * TextField for the Color for the value 0.
     */
    @FXML
    private TextField tfColorZero;

    /**
     * TextField for the Color for the value max.
     */
    @FXML
    private TextField tfColorMax;

    @FXML
    private TextField tfSupersampling;

    /**
     * Create FractalGUI by loading FractalGUI.fxml.
     *
     */
    public BuddhabrotGUI() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BuddhabrotGUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException exception) {
            System.err.println(exception.toString());
            throw new RuntimeException(exception);
        }
    }

    /**
     * Handle the event onAction of the button "Start Rendering".
     *
     * @param ae the ActionEvent.
     */
    @FXML
    public void handleStartRenderingOnAction(ActionEvent ae) {
        stopRender();
        startRender();
        ae.consume();
    }

    /**
     * Handle the event onAction of the button "Stop Rendering".
     *
     * @param ae the ActionEvent.
     */
    @FXML
    public void handleStopRenderingOnAction(ActionEvent ae) {
        stopRender();
        ae.consume();
    }

    /**
     * Initialize the FractalGUI.
     * Set the Callback, the TextField and the ComboBox.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        canvas.setCallbackRender(obj -> {
            startRender();
            return null;
        });

        tfMaxIterations.setText("100");
        tfPower.setText("(2.0, 0.0)");
        tfColorZero.setText("#000000");
        tfColorMax.setText("#ffffff");
    }

    /**
     * Render the selected fractal with the given parameters.
     */
    private void startRender() {
        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());

        fractalMaker = new BuddhabrotSimple(canvas.getCartesianPlane(), wi,
                getMaxIterations(), getPower(), getSupersampling(),
                getColorZero(), getColorMax());

        pbRendering.progressProperty().bind(fractalMaker.progressProperty());

        fractalMaker.setOnSucceeded(t -> {
            synchronized (canvas) {
                canvas.getGraphicsContext2D().drawImage(wi, 0, 0);
            }
        });

        Thread thread = new Thread(fractalMaker);
        thread.start();
    }

    /**
     * Stop the current rendering.
     */
    public void stopRender() {
        if (fractalMaker != null) {
            fractalMaker.cancel();
        }
        pbRendering.progressProperty().unbind();
        pbRendering.setProgress(0);
    }

    /**
     * Get the max number of iterations from tfMaxIterations.
     *
     * @return the max number of iterations.
     */
    private int getMaxIterations() {
        return Integer.parseInt(tfMaxIterations.getText());
    }

    private int getSupersampling() {
        return Integer.parseInt(tfSupersampling.getText());
    }

    /**
     * Get the power from tfPower.
     *
     * @return the power.
     */
    private Complex getPower() {
        return Complex.parseComplex(tfPower.getText());
    }

    public Color getColorZero() {
        return Color.web(tfColorZero.getText());
    }

    public Color getColorMax() {
        return Color.web(tfColorMax.getText());
    }

}
