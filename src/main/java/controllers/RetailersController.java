/*
 * Author : Suraj Dakua
 * Date : 08/11/2020
 * Description : Retailer class to ADD/Update retailers details.
 */

package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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


public class RetailersController implements Initializable {

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
    private TextField gstNo;

    @FXML
    private TextField retailerName;

    @FXML
    private TextField retailerContact;

    @FXML
    private ObservableList<ObservableList> data;

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
            String sqlQuery = "SELECT * FROM RETAILER_DETAILS";

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

    public void insertData(){
        /* Capture textField values into variables and pass this variables as a param to SQL Insert Query. */
        String retailerGST = gstNo.getText();
        String retailersName = retailerName.getText();
        String retailerMobile = retailerContact.getText();


        /* Create connection to database. */
        connection = DBConnection.getConnection();
        try{
            /* Write insert SQL Query. */
            String insertQuery = "INSERT INTO RETAILER_DETAILS(GST_NO, RETAILER_NAME, RTL_MOB_NO) VALUES(?, ?, ?)";

            /* Prepared Statement for above Insert Query. */
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, retailerGST);
            preparedStatement.setString(2, retailersName);
            preparedStatement.setLong(3, Long.valueOf(retailerMobile));

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
                    String selectQuery = "SELECT * FROM RETAILER_DETAILS WHERE GST_NO = ?";

                    PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                    preparedStatement.setString(1, String.valueOf(cellData));

                    /* Result set. */
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()){
                        /* Get data from table. */
                        String getRetailerGST = resultSet.getString("GST_NO");
                        String getRetailerName = resultSet.getString("RETAILER_NAME");
                        Long getProductQty = resultSet.getLong("RTL_MOB_NO");


                        /* Set form fields values. */
                        gstNo.setText(resultSet.getString("GST_NO"));
                        retailerName.setText(resultSet.getString("RETAILER_NAME"));
                        retailerContact.setText(resultSet.getString("RTL_MOB_NO"));
                    }

                }catch(SQLException e){
                    e.printStackTrace();
                    new PromptDialogController("DB Error", "Something Went Wrong !");
                }

            }});
    }

    public void updateData(){
        /* Capture textField values into variables and pass this variables as a param to SQL Insert Query. */
        String retailerGST = gstNo.getText();
        String retailersName = retailerName.getText();
        String retailerMobile = retailerContact.getText();

        try{
            connection = DBConnection.getConnection();
            /* Update fields values of inventory table. */
            String updateQuery = "UPDATE RETAILER_DETAILS SET GST_NO = ?, RETAILER_NAME = ?, RTL_MOB_NO = ? " +
                    "WHERE GST_NO = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuery);

            preparedStatement1.setString(1, retailerGST);
            preparedStatement1.setString(2, retailersName);
            preparedStatement1.setLong(3, Long.valueOf(retailerMobile));
            preparedStatement1.setString(4, String.valueOf(cellData));


            preparedStatement1.executeUpdate();
            connection.commit();
            refreshTable();
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
