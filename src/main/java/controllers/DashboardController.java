/*
    * Author : Suraj Dakua
    * Date : 06/11/2020
    * Description : Dashboard graphs and charts.
*/

package main.java.controllers;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.scene.chart.*;

public class DashboardController implements Initializable{
    final static String austria = "Austria";
    final static String brazil = "Brazil";
    final static String france = "France";
    final static String italy = "Italy";
    final static String usa = "USA";

    @FXML
    private PieChart pieChart;

    @FXML
    private CategoryAxis xAxis = new CategoryAxis();;

    @FXML
    private NumberAxis yAxis = new NumberAxis();

    @FXML
    private BarChart<String, Number> barChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPieChart();
        loadBarGraph();
    }


    private void loadPieChart()
    {

        PieChart.Data slice1 = new PieChart.Data("Breakfast", 214);
        PieChart.Data slice2 = new PieChart.Data("Lunch"  , 33);
        PieChart.Data slice3 = new PieChart.Data("Dinner" , 36);
        PieChart.Data slice4 = new PieChart.Data("Beverages" , 33);

        pieChart.getData().add(slice1);
        pieChart.getData().add(slice2);
        pieChart.getData().add(slice3);
        pieChart.getData().add(slice4);

        pieChart.setTitle("Daily Food Categories sold.");
    }

    private void loadBarGraph(){

        final StackedBarChart<String, Number> sbc =
                new StackedBarChart<String, Number>(xAxis, yAxis);
        final XYChart.Series<String, Number> series1 =
                new XYChart.Series<String, Number>();
        final XYChart.Series<String, Number> series2 =
                new XYChart.Series<String, Number>();
        final XYChart.Series<String, Number> series3 =
                new XYChart.Series<String, Number>();

        barChart.setTitle("Weekly/Monthly Food Sales.");
        xAxis.setLabel("Country");
        xAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList(austria, brazil, france, italy, usa)));
        yAxis.setLabel("Value");
        series1.setName("2003");
        series1.getData().add(new XYChart.Data<String, Number>(austria, 25601.34));
        series1.getData().add(new XYChart.Data<String, Number>(brazil, 20148.82));
        series1.getData().add(new XYChart.Data<String, Number>(france, 10000));
        series1.getData().add(new XYChart.Data<String, Number>(italy, 35407.15));
        series1.getData().add(new XYChart.Data<String, Number>(usa, 12000));
        series2.setName("2004");
        series2.getData().add(new XYChart.Data<String, Number>(austria, 57401.85));
        series2.getData().add(new XYChart.Data<String, Number>(brazil, 41941.19));
        series2.getData().add(new XYChart.Data<String, Number>(france, 45263.37));
        series2.getData().add(new XYChart.Data<String, Number>(italy, 117320.16));
        series2.getData().add(new XYChart.Data<String, Number>(usa, 14845.27));
        series3.setName("2005");
        series3.getData().add(new XYChart.Data<String, Number>(austria, 45000.65));
        series3.getData().add(new XYChart.Data<String, Number>(brazil, 44835.76));
        series3.getData().add(new XYChart.Data<String, Number>(france, 18722.18));
        series3.getData().add(new XYChart.Data<String, Number>(italy, 17557.31));
        series3.getData().add(new XYChart.Data<String, Number>(usa, 92633.68));
        Scene scene = new Scene(sbc, 750, 700);
        barChart.getData().addAll(series1, series2, series3);
        }
}
