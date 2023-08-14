package com.programming.assignment;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RainfallVisualizer extends Application {

    // Create a bar chart with rainfall data
    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Years");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        try (BufferedReader reader = new BufferedReader(new FileReader("Resources\\Copperlode_Dam_data_analysed.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fileContents = line.trim().split(",");
                try {
                    double monthlyTotal = Double.parseDouble(fileContents[2]);
                    String year = fileContents[0];

                    XYChart.Series<String, Number> data = new XYChart.Series<>();
                    data.setName("Total Monthly Rainfall");

                    data.getData().add(new XYChart.Data<>(year, monthlyTotal));
                    barChart.getData().add(data);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Handle mouse clicks on the columns
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                node.setOnMouseClicked(event -> showDetails(data));
            }
        }

        return barChart;
    }

    // Display details of the selected column
    private void showDetails(XYChart.Data<String, Number> data) {
        String year = data.getXValue();
        double totalRainfall = data.getYValue().doubleValue();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Column Details");
        alert.setContentText("Year: " + year +
                "\nTotal Rainfall: " + totalRainfall);
        alert.showAndWait();
    }

    @Override
    public void start(Stage stage) {
        int width = 218 * 6 + 40;
        int height = 500;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");

        Button monthlyTotals = new Button("Monthly Totals");
        HBox buttonBox = new HBox(monthlyTotals);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.setTitle("Rainfall Visualizer");
        stage.setResizable(true);
        stage.show();

        // Display the bar chart on button click
        monthlyTotals.setOnAction(event -> root.setCenter(createBarChart()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
