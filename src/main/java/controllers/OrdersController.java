/*
 * Author : Suraj Dakua
 * Date : 07/11/2020
 * Description : This class is responsible to show orders of end-user and confirm order from canteen owner.
 */

package main.java.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/* Import all JavaFX Libraries. */
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import main.java.others.DBConnection;

public class OrdersController implements Initializable {

    /* Private members of JavaFX. */
    @FXML
    private ObservableList<ObservableList> data;

    @FXML
    private TableView tableView;

    @FXML
    private JFXButton btnRefresh;

    /* Connect to database and retrieve data from the table and store the data into a list dynamically. */
    public void buildData(){
	/* create connection object. */
        Connection connection;
        data = FXCollections.observableArrayList();
        try{
            connection = DBConnection.getConnection();
            /* SQL Query to select columns from ORDERS and ORDERS_DETAILS two tables based on equijoin.*/
            String SQL = "SELECT ORDERS.ORDER_ID, ORDERS_DETAILS.FOOD_ID, ORDERS.GR_NO,ORDERS_DETAILS.FOOD_ITEM,ORDERS.DEPT_NAME," +
                    " ORDERS.EMAIL_ID," + " ORDERS.ORDER_DATE, " + "ORDERS.PAYMENT_STATUS, ORDERS_DETAILS.FOOD_PRICE," +
                    "ORDERS_DETAILS.DISCOUNT  " + "from ORDERS, ORDERS_DETAILS WHERE ORDERS.ORDER_ID = ORDERS_DETAILS.ORDER_ID";

            /* result set of the database. */
            ResultSet rs = connection.createStatement().executeQuery(SQL);

            /* Add table columns dynamically. */
            for(int i=0 ; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

		/* Add data inside stored variable col to tableView object. */
                tableView.getColumns().addAll(col);
            }

            /* Add column data to Obervable List. */
            while(rs.next()){
                /* Iterate every row present inside the table. */
		        /* Create row variable which is of list type which is dynamic arrray. */
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    /* Iterate through the columns. */
		            /* Add rows of the table to the row variable. */
                    row.add(rs.getString(i));
                }

                /* Add data to the list. */
                data.add(row);
            }

            /* If succesfully data added to list(row) then append the list to tableview. */
            tableView.setItems(data);

	    /* This particular line will set the column width of same size in the output pane. */
            tableView.setColumnResizePolicy(tableView.CONSTRAINED_RESIZE_POLICY);


        }catch(Exception e){
            e.printStackTrace();
            new PromptDialogController("Error", "Cannot retrieve data from table.");
        }
    }

    /* Initialize this FXML file with the buildData() function. */
    public void initialize(URL location, ResourceBundle resourceBundle){
        buildData();
    }

}