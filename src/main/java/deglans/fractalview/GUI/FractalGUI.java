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

import deglans.fractalview.utility.Complex;
import deglans.fractalview.mandelbrot.MandelbrotBase;
import deglans.fractalview.mandelbrot.DataBox;
import deglans.fractalview.mandelbrot.FractalFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;

/**
 * FractalGUI manage the interaction between user and FractalFactory.
 *
 * @version 0.12
 * @author Deglans Dalpasso
 */
public class FractalGUI extends BorderPane implements Initializable {

    /**
     * The fractal maker.
     */
    private MandelbrotBase fractalMaker = null;

    /**
     * CartesianCanvasGUI.
     */
    @FXML
    private CartesianCanvasGUI canvas;

    /**
     * ComboBox for select the fractal type.
     */
    @FXML
    private ComboBox cbFractalType;

    /**
     * ProgressBar for the calculus.
     */
    @FXML
    private ProgressBar pbRendering;

    /**
     * ColorPaletteGUI.
     */
    @FXML
    private ColorPaletteGUI colorPaletteGUI;

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
     * TextField for the constant (used by Julia like fractals).
     */
    @FXML
    private TextField tfConstant;

    /**
     * Create FractalGUI by loading FractalGUI.fxml.
     *
     */
    public FractalGUI() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FractalGUI.fxml"));
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

        canvas.setCallbackRightClick(z -> {
            setConstant(z);
            return null;
        });

        tfMaxIterations.setText(Integer.toString(DataBox.DEFAULT_MAX_ITERATIONS));
        tfPower.setText(DataBox.DEFAULT_POWER.toString());
        tfConstant.setText(DataBox.DEFAULT_CONSTANT.toString());

        cbFractalType.setItems(FXCollections.observableArrayList(FractalFactory.FRACTAL_LIST));
        cbFractalType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                canvas.resetPlane();
            }
        });
        cbFractalType.setValue(FractalFactory.FRACTAL_LIST[0]);
    }

    /**
     * Render the selected fractal with the given parameters.
     */
    private void startRender() {
        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());

        fractalMaker = FractalFactory.bulidFractal(cbFractalType.getSelectionModel().getSelectedItem().toString(),
                getDataBox(wi), colorPaletteGUI.getColorPalette(getMaxIterations()));

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
     * Get the information for make fractals.
     *
     * @param wi the image where save data.
     * @return the DataBox.
     */
    private DataBox getDataBox(WritableImage wi) {
        return new DataBox(getMaxIterations(), getPower(), getConstant(),
                canvas.getCartesianPlane(), wi);
    }

    /**
     * Get the max number of iterations from tfMaxIterations.
     *
     * @return the max number of iterations.
     */
    private int getMaxIterations() {
        return Integer.parseInt(tfMaxIterations.getText());
    }

    /**
     * Get the power from tfPower.
     *
     * @return the power.
     */
    private Complex getPower() {
        return Complex.parseComplex(tfPower.getText());
    }

    /**
     * Need for the right click features.
     *
     * @param c the Complex number to set.
     */
    public void setConstant(Complex c) {
        tfConstant.setText(c.toString());
    }

    /**
     * Get the constant from tfConstant.
     *
     * @return the constant.
     */
    private Complex getConstant() {
        return Complex.parseComplex(tfConstant.getText());
    }

}
