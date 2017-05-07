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
import deglans.fractalview.utility.CartesianPlane;
import deglans.fractalview.mandelbrot.MandelbrotBase;
import deglans.fractalview.mandelbrot.DataBox;
import deglans.fractalview.mandelbrot.Animation;
import deglans.fractalview.mandelbrot.FractalFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
 * AnimationGUI manage the interaction between user and Animation class.
 *
 * @version 0.12
 * @author Deglans Dalpasso
 */
public class AnimationGUI extends BorderPane implements Initializable {

    /**
     * CartesianCanvasGUI for the preview.
     */
    @FXML
    private CartesianCanvasGUI canvas;

    /**
     * ComboBox for select the fractal to animate.
     */
    @FXML
    private ComboBox cbFractalType;

    /**
     * Number of frames for the animation.
     */
    @FXML
    private TextField tfTotalFrames;

    /**
     * TextField of max iterations for the loop that calculate the fractal.
     */
    @FXML
    private TextField tfMaxIterations;

    /**
     * ProgressBar for show calculus state.
     */
    @FXML
    private ProgressBar pbRendering;

    /**
     * TextField of up left edge at start of the animation.
     */
    @FXML
    private TextField tfUpLeftStart;

    /**
     * TextField of down right edge at start of the animation.
     */
    @FXML
    private TextField tfDownRightStart;

    /**
     * TextField of power at start of the animation.
     */
    @FXML
    private TextField tfPowerStart;

    /**
     * TextField of constant at start of the animation (used by Julia like fractals).
     */
    @FXML
    private TextField tfConstantStart;

    /**
     * ProgressBar for the preview at start.
     */
    @FXML
    private ProgressBar pbPreviewStart;

    /**
     * The fractal maker at start.
     */
    private MandelbrotBase fractalMakerStart = null;

    /**
     * TextField of up left edge at the end of the animation.
     */
    @FXML
    private TextField tfUpLeftEnd;

    /**
     * TextField of down right edge at the end of the animation.
     */
    @FXML
    private TextField tfDownRightEnd;

    /**
     * TextField of power at the end of the animation.
     */
    @FXML
    private TextField tfPowerEnd;

    /**
     * TextField of constant at the end of the animation (used by Julia like fractals).
     */
    @FXML
    private TextField tfConstantEnd;

    /**
     * ProgressBar for the preview at end.
     */
    @FXML
    private ProgressBar pbPreviewEnd;

    /**
     * The fractal maker at end.
     */
    private MandelbrotBase fractalMakerEnd = null;

    /**
     * ColorPaletteGUI for color the fractals.
     */
    @FXML
    private ColorPaletteGUI colorPaletteGUI;

    /**
     * The animation maker.
     */
    private Animation animationMaker;

    /**
     * Create AnimationGUI by loading AnimationGUI.fxml.
     */
    public AnimationGUI() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AnimationGUI.fxml"));
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
     * Handle the event onAction of the button "Start preview" in the Start Tab.
     *
     * @param ae the ActionEvent.
     */
    public void handleStartPreviewAtStartOnAction(ActionEvent ae) {
        handleStopPreviewAtStartOnAction(null);

        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());

        CartesianPlane tmp = new CartesianPlane(canvas.getWidth(), canvas.getHeight(),
                Complex.parseComplex(tfUpLeftStart.getText()),
                Complex.parseComplex(tfDownRightStart.getText()));

        DataBox start = new DataBox(Integer.parseInt(tfMaxIterations.getText()),
                Complex.parseComplex(tfPowerStart.getText()),
                Complex.parseComplex(tfConstantStart.getText()),
                tmp, wi);

        fractalMakerStart = FractalFactory.bulidFractal(cbFractalType.getSelectionModel().getSelectedItem().toString(),
                start, colorPaletteGUI.getColorPalette(Integer.parseInt(tfMaxIterations.getText())));

        pbPreviewStart.progressProperty().bind(fractalMakerStart.progressProperty());

        fractalMakerStart.setOnSucceeded(t -> {
            synchronized (canvas) {
                canvas.getGraphicsContext2D().drawImage(wi, 0, 0);
            }
        });

        Thread thread = new Thread(fractalMakerStart);
        thread.start();

        ae.consume();
    }

    /**
     * Handle the event onAction of the button "Stop preview" in the Start Tab.
     *
     * @param ae the ActionEvent.
     */
    public void handleStopPreviewAtStartOnAction(ActionEvent ae) {
        if (fractalMakerStart != null) {
            fractalMakerStart.cancel();
        }
        pbPreviewStart.progressProperty().unbind();
        pbPreviewStart.setProgress(0);
    }

    /**
     * Handle the event onAction of the button "Start preview" in the End Tab.
     *
     * @param ae the ActionEvent.
     */
    public void handleStartPreviewAtEndOnAction(ActionEvent ae) {
        handleStopPreviewAtEndOnAction(null);

        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());

        CartesianPlane tmp = new CartesianPlane(canvas.getWidth(), canvas.getHeight(),
                Complex.parseComplex(tfUpLeftEnd.getText()),
                Complex.parseComplex(tfDownRightEnd.getText()));

        DataBox end = new DataBox(Integer.parseInt(tfMaxIterations.getText()),
                Complex.parseComplex(tfPowerEnd.getText()),
                Complex.parseComplex(tfConstantEnd.getText()),
                tmp, wi);

        fractalMakerEnd = FractalFactory.bulidFractal(cbFractalType.getSelectionModel().getSelectedItem().toString(),
                end, colorPaletteGUI.getColorPalette(Integer.parseInt(tfMaxIterations.getText())));

        pbPreviewEnd.progressProperty().bind(fractalMakerEnd.progressProperty());

        fractalMakerEnd.setOnSucceeded(t -> {
            synchronized (canvas) {
                canvas.getGraphicsContext2D().drawImage(wi, 0, 0);
            }
        });

        Thread thread = new Thread(fractalMakerEnd);
        thread.start();

        ae.consume();
    }

    /**
     * Handle the event onAction of the button "Stop preview" in the End Tab.
     *
     * @param ae the ActionEvent.
     */
    public void handleStopPreviewAtEndOnAction(ActionEvent ae) {
        if (fractalMakerEnd != null) {
            fractalMakerEnd.cancel();
        }
        pbPreviewEnd.progressProperty().unbind();
        pbPreviewEnd.setProgress(0);
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
     * Initialize AnimationGUI.
     * Set all TextField and the ComboBox.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFractalType.setItems(FXCollections.observableArrayList(FractalFactory.FRACTAL_LIST));

//        cbFractalType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//            @Override
//            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                canvas.resetPlane();
//            }
//        });

        cbFractalType.setValue(FractalFactory.FRACTAL_LIST[0]);

        tfTotalFrames.setText(Integer.toString(Animation.DEFAULT_TOTAL_FRAMES));
        tfMaxIterations.setText(Integer.toString(DataBox.DEFAULT_MAX_ITERATIONS));

        tfUpLeftStart.setText(DataBox.DEFAULT_UP_LEFT.toString());
        tfDownRightStart.setText(DataBox.DEFAULT_DOWN_RIGHT.toString());
        tfPowerStart.setText(DataBox.DEFAULT_POWER.toString());
        tfConstantStart.setText(DataBox.DEFAULT_CONSTANT.toString());

        tfUpLeftEnd.setText(DataBox.DEFAULT_UP_LEFT.toString());
        tfDownRightEnd.setText(DataBox.DEFAULT_DOWN_RIGHT.toString());
        tfPowerEnd.setText(DataBox.DEFAULT_POWER.toString());
        tfConstantEnd.setText(DataBox.DEFAULT_CONSTANT.toString());
    }

    /**
     * Render the animation.
     * Load all the data from GUI then start the calculus.
     */
    private void startRender() {
        CartesianPlane tmp = new CartesianPlane(canvas.getWidth(), canvas.getHeight(),
                Complex.parseComplex(tfUpLeftStart.getText()),
                Complex.parseComplex(tfDownRightStart.getText()));

        DataBox start = new DataBox(Integer.parseInt(tfMaxIterations.getText()),
                Complex.parseComplex(tfPowerStart.getText()),
                Complex.parseComplex(tfConstantStart.getText()),
                tmp, null);

        tmp = new CartesianPlane(canvas.getWidth(), canvas.getHeight(),
                Complex.parseComplex(tfUpLeftEnd.getText()),
                Complex.parseComplex(tfDownRightEnd.getText()));

        DataBox end = new DataBox(Integer.parseInt(tfMaxIterations.getText()),
                Complex.parseComplex(tfPowerEnd.getText()),
                Complex.parseComplex(tfConstantEnd.getText()),
                tmp, null);

        animationMaker = new Animation(cbFractalType.getSelectionModel().getSelectedItem().toString(),
                Integer.parseInt(tfTotalFrames.getText()), start, end,
                colorPaletteGUI.getColorPalette(Integer.parseInt(tfMaxIterations.getText())));

        pbRendering.progressProperty().bind(animationMaker.progressProperty());

        Thread thread = new Thread(animationMaker);
        thread.start();
    }

    /**
     * Stop the current rendering.
     */
    private void stopRender() {
        if (animationMaker != null) {
            animationMaker.cancelFrameThreads();
            animationMaker.cancel();
        }
        pbRendering.progressProperty().unbind();
        pbRendering.setProgress(0);
    }

}
