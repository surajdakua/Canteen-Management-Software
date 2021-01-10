/*
 * Author : Suraj Dakua
 * Date : 09/11/2020
 * Description : Food class to ADD/Update/Delete items to the canteen food menu.
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


public class FoodController implements Initializable {

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
    private TextField foodID;

    @FXML
    private TextField foodAmount;

    @FXML
    private TextField foodItem;

    @FXML
    private TextField foodDescription;

    @FXML
    private TextField foodItemCategory;

    @FXML
    private TextField foodDiscount;

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
            String sqlQuery = "SELECT FOOD_ID, FOOD_ITEM, FOOD_ITEM_CATEGORY, FOOD_PRICE FROM FOOD";

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
            stage.setTitle("Upload Food Photo");
            stage.show();
            stage.close();
        }catch (Exception e){
            e.printStackTrace();
            new PromptDialogController("File Upload Error", "Something went wrong while uploading path.");
        }

    }

    public void insertData(){
        /* Capture textField values into variables and pass this variables as a param to SQL Insert Query. */
        String foodCode = foodID.getText();
        String foodName = foodItem.getText();
        String foodPrice = foodAmount.getText();
        String foodDescrip = foodDescription.getText();
        String foodNameCatg = foodItemCategory.getText();
        String foodDisc = foodDiscount.getText();

        /* Create connection to database. */
        connection = DBConnection.getConnection();
        try{
            /* Write insert SQL Query. */
            String insertQuery = "INSERT INTO FOOD(FOOD_ID, FOOD_PRICE, DISCOUNT, FOOD_ITEM, FOOD_ITEM_CATEGORY, FOOD_DESCRIPTION, FOOD_IMG) " +
                    " VALUES(?, ?, ?, ?, ?, ?, ?)";

            /* Prepared Statement for above Insert Query. */
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, Integer.valueOf(foodCode));
            preparedStatement.setDouble(2, Double.valueOf(foodPrice));
            preparedStatement.setFloat(3, Float.valueOf(foodDisc));
            preparedStatement.setString(4, foodName);
            preparedStatement.setString(5, foodNameCatg);
            preparedStatement.setString(6, foodDescrip);
            preparedStatement.setString(7, imagePath);

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
                    String selectQuery = "SELECT * FROM FOOD WHERE FOOD_ID = ?";

                    PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                    preparedStatement.setInt(1, Integer.valueOf(cellData));

                    /* Result set. */
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()){
                        /* Get data from table. */
                        int getFoodCode = resultSet.getInt("FOOD_ID");
                        String getFoodName = resultSet.getString("FOOD_ITEM");
                        int getFoodPrice = resultSet.getInt("FOOD_PRICE");
                        String getFoodDescp = resultSet.getString("FOOD_DESCRIPTION");
                        float getFoodDisc = resultSet.getFloat("DISCOUNT");
                        String getFoodCatg = resultSet.getString("FOOD_ITEM_CATEGORY");

                        /* Set form fields values. */
                        foodID.setText(resultSet.getString("FOOD_ID"));
                        foodItem.setText(resultSet.getString("FOOD_ITEM"));
                        foodAmount.setText(resultSet.getString("FOOD_PRICE"));
//                        foodDescription.setText(resultSet.getString("FOOD_DESCRIPTION"));
                        foodItemCategory.setText(resultSet.getString("FOOD_ITEM_CATEGORY"));
                        foodDiscount.setText(resultSet.getString("DISCOUNT"));
                    }

                }catch(SQLException e){
                    e.printStackTrace();
                    new PromptDialogController("DB Error", "Something Went Wrong !");
                }

            }});
    }

    public void updateData(){
        String foodCode = foodID.getText();
        String foodName = foodItem.getText();
        String foodPrice = foodAmount.getText();
        String foodDisc = foodDiscount.getText();
        String foodDescp = foodDescription.getText();
        String foodItemCatg = foodItemCategory.getText();

        try{
            connection = DBConnection.getConnection();
            /* Update fields values of inventory table. */
            String updateQuery = "UPDATE FOOD SET FOOD_ID = ?, FOOD_ITEM = ?, FOOD_PRICE = ?, FOOD_DESCRIPTION = ?, FOOD_ITEM_CATEGORY = ?, " +
                    "DISCOUNT = ?, FOOD_IMG = ? WHERE FOOD_ID = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuery);

            preparedStatement1.setInt(1, Integer.valueOf(foodCode));
            preparedStatement1.setString(2, foodName);
            preparedStatement1.setDouble(3, Double.valueOf(foodPrice));
            preparedStatement1.setString(4, foodDescp);
            preparedStatement1.setString(5, foodItemCatg);
            preparedStatement1.setString(6, foodDisc);
            preparedStatement1.setString(7, imagePath);
            preparedStatement1.setInt(8, Integer.valueOf(cellData));

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
            String updateQuery = "DELETE FOOD WHERE FOOD_ID = ?";
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
