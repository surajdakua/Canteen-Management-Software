/*
    * Author : Suraj Dakua
    * Date : 11/11/2020
    * Description: This class is responsible to add/update/delete Canteen Staff.
*/

package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.others.DBConnection;
import javafx.scene.control.TableColumn.CellDataFeatures;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class CanteenStaffController implements Initializable {

    @FXML
    private JFXButton btnInsert;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private TableView tableViewForCanteenStaff;

    @FXML
    private TextField textField;

    @FXML
    private TextField staffCode;

    @FXML
    private TextField staffName;

    @FXML
    private TextField staffAge;

    @FXML
    private TextField staffSalary;

    @FXML
    private TextField staffHireDate;

    @FXML
    private TextField staffDesignation;

    @FXML
    private TextField staffMobNo;

    @FXML
    private JFXButton btnUploadIdProof;

    @FXML
    private TextField staffAddress;

    @FXML
    private JFXButton btnUploadStaffPhoto;

    @FXML
    private ObservableList<ObservableList> data;

    private String imagePathForStaffPhoto;
    private String imagePathForStaffIdProof;
    private Connection connection = null;
    private static String cellDataForStaff = null;

    private void refreshTable(){
        tableViewForCanteenStaff.refresh();
    }

    public void loadStaffData(){

        data = FXCollections.observableArrayList();
        try{
            /* Create Connection object connection from DBConnection class.*/
            connection = DBConnection.getConnection();

            /* Make SQL Query. */
            String sqlQuery = "SELECT STAFF_ID, STAFF_NAME as NAME, STAFF_DESIGNATION as Designation FROM CANTEEN_STAFF";

            /* Execute above preparedStatement. */
            ResultSet resultSet = connection.createStatement().executeQuery(sqlQuery);

            /*
             * Add all columnar data to dynamic array.
             * Then use JavaFX collections
             * There is a collection or Iterator which is ObervableList.
             * This is same as ArrayList.
             * Use for/while loop to iterate rows inside the table.
             */

            /*
             * MetaData : Data with description
             * Example : "Name" : "Jack."
             *           "Age" : "23"
             */

            for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
                /* Declare const variable with initial value of i. */
                final int j = i;
                TableColumn tableColumn = new TableColumn(resultSet.getMetaData().getColumnName(i+1));

                /* * Callback is a function inside function.
                 * Execute inner function first then process outer function.
                 */
                tableColumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                /* Add all string value which is inside tableColumn object to tableView object. */
                tableViewForCanteenStaff.getColumns().addAll(tableColumn);
            }

            /* Fetch all rows of the table. */
            while(resultSet.next()){
                /* rowData is a dynamic array which increase or decrease in size upon how data is being added. */
                ObservableList<String> rowData = FXCollections.observableArrayList();

                for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++){
                    /* Add all the rows to the variable of list type declared as rowData. */
                    rowData.add(resultSet.getString(i));
                }

                /* Add all the rowData to data variable which is of type list. */
                data.add(rowData);
            }

            /* Set tableView items use setItems function and pass the data variable to this function. */
            tableViewForCanteenStaff.setItems(data);

            /* Set columns width of table to equal width. */
            tableViewForCanteenStaff.setColumnResizePolicy(tableViewForCanteenStaff.CONSTRAINED_RESIZE_POLICY);

        }catch (SQLException e){
            e.printStackTrace();
            new PromptDialogController("DB Error", "Something went wrong.");
        }
    }

    public void getCanteenStaffData(){
        /* Double on table column to select the product code. */
        tableViewForCanteenStaff.setOnMouseClicked( event -> {
            if( event.getClickCount() == 2 ) {
                /* This table position will select only one cell/row. */
                TablePosition tablePosition = (TablePosition) tableViewForCanteenStaff.getSelectionModel().getSelectedCells().get(0);

                /* get the data of cell in row variable. */
                int row = tablePosition.getRow();

                /* Only select column index zero. */
                TableColumn tableColumn = tablePosition.getTableView().getVisibleLeafColumn(0);

                /*
                 * Save selected cell data into cellData variable which is of type Integer object.
                 * This concept is known as Boxing or Auto-Boxing because we are wrapping primitive data into Integer wrapper class. */
                cellDataForStaff = (String) tableColumn.getCellObservableValue(tableViewForCanteenStaff.getItems().get(row)).getValue();

                try{
                    /* Eshtablish connection to Database. */
                    connection = DBConnection.getConnection();

                    /* Prepare select query having where condition prodCode = cellData. */
                    String selectQuery = "SELECT * FROM CANTEEN_STAFF WHERE STAFF_ID = ?";

                    PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                    preparedStatement.setInt(1, Integer.valueOf(cellDataForStaff));

                    /* Result set. */
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()){
                        /* Get data from table. */
                        Long getStaffCode = resultSet.getLong("STAFF_ID");
                        String getStaffName = resultSet.getString("STAFF_NAME");
                        int getStaffAge = resultSet.getInt("STAFF_AGE");
                        double getStaffSal = resultSet.getDouble("STAFF_SAL");
                        String getStaffHireDate = resultSet.getString("HIREDATE");
                        String getStaffDesignation = resultSet.getString("STAFF_DESIGNATION");
                        Long getStaffMobNo = resultSet.getLong("STAFF_MOB_NO");
                        String getStaffID = resultSet.getString("ID_PROOF");
                        String getStaffAddress = resultSet.getString("STAFF_ADDRESS");
                        String getStaffPhoto = resultSet.getString("STAFF_PHOTO");

                        /* Set form fields values. */
                        staffCode.setText(resultSet.getString("STAFF_ID"));
                        staffAddress.setText(getStaffAddress);
                        staffAge.setText(resultSet.getString("STAFF_AGE"));
                        staffDesignation.setText(getStaffDesignation);
                        staffHireDate.setText(getStaffHireDate);
                        staffName.setText(getStaffName);
                        staffSalary.setText(resultSet.getString("STAFF_SAL"));
                        staffMobNo.setText(resultSet.getString("STAFF_MOB_NO"));

                    }

                }catch(SQLException e){
                    e.printStackTrace();
                    new PromptDialogController("DB Error", "Something Went Wrong !");
                }

            }});
    }

    public void captureStaffImagePath(ActionEvent event){
        /* To upload file in JavaFX use FileChooser interface. */
        try{
            Stage stage = new Stage();
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                String fileAsString = file.toString();
                imagePathForStaffPhoto = fileAsString;
            }
            HBox root = new HBox();

            root.setSpacing(20);
            Scene scene = new Scene(root,350,100);
            stage.setScene(scene);
            stage.setTitle("File Upload Window");
            stage.show();
            stage.close();
        }catch (Exception e){
            e.printStackTrace();
            new PromptDialogController("File Upload Error", "Something went wrong while uploading path.");
        }

    }

    public void captureStaffIdProof(ActionEvent event){
        /* To upload file in JavaFX use FileChooser interface. */
        try{
            Stage stage = new Stage();
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                String fileAsString = file.toString();
                imagePathForStaffIdProof = fileAsString;
            }
            HBox root = new HBox();

            root.setSpacing(20);
            Scene scene = new Scene(root,350,100);
            stage.setScene(scene);
            stage.setTitle("File Upload Window");
            stage.show();
            stage.close();
        }catch (Exception e){
            e.printStackTrace();
            new PromptDialogController("File Upload Error", "Something went wrong while uploading path.");
        }

    }

    public void insertData(){
        /* Capture textField values into variables and pass this variables as a param to SQL Insert Query. */
        String staffID = staffCode.getText();
        String canteenStaffName = staffName.getText();
        String staffMobile = staffMobNo.getText();
        String staffSal = staffSalary.getText();
        String staffHiring = staffHireDate.getText();
        String canteenStaffAge = staffAge.getText();
        String staffAdd = staffAddress.getText();
        String staffDesig = staffDesignation.getText();

        /* Create connection to database. */
        connection = DBConnection.getConnection();
        try{
            /* Write insert SQL Query. */
            String insertQuery = "INSERT INTO CANTEEN_STAFF(STAFF_ID, STAFF_NAME, STAFF_AGE, STAFF_SAL, HIREDATE, STAFF_DESIGNATION, " +
                    "STAFF_MOB_NO, ID_PROOF, STAFF_ADDRESS, STAFF_PHOTO) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            /* Prepared Statement for above Insert Query. */
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setLong(1, Long.valueOf(staffID));
            preparedStatement.setString(2, canteenStaffName);
            preparedStatement.setLong(7, Long.valueOf(staffMobile));
            preparedStatement.setLong(4, Long.valueOf(staffSal));
            preparedStatement.setDate(5, Date.valueOf(staffHiring));
            preparedStatement.setInt(3, Integer.valueOf(canteenStaffAge));
            preparedStatement.setString(9, staffAdd);
            preparedStatement.setString(6, staffDesig);
            preparedStatement.setString(8, imagePathForStaffIdProof);
            preparedStatement.setString(10, imagePathForStaffPhoto);

            /* Execute SQL query and save result into resultSet.*/
            preparedStatement.executeQuery();
            connection.commit();
            refreshTable();
        }catch(SQLException e){
            e.printStackTrace();
            new PromptDialogController("Insert Error", "Something went wrong!");
        }

    }

    public void updateData(){
        String staffID = staffCode.getText();
        String canteenStaffName = staffName.getText();
        String staffMobile = staffMobNo.getText();
        String staffSal = staffSalary.getText();
        String staffHiring = staffHireDate.getText();
        String canteenStaffAge = staffAge.getText();
        String staffAdd = staffAddress.getText();
        String staffDesig = staffDesignation.getText();

        try{
            connection = DBConnection.getConnection();
            /* Update fields values of inventory table. */
            String updateQuery = "UPDATE CANTEEN_STAFF SET STAFF_ID = ?, STAFF_NAME = ?, STAFF_AGE = ?, STAFF_SAL = ?, STAFF_DESIGNATION = ?, STAFF_MOB_NO = ?, " +
                    "ID_PROOF = ?, STAFF_ADDRESS = ?, STAFF_PHOTO = ? WHERE STAFF_ID = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuery);

            preparedStatement1.setLong(1, Long.valueOf(staffID));
            preparedStatement1.setString(2, canteenStaffName);
            preparedStatement1.setLong(6, Long.valueOf(staffMobile));
            preparedStatement1.setLong(4, Long.valueOf(staffSal));
            preparedStatement1.setInt(3, Integer.valueOf(canteenStaffAge));
            preparedStatement1.setString(8, staffAdd);
            preparedStatement1.setString(5, staffDesig);
            preparedStatement1.setString(7, imagePathForStaffIdProof);
            preparedStatement1.setString(9, imagePathForStaffPhoto);
            preparedStatement1.setInt(10, Integer.valueOf(cellDataForStaff));

            preparedStatement1.executeUpdate();
            connection.commit();
            refreshTable();
        }catch (SQLException e){
            e.printStackTrace();
            new PromptDialogController("Error", "Something went wrong.");
        }
    }

    public void deleteData(){
        try{
            connection = DBConnection.getConnection();
            /* Update fields values of inventory table. */
            String deleteQuery = "DELETE CANTEEN_STAFF WHERE STAFF_ID = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(deleteQuery);

            preparedStatement1.setInt(1, Integer.valueOf(cellDataForStaff));

            preparedStatement1.executeUpdate();
            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
            new PromptDialogController("Error", "Something went wrong.");
        }
    }

    public void initialize(URL location, ResourceBundle resourceBundle){
        loadStaffData();

        /* Select single column for table canteen staff. */
        tableViewForCanteenStaff.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableViewForCanteenStaff.getSelectionModel().setCellSelectionEnabled(true);

    }
}

