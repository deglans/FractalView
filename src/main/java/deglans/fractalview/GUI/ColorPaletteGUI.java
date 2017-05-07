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

import deglans.fractalview.utility.ColorPalette;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * ColorPaletteGUI allow to create a palette for coloring the fractals.
 *
 * @version 0.11
 * @author Deglans Dalpasso
 */
public class ColorPaletteGUI extends VBox implements Initializable {

    /**
     * Table for colors and relative stops.
     */
    @FXML
    private TableView<ColorRow> colorsTable;

    /**
     * Column for colors.
     */
    @FXML
    private TableColumn<ColorRow, MyColor> colorColumn;

    /**
     * Column for stops.
     */
    @FXML
    private TableColumn<ColorRow, Double> stopColumn;

    /**
     * Data for colorsTable.
     */
    private ObservableList<ColorRow> dataColorsTable;

    /**
     * TextField for insert a new color on colorsTable.
     */
    @FXML
    private TextField tfColor;

    /**
     * TextField for insert a new stop on colorsTable.
     */
    @FXML
    private TextField tfStop;

    /**
     * TextField for the color of the points that are in the set.
     */
    @FXML
    private TextField tfColorSet;

    /**
     * Create ColorPaletteGUI by loading ColorPaletteGUI.fxml.
     */
    public ColorPaletteGUI() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ColorPaletteGUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.err.println(exception.toString());
            throw new RuntimeException(exception);
        }
    }

    /**
     * Handle the event onAction of the button Add.
     * At the moment the content of TextField tfStop is ignored and the stops
     * are redistribute in a linear fashion.
     *
     * @param ae the ActionEvent.
     */
    @FXML
    public void handleAddOnAction(ActionEvent ae) {
        dataColorsTable.add(new ColorRow(new MyColor(tfColor.getText()),
                Double.parseDouble(tfStop.getText())));
        tfColor.clear();
        tfStop.clear();
        linearDistribution();
        ae.consume();
    }

    /**
     * Distribute the stops in a linear fashion.
     */
    private void linearDistribution() {
        double step = 1.0 / (dataColorsTable.size() - 1);
        int k = 0;
        for (ColorRow p : dataColorsTable) {
            if (k == 0) {
                p.setStop(0);
            }
            else if(k == (dataColorsTable.size() - 1)) {
                p.setStop(1);
            }
            else {
                p.setStop(step * k);
            }
            k++;
        }
    }

    /**
     * Initialize ColorPaletteGUI.
     * Initialize and insert default value in the table and in the TextField.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataColorsTable = FXCollections.observableArrayList(
                new ColorRow(new MyColor(1, 1, 1), 0),
                new ColorRow(new MyColor(0, 0, 0), 1)
        );
        colorsTable.setItems(dataColorsTable);

        Callback<TableColumn<ColorRow, MyColor>,
            TableCell<ColorRow, MyColor>> cellFactoryColor
                = (TableColumn<ColorRow, MyColor> p) -> new EditingCellColor();

        colorColumn.setCellValueFactory(
            new PropertyValueFactory<>("color"));
        colorColumn.setCellFactory(cellFactoryColor);
        colorColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<ColorRow, MyColor> t) -> {
                ((ColorRow) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setColor(t.getNewValue());
        });

        Callback<TableColumn<ColorRow, Double>,
            TableCell<ColorRow, Double>> cellFactoryDouble
                = (TableColumn<ColorRow, Double> p) -> new EditingCellStop();

        stopColumn.setCellValueFactory(
            new PropertyValueFactory<>("stop"));
        stopColumn.setCellFactory(cellFactoryDouble);
        stopColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<ColorRow, Double> t) -> {
                ((ColorRow) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setStop(t.getNewValue());
        });

        tfColorSet.setText("#000000");
    }

    /**
     * Return the color for the points that are in the set.
     *
     * @return the color for the points that are in the set.
     */
    private Color getColorSet() {
        return Color.web(tfColorSet.getText());
    }

    /**
     * Return the data that are in colorColumn.
     *
     * @return the data that are in colorColumn.
     */
    private Color[] getColors() {
        Color[] tmp;

        tmp = new Color[dataColorsTable.size()];
        int n = 0;
        for (ColorRow c : dataColorsTable) {
            tmp[n] = c.getColor().getPureColor();
            n++;
        }

        return tmp;
    }

    /**
     * Return the data that are in stopColumn.
     *
     * @return the data that are in stopColumn.
     */
    private double[] getStops() {
        double[] tmp;

        tmp = new double[dataColorsTable.size()];
        int n = 0;
        for (ColorRow c : dataColorsTable) {
            tmp[n] = c.getStop();
            n++;
        }

        return tmp;
    }

    /**
     * Create the ColorPalette.
     *
     * @param paletteLength the length of the palette.
     * @return the palette.
     */
    public ColorPalette getColorPalette(int paletteLength) {
        return new ColorPalette(paletteLength, getColors(), getStops(), getColorSet());
    }

    /**
     * Convert a Color into string in the web format.
     *
     * @param color the Color to convert.
     * @return the Color in web format.
     */
    private static String color2string_web(Color color) {
        String red_s = toHex((int)(color.getRed()*255.0));
        String green_s = toHex((int)(color.getGreen()*255.0));
        String blue_s = toHex((int)(color.getBlue()*255.0));
        return "#" + red_s + green_s + blue_s;
    }

    /**
     * Convert a MyColor into string in the web format.
     *
     * @param color the MyColor to convert.
     * @return the MyColor in web format.
     */
    private static String color2string_web(MyColor color) {
        String red_s = toHex((int)(color.getRed()*255.0));
        String green_s = toHex((int)(color.getGreen()*255.0));
        String blue_s = toHex((int)(color.getBlue()*255.0));
        return "#" + red_s + green_s + blue_s;
    }

    /**
     * Convert an integer into hex format string of at last two digit.
     * Helper function for color2string_web().
     *
     * @param n
     * @return
     */
    private static String toHex(int n) {
        String s = Integer.toHexString(n);
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * MyColor is a wrapper class for Color class.
     * The main scope of this class is to make a toString() function and a
     * constructor that take a string both in web format.
     * The color is always opaque.
     */
    public class MyColor {

        /**
         * Color instance to be wrapped.
         */
        private final Color color;

        /**
         * Wrapper constructor for Color that take the RGB component.
         *
         * @param red red component ranging from 0 to 1.
         * @param green green component ranging from 0 to 1.
         * @param blue blue component ranging from 0 to 1.
         */
        MyColor(double red, double green, double blue) {
            color = new Color(red, green, blue, 1);
        }

        /**
         * The color in web format, if it isn't opaque, make it opaque.
         *
         * @param color color in web format.
         */
        MyColor(String color) {
            Color c = Color.web(color);
            this.color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 1);
        }

        /**
         * Return this color in web format string.
         *
         * @return this color in web format string.
         */
        @Override
        public String toString() {
            return color2string_web(color);
        }

        /**
         * Return the Color object.
         *
         * @return the Color object.
         */
        Color getPureColor() {
            return color;
        }

        /**
         * Return the red component ranging from 0 to 1.
         *
         * @return the red component ranging from 0 to 1.
         */
        double getRed() {
            return color.getRed();
        }

        /**
         * Return the green component ranging from 0 to 1.
         *
         * @return the green component ranging from 0 to 1.
         */
        double getGreen() {
            return color.getGreen();
        }

        /**
         * Return the blue component ranging from 0 to 1.
         *
         * @return the blue component ranging from 0 to 1.
         */
        double getBlue() {
            return color.getBlue();
        }

    }

    /**
     * ColorRow is the data model for the TableView.
     */
    public static class ColorRow {

        /**
         * MyColor for the Color column.
         */
        private MyColor color;

        /**
         * Double for the Stop column.
         */
        private Double stop;

        /**
         * Create an instance of ColorRow.
         *
         * @param color the color
         * @param stop the stop
         */
        public ColorRow(MyColor color, double stop) {
            this.color = color;
            this.stop = stop;
        }

        /**
         * Getter for the color.
         *
         * @return the color.
         */
        public MyColor getColor() {
            return color;
        }

        /**
         * Getter for the stop.
         *
         * @return the stop.
         */
        public double getStop() {
            return stop;
        }

        /**
         * Setter for the color.
         *
         * @param color the new color.
         */
        public void setColor(MyColor color) {
            this.color = color;
        }

        /**
         * Setter for the stop.
         *
         * @param stop the new stop.
         */
        public void setStop(double stop) {
            this.stop = stop;
        }

    }

    /**
     * Helper class for edit well the data in the TableView, column Color.
     * http://docs.oracle.com/javase/8/javafx/user-interface-tutorial/table-view.htm
     */
    class EditingCellColor extends TableCell<ColorRow, MyColor> {

        private TextField textField;

        public EditingCellColor() {}

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(color2string_web(getItem()));
            setGraphic(null);
        }

        @Override
        public void updateItem(MyColor item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(
                (ObservableValue<? extends Boolean> arg0,
                Boolean arg1, Boolean arg2) -> {
                    if (!arg2) {
                        commitEdit(new MyColor(textField.getText()));
                    }
            });
            textField.setOnAction(t -> {
                commitEdit(new MyColor(textField.getText()));
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }

    /**
     * Helper class for edit well the data in the TableView, column Stop.
     * http://docs.oracle.com/javase/8/javafx/user-interface-tutorial/table-view.htm
     */
    class EditingCellStop extends TableCell<ColorRow, Double> {

        private TextField textField;

        public EditingCellStop() {}

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem().toString());
            setGraphic(null);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(
                (ObservableValue<? extends Boolean> arg0,
                Boolean arg1, Boolean arg2) -> {
                    if (!arg2) {
                        commitEdit(Double.parseDouble(textField.getText()));
                    }
            });
            textField.setOnAction(t -> {
                commitEdit(Double.parseDouble(textField.getText()));
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }

}
