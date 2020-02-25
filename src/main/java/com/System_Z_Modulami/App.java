package com.System_Z_Modulami;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class App extends Application {
	static final int MAX_TEXT_AREA_WIDTH = 300;
	static final int MAX_TEXT_AREA_HEIGH = 625;
	static final int GRID_PADDING = 10;

	private String fileNameToModul5 = "";

	static private Set<String> finalColumnNames;
	static Map<Double, String> tempMapWithAllValues;
	static Double[] classifyValues;
	static int distance;
	static int rowInsideLambda = 0;
	static int[] predictedValues;
	static int[] realValues;

	static List<String> columnNames;

	static File file = null;
	static int savedColumnNumber = 0;
	static String editedText = "";
	static String notEditedText = "";
	static int rows = 0;
	static int columns = 0;
	static int totalColumns = 0;
	static boolean isAmountOfColumnsChecked = false;
	static List<String> allLines;
	static Map<String, Integer> editedWords;
	static Map<Integer, Map<String, Integer>> editedWordsSet;
	static double[][] data;
	static double[][] dataChanged;
	static double[][] dataChangedTemp = null;
	static double[][] dataToValidate = null;
	static double[][] dataAfterStand;
	static double[][] dataAfterStandTemp = null;
	static double[][] dataMinMax;
	static double[][] dataPercent;
	static StringBuilder sb = new StringBuilder();
	static boolean isABC = false;
	static Integer anotherCounterGlobal = 0;
	static int countLoops = 0;
	static int normalColumns = 0;
	static int maxClassify = Integer.MIN_VALUE;
	static int indexClassify = 0;
	static short option = 0;

	static List<double[]> columnValueData;
	static double[] columnData;
	static TextArea leftTextArea;
//	static TextArea rightTextArea;
	static TableView<double[]> rightTableView;
	static ObservableList<double[]> obsList;
	static ObservableList<double[]> obsDiscList;
	static ObservableList<double[]> obsNormList;
	static int totalCounter = 0;
	static List<CustomList> listMinValues = new ArrayList<>();
	static List<CustomList> listMaxValues = new ArrayList<>();
	static List<Integer> listWithSavedColumnNumber = new ArrayList<>();
	static String newClassifiedObject = "";

	static int predictionCounter = 0;
	Map<Integer, Double> predictionsMap;

	/// Grouping
	static Set<Integer> generated;
	static Random rng = new Random();
	static int noOfK = 0;
	static List<Point> points;
	static List<Integer> groupingListWithIndexes;
	static List<Integer> tempGroupingListWithIndexes;

	static Map<Integer, Integer> firstAmountIterationWithIndexes;
	static Map<Integer, Integer> finalAmountIterationWithIndexes;

	static Map<Integer, List<Point>> firstIterationWithIndexes;
	static Map<Integer, List<Point>> finalIterationWithIndexes;

	static List<List<double[]>> firstIterationList;
	static List<List<double[]>> finalIterationList;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("System wspomagania decyzji");
		BorderPane borderPane = new BorderPane();

		initLeft(stage, borderPane);
		initRight(stage, borderPane);
		initTop(stage, borderPane);

		initBottom(stage, borderPane);
		stage.setScene(new Scene(borderPane, 1850, 950));
		stage.setX(0);
		stage.setY(0);
		stage.show();
	}

	public void setDiscreteValues(int sections) {
		for (int j = 1; j < (totalColumns - 1); j++)
			discretizeData2(sections, j);
	}

	public void discretizeData2(int sections, int column) {
		Map<Double, Double> secondInnerMap;
		Map<Double, Map<Double, Double>> firstInnerMap = new TreeMap<>();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int j = 0; j < rows; j++) {
			if (data[j][column] > max)
				max = data[j][column];
			if (data[j][column] < min)
				min = data[j][column];
		}

		double step = 0.0;

		step = (max - min) / sections;

		double[][] valSections = new double[sections][(int) totalColumns];

		for (int j = 0; j < sections; j++) {
			valSections[j][column] = min + (1 + j) * step;
		}

		for (int j = 0; j < rows; j++) {
			for (int k = 0; k < sections; k++) {
				if (data[j][column] <= valSections[k][column]) {
					data[j][column] = k + 1;
					break;
				}

			}
		}

	}

	public void initBottom(Stage stage, BorderPane borderPane) {
		GridPane gridPane = new GridPane();
		initGridPane(gridPane);
		Label discLabel = new Label("Dyskretyzacja");
		TextField discTextField = new TextField();
		discTextField.setPromptText("Ilość przedziałów");
		TextField discColumnTextField = new TextField();
		discColumnTextField.setPromptText("kolumna");
		Button discButton = new Button("Dyskretyzuj");
		discButton.setOnAction(event -> {
			if (!discTextField.getText().matches("-?\\d+") || !discColumnTextField.getText().matches("-?\\d+")) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Wpisz poprawną ilość sekcji!");
				alert.showAndWait();
			} else {
				discretizeData(Integer.parseInt(discTextField.getText()), Integer.parseInt(discColumnTextField.getText()));
			}
		});

		Label normLabel = new Label("Normalizacja");
		Button normButton = new Button("Normalizuj");
		TextField normTextField = new TextField();
		normTextField.setPromptText("kolumna");
		normButton.setOnAction(event -> {
			if (!normTextField.getText().matches("-?\\d+")) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Wpisz poprawną ilość!");
				alert.showAndWait();
			} else {
				normalizeData(Integer.parseInt(normTextField.getText()));
			}
		});

		Label minmaxLabel = new Label("Przedziały");
		TextField minTextField = new TextField();
		minTextField.setPromptText("min");
		TextField maxTextField = new TextField();
		maxTextField.setPromptText("max");
		TextField minMaxColumnTextField = new TextField();
		minMaxColumnTextField.setPromptText("kolumna");
		Button minmaxButton = new Button("Zmień przedziały");
		minmaxButton.setOnAction(event -> {
			if (!minMaxColumnTextField.getText().matches("-?\\d+")) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Wpisz poprawną ilość!");
				alert.showAndWait();
			} else {
				minMaxData(Double.parseDouble(minTextField.getText()), Double.parseDouble(maxTextField.getText()), Integer.parseInt(minMaxColumnTextField.getText()));
			}
		});

		Label percentLabel = new Label("Procenty");
		TextField percentColumnTextField = new TextField();
		percentColumnTextField.setPromptText("kolumna");
		TextField percentValueColumnTextField = new TextField();
		percentValueColumnTextField.setPromptText("procent danych");
		Button percentButton = new Button("Sprawdz");
		percentButton.setOnAction(event -> {
			if (!percentColumnTextField.getText().matches("-?\\d+") || !percentValueColumnTextField.getText().matches("-?\\d+")) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Wpisz poprawną ilość!");
				alert.showAndWait();
			} else {
				percentData(Integer.parseInt(percentColumnTextField.getText()), Integer.parseInt(percentValueColumnTextField.getText()));
			}

		});

		Label chart2dLabel = new Label("Wykres");
		TextField chartColumnTextField = new TextField();
		chartColumnTextField.setPromptText("kolumna");
		Button chart2dButton = new Button("Stwórz liniowy 2D");
		chart2dButton.setOnAction(event -> make2dChart(stage, Integer.parseInt(chartColumnTextField.getText()), "liniiowy 2D"));
		Button chart2ddiscButton = new Button("Stwórz słupkowy 2D");
		chart2ddiscButton.setOnAction(event -> make2dBarChart(stage, Integer.parseInt(chartColumnTextField.getText()), "słupkowy 2D"));
		Button chart2dnormButton = new Button("Stwórz kropkowy 2D");
		chart2dnormButton.setOnAction(event -> make2dScatterChart(stage, Integer.parseInt(chartColumnTextField.getText()), "kropkowy 2D"));

		Label chart2dLabel2 = new Label("Wykres");
		TextField chartColumnTextField2 = new TextField();
		chartColumnTextField2.setPromptText("kolumny np 1,2,3,4");
		Button chart2dButton2 = new Button("Stwórz liniowy 2D");
		chart2dButton2.setOnAction(event -> make2dChart2(stage, chartColumnTextField2.getText(), "liniiowy 2D"));
		Button chart2ddiscButton2 = new Button("Stwórz słupkowy 2D");
		chart2ddiscButton2.setOnAction(event -> make2dBarChart2(stage, chartColumnTextField2.getText(), "słupkowy 2D"));
		Button chart2dnormButton2 = new Button("Stwórz kropkowy 2D");
		chart2dnormButton2.setOnAction(event -> make2dScatterChart2(stage, chartColumnTextField2.getText(), "kropkowy 2D"));

		Label chart2dLabel3 = new Label("Wykres");
		TextField chartColumnTextField3 = new TextField();
		chartColumnTextField3.setPromptText("kolumny np 1,2");
		Button chart2dButton3 = new Button("Stwórz liniowy 2D");
//		chart2dButton3.setOnAction(event -> make2dChart3(stage, chartColumnTextField3.getText(), "liniiowy 2D"));
		Button chart2ddiscButton3 = new Button("Stwórz słupkowy 2D");
//		chart2ddiscButton3.setOnAction(event -> make2dBarChart3(stage, chartColumnTextField3.getText(), "słupkowy 2D"));
		Button chart2dnormButton3 = new Button("Stwórz kropkowy 2D");
		chart2dnormButton3.setOnAction(event -> make2dScatterChart3(stage, chartColumnTextField3.getText(), "kropkowy 2D"));

		Label chartJudge2DLabel = new Label("Wykres oceny");
		Button chartJudge2DButton = new Button("Stwórz kropkowy 2D");
		chartJudge2DButton.setOnAction(event -> makeClassifyChart2D(stage, "klasyfikacja oceny 2D"));

		Label loadFileLabel = new Label("Wczytaj");
		Button loadFileButton = new Button("Wczytaj plik");
		loadFileButton.setOnAction(event -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Wybierz guzik odpowiadający sortowaniu danych tekstowych.");
			alert.setTitle("Wczytywanie danych");
			alert.setHeaderText("Wybór wczytywania danych tekstowych.");
			ButtonType buttonTypeOne = new ButtonType("Posortuj względem wystąpienia");
			ButtonType buttonTypeTwo = new ButtonType("Posortuj alfabetycznie");
			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				isABC = false;
			} else if (result.get() == buttonTypeTwo) {
				isABC = true;
			}
			loadFile(stage, borderPane);
		});

		Label firstDataLabel = new Label("Wczytaj");
		Button firstDataButton = new Button("Wczytaj początkowe dane");
		firstDataButton.setOnAction(event -> {
			rightTableView.setItems(obsList);
		});

		int column = 0;

		DataUtils.initNode(chartJudge2DLabel);
		DataUtils.initNode(chartJudge2DButton);
		DataUtils.initNode(loadFileLabel);
		DataUtils.initNode(loadFileButton);
		DataUtils.initNode(discLabel);
		DataUtils.initNode(discTextField);
		DataUtils.initNode(discColumnTextField);
		DataUtils.initNode(discButton);
		DataUtils.initNode(normLabel);
		DataUtils.initNode(normTextField);
		DataUtils.initNode(normButton);
		DataUtils.initNode(minmaxLabel);
		DataUtils.initNode(minTextField);
		DataUtils.initNode(maxTextField);
		DataUtils.initNode(minMaxColumnTextField);
		DataUtils.initNode(minmaxButton);
		DataUtils.initNode(percentLabel);
		DataUtils.initNode(percentColumnTextField);
		DataUtils.initNode(percentValueColumnTextField);
		DataUtils.initNode(percentButton);
		DataUtils.initNode(chart2dLabel);
		DataUtils.initNode(chart2dButton);
		DataUtils.initNode(chart2ddiscButton);
		DataUtils.initNode(chart2dnormButton);
		DataUtils.initNode(chartColumnTextField);
		DataUtils.initNode(chart2dLabel2);
		DataUtils.initNode(chart2dButton2);
		DataUtils.initNode(chart2ddiscButton2);
		DataUtils.initNode(chart2dnormButton2);
		DataUtils.initNode(chartColumnTextField2);
		DataUtils.initNode(chart2dLabel3);
		DataUtils.initNode(chart2dButton3);
		DataUtils.initNode(chart2ddiscButton3);
		DataUtils.initNode(chart2dnormButton3);
		DataUtils.initNode(chartColumnTextField3);

		gridPane.add(loadFileLabel, 0, column);
		gridPane.add(loadFileButton, 1, column++);

		gridPane.add(discLabel, 0, column);
		gridPane.add(discTextField, 1, column);
		gridPane.add(discColumnTextField, 2, column);
		gridPane.add(discButton, 3, column++);

		gridPane.add(normLabel, 0, column);
		gridPane.add(normTextField, 1, column);
		gridPane.add(normButton, 2, column++);

		gridPane.add(minmaxLabel, 0, column);
		gridPane.add(minTextField, 1, column);
		gridPane.add(maxTextField, 2, column);
		gridPane.add(minMaxColumnTextField, 3, column);
		gridPane.add(minmaxButton, 4, column++);

		gridPane.add(percentLabel, 0, column);
		gridPane.add(percentColumnTextField, 1, column);
		gridPane.add(percentValueColumnTextField, 2, column);
		gridPane.add(percentButton, 3, column++);

		gridPane.add(chart2dLabel, 0, column);
		gridPane.add(chartColumnTextField, 1, column);
		gridPane.add(chart2dButton, 2, column);
		gridPane.add(chart2ddiscButton, 3, column);
		gridPane.add(chart2dnormButton, 4, column++);

		gridPane.add(chart2dLabel2, 0, column);
		gridPane.add(chartColumnTextField2, 1, column);
		gridPane.add(chart2dButton2, 2, column);
		gridPane.add(chart2ddiscButton2, 3, column);
		gridPane.add(chart2dnormButton2, 4, column++);

		gridPane.add(chart2dLabel3, 0, column);
		gridPane.add(chartColumnTextField3, 1, column);
//		gridPane.add(chart2dButton3, 2, column);
//		gridPane.add(chart2ddiscButton3, 3, column);
		gridPane.add(chart2dnormButton3, 2, column++);

		gridPane.add(chartJudge2DLabel, 0, column);
		gridPane.add(chartJudge2DButton, 1, column++);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(gridPane);
		scrollPane.setMaxHeight(400);

		borderPane.setBottom(scrollPane);

	}

	private void makeClassifyChart2D(Stage stage, String string) {
		final NumberAxis yAxis = new NumberAxis();
		final NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Liczba sąsiadów");
		yAxis.setLabel("Procent poprawnie zakwalifikowanych");
		final ScatterChart<Number, Number> bc = new ScatterChart<Number, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + string + ")");

		XYChart.Series series = new XYChart.Series();
		int counter = 0;

		for (Entry<Integer, Double> entry : predictionsMap.entrySet()) {
			series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
		}

		bc.getData().add(series);

		Scene scene = new Scene(bc, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void make2dScatterChart3(Stage stage, String columns, String string) {
		List<String> cols = Arrays.asList(columns.split(","));
		int column1 = Integer.parseInt(cols.get(0));
		int column2 = Integer.parseInt(cols.get(1));

		double oldMin = Double.MAX_VALUE;
		double oldMax = Double.MIN_VALUE;
		for (int j = 0; j < rows; j++) {
			if (dataChanged[j][column1] > oldMax)
				oldMax = dataChanged[j][column1];
			if (dataChanged[j][column1] < oldMin)
				oldMin = dataChanged[j][column1];
		}

		final NumberAxis xAxis = new NumberAxis(oldMin - oldMin * 0.1, oldMax + oldMax * 0.1, (oldMax - oldMin) / 10);

		oldMin = Double.MAX_VALUE;
		oldMax = Double.MIN_VALUE;
		for (int j = 0; j < rows; j++) {
			if (dataChanged[j][column2] > oldMax)
				oldMax = dataChanged[j][column2];
			if (dataChanged[j][column2] < oldMin)
				oldMin = dataChanged[j][column2];
		}

		final NumberAxis yAxis = new NumberAxis(oldMin - oldMin * 0.1, oldMax + oldMax * 0.1, (oldMax - oldMin) / 10);
		xAxis.setLabel(cols.get(0));
		yAxis.setLabel(cols.get(1));
		final ScatterChart<Number, Number> bc = new ScatterChart<Number, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + string + ")");

		XYChart.Series series = new XYChart.Series();
		int counter = 0;

		for (int i = 1; i < totalColumns - 1; i++) {
			makeScatterBetter(bc, i);
		}

		Scene scene = new Scene(bc, 200, 200);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void makeScatterBetter(ScatterChart<Number, Number> bc, int i) {
		XYChart.Series series = new XYChart.Series();
		series.setName("kolumna: " + (columnNames.get(i - 1)));
		int counter = 0;
		for (int j = 0; j < rows; j++) {
			if (dataChanged[j][totalColumns - 1] == (double) i)
				series.getData().add(new XYChart.Data(dataChanged[j][1], dataChanged[j][2]));
		}
		bc.getData().add(series);

	}

	private void make2dScatterChart2(Stage stage, String columns, String string) {
		List<String> cols = Arrays.asList(columns.split(","));
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Próbka");
		yAxis.setLabel("Wartość");
		final ScatterChart<Number, Number> bc = new ScatterChart<Number, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + string + ")");
		for (int i = 0; i < cols.size(); i++)
			makeScatter(bc, Integer.parseInt(cols.get(i)));
		Scene scene = new Scene(bc, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void make2dBarChart2(Stage stage, String columns, String string) {
		List<String> cols = Arrays.asList(columns.split(","));
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Próbka");
		yAxis.setLabel("Wartość");
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + string + ")");
		for (int i = 0; i < cols.size(); i++)
			makeBar(bc, Integer.parseInt(cols.get(i)));
		Scene scene = new Scene(bc, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void make2dChart2(Stage stage, String columns, String string) {
		List<String> cols = Arrays.asList(columns.split(","));
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Próbka");
		yAxis.setLabel("Wartość");
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("System wspomagania danych (" + string + ")");
		for (int i = 0; i < cols.size(); i++) {
			makeLine(lineChart, Integer.parseInt(cols.get(i)));
		}

		Scene scene = new Scene(lineChart, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void make2dScatterChart(Stage stage, int column, String text) {
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Próbka");
		yAxis.setLabel("Wartość");
		final ScatterChart<Number, Number> bc = new ScatterChart<Number, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + text + ")");
		makeScatter(bc, column);
		Scene scene = new Scene(bc, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void makeScatter(ScatterChart<Number, Number> sc, int column) {
		XYChart.Series series = new XYChart.Series();
		series.setName("kolumna nr" + (column));
		int counter = 0;
		for (int j = 0; j < rows; j++) {
			series.getData().add(new XYChart.Data(++counter, dataChanged[j][column]));
		}
		sc.getData().add(series);

	}

	private void make2dBarChart(Stage stage, int column, String text) {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Próbka");
		yAxis.setLabel("Wartość");
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + text + ")");
		makeBar(bc, column);
		Scene scene = new Scene(bc, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	public void makeBar(BarChart<String, Number> bc, int columnId) {
		Map<Double, Integer> tempMap = new TreeMap<>();
		for (int i = 0; i < rows; i++) {
			if (tempMap.containsKey(dataChanged[i][columnId])) {
				tempMap.put(dataChanged[i][columnId], tempMap.get(dataChanged[i][columnId]) + 1);
			} else {
				tempMap.put(dataChanged[i][columnId], 1);
			}

		}
		XYChart.Series series = new XYChart.Series();
		series.setName("kolumna nr" + (columnId));

		for (Entry<Double, Integer> map : tempMap.entrySet()) {
			series.getData().add(new XYChart.Data(String.valueOf(map.getKey()), map.getValue()));
		}
		bc.getData().add(series);
	}

	private void make2dChart(Stage stage, int column, String text) {
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Próbka");
		yAxis.setLabel("Wartość");
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("System wspomagania danych (" + text + ")");
		makeLine(lineChart, column);
		Scene scene = new Scene(lineChart, 900, 800);

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	public void makeLine(LineChart<Number, Number> lineChart, int columnId) {
		XYChart.Series series = new XYChart.Series();
		series.setName("kolumna nr" + (columnId));
		int counter = 0;
		for (int j = 0; j < rows; j++) {
			series.getData().add(new XYChart.Data(++counter, dataChanged[j][columnId]));
		}
		lineChart.getData().add(series);
	}

	private void percentData(int column, int percent) {
		List<Double> listTemp = new ArrayList<>();
		totalColumns++;
		dataChanged = new double[rows][totalColumns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				if (j < columns) {
					dataChanged[i][j] = data[i][j];
				} else if (j >= columns && j < totalColumns - 1) {
					dataChanged[i][j] = dataChangedTemp[i][j];
				} else {
					dataChanged[i][j] = 0.0;
				}
			}
		}

		for (int j = 0; j < rows; j++) {
			listTemp.add(dataChanged[j][column]);
		}

		for (int j = 0; j < rows; j++) {
			dataChanged[j][totalColumns - 1] = listTemp.get(j);
		}

		int numberOfValues = (int) (rows * (((double) percent / 100)));
		System.out.println(numberOfValues);

		listMinValues.add(new CustomList(totalCounter));
		listMaxValues.add(new CustomList(totalCounter));

		Collections.sort(listTemp);
		for (int i = 0; i < listTemp.size(); i++)
			if (i < numberOfValues)
				listMinValues.get(totalCounter).getList().add(listTemp.get(i));

		Collections.reverse(listTemp);
		for (int i = 0; i < listTemp.size(); i++)
			if (i < numberOfValues)
				listMaxValues.get(totalCounter).getList().add(listTemp.get(i));

		dataChangedTemp = dataChanged;
		obsDiscList = FXCollections.observableArrayList(dataChanged);
		rightTableView.setItems(obsDiscList);
		rightTableView.getColumns().add(createAdditionalColumnWithColor("Procent C" + column + "/" + percent + "\nMin-niebieski\nMax-czerwony", totalColumns - 1, totalCounter));
		totalCounter++;
	}

	private void makeCovarianceMatrix() {

	}

	private void minMaxData(double newMin, double newMax, int column) {
		totalColumns++;
		dataChanged = new double[rows][totalColumns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				if (j < columns) {
					dataChanged[i][j] = data[i][j];
				} else if (j >= columns && j < totalColumns - 1) {
					dataChanged[i][j] = dataChangedTemp[i][j];
				} else {
					dataChanged[i][j] = 0.0;
				}
			}
		}

		double oldMin = Double.MAX_VALUE;
		double oldMax = Double.MIN_VALUE;
		for (int j = 0; j < rows; j++) {
			if (dataChanged[j][column] > oldMax)
				oldMax = dataChanged[j][column];
			if (dataChanged[j][column] < oldMin)
				oldMin = dataChanged[j][column];
		}

		System.out.println("Min=" + oldMin + ", Max=" + oldMax);

		for (int j = 0; j < rows; j++) {
			dataChanged[j][totalColumns - 1] = ((dataChanged[j][column] - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
			dataChanged[j][totalColumns - 1] = BigDecimal.valueOf(dataChanged[j][totalColumns - 1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
		}

		dataChangedTemp = dataChanged;
		obsDiscList = FXCollections.observableArrayList(dataChanged);
		rightTableView.setItems(obsDiscList);
		rightTableView.getColumns().add(createAdditionalColumn("Min/Max C" + column + "\nOld min-" + oldMin + "\nOld max-" + oldMax + "\nNew min-" + newMin + "\nNew max-" + newMax, totalColumns - 1));

	}

	public void normalizeData(int column) {
		totalColumns++;

		dataChanged = new double[rows][totalColumns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				if (j < columns) {
					dataChanged[i][j] = data[i][j];
				} else if (j >= columns && j < totalColumns - 1) {
					dataChanged[i][j] = dataChangedTemp[i][j];
				} else {
					dataChanged[i][j] = 0.0;
				}
			}
		}

		double dev = 0.0;
		double arthAve = 0.0;

		for (int j = 0; j < rows; j++) {
			arthAve += dataChanged[j][column];
		}
		arthAve /= rows;

		for (int j = 0; j < rows; j++)
			dev += Math.pow((dataChanged[j][column] - arthAve), 2);

		dev = Math.sqrt(dev / rows);

		for (int j = 0; j < rows; j++) {
			double val = (dataChanged[j][column] - arthAve) / dev;
			dataChanged[j][totalColumns - 1] = BigDecimal.valueOf(val).setScale(5, RoundingMode.HALF_UP).doubleValue();
		}

		dataChangedTemp = dataChanged;
		obsDiscList = FXCollections.observableArrayList(dataChanged);
		rightTableView.setItems(obsDiscList);
		rightTableView.getColumns().add(createAdditionalColumn("Normalizacja C" + column, totalColumns - 1));

	}

	public void discretizeData(int sections, int column) {
		totalColumns++;
		dataChanged = new double[rows][totalColumns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				if (j < columns) {
					dataChanged[i][j] = data[i][j];
				} else if (j >= columns && j < totalColumns - 1) {
					dataChanged[i][j] = dataChangedTemp[i][j];
				} else {
					dataChanged[i][j] = 0.0;
				}
			}
		}

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int j = 0; j < rows; j++) {
			if (dataChanged[j][column] > max)
				max = dataChanged[j][column];
			if (dataChanged[j][column] < min)
				min = dataChanged[j][column];
		}

		double step = 0.0;

		step = (max - min) / sections;

		double[][] valSections = new double[sections][columns];

		for (int j = 0; j < sections; j++) {
			valSections[j][column] = min + (1 + j) * step;
		}

		for (int j = 0; j < rows; j++) {
			for (int k = 0; k < sections; k++) {
				if (dataChanged[j][column] <= valSections[k][column]) {
					dataChanged[j][totalColumns - 1] = k + 1;
					break;
				}

			}
		}

		dataChangedTemp = dataChanged;
		// System.out.println("Columns: " + totalColumns + ", length: " +
		// dataAfterDisc.length);
//		for (int i = 0; i < rows; i++) {
//			for (int j = 0; j < totalColumns; j++) {
//				System.out.print(dataAfterDisc[i][j] + " ");
//			}
//			System.out.println("------------");
//		}

		obsDiscList = FXCollections.observableArrayList(dataChanged);
		rightTableView.setItems(obsDiscList);
		rightTableView.getColumns().add(createAdditionalColumn("Dyskretyzacja C" + column + "/" + sections, totalColumns - 1));

	}

	public String formatDouble(double item) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(item);
	}

	public String formatDouble2(double item) {
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(item);
	}

	public String formatDoubleNorm(double item) {
		DecimalFormat df = new DecimalFormat("0.00000");
		return df.format(item);
	}

	public void initGridPane(GridPane grid) {
		grid.setHgap(GRID_PADDING);
		grid.setVgap(GRID_PADDING);
		grid.setPadding(new Insets(GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING));
	}

	public void initLeft(Stage stage, BorderPane borderPane) {
		VBox vBox = new VBox();
		initVBox(vBox);
		Label leftLabel = new Label("Tekst przed edycją");
		leftTextArea = new TextArea();
		leftTextArea.setMinHeight(500);
		leftTextArea.setMaxHeight(500);
		leftTextArea.setMinWidth(600);
		leftTextArea.setMaxWidth(600);
		vBox.getChildren().addAll(leftLabel, leftTextArea);
		borderPane.setLeft(vBox);
	}

	public void initRight(Stage stage, BorderPane borderPane) {
		VBox vBox = new VBox();
		initVBox(vBox);
		Label rightLabel = new Label("Tekst po edycji");
		rightTableView = new TableView<>();
		rightTableView.setMinWidth(1200);
		rightTableView.setMaxWidth(1200);
		rightTableView.setMinHeight(500);
		rightTableView.setMaxHeight(500);
		// rightTableView.scroll

		vBox.getChildren().addAll(rightLabel, rightTableView);
		borderPane.setRight(vBox);
	}

	public void initTop(Stage stage, BorderPane borderPane) {
		GridPane gridPane = new GridPane();
		initGridPane(gridPane);
		Button loadFileButton = new Button("Pokaż metryki");
		loadFileButton.setMinWidth(gridPane.getPrefWidth());
		loadFileButton.setOnAction(event -> {
			countLoops = 0;
			Label metricsLabel = new Label("Metryki");
			Label judgeLabel = new Label("Ocena Klasyfikacji");
			Label nJudgeLabel = new Label("Ocena Klasyfikacji (n)");
			Label sJudgeLabel = new Label("Standaryzacja Klasyfikacji (n)");
			Label distanceLabel = new Label("Sasiedzi");
			Label pointsLabel = new Label("Punkty");

			Button euButton = new Button("Metryka Euklidesa");
			Button manButton = new Button("Metryka Manhattan");
			Button czButton = new Button("Metryka Czebyszewa");
			Button mahButton = new Button("Metryka Mahalanobisa");

			Button jeuButton = new Button("Ocena Euklidesa");
			Button jmanButton = new Button("Ocena Manhattan");
			Button jczButton = new Button("Ocena Czebyszewa");
			Button jmahButton = new Button("Ocena Mahalanobisa");

			Button alljeuButton = new Button("Ocena Euklidesa (n) sąsiadów");
			Button alljmanButton = new Button("Ocena Manhattan (n) sąsiadów");
			Button alljczButton = new Button("Ocena Czebyszewa (n) sąsiadów");
			Button alljmahButton = new Button("Ocena Mahalanobisa (n) sąsiadów");

			Button salljeuButton = new Button("Standaryzacja Euklidesa (n) sąsiadów");
			Button salljmanButton = new Button("Standaryzacja Manhattan (n) sąsiadów");
			Button salljczButton = new Button("Standaryzacja Czebyszewa (n) sąsiadów");
			Button salljmahButton = new Button("Standaryzacja Mahalanobisa (n) sąsiadów");

			Button randValuesButton = new Button("Losuj dane");
			TextField disTextField = new TextField();
			disTextField.setPromptText("Odległość");

			DataUtils.initNode(metricsLabel);
			DataUtils.initNode(judgeLabel);
			DataUtils.initNode(distanceLabel);
			DataUtils.initNode(nJudgeLabel);
			DataUtils.initNode(sJudgeLabel);
			DataUtils.initNode(pointsLabel);

			DataUtils.initNode(euButton);
			DataUtils.initNode(manButton);
			DataUtils.initNode(czButton);
			DataUtils.initNode(mahButton);

			DataUtils.initNode(jeuButton);
			DataUtils.initNode(jmanButton);
			DataUtils.initNode(jczButton);
			DataUtils.initNode(jmahButton);

			DataUtils.initNode(alljeuButton);
			DataUtils.initNode(alljmanButton);
			DataUtils.initNode(alljczButton);
			DataUtils.initNode(alljmahButton);

			DataUtils.initNode(salljeuButton);
			DataUtils.initNode(salljmanButton);
			DataUtils.initNode(salljczButton);
			DataUtils.initNode(salljmahButton);

			DataUtils.initNode(randValuesButton);

			DataUtils.initNode(disTextField);

			gridPane.add(metricsLabel, 0, 1);
			gridPane.add(judgeLabel, 0, 2);
			gridPane.add(nJudgeLabel, 0, 3);
			gridPane.add(sJudgeLabel, 0, 4);
			gridPane.add(distanceLabel, 0, 5);
			gridPane.add(pointsLabel, 0, 6);

			gridPane.add(euButton, 1, 1);
			gridPane.add(manButton, 2, 1);
			gridPane.add(czButton, 3, 1);
			gridPane.add(mahButton, 4, 1);
			gridPane.add(randValuesButton, 5, 1);

			gridPane.add(jeuButton, 1, 2);
			gridPane.add(jmanButton, 2, 2);
			gridPane.add(jczButton, 3, 2);
			gridPane.add(jmahButton, 4, 2);

			gridPane.add(alljeuButton, 1, 3);
			gridPane.add(alljmanButton, 2, 3);
			gridPane.add(alljczButton, 3, 3);
			gridPane.add(alljmahButton, 4, 3);

			gridPane.add(salljeuButton, 1, 4);
			gridPane.add(salljmanButton, 2, 4);
			gridPane.add(salljczButton, 3, 4);
			gridPane.add(salljmahButton, 4, 4);

			gridPane.add(disTextField, 1, 5);

			int x = columns - 2;
			TextField[] vals = new TextField[x];
			Random r = new Random();
			int randomValue = r.nextInt(14) + 3;
			disTextField.setText(randomValue + "");

			classifyValues = new Double[x];
			distance = Integer.valueOf(disTextField.getText().toString());
			int co = 0;
			int currColumn = 6;
			for (int i = 0; i < x; i++) {
				randomValue = r.nextInt(14) + 3;
				TextField pointValueTextField = new TextField(randomValue + "");
				pointValueTextField.setPromptText("Kolumna " + (i + 1));
				DataUtils.initNode(pointValueTextField);
				vals[i] = pointValueTextField;
				if (co > 8) {
					currColumn++;
					co = 0;
				}
				gridPane.add(pointValueTextField, (co + 1), currColumn);
				co++;
			}

			randValuesButton.setOnAction(e -> {
				int randValue = 0;
				for (int i = 0; i < x; i++) {
					randValue = r.nextInt(14) + 3;
					vals[i].setText(randValue + " ");
				}
				randValue = r.nextInt(14) + 3;
				disTextField.setText(randValue + "");
			});
			// setDiscreteValues(15);
			euButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				for (int i = 0; i < x; i++)
					classifyValues[i] = Double.parseDouble(vals[i].getText().toString());
				classify(stage, classifyValues, distance, (short) 1);
			});
			manButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				for (int i = 0; i < x; i++)
					classifyValues[i] = Double.parseDouble(vals[i].getText().toString());
				classify(stage, classifyValues, distance, (short) 2);
			});
			czButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				for (int i = 0; i < x; i++)
					classifyValues[i] = Double.parseDouble(vals[i].getText().toString());
				classify(stage, classifyValues, distance, (short) 3);
			});
			mahButton.setOnAction(ev -> {
				makeCovarianceMatrix();
				distance = Integer.valueOf(disTextField.getText().toString());
				judgeClassify(stage, distance, (short) 4);
			});

			jeuButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				judgeClassify(stage, distance, (short) 1);
			});
			jmanButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				judgeClassify(stage, distance, (short) 2);
			});
			jczButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				judgeClassify(stage, distance, (short) 3);
			});
			jmahButton.setOnAction(ev -> {
				distance = Integer.valueOf(disTextField.getText().toString());
				makeCovarianceMatrix();
				judgeClassify(stage, distance, (short) 4);
			});

			alljeuButton.setOnAction(ev -> judgeAllNeightboursClassivy(stage, (short) 1));
			alljmanButton.setOnAction(ev -> judgeAllNeightboursClassivy(stage, (short) 2));
			alljczButton.setOnAction(ev -> judgeAllNeightboursClassivy(stage, (short) 3));
			alljmahButton.setOnAction(ev -> {
				makeCovarianceMatrix();
				judgeAllNeightboursClassivy(stage, (short) 4);
			});

			salljeuButton.setOnAction(ev -> standJudgeAllNeightboursClassivy(stage, (short) 1));
			salljmanButton.setOnAction(ev -> standJudgeAllNeightboursClassivy(stage, (short) 2));
			salljczButton.setOnAction(ev -> standJudgeAllNeightboursClassivy(stage, (short) 3));
			salljmahButton.setOnAction(ev -> {
				makeCovarianceMatrix();
				standJudgeAllNeightboursClassivy(stage, (short) 4);
			});

		});

		Button showGroupingButton = new Button("Pokaż Grupowanie");

		showGroupingButton.setOnAction(event -> {
			Label kNumberLabel = new Label("Liczba k");
			DataUtils.initNode(kNumberLabel);
			gridPane.add(kNumberLabel, 0, 2);

			TextField kNumberTextField = new TextField();
			kNumberTextField.setPromptText("Liczba k");
			kNumberTextField.setText((editedWords.size()) + "");
			DataUtils.initNode(kNumberTextField);
			gridPane.add(kNumberTextField, 1, 2);

			Button euButton = new Button("Metryka Euklidesa");
			Button manButton = new Button("Metryka Manhattan");
			Button czButton = new Button("Metryka Czebyszewa");
			Button mahButton = new Button("Metryka Mahalanobisa");

			DataUtils.initNode(euButton);
			DataUtils.initNode(manButton);
			DataUtils.initNode(czButton);
			DataUtils.initNode(mahButton);
			gridPane.add(euButton, 1, 1);
			gridPane.add(manButton, 2, 1);
			gridPane.add(czButton, 3, 1);
			gridPane.add(mahButton, 4, 1);

			Label metricsLabel = new Label("Metryki");
			DataUtils.initNode(metricsLabel);
			gridPane.add(metricsLabel, 0, 1);

			euButton.setOnAction(ev -> {
				noOfK = Integer.valueOf(kNumberTextField.getText().toString());
				groupClassify(stage, noOfK, (short) 1);
			});

			manButton.setOnAction(ev -> {
				noOfK = Integer.valueOf(kNumberTextField.getText().toString());
				groupClassify(stage, noOfK, (short) 2);
			});

			czButton.setOnAction(ev -> {
				noOfK = Integer.valueOf(kNumberTextField.getText().toString());
				groupClassify(stage, noOfK, (short) 3);
			});

		});

		Button decisionTreeButton = new Button("Drzewa decyzyjne");
		decisionTreeButton.setOnAction(evr -> {
			DecisionTree decisionTree = new DecisionTree();
			try {
				decisionTree.initDecisionTree(dataChanged, columnNames, columns, rows);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		Button cutButton = new Button("Ostatni moduł");
		cutButton.setOnAction(evr -> {
			Modul5 modul5 = new Modul5();
			try {
				finalColumnNames.forEach(System.out::println);
				modul5.initModul5(dataChanged, columnNames, columns, rows, finalColumnNames, fileNameToModul5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Button hideGroupingButton = new Button("Schowaj");

		hideGroupingButton.setOnAction(event -> {
			gridPane.getChildren().removeAll(gridPane.getChildren());
			gridPane.add(loadFileButton, 0, 0);
			DataUtils.initNode(loadFileButton);
			gridPane.add(showGroupingButton, 1, 0);
			gridPane.add(hideGroupingButton, 4, 0);
			gridPane.add(cutButton, 3, 0);
			gridPane.add(decisionTreeButton, 2, 0);
			DataUtils.initNode(showGroupingButton);
			DataUtils.initNode(hideGroupingButton);
			DataUtils.initNode(cutButton);

		});

		DataUtils.initNode(loadFileButton);
		DataUtils.initNode(showGroupingButton);
		DataUtils.initNode(hideGroupingButton);
		DataUtils.initNode(cutButton);
		DataUtils.initNode(decisionTreeButton);
		gridPane.add(loadFileButton, 0, 0);
		gridPane.add(showGroupingButton, 1, 0);
		gridPane.add(hideGroupingButton, 4, 0);
		gridPane.add(decisionTreeButton, 2, 0);
		gridPane.add(cutButton, 3, 0);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(gridPane);
		scrollPane.setMaxHeight(225);
		borderPane.setTop(scrollPane);

	}

	private Double[][] getFreshData(Double[][] data) {
		Double[][] tempData = new Double[(int) rows][(int) totalColumns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				tempData[i][j] = data[i][j];
			}
		}
		return tempData;
	}

	private void groupClassify(Stage stage, int noOfK, short option) {
		GridPane gridPane = new GridPane();
		initGridPane(gridPane);

		StringBuilder sb = new StringBuilder();

		firstAmountIterationWithIndexes = new TreeMap<>();
		finalAmountIterationWithIndexes = new TreeMap<>();

		firstIterationWithIndexes = new TreeMap<>();
		finalIterationWithIndexes = new TreeMap<>();

		firstIterationList = new ArrayList<>();
		finalIterationList = new ArrayList<>();

		int groupingCounter = 1;

		generateRandomNoOfK(noOfK);
		List<Integer> generatedValues = new ArrayList<>(generated);
		Double d = 0.0;

		Double[][] dMatrix = new Double[noOfK][columns - 2];

		for (int o = 0; o < noOfK; o++) {
			for (int oo = 0; oo < columns - 2; oo++) {
				dMatrix[o][oo] = 0.0;
			}
		}

		points = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			double[] axis = new double[columns - 2];
			for (int k = 0; k < columns - 2; k++)
				axis[k] = dataChanged[i][k + 1];
			int index = (int) dataChanged[i][columns - 1];
			Point p = new Point(axis, index);
			points.add(p);
		}

		for (int asd = 0; asd < points.size(); asd++) {
			if (firstAmountIterationWithIndexes.containsKey(points.get(asd).getNewIndex())) {
				firstAmountIterationWithIndexes.put(points.get(asd).getNewIndex(), firstAmountIterationWithIndexes.get(points.get(asd).getNewIndex()) + 1);
			} else {
				firstAmountIterationWithIndexes.put(points.get(asd).getNewIndex(), 0);
			}
		}

		for (int i = 0; i < points.size(); i++) {
			if (generatedValues.contains(i)) {
				points.get(i).setNewIndex(groupingCounter++);
				continue;
			}

			Double[] values = new Double[columns - 2];
			for (int k = 0; k < columns - 2; k++)
				values[k] = dataChanged[i][k + 1];

			switch (option) {
			case 1:
				euMetricGrouping(values, noOfK, generatedValues, i);
				break;
			case 2:
				manMetricGrouping(values, noOfK, generatedValues, i);
				break;
			case 3:
				czeMetricGrouping(values, noOfK, generatedValues, i);
				break;
			case 4:
				break;
			default:
			}

		}

		for (int asd = 0; asd < points.size(); asd++) {
			if (firstIterationWithIndexes.containsKey(points.get(asd).getNewIndex())) {
				List<Point> list = new ArrayList<>(firstIterationWithIndexes.get(points.get(asd).getNewIndex()));
				list.add(points.get(asd));
				firstIterationWithIndexes.put(points.get(asd).getNewIndex(), list);
			} else {
				List<Point> list = new ArrayList<>();
				list.add(points.get(asd));
				firstIterationWithIndexes.put(points.get(asd).getNewIndex(), list);
			}
		}

		firstIterationWithIndexes.forEach((e, v) -> {
			// System.out.println(e + "=====================");
			List<double[]> list2 = new ArrayList<>();
			v.forEach(k -> {
//				System.out.println(k.toString() + "");
				list2.add(k.getAxis());
			});

			firstIterationList.add(list2);

		});

		groupingListWithIndexes = new ArrayList<>();
		groupingListWithIndexes.add(0);
		tempGroupingListWithIndexes = new ArrayList<>();

		while (!groupingListWithIndexes.equals(tempGroupingListWithIndexes)) {
			if (!groupingListWithIndexes.isEmpty())
				groupingListWithIndexes.clear();
			if (!tempGroupingListWithIndexes.isEmpty())
				tempGroupingListWithIndexes.clear();
			groupingListWithIndexes = new ArrayList<>();
			tempGroupingListWithIndexes = new ArrayList<>();
			for (int i = 0; i < points.size(); i++) {
				groupingListWithIndexes.add(points.get(i).getNewIndex());
//				System.out.println(points.get(i));
			}

			for (int column = 0; column < noOfK; column++) {
//				System.out.println("Cluster: " + (column + 1));
				for (int i = 0; i < columns - 2; i++) {
					dMatrix[column][i] = calculateAvg(column, i);
//					System.out.print(formatDouble(dMatrix[column][i]) + " ");
				}
//				System.out.println();
			}

			for (int i = 0; i < points.size(); i++) {
				Double[] values = new Double[columns - 2];
				for (int k = 0; k < columns - 2; k++)
					values[k] = dataChanged[i][k + 1];

				switch (option) {
				case 1:
					euMetricGroupingAgain(values, dMatrix, noOfK, generatedValues, i);
					break;
				case 2:
					manMetricGroupingAgain(values, dMatrix, noOfK, generatedValues, i);
					break;
				case 3:
					System.out.println("Start");
					czeMetricGroupingAgain(values, dMatrix, noOfK, generatedValues, i);
					System.out.println("Stop");
					break;
				case 4:
					break;
				default:
				}
			}
		}
		System.out.println("\n\n\n\nDruga iteracja");
		for (int asd = 0; asd < points.size(); asd++) {
			if (finalIterationWithIndexes.containsKey(points.get(asd).getNewIndex())) {
				List<Point> list = new ArrayList<>(finalIterationWithIndexes.get(points.get(asd).getNewIndex()));
				list.add(points.get(asd));
				finalIterationWithIndexes.put(points.get(asd).getNewIndex(), list);
			} else {
				List<Point> list = new ArrayList<>();
				list.add(points.get(asd));
				finalIterationWithIndexes.put(points.get(asd).getNewIndex(), list);
			}
		}

		finalIterationWithIndexes.forEach((e, v) -> {
			// System.out.println(e + "=====================");
			List<double[]> list2 = new ArrayList<>();
			v.forEach(k -> {
//				System.out.println(k.toString() + "");
				list2.add(k.getAxis());
			});

			finalIterationList.add(list2);
			System.out.println();

		});

//		System.out.println("Takie same");

//		int noOfClassifiedGood = 0;
//
//		for (int i = 0; i < rows; i++) {
//			if (dataChanged[i][columns - 1] == points.get(i).getNewIndex()) {
//				noOfClassifiedGood++;
//			}
//		}
//
//		double classifyAsse = ((double) noOfClassifiedGood / (double) (rows)) * 100;
//		System.out.print("Ocena grupowania=" + formatDouble(classifyAsse) + " dla wierszy: ");
//		generatedValues.forEach(e -> System.out.print("[" + e + "]"));
//		System.out.println();
//
//		int[] originalClassifies = new int[rows];
//		for (int i = 0; i < rows; i++) {
//			originalClassifies[i] = (int) dataChanged[i][columns - 1];
//		}
//
//		int[] changedClassifies = new int[rows];
//		for (int i = 0; i < rows; i++) {
//			changedClassifies[i] = (int) points.get(i).getNewIndex();
//		}

		for (int asd = 0; asd < points.size(); asd++) {
			if (finalAmountIterationWithIndexes.containsKey(points.get(asd).getNewIndex())) {
				finalAmountIterationWithIndexes.put(points.get(asd).getNewIndex(), finalAmountIterationWithIndexes.get(points.get(asd).getNewIndex()) + 1);
			} else {
				finalAmountIterationWithIndexes.put(points.get(asd).getNewIndex(), 0);
			}
		}

		sb.append("Grupy przed modyfikacjami:   ");
		System.out.print("Poczatkowa grupa:   ");
		for (Entry<Integer, Integer> tempMap : firstAmountIterationWithIndexes.entrySet()) {
			sb.append("klasa " + tempMap.getKey() + ": " + tempMap.getValue() + ", ");
			System.out.print("klasa " + tempMap.getKey() + ": " + tempMap.getValue() + ", ");
		}

		sb.append("\nGrupy po modyfikacjach:        ");
		System.out.print("\nKońcowa      grupa:   ");
		for (Entry<Integer, Integer> tempMap : finalAmountIterationWithIndexes.entrySet()) {
			sb.append("klasa " + tempMap.getKey() + ": " + tempMap.getValue() + ", ");
			System.out.print("klasa " + tempMap.getKey() + ": " + tempMap.getValue() + ", ");
		}

		sb.append("\n");
		System.out.print("\n");
		for (int iteration = 0; iteration < firstIterationList.size(); iteration++) {
			sb.append("\nMiara jaccarda   " + (iteration + 1) + " klasa: " + formatDouble2(jaccardSimilarity(firstIterationList.get(iteration), finalIterationList.get(iteration))));
			sb.append("\nMiara dice          " + (iteration + 1) + " klasa: " + formatDouble2(diceCoefficient(firstIterationList.get(iteration), finalIterationList.get(iteration))));
			System.out.println("Miara jaccarda   " + (iteration + 1) + " klasa: " + formatDouble2(jaccardSimilarity(firstIterationList.get(iteration), finalIterationList.get(iteration))));
			System.out.println("Miara dice          " + (iteration + 1) + " klasa: " + formatDouble2(diceCoefficient(firstIterationList.get(iteration), finalIterationList.get(iteration))));
		}

		// System.out.println("Miara jaccarda: " + jaccardSimilarity(originalClassifies,
		// changedClassifies));
		// System.out.println("Miara dice: " + diceCoefficient(originalClassifies,
		// changedClassifies));

		TextArea ta = new TextArea();
		ta.setMinSize(1200, 800);
		ta.setText(sb.toString());
		gridPane.add(ta, 0, 0);
		Scene scene = new Scene(gridPane, 1200, 800);
		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private static double jaccardSimilarity(List<double[]> a, List<double[]> b) {

		Set<double[]> s1 = new HashSet<double[]>();
		for (int i = 0; i < a.size(); i++) {
			s1.add(a.get(i));
		}
		Set<double[]> s2 = new HashSet<double[]>();
		for (int i = 0; i < b.size(); i++) {
			s2.add(b.get(i));
		}

		final int sa = s1.size();
		final int sb = s2.size();
		s1.retainAll(s2);
		final int intersection = s1.size();
		return 1d / (sa + sb - intersection) * intersection;
	}

	private static double applyOn2Arrays(double[] a, double[] b) {
		double d = 0.0;
		for (int i = 0; i < a.length; i++) {
			d += a[i] + b[i];
		}

		return d;
	}

	public static double diceCoefficient(List<double[]> a, List<double[]> b) {
		Set<Double> nx = new HashSet<>();
		Set<Double> ny = new HashSet<>();

		for (int i = 0; i < a.size() - 1; i++) {
			double[] x1 = a.get(i);
			double[] x2 = a.get(i + 1);
			double tmp = applyOn2Arrays(x1, x2);
			nx.add(tmp);
		}

		for (int i = 0; i < b.size() - 1; i++) {
			double[] x1 = b.get(i);
			double[] x2 = b.get(i + 1);
			double tmp = applyOn2Arrays(x1, x2);
			ny.add(tmp);
		}

		Set<Double> intersection = new HashSet<>(nx);
		intersection.retainAll(ny);
		double totcombigrams = intersection.size();

		return (2 * totcombigrams) / (nx.size() + ny.size());
	}

	private double calculateAvg(int column, int a) {
		double d = 0.0;
		int countPoints = 0;
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).getNewIndex() == column + 1) {
				d += points.get(i).getAxis()[a];
				++countPoints;
			}
		}
		return d / countPoints;
	}

	private void generateRandomNoOfK(int k) {
		int tempValue = rows / k;
		generated = new LinkedHashSet<Integer>();
		while (generated.size() < k) {
			Integer next = rng.nextInt(tempValue) + tempValue * generated.size();
			generated.add(next);
		}
//		generated = new LinkedHashSet<Integer>();
//		generated.add(0);
//		generated.add(1);
//		generated.add(2);
//		generated.forEach(System.out::println);
	}

	private void classify(Stage stage, Double[] values, Integer val, short option) {
		int additionalValue = 1;
		GridPane gridPane = new GridPane();
		initGridPane(gridPane);
		Map<Double, Integer> tempMapWithIndexes = new TreeMap<>();
		for (int i = 0; i < rows; i++) {
			Double d = 0.0;
			switch (option) {
			case 1:
				d = euMetric(values, i);
				break;
			case 2:
				d = manMetric(values, i);
				break;
			case 3:
				d = czeMetric(values, i);
				break;
			case 4:
				d = mahMetric(values, i);
				break;
			default:
				break;
			}
			tempMapWithIndexes.put(d, i);
		}

//		for (Map.Entry<Double, Integer> mapi : tempMapWithIndexes.entrySet()) {
//			System.out.println(mapi.getValue() + "-" + mapi.getKey());
//		}

		int neightbours = val + additionalValue;
		int currObjectCounter = 0;
		Map<Integer, Integer> tempK = new TreeMap<>();
		int toColumn = columns - 1;

		for (Map.Entry<Double, Integer> mapi : tempMapWithIndexes.entrySet()) {
			if (currObjectCounter++ < neightbours) {
				int tempMapVal = (int) Math.round(dataChanged[mapi.getValue()][toColumn]);
				if (tempK.containsKey(tempMapVal)) {
					int newValue = (int) (tempK.get(tempMapVal) + 1.0);
					tempK.put(tempMapVal, newValue);
				} else {
					tempK.put(tempMapVal, 1);
				}
			}
		}

		Entry<Double, Integer> ent = ((TreeMap<Double, Integer>) tempMapWithIndexes).firstEntry();
		System.out.println("Wielkosc mapy = " + tempMapWithIndexes.size());
		System.out.println("Najmniejsza wartość = " + ent);
		System.out.println();
//		for (int j = 0; j < val + additionalValue; j++) {
//			System.out.print(j + " - ");
//			for (int i = 1; i < columns; i++) {
//				System.out.print(tempData[j][i] + " ");
//			}
//			System.out.println();
//		}

		maxClassify = Integer.MIN_VALUE;
		indexClassify = 0;
		tempK.forEach((e, v) -> {
			if (v > maxClassify) {
				maxClassify = v;
				indexClassify = e;
			}
		});

		editedWords.forEach((e, v) -> {
			if (v == indexClassify) {
				System.out.println("Nowa klasyfikacja obiektu to: " + e);
				newClassifiedObject = e;
			}
		});

		Label newClassifyLabel = new Label("Nowy obiekt został zakwalifikowany do " + newClassifiedObject);

		gridPane.add(newClassifyLabel, 0, 0);

		Scene scene = new Scene(gridPane, 300, 300);
		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();

	}

	private void judgeAllNeightboursClassivy(Stage stage, short option) {
		predictionsMap = new TreeMap<>();
		predictionCounter = 0;
		for (int i = 1; i < rows; i++) {
			judgeClassify(stage, i, option);
		}
	}

	private void standJudgeAllNeightboursClassivy(Stage stage, short option) {
		predictionsMap = new TreeMap<>();
		predictionCounter = 0;
		dataAfterStand = new double[rows][columns];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				dataAfterStand[i][j] = data[i][j];

		for (int i = 0; i < columns; i++)
			standarizeColumn(i);

		for (int i = 1; i < rows; i++) {
			judgeClassifyAfterStandarization(stage, i, option);
		}
	}

	private void standarizeColumn(int column) {
		double dev = 0.0;
		double arthAve = 0.0;

		for (int j = 0; j < rows; j++) {
			arthAve += dataAfterStand[j][column];
		}
		arthAve /= rows;

		for (int j = 0; j < rows; j++)
			dev += Math.pow((dataAfterStand[j][column] - arthAve), 2);

		dev = Math.sqrt(dev / rows);

		for (int j = 0; j < rows; j++)
			dataAfterStand[j][column] = (dataAfterStand[j][column] - arthAve) / dev;
	}

	private void judgeClassifyAfterStandarization(Stage stage, Integer val, short option) {
		int additionalValue = 1;
		GridPane gridPane = new GridPane();
		initGridPane(gridPane);
		Map<Double, Integer> tempMapWithIndexes;
		Double[] values = null;
		predictedValues = new int[rows];
		realValues = new int[rows];
		for (int j = 0; j < rows; j++) {
			tempMapWithIndexes = new TreeMap<>();
			values = new Double[columns - 2];
			for (int k = 0; k < columns - 2; k++)
				values[k] = dataAfterStand[j][k + 1];

			for (int i = 0; i < rows; i++) {
				if (i == j)
					continue;
				Double d = 0.0;
				switch (option) {
				case 1:
					d = seuMetric(values, i);
					break;
				case 2:
					d = smanMetric(values, i);
					break;
				case 3:
					d = sczeMetric(values, i);
					break;
				case 4:
					d = smahMetric(values, i);
					break;
				default:
					break;
				}
				tempMapWithIndexes.put(d, i);
			}

			int neightbours = val + additionalValue;
			int currObjectCounter = 0;
			Map<Integer, Integer> tempK = new TreeMap<>();
			int toColumn = columns - 1;

			for (Map.Entry<Double, Integer> mapi : tempMapWithIndexes.entrySet()) {
				if (currObjectCounter++ < neightbours) {
					int tempMapVal = (int) Math.round(dataAfterStand[mapi.getValue()][toColumn]);
					if (tempK.containsKey(tempMapVal)) {
						int newV = (int) (tempK.get(tempMapVal) + 1.0);
						tempK.put(tempMapVal, newV);
					} else {
						tempK.put(tempMapVal, 1);
					}
				}
			}

			maxClassify = Integer.MIN_VALUE;
			indexClassify = 0;
			tempK.forEach((e, v) -> {
				if (v > maxClassify) {
					maxClassify = v;
					indexClassify = e;
				}
			});

			rowInsideLambda = j;
			editedWords.forEach((e, v) -> {
				if (v == indexClassify) {
					// System.out.println("Nowa klasyfikacja obiektu to: " + e + "(" + v + ")");
					newClassifiedObject = e;
					predictedValues[rowInsideLambda] = Integer.valueOf(v);
					realValues[rowInsideLambda] = (int) dataAfterStand[rowInsideLambda][columns - 1];
				}
			});

		}

		int correctPredictions = 0;
		for (int r = 0; r < predictedValues.length; r++) {
			if (predictedValues[r] == realValues[r])
				correctPredictions++;
			// System.out.println(predictedValues[r] + "-" + realValues[r]);
		}

		double classifyAsse = ((double) correctPredictions / (double) (rows)) * 100;
		System.out.println("Ocena klasyfikacji=" + formatDouble(classifyAsse) + "% dla ilości sąsiadów=" + val);
		predictionsMap.put(++predictionCounter, BigDecimal.valueOf(classifyAsse).setScale(5, RoundingMode.HALF_UP).doubleValue());

	}

	private void judgeClassify(Stage stage, Integer val, short option) {

		int additionalValue = 1;
		GridPane gridPane = new GridPane();
		initGridPane(gridPane);
		Map<Double, Integer> tempMapWithIndexes;
		Double[] values = null;
		predictedValues = new int[rows];
		realValues = new int[rows];
		tempMapWithAllValues = new TreeMap<>();
		for (int j = 0; j < rows; j++) {
			tempMapWithIndexes = new TreeMap<>();
			values = new Double[columns - 2];
			for (int k = 0; k < columns - 2; k++)
				values[k] = dataChanged[j][k + 1];
			for (int i = 0; i < rows; i++) {
				if (i == j)
					continue;
				Double d = 0.0;
				switch (option) {
				case 1:
					d = euMetric(values, i);
					break;
				case 2:
					d = manMetric(values, i);
					break;
				case 3:
					d = czeMetric(values, i);
					break;
				case 4:
					d = mahMetric(values, i);
					break;
				default:
					break;
				}
				tempMapWithIndexes.put(d, i);
			}

			int neightbours = val + additionalValue;
			int currObjectCounter = 0;
			Map<Integer, Integer> tempK = new TreeMap<>();
			int toColumn = columns - 1;

			for (Map.Entry<Double, Integer> mapi : tempMapWithIndexes.entrySet()) {
				if (currObjectCounter++ < neightbours) {
					int tempMapVal = (int) Math.round(dataChanged[mapi.getValue()][toColumn]);
					if (tempK.containsKey(tempMapVal)) {
						int newV = (int) (tempK.get(tempMapVal) + 1.0);
						tempK.put(tempMapVal, newV);
					} else {
						tempK.put(tempMapVal, 1);
					}
				}
			}

			maxClassify = Integer.MIN_VALUE;
			indexClassify = 0;
			tempK.forEach((e, v) -> {
				if (v > maxClassify) {
					maxClassify = v;
					indexClassify = e;
				}
			});

			rowInsideLambda = j;
			editedWords.forEach((e, v) -> {
				if (v == indexClassify) {
					// System.out.println("Nowa klasyfikacja obiektu to: " + e + "(" + v + ")");
					newClassifiedObject = e;
					predictedValues[rowInsideLambda] = Integer.valueOf(v);
					realValues[rowInsideLambda] = (int) dataChanged[rowInsideLambda][columns - 1];
				}
			});

		}

		int correctPredictions = 0;
		for (int r = 0; r < predictedValues.length; r++) {
			if (predictedValues[r] == realValues[r])
				correctPredictions++;
			// System.out.println(predictedValues[r] + "-" + realValues[r]);
		}

		double classifyAsse = ((double) correctPredictions / (double) (rows)) * 100;
		System.out.println("Ocena klasyfikacji=" + formatDouble(classifyAsse) + "% dla ilości sąsiadów=" + val);
		if (predictionsMap != null)
			predictionsMap.put(++predictionCounter, BigDecimal.valueOf(classifyAsse).setScale(5, RoundingMode.HALF_UP).doubleValue());

	}

	public Double euMetric(Double[] values, int row) {
		Double val = 0.0;
		for (int i = 0; i < values.length; i++) {
			val += Math.pow(values[i] - dataChanged[row][i + 1], 2);
//			System.out.println("Euclides: "+values[i]+"----"+dataChanged[row][i + 1]);
		}
		return Math.sqrt(val);
	}

	public void euMetricGrouping(Double[] values, int k, List<Integer> generatedValues, int v) {
		Double val = 0.0;
		Double min = Double.MAX_VALUE;
		int index = 0;
		for (int j = 0; j < k; j++) {
			val = 0.0;
			int currentRow = generatedValues.get(j);
			for (int i = 0; i < values.length; i++) {
				val += Math.pow(values[i] - points.get(currentRow).getAxis()[i], 2);
				// System.out.print(points.get(currentRow).getAxis()[i] + " ");
			}
			if (val < min) {
				min = val;
				index = j;
				points.get(v).setMinDistance(Math.sqrt(min));
				points.get(v).setNewIndex(index + 1);
				// System.out.println(points.get(v).toString());

			}
			// System.out.println("Min w srodku: " + formatDouble(val) + " dla wiersza: " +
			// (currentRow + 1));
		}
//		System.out.println("Min po liczeniu: " + formatDouble(min) + ". Nowa klasa to: " + (index + 1) + ", stara to: " + points.get(v).getOldIndex());
	}

	public void euMetricGroupingAgain(Double[] values, Double[][] values2, int k, List<Integer> generatedValues, int v) {
		Double val = 0.0;
		Double min = Double.MAX_VALUE;
		int index = 0;
		int oldIndex = 0;
		oldIndex = points.get(v).getNewIndex();
		for (int j = 0; j < k; j++) {
			val = 0.0;
			for (int i = 0; i < values.length; i++) {
				val += Math.pow(values[i] - values2[j][i], 2);
				// System.out.print(formatDouble(values2[j][i]) + " ");
			}
			if (val < min) {
				min = val;
				index = j;
				points.get(v).setMinDistance(Math.sqrt(min));
				points.get(v).setNewIndex(index + 1);
				// System.out.println(points.get(v).toString());

			}
			// System.out.println("Min w srodku: " + formatDouble(val) + " dla wiersza: " +
			// j);
		}
//		System.out.println("Min po liczeniu: " + formatDouble(min) + ". Nowa klasa to: " + points.get(v).getNewIndex() + ", stara to: " + oldIndex);
		tempGroupingListWithIndexes.add(points.get(v).getNewIndex());
	}

	public void manMetricGrouping(Double[] values, int k, List<Integer> generatedValues, int v) {
		Double val = 0.0;
		Double min = Double.MAX_VALUE;
		int index = 0;
		for (int j = 0; j < k; j++) {
			val = 0.0;
			int currentRow = generatedValues.get(j);
			for (int i = 0; i < values.length; i++) {
				val += Math.abs(values[i] - points.get(currentRow).getAxis()[i]);
				// System.out.print(points.get(currentRow).getAxis()[i] + " ");
			}
			if (val < min) {
				min = val;
				index = j;
				points.get(v).setMinDistance(min);
				points.get(v).setNewIndex(index + 1);
				// System.out.println(points.get(v).toString());

			}
			// System.out.println("Min w srodku: " + formatDouble(val) + " dla wiersza: " +
			// (currentRow + 1));
		}
//		System.out.println("Min po liczeniu: " + formatDouble(min) + ". Nowa klasa to: " + (index + 1) + ", stara to: " + points.get(v).getOldIndex());
	}

	public void manMetricGroupingAgain(Double[] values, Double[][] values2, int k, List<Integer> generatedValues, int v) {
		Double val = 0.0;
		Double min = Double.MAX_VALUE;
		int index = 0;
		int oldIndex = 0;
		oldIndex = points.get(v).getNewIndex();
		for (int j = 0; j < k; j++) {
			val = 0.0;
			for (int i = 0; i < values.length; i++) {
				val += Math.abs(values[i] - values2[j][i]);
				// System.out.print(formatDouble(values2[j][i]) + " ");
			}
			if (val < min) {
				min = val;
				index = j;
				points.get(v).setMinDistance(min);
				points.get(v).setNewIndex(index + 1);
				// System.out.println(points.get(v).toString());

			}
			// System.out.println("Min w srodku: " + formatDouble(val) + " dla wiersza: " +
			// j);
		}
//		System.out.println("Min po liczeniu: " + formatDouble(min) + ". Nowa klasa to: " + points.get(v).getNewIndex() + ", stara to: " + oldIndex);
		tempGroupingListWithIndexes.add(points.get(v).getNewIndex());
	}

	public Double manMetric(Double[] values, int row) {
		Double val = 0.0;
		for (int i = 0; i < values.length; i++) {
			val += Math.abs(values[i] - dataChanged[row][i + 1]);
//			System.out.println("Man: "+values[i]+"----"+dataChanged[row][i + 1]);
		}
		return val;
	}

	public void czeMetricGrouping(Double[] values, int k, List<Integer> generatedValues, int v) {
		Double val = 0.0;
		Double min = Double.MAX_VALUE;
		int index = 0;
		for (int j = 0; j < k; j++) {
			val = 0.0;
			int currentRow = generatedValues.get(j);
			for (int i = 0; i < values.length; i++) {
				Double d = Math.abs(values[i] - points.get(currentRow).getAxis()[i]);
				if (val < d) {
					val += d;
				}
				// System.out.print(points.get(currentRow).getAxis()[i] + " ");
			}
			if (val < min) {
				min = val;
				index = j;
				points.get(v).setMinDistance(min);
				points.get(v).setNewIndex(index + 1);
				// System.out.println(points.get(v).toString());

			}
			// System.out.println("Min w srodku: " + formatDouble(val) + " dla wiersza: " +
			// (currentRow + 1));
		}
//		System.out.println("Min po liczeniu: " + formatDouble(min) + ". Nowa klasa to: " + (index + 1) + ", stara to: " + points.get(v).getOldIndex());
	}

	public void czeMetricGroupingAgain(Double[] values, Double[][] values2, int k, List<Integer> generatedValues, int v) {
		Double val = 0.0;
		Double min = Double.MAX_VALUE;
		int index = 0;
		int oldIndex = 0;
		oldIndex = points.get(v).getNewIndex();
		for (int j = 0; j < k; j++) {
			val = 0.0;
			for (int i = 0; i < values.length; i++) {
				Double d = Math.abs(values[i] - values2[j][i]);
				if (val < d) {
					val += d;
				}
				// System.out.print(formatDouble(values2[j][i]) + " ");
			}
			if (val < min) {
				min = val;
				index = j;
				points.get(v).setMinDistance(min);
				points.get(v).setNewIndex(index + 1);
				// System.out.println(points.get(v).toString());

			}
			// System.out.println("Min w srodku: " + formatDouble(val) + " dla wiersza: " +
			// j);
		}
//		System.out.println("Min po liczeniu: " + formatDouble(min) + ". Nowa klasa to: " + points.get(v).getNewIndex() + ", stara to: " + oldIndex);
		tempGroupingListWithIndexes.add(points.get(v).getNewIndex());
	}

	public Double czeMetric(Double[] values, int row) {
		Double val = 0.0;
		for (int i = 0; i < values.length; i++) {
			Double d = Math.abs(values[i] - dataChanged[row][i + 1]);
//			System.out.println("Cze: "+values[i]+"----"+dataChanged[row][i + 1]);
			if (val < d) {
				val += d;
			}
		}
		return val;
	}

	public Double mahMetric(Double[] values, int row) {
		return 0.0;
	}

	public Double seuMetric(Double[] values, int row) {
		Double val = 0.0;
		for (int i = 0; i < values.length; i++) {
			val += Math.pow(values[i] - dataAfterStand[row][i + 1], 2);
		}
		return Math.sqrt(val);
	}

	public Double smanMetric(Double[] values, int row) {
		Double val = 0.0;
		for (int i = 1; i < values.length; i++) {
			val += Math.abs(values[i - 1] - dataAfterStand[row][i]);
		}
		return val;
	}

	public Double sczeMetric(Double[] values, int row) {
		Double val = 0.0;
		for (int i = 1; i < values.length; i++) {
			Double d = Math.abs(values[i - 1] - dataAfterStand[row][i - 1]);
			if (val < d) {
				val += d;
			}
		}
		return val;
	}

	public Double smahMetric(Double[] values, int row) {
		return Math.sqrt(0.0);
	}

	public void initHBox(HBox hBox) {
		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setSpacing(10);
	}

	public void initVBox(VBox vBox) {
		vBox.setPadding(new Insets(15, 12, 15, 12));
		vBox.setSpacing(10);
	}

	public void loadFile(Stage stage, BorderPane borderPane) {
		columns = 0;
		rows = 0;
		editedText = "";
		notEditedText = "";
		columnNames = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		// Set to user directory or go to default if cannot access
		String userDirectoryString = System.getProperty("user.dir");
		File userDirectory = new File(userDirectoryString);
		if (!userDirectory.canRead()) {
			userDirectory = new File("c:/Users/Kamil/Desktop/Semestr 2/Systemy wspomagania decyzji");
		}
		fileChooser.setInitialDirectory(userDirectory);
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			fileNameToModul5 = selectedFile.getName().toString();
			try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
				String line;
				String[] words = null;
				allLines = new ArrayList<>();
				isAmountOfColumnsChecked = false;
				while ((line = reader.readLine()) != null) {
					notEditedText += line + "\n";
					if (line.startsWith("#") || line.trim().isEmpty())
						continue;
					if (line.matches("^[A-Z].*$") || line.matches("^[a-z].*$")) {
						String[] names = line.split("\\s*(\\s|=>|;)\\s*");
						for (int i = 0; i < names.length; i++) {
							columnNames.add(names[i]);
						}
						continue;
					}
					if (!isAmountOfColumnsChecked) {
						words = line.split("\\s*(\\s|=>|;)\\s*");
						columns = words.length + 1;
						totalColumns = columns;
						isAmountOfColumnsChecked = true;
					}
					line = line.replace(",", ".");
					editedText += line + "\n";
					allLines.add(line);
					rows++;
				}
				leftTextArea.setText(notEditedText);
				parseFile(stage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void parseFile(Stage stage) {
		finalColumnNames = new LinkedHashSet<>();
		listWithSavedColumnNumber = new ArrayList<>();
		anotherCounterGlobal = 0;
		sb = new StringBuilder();
		int columnCounter = 1;
		int mapCounter = 1;
		String[] words = null;
		normalColumns = 0;
		data = new double[rows][columns];
		boolean isSavedColumn = false;
		editedWordsSet = new TreeMap<>();
		Map<String, Integer> newTempMap;
		int anotherCounter = 0;
		if (isABC) {

			for (int i = 0; i < columns; i++) {
				editedWords = new TreeMap<>();
				for (int j = 0; j < rows; j++) {
					if (i > 0) {
						words = allLines.get(j).split("\\s*(\\s|=>|;)\\s*");
						String item = words[i - 1];
						if (item.matches("^[A-Z].*$") || item.matches("^[a-z].*$") || (i == (columns - 1))) {
							if (editedWords.containsKey(item)) {
								item = String.valueOf(editedWords.get(item));
								finalColumnNames.add(item);
							} else {
								editedWords.put(item, mapCounter++);
								finalColumnNames.add(item);
								item = String.valueOf(editedWords.get(item));
							}
						}
					}
				}
			}

			int tempCounter = 1;
			for (Map.Entry<String, Integer> map : editedWords.entrySet()) {
				editedWordsSet.put(anotherCounter, editedWords);
				editedWords.put(map.getKey(), tempCounter++);
				anotherCounter++;
			}

			for (int i = 0; i < columns; i++) {
				newTempMap = new LinkedHashMap<>();
				isSavedColumn = false;
				for (int j = 0; j < rows; j++) {
					if (i == 0) {
						data[j][i] = BigDecimal.valueOf(columnCounter++).setScale(0, RoundingMode.CEILING).doubleValue();
					} else {
						words = allLines.get(j).split("\\s*(\\s|=>|;)\\s*");
						String item = words[i - 1];
						if (item.startsWith(".")) {
							item = "0" + item;
						} else if (item.matches("^[A-Z].*$") || item.matches("^[a-z].*$") || (i == (columns - 1))) {
							if (editedWords.containsKey(item)) {
								item = String.valueOf(editedWords.get(item));
								finalColumnNames.add(item);
							} else {
								editedWordsSet.put(anotherCounter, newTempMap);
//								for (Entry<Integer, Map<String, Integer>> map : editedWordsSet.entrySet()) {
//								System.out.println(map.getKey() + "-" + map.getValue() + "\n");
//							}
								finalColumnNames.add(item);

								editedWords.put(item, mapCounter++);
								item = String.valueOf(editedWords.get(item));
							}
							if (!isSavedColumn) {
								isSavedColumn = !isSavedColumn;
								listWithSavedColumnNumber.add(i);

							}
						}
						data[j][i] = Double.parseDouble(item);
					}
				}
			}

		} else {

			for (int i = 0; i < columns; i++) {
				editedWords = new LinkedHashMap<>();
				mapCounter = 1;
				isSavedColumn = false;
				for (int j = 0; j < rows; j++) {
					if (i == 0) {
						data[j][i] = BigDecimal.valueOf(columnCounter++).setScale(0, RoundingMode.CEILING).doubleValue();
					} else {
						words = allLines.get(j).split("\\s*(\\s|=>|;)\\s*");
						String item = words[i - 1];
						if (item.startsWith(".")) {
							item = "0" + item;
						} else if (item.matches("^[A-Z].*$") || item.matches("^[a-z].*$") || (i == (columns - 1))) {
							if (editedWords.containsKey(item)) {
								item = String.valueOf(editedWords.get(item));
							} else {
								editedWordsSet.put(anotherCounter, editedWords);
								editedWords.put(item, mapCounter++);

								item = String.valueOf(editedWords.get(item));
								finalColumnNames.add(words[i - 1]);
							}
							if (!isSavedColumn) {
								isSavedColumn = !isSavedColumn;
								listWithSavedColumnNumber.add(i);
								anotherCounter++;

							}
						}
						data[j][i] = Double.parseDouble(item);
					}
				}
			}
		}

		obsList = FXCollections.observableArrayList(data);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				sb.append(data[i][j] + "     ");
			}
			sb.append("\n");
		}

		dataChanged = data;

		// listWithSavedColumnNumber.forEach(System.out::println);
		// System.out.println(rows);

//		for (Entry<Integer, Map<String, Integer>> map : editedWordsSet.entrySet()) {
//			System.out.println(map.getKey() + "-" + map.getValue() + "\n");
//		}

		initLeftTable(stage);
	}

	public void initLeftTable(Stage stage) {
		rightTableView.getColumns().setAll(createColumns());
		rightTableView.setItems(obsList);
	}

	public List<TableColumn<double[], Double>> createColumns() {
		return IntStream.range(0, columns).mapToObj(this::createColumn).collect(Collectors.toList());
	}

	public TableColumn<double[], Double> createColumn(int c) {
		TableColumn<double[], Double> col = null;
		if (c == 0) {
			col = new TableColumn<>("#");
			col.setCellFactory(column -> {
				return new TableCell<double[], Double>() {
					@Override
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setText("");
						} else {
							int val = (int) Math.round(item);
							setText("" + val);
						}
					}
				};
			});

		} else if (listWithSavedColumnNumber.contains(c)) {
			// System.out.println(c);
			String text = editedWordsSet.get(anotherCounterGlobal++) + "\n";
			col = new TableColumn<>("       C" + (c) + "\n" + text + "");
			col.setCellFactory(column -> {
				return new TableCell<double[], Double>() {

					@Override
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setText("");
						} else {
							int val = (int) Math.round(item);
							setText("" + val);
						}
					}

				};
			});
		} else {
			col = new TableColumn<>("C" + (c));
		}
		col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[c]));
		col.setMinWidth(75);
		col.setStyle("-fx-alignment: CENTER;");
		return col;
	}

	public TableColumn<double[], Double> createAdditionalColumn(String name, int c) {
		TableColumn<double[], Double> col = new TableColumn<>(name);
		col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[c]));
		col.setMinWidth(75);
		col.setStyle("-fx-alignment: CENTER;");
		// System.out.println("C=" + c);
		return col;
	}

	public TableColumn<double[], Double> createAdditionalColumnWithColor(String name, int c, int column2) {
		TableColumn<double[], Double> col = new TableColumn<>(name);
		col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[c]));
		col.setCellFactory(column -> {
			return new TableCell<double[], Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText("");
					} else {
						setText("" + item);
						setTextFill(Color.BLACK);
						this.setStyle("-fx-background-color: default;-fx-alignment: CENTER;");
						for (int i = 0; i < listMinValues.get(column2).getList().size(); i++) {
							if (item.doubleValue() == listMinValues.get(column2).getList().get(i)) {
								setTextFill(Color.WHITE);
								this.setStyle("-fx-background-color: blue;-fx-alignment: CENTER;");
							}
						}
						for (int i = 0; i < listMaxValues.get(column2).getList().size(); i++) {
							if (item.doubleValue() == listMaxValues.get(column2).getList().get(i)) {
								setTextFill(Color.WHITE);
								this.setStyle("-fx-background-color: red;-fx-alignment: CENTER;");
							}
						}
					}
				}
			};
		});
		col.setMinWidth(75);
		col.setStyle("-fx-alignment: CENTER;");
		// System.out.println("C=" + c);
		return col;
	}

	public static String parseD(double num) {
		if ((int) num == num)
			return Integer.toString((int) num);
		return String.valueOf(num);
	}

}
