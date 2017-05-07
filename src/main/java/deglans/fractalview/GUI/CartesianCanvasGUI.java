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
import deglans.fractalview.utility.CanvasPoint;
import deglans.fractalview.mandelbrot.DataBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * CartesianCanvasGUI manage the interaction between user and fractals.
 * This class allow to zoom and drag&drop fractals.
 * With right click you can select a constant for Julia like fractals.
 *
 * @version 0.11
 * @author Deglans Dalpasso
 */
public class CartesianCanvasGUI extends Canvas implements Initializable {

    /**
     * CartesianPlane for conversions.
     */
    private CartesianPlane cartesianPlane = null;

    /**
     * Tooltip for show coordinates in the CartesianPlane.
     */
    private final Tooltip tooltip;

    /**
     * Callback for the fractal rendering.
     */
    private Callback render = null;

    /**
     * Callback for the right click features.
     */
    private Callback<Complex, Void> rightClick = null;

    /**
     * Start position for the drag & drop.
     */
    private CanvasPoint startDragAndDrop;

    /**
     * Image to be shown during drag & drop.
     */
    private Image imageDragAndDrop;

    /**
     * Create CartesianCanvasGUI by loading CartesianCanvasGUI.fxml.
     */
    public CartesianCanvasGUI() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CartesianCanvasGUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException exception) {
            System.err.println(exception.toString());
            throw new RuntimeException(exception);
        }

        tooltip = new Tooltip("Error!");
        Tooltip.install(this, tooltip);

        resetPlane();
    }

    /**
     * Handle the event OnMouseClicked for the right click features.
     *
     * @param me the MouseEvent.
     */
    @FXML
    public void handleOnMouseClicked(MouseEvent me) {
        if (me.getButton() == MouseButton.SECONDARY) {
            Complex pos = cartesianPlane.toComplex(me.getX(), me.getY());
            if (rightClick != null) {
                rightClick.call(pos);
            }
        }
        me.consume();
    }

    /**
     * Handle the event OnMouseMoved for set the Tooltip.
     *
     * @param me the MouseEvent.
     */
    @FXML
    public void handleOnMouseMoved(MouseEvent me) {
        Complex pos = cartesianPlane.toComplex(me.getX(), me.getY());
        tooltip.setText(pos.toString());
        me.consume();
    }

    /**
     * Handle the event OnMousePressed, this is the start of drag & drop.
     *
     * @param me the MouseEvent.
     */
    @FXML
    public void handleOnMousePressed(MouseEvent me) {
        if (me.getButton() == MouseButton.PRIMARY) {
            startDragAndDrop = new CanvasPoint(me.getX(), me.getY());
            imageDragAndDrop = snapshot(null, null);
        }
        me.consume();
    }

    /**
     * Handle the event OnMouseDragged, this is the continuous of drag & drop.
     *
     * @param me the MouseEvent.
     */
    @FXML
    public void handleOnMouseDragged(MouseEvent me) {
        if (me.isPrimaryButtonDown()) {
            GraphicsContext gc = getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, getWidth(), getHeight());
            gc.drawImage(imageDragAndDrop,
                    me.getX() - startDragAndDrop.getX(), me.getY() - startDragAndDrop.getY());
        }
        me.consume();
    }

    /**
     * Handle the event OnMouseReleased, this is the end of drag & drop.
     *
     * @param me the MouseEvent.
     */
    @FXML
    public void handleOnMouseReleased(MouseEvent me) {
        if (me.getButton() == MouseButton.PRIMARY) {
            Complex start = cartesianPlane.toComplex(startDragAndDrop.getX(), startDragAndDrop.getY());
            Complex stop = cartesianPlane.toComplex(me.getX(), me.getY());
            cartesianPlane.move(start, stop);
            if (render != null) {
                render.call(null);
            }
        }
        me.consume();
    }

    /**
     * Handle the event OnScroll.
     * Default zoom factor 2.
     * Use Control for have a zoom factor of 1.1.
     * Use Shift for have a zoom factor of 10.
     *
     * @param se the ScrollEvent.
     */
    @FXML
    public void handleOnScroll(ScrollEvent se) {
        double scaleBase = se.isControlDown() ? 1.1 : se.isShiftDown() ? 10 : 2;
        double byScale = (se.getDeltaY() > 0) ? 1 / scaleBase : scaleBase;
        cartesianPlane.zoomAtMousePos(cartesianPlane.toComplex(se.getX(), se.getY()), byScale);
        if (render != null) {
            render.call(null);
        }
        se.consume();
    }

    /**
     * Initialize CartesianCanvasGUI.
     * Do nothing.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Reset the CartesianPlane to default value.
     * See DataBox class.
     */
    public void resetPlane() {
        cartesianPlane = new CartesianPlane(getWidth(), getHeight(),
                DataBox.DEFAULT_UP_LEFT, DataBox.DEFAULT_DOWN_RIGHT);

        if (render != null) {
            render.call(null);
        }
        else {
            GraphicsContext gc = getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Return the CartesianPlane.
     *
     * @return the CartesianPlane.
     */
    public CartesianPlane getCartesianPlane() {
        return cartesianPlane;
    }

    /**
     * Set the Callback for the fractal rendering.
     *
     * @param render the Callback function for rendering.
     */
    public void setCallbackRender(Callback render) {
        this.render = render;
    }

    /**
     * Set the Callback for the right click features.
     *
     * @param rightClick the Callback function for the right click features.
     */
    public void setCallbackRightClick(Callback<Complex, Void> rightClick) {
        this.rightClick = rightClick;
    }

}
