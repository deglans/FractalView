/*
 * Copyright (c) 2016 Deglans Dalpasso.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Deglans Dalpasso - initial API and implementation and initial documentations
 */
package deglans.fractalview;

import deglans.fractalview.GUI.MainGUI;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FractalView is a program for generating fractals.
 * This is the class that load the program.
 *
 * @version 0.10
 * @author Deglans Dalpasso
 */
public class Main extends Application {

    /**
     * GUI initialization.
     * Load the class MainGUI and place it in the main window.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Default code
//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add("/styles/Styles.css");
//        stage.setTitle("JavaFX and Maven");
//        stage.setScene(scene);
//        stage.show();

        // File test
//        Path inputPath = Paths.get("ciao.txt");
//        Path fullPath = inputPath.toAbsolutePath();
//        System.out.println(fullPath.toString());

        // GUI initialization
        Parent root = new MainGUI();
        Scene scene = new Scene(root);
        stage.setTitle("Fractal View");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
