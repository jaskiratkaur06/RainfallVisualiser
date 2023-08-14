/**
 * RainfallGui - A JavaFX application for analyzing rainfall data from a CSV file.
 * This program allows users to open a CSV file containing rainfall data, process and analyze the data,
 * and then generate an analyzed CSV file with aggregated information for each year and month.
 * Author: [Your Name]
 * Date: [Current Date]
 */

package com.programming.assignment;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RainfallGui extends Application {

    public void start(Stage stage) {
        // The default file path
        String defaultPath = "C:\\Users\\jaski\\OneDrive\\Desktop\\Programming III\\assignment\\Resources";

        // Creating a new menu
        Menu fileMenu = new Menu("Open File");

        // Creating menu Items
        MenuItem item = new MenuItem("Choose .csv file");
        fileMenu.getItems().addAll(item);

        // Creating a File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .csv formatted file");
        fileChooser.setInitialDirectory(new File(defaultPath));
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Comma-separated value", "*.csv"));

        // Adding action on the menu item
        item.setOnAction(event -> {
            // Opening a dialog box
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                String fileLocation = file.toString();
                System.out.println("The file to be analyzed is:");
                System.out.println(fileLocation);
                RainfallDataProcessor(fileLocation);
                stage.close();
                // Call to the rainfall analyzer class
            } else {
                stage.close();
            }
        });

        // Creating a menu bar and adding menu to it.
        MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.setTranslateX(160);
        menuBar.setTranslateY(30);

        Text message = new Text(100, 20, "Click the button to open a file.");

        // Setting the stage
        Group root = new Group(menuBar);
        Scene scene = new Scene(root, 300, 300, Color.CORNFLOWERBLUE);
        root.getChildren().add(message);
        stage.setTitle("Rainfall Analyzer GUI");
        stage.setScene(scene);
        stage.show();
    }

    public void RainfallDataProcessor(String inputFile) {
        File file = new File(inputFile);
        String fileName = file.getName();
        String outputFile = fileName + "_analysed" + ".csv";

        RainfallAnalysis(inputFile, outputFile);
    }

    public static void RainfallAnalysis(String inputFile, String outputFile) {
        String inputFilePath = inputFile;
        String outputFilePath = getOutputFilePath(inputFilePath);

        Map<String, RainfallData> dataMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String year = parts[2];
                String month = parts[3];
                String rainfallStr = parts.length > 5 ? parts[5] : "";

                if (!rainfallStr.isEmpty()) {
                    double rainfall = Double.parseDouble(rainfallStr);

                    String key = year + "-" + month;
                    dataMap.computeIfAbsent(key, k -> new RainfallData()).addRainfall(rainfall);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeAnalyzedDataToFile(dataMap, outputFilePath);
    }

    private static String getOutputFilePath(String inputFilePath) {
        String[] parts = inputFilePath.split("\\.");
        String name = parts[0];
        String extension = parts[1];
        return name + "_analysed." + extension;
    }
    private static void writeAnalyzedDataToFile(Map<String, RainfallData> dataMap, String outputFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            writer.println("Year, Month, Total Rainfall, Min Rainfall, Max Rainfall");
            for (Map.Entry<String, RainfallData> entry : dataMap.entrySet()) {
                String[] keyParts = entry.getKey().split("-");
                String year = keyParts[0];
                String month = keyParts[1];
                RainfallData rainfallData = entry.getValue();

                writer.printf("%s, %s, %.2f, %.2f, %.2f%n",
                        year, month, rainfallData.getTotalRainfall(), rainfallData.getMinRainfall(), rainfallData.getMaxRainfall());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class RainfallData {
    private double totalRainfall;
    private double minRainfall = Double.MAX_VALUE;
    private double maxRainfall = Double.MIN_VALUE;
    private int count;

    public void addRainfall(double rainfall) {
        totalRainfall += rainfall;
        minRainfall = Math.min(minRainfall, rainfall);
        maxRainfall = Math.max(maxRainfall, rainfall);
        count++;
    }

    public double getTotalRainfall() {
        return totalRainfall;
    }

    public double getMinRainfall() {
        return minRainfall;
    }

    public double getMaxRainfall() {
        return maxRainfall;
    }
}
