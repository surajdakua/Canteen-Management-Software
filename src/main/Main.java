/*
    * Author : Suraj Dakua
    * Date : 07/11/2020
    * Description : Driver code of CMS.
* */

package main;

import main.java.controllers.PromptDialogController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/main/resources/view/login.fxml"));
            Scene scene = new Scene(root);
            String css = this.getClass().getResource("/main/resources/css/login.css").toExternalForm();
            scene.getStylesheets().add(css); // Adding stylesheet
            primaryStage.setTitle("CMS");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("/main/resources/icons/Accounts_main.png"));
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new PromptDialogController("Error!", "Error Occurred. Failed to initialize system.");
        }

    }

    public static void main(String[] args) {

        launch(args);
    }
}
