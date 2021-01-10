/*
    * Author : Suraj Dakua
    * Date : 08/11/2020
    * Description : Inventory class to ADD/Update/Delete items to the canteen inventory.
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class Inventory implements Initializable {

    @FXML
    private JFXButton btnInsert;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private TableView tableView;

    @FXML
    private TextField textField;

    @FXML
    private TextField productCode;

    @FXML
    private TextField productQty;

    @FXML
    private TextField productName;

    @FXML
    private TextField productPrice;

    @FXML
    private TextField rtlName;

    @FXML
    private JFXButton btnUploadFile;

    @FXML
    private ObservableList<ObservableList> data;

    private String imagePath;
    private Connection connection = null;
    private static String cellData = null;

    private void refreshTable(){
        tableView.refresh();
    }

    public void loadTableData(){

        data = FXCollections.observableArrayList();
        try{
            /* Create Connection object connection from DBConnection class.*/
            connection = DBConnection.getConnection();

            /* Make SQL Query. */
            String sqlQuery = "SELECT PRODUCT_CODE, PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_QTY FROM INVENTORY";

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
                tableView.getColumns().addAll(tableColumn);
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
            tableView.setItems(data);

            /* Set columns width of table to equal width. */
            tableView.setColumnResizePolicy(tableView.CONSTRAINED_RESIZE_POLICY);

        }catch (SQLException e){
            e.printStackTrace();
            new PromptDialogController("DB Error", "Something went wrong.");
        }
    }

    public void captureImagePath(ActionEvent event){
        /* To upload file in JavaFX use FileChooser interface. */
        try{
            Stage stage = new Stage();
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                String fileAsString = file.toString();
                imagePath = fileAsString;
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
        String prodCode = productCode.getText();
        String prodName = productName.getText();
        String prodPrice = productPrice.getText();
        String prodQty = productQty.getText();
        String prodRetailerName = rtlName.getText();

        /* Create connection to database. */
        connection = DBConnection.getConnection();
        try{
            /* Write insert SQL Query. */
            String insertQuery = "INSERT INTO INVENTORY(PRODUCT_CODE, PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_QTY, RETAILER_NAME, PRODUCT_IMG) VALUES(?, ?, ?, ?, ?, ?)";

            /* Prepared Statement for above Insert Query. */
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, Integer.valueOf(prodCode));
            preparedStatement.setString(2, prodName);
            preparedStatement.setInt(3, Integer.valueOf(prodPrice));
            preparedStatement.setInt(4, Integer.valueOf(prodQty));
            preparedStatement.setString(5, prodRetailerName);
            preparedStatement.setString(6, imagePath);

            /* Execute SQL query and save result into resultSet.*/
            preparedStatement.executeQuery();
            connection.commit();
            refreshTable();
        }catch(SQLException e){
            e.printStackTrace();
            new PromptDialogController("Insert Error", "Something went wrong!");
        }

    }

    /* Common function for Update/Delete.*/
    public void getData(){
        /* Double on table column to select the product code. */
        tableView.setOnMouseClicked( event -> {
            if( event.getClickCount() == 2 ) {
                /* This table position will select only one cell/row. */
                TablePosition tablePosition = (TablePosition) tableView.getSelectionModel().getSelectedCells().get(0);

                /* get the data of cell in row variable. */
                int row = tablePosition.getRow();

                /* Only select column index zero. */
                TableColumn tableColumn = tablePosition.getTableView().getVisibleLeafColumn(0);

                /*
                 * Save selected cell data into cellData variable which is of type Integer object.
                 * This concept is known as Boxing or Auto-Boxing because we are wrapping primitive data into Integer wrapper class. */
                cellData = (String) tableColumn.getCellObservableValue(tableView.getItems().get(row)).getValue();

                try{
                    /* Eshtablish connection to Database. */
                    connection = DBConnection.getConnection();

                    /* Prepare select query having where condition prodCode = cellData. */
                    String selectQuery = "SELECT * FROM INVENTORY WHERE PRODUCT_CODE = ?";

                    PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                    preparedStatement.setInt(1, Integer.valueOf(cellData));

                    /* Result set. */
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()){
                        /* Get data from table. */
                        int getProductCode = resultSet.getInt("PRODUCT_CODE");
                        String getProductName = resultSet.getString("PRODUCT_NAME");
                        int getProductQty = resultSet.getInt("PRODUCT_QTY");
                        double getProductPrice = resultSet.getDouble("PRODUCT_PRICE");
                        String prodRetailerName = resultSet.getString("RETAILER_NAME");

                        /* Set form fields values. */
                        productCode.setText(resultSet.getString("PRODUCT_CODE"));
                        productName.setText(getProductName);
                        rtlName.setText(prodRetailerName);
                        productQty.setText(resultSet.getString("PRODUCT_QTY"));
                        productPrice.setText(resultSet.getString("PRODUCT_PRICE"));
                    }

                }catch(SQLException e){
                    e.printStackTrace();
                    new PromptDialogController("DB Error", "Something Went Wrong !");
                }

            }});
    }

    public void updateData(){
        String prodCode = productCode.getText();
        String prodName = productName.getText();
        String prodPrice = productPrice.getText();
        String prodQty = productQty.getText();
        String prodRetailerName = rtlName.getText();

        try{
            connection = DBConnection.getConnection();
            /* Update fields values of inventory table. */
            String updateQuery = "UPDATE INVENTORY SET PRODUCT_CODE = ?, PRODUCT_NAME = ?, PRODUCT_QTY = ?, PRODUCT_PRICE = ?, RETAILER_NAME = ?, PRODUCT_IMG = ? " +
                    "WHERE PRODUCT_CODE = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuery);

            preparedStatement1.setInt(1, Integer.valueOf(prodCode));
            preparedStatement1.setString(2, prodName);
            preparedStatement1.setInt(3, Integer.valueOf(prodPrice));
            preparedStatement1.setInt(4, Integer.valueOf(prodQty));
            preparedStatement1.setString(5, prodRetailerName);
            preparedStatement1.setString(6, imagePath);
            preparedStatement1.setInt(7, Integer.valueOf(cellData));

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
            String updateQuery = "DELETE INVENTORY WHERE PRODUCT_CODE = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuery);

            preparedStatement1.setInt(1, Integer.valueOf(cellData));

            preparedStatement1.executeUpdate();
            connection.commit();
            tableView.refresh();
        }catch (SQLException e){
            e.printStackTrace();
            new PromptDialogController("Error", "Something went wrong.");
        }
    }

    public void initialize(URL location, ResourceBundle resourceBundle){
        loadTableData();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
   }
}
