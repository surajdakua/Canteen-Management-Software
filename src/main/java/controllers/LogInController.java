/*
 * Author : Suraj Dakua
 * Date : 06/11/2020
 * Description : Log in module or entry point of our application.
 * */

package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import main.java.others.DBConnection;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;


public class LogInController implements Initializable{
    /*
     * LoginController is a class.
     * Class have private and public fields known as data and methods.
     * Similarly we have in JavaFX Application known as @FXML.
     */
    @FXML
    private JFXButton btnLogIn;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private Label lblWarnUsername;
    @FXML
    private Label lblWarnPassword;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXTextField txtPasswordShown;
    @FXML
    private JFXCheckBox chkPasswordMask;

    /*
     * private members of class LogInController.
     * final is similar to const. Java doesn't support const keyword.
     * declaring something as final means it's value is read-only.
     * Members are static means can be accessed by static methods & non-static methods also.
     * But static methods cannot access non-static members.
     * value is shared in the module.
     * static means they don't belong to object.
     */
    @FXML
    private JFXCheckBox chkSaveCredentials;

    private static final String DIALOG_URL = "/main/resources/view/dialog.fxml";
    private static final String RED = "-fx-text-fill: red";
    public static String loggerUsername = "";
    public static String loggerAccessLevel = "";

    /* Define FXML methods which use FXML members. */
    /* Fire this function when mouse event occurs. */
    @FXML
    void ctrlLogInCheck(ActionEvent event)  {
        /* Call method of class LogInController. */
        userLogger();
    }

    /* Fire this function when user hits enter key. */
    @FXML
    void onEnterKey(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            userLogger();
        }
    }

    @FXML
    public void chkPasswordMaskAction()
    {
        if (chkPasswordMask.isSelected())
        {
            txtPasswordShown.setText(txtPassword.getText());
            txtPasswordShown.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordShown.getText());
            txtPassword.setVisible(true);
            txtPasswordShown.setVisible(false);
        }
    }

    /**
     * This method will set the previous saved username
     * password if any. In addition this method is responsible
     * for password visibility toggling
     */

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        Connection connection = DBConnection.getConnection();

        try {
            PreparedStatement getCredents = connection.prepareStatement("SELECT * FROM ADMIN_DETAILS");
            ResultSet resultSet = getCredents.executeQuery();

            if(resultSet.next()) {
                txtUsername.setText(resultSet.getString(1));
                txtPassword.setText(resultSet.getString(2));
            }
            txtPasswordShown.setVisible(false);

            txtUsername.setOnMouseClicked(event -> {
                lblWarnUsername.setVisible(false);
            });

            txtPasswordShown.setOnMouseClicked(event -> {
                lblWarnPassword.setVisible(false);
            });

            txtPassword.setOnMouseClicked(event -> {
                lblWarnPassword.setVisible(false);
            });

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void userLogger() {

        String username = txtUsername.getText();
        String password;

        /* Show/Hide password field. */
        if(chkPasswordMask.isSelected())
            password = txtPasswordShown.getText();
        else
            password = txtPassword.getText();

        /* Check if fields are blank. If blank then coonect to database. */

        if (username.equals("")) {
            lblWarnUsername.setVisible(true);
        } else if(password.equals("")) {
            lblWarnPassword.setVisible(true);
        } else {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT * FROM ADMIN_DETAILS WHERE ADMIN_NAME = ? AND ADMIN_PASSWORD = ?";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    /* Setting user credentials for further processing */
                    loggerUsername = rs.getString("ADMIN_NAME");


                    Stage logIn = (Stage) btnLogIn.getScene().getWindow();

                    Stage base = new Stage();
                    Parent root = null;

                    /* Moving to InitializerController Class to load all required main.resources */
                    try {
                        root = FXMLLoader.load(getClass().getResource("/main/resources/view/base.fxml"));
                        Scene s = new Scene(root);
                        logIn.setScene(s);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    new PromptDialogController("Authentication Error!", "Either username or password did not match!");
                }

                con.close();
            } catch (SQLException e) {
                System.out.println(e.getErrorCode());
                e.printStackTrace();

            }
        }
    }

}
