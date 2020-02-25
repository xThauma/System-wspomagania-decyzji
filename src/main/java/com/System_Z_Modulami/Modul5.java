package com.System_Z_Modulami;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Modul5 {

	private double[][] originalData;
	private double[][] originalDataWithCuts;
	private double[][] originalSavedData;
	private double[][] tempData;
	private int totalColumns, totalRows, savedRows;
	private List<String> columnNames;
	private Map<Integer, Set<Integer>> map;
	private Map<Integer, Set<Integer>> mapWithIndexes;
	private List<String> finalColumnNames;
	private boolean isFinished = false;
	Map<Integer, Map<Integer, Double>> values;
	private int globalCounter = 0;
	private int globalCounter2 = 0;
	private Map<Integer, List<Double>> valsToSkip;
	private StringBuilder sb;
	private int numOfCuts = 0;
	Map<Integer, List<String>> mapToCSV;
	private String fileName;
	private String newClassifiedObject = "";
	private boolean isSkipping = true;

	private int deletedOnes = 0;

	private Map<Integer, List<Integer>> extraValsToRemove;

	public void initModul5(double[][] originalData, List<String> columnNames, int totalColumns, int totalRows, Set<String> finalColumnNames, String fileName) throws Exception {
		this.originalData = originalData;
		this.columnNames = columnNames;
		this.totalColumns = totalColumns;
		this.totalRows = totalRows;
		this.savedRows = totalRows;
		this.fileName = fileName;
		valsToSkip = new TreeMap<>();
		this.finalColumnNames = new ArrayList<>();
		this.extraValsToRemove = new TreeMap<>();
		mapToCSV = new TreeMap<>();
		this.sb = new StringBuilder();
		for (String s : finalColumnNames) {
			this.finalColumnNames.add(s);
		}

		for (int i = 1; i < totalColumns - 1; i++) {
			List<Double> temp = new ArrayList<>();
			valsToSkip.put(i, temp);
		}

		originalDataWithCuts = getFreshData2(originalData);
//		printTable2(originalDataWithCuts);

		sb.append("Wiersze: " + totalRows + ", kolumny: " + (totalColumns - 1));
		System.out.println("Wiersze: " + totalRows + ", kolumny: " + (totalColumns - 1));
		System.out.print("Nazwy kolumn: ");
		sb.append("\nNazwy kolumn: ");

		Arrays.asList(columnNames).forEach(e -> {
			System.out.print(e);
			sb.append(e);
		});
		sb.append("\n");
		System.out.println();
		Stage stage = new Stage();
		stage.setTitle("Moduł 5");
		System.out.println("\nNieposortowane dane");
		values = new TreeMap<>();
		originalSavedData = getFreshData(originalData);
		tempData = getFreshData(originalData);
//		printTable(tempData);
//		removeDuplicates();
//		tempData = getFreshData(originalData);
//		System.out.println("\nPo skasowaniu duplikatów");
//		printTable(tempData);

		while (!isFinished) {
//			System.out.println("Ilość wierszy: " + originalData.length);
			if (originalData.length <= 1)
				isFinished = true;
			map = new TreeMap<>();
			for (int j = 1; j < totalColumns - 1; j++) {
//				System.out.println("\nSort by: " + j + " column");
				tempData = getFreshData(originalData);
				sortbyColumn(tempData, j);
//				printTable(tempData);
				cutData(tempData, j);
			}
			int max = Integer.MIN_VALUE;
			for (Entry<Integer, Set<Integer>> tmp : map.entrySet()) {
				if (tmp.getValue().size() >= max)
					max = tmp.getValue().size();
			}
			int counter = 0;
			numOfCuts++;
			sb.append("\nUcinanie: " + max + " elementów. Cięcie nr: " + numOfCuts);
			System.out.println("Ucinanie: " + max + " elementów. Cięcie nr: " + numOfCuts);

			for (Entry<Integer, Set<Integer>> tmp : map.entrySet()) {
				if (tmp.getValue().size() == max)
					for (Integer list : tmp.getValue()) {
						// System.out.println("Lista:" + list);
						if (counter++ == tmp.getValue().size() - 1) {
							for (int k = 0; k < originalData.length; k++) {
								if (originalData[k][0] == (double) list) {
									if (columnNames.size() < 1) {
										sb.append("\nKolumna: nazwa nie znana, wartość: " + originalData[k][tmp.getKey()]);
									} else
										sb.append("\nKolumna: " + columnNames.get(tmp.getKey() - 1) + ", wartość: " + originalData[k][tmp.getKey()]);
									Map<Integer, Double> values2 = new TreeMap<>();
									values2.put(tmp.getKey(), originalData[k][tmp.getKey()]);
									values.put(globalCounter++, values2);
								}
							}

						}
						originalDataWithCuts[list - 1][totalColumns] = numOfCuts;
						originalData = removerow(originalData, list);

					}
			}
//			int max2 = Integer.MIN_VALUE;
//			for (Entry<Integer, List<Integer>> tmp : extraValsToRemove.entrySet()) {
//				if (tmp.getValue().size() >= max2)
//					max2 = tmp.getValue().size();
//			}
//			for (Entry<Integer, List<Integer>> tmp : extraValsToRemove.entrySet()) {
//				if (tmp.getValue().size() == max2)
//					for (Integer list : tmp.getValue())
//						originalData = removerow2(originalData, list);
//			}

		}
		sb.append("\n" + printTableWithout0(originalDataWithCuts));

		System.out.println("@RELATION " + fileName.substring(0, fileName.length() - 4));
		for (int i = 0; i < columnNames.size(); i++) {

			if (i == columnNames.size() - 1) {
				System.out.print("@ATTRIBUTE class {");
				for (String ss : finalColumnNames) {
					System.out.print(ss + ",");
				}
				System.out.println("}");
			} else {
				System.out.println("@ATTRIBUTE " + columnNames.get(i) + " REAL");
			}
		}
		System.out.println("\n@DATA");
		mapToCSV.forEach((e, v) -> {
			System.out.println(v);
		});

		System.out.println("%");
		System.out.println("%");
		System.out.println("%");

//		if (totalColumns - 2 == 2)
//			make2dScatterChart(stage, "Wykres 2D");
		saveCSVFile();
		saveARFfile();

		System.out.println(deletedOnes);
		BorderPane bp = new BorderPane();
		GridPane gridPane = new GridPane();
		int count = 0;
		Label newLabel = new Label("Klasyfikacja");
		int x = totalColumns - 2;
		DataUtils.initNode(newLabel);
		gridPane.add(newLabel, 0, 0);
		TextField[] vals = new TextField[x];
		for (int i = 0; i < x; i++) {
			TextField pointValueTextField = new TextField("");
			vals[i] = pointValueTextField;
			pointValueTextField.setPromptText("Kolumna " + (i + 1));
			DataUtils.initNode(pointValueTextField);
			gridPane.add(pointValueTextField, ++count, 0);
		}
		Button b = new Button("Skalsyfikuj");
		DataUtils.initNode(b);
		gridPane.add(b, ++count, 0);

		b.setOnAction(e -> {
			double[] classifyValues = new double[x];
			for (int i = 0; i < x; i++)
				classifyValues[i] = Double.parseDouble(vals[i].getText().toString());
			classifyNewObject(classifyValues, stage);
		});

		bp.setTop(gridPane);
		VBox vBox = new VBox();
		TextArea ta = new TextArea(sb.toString());
		ta.setMinSize(500, 800);

		vBox.getChildren().add(ta);
		bp.setCenter(vBox);

		stage.setScene(new Scene(bp, 1000, 1000));
		stage.setX(0);
		stage.setY(0);
		stage.show();
	}

	private void classifyNewObject(double[] classifyValues, Stage stage) {
		System.out.println("Przekazane wartości: ");
		for (int i = 0; i < classifyValues.length; i++) {
			System.out.println("Kolumna nr: " + i + ": " + classifyValues[i]);
		}

		int tempCut = 0;
		for (Entry<Integer, Map<Integer, Double>> tmp : values.entrySet()) {
			System.out.println("Ieracja: " + tmp.getKey());
			for (Entry<Integer, Double> tmp2 : tmp.getValue().entrySet()) {
				System.out.println("Kolumna: " + tmp2.getKey() + ", cięcie: " + tmp2.getValue());
				if ((Double) classifyValues[tmp2.getKey() - 1] >= tmp2.getValue()) {
					tempCut = tmp.getKey() + 1;
				}
			}
		}

		newClassifiedObject += "Nowy obiekt ma wektor: (";
		for (int k = 1; k < tempCut; k++) {
			newClassifiedObject += "0";
		}
		newClassifiedObject += "1";

		for (int k = (int) tempCut; k < numOfCuts; k++) {
			newClassifiedObject += "0";
		}
		newClassifiedObject += "), a jego klasa to: ";

		Map<Integer, Map<String, Integer>> tempMap = new TreeMap<>();

		for (Entry<Integer, List<String>> tmp : mapToCSV.entrySet()) {
			for (int i = 0; i < tmp.getValue().size() - 1; i++) {
				if (tempMap.containsKey(i)) {
					Map<String, Integer> tamp = tempMap.get(i);
					tamp.put(tmp.getValue().get(tmp.getValue().size() - 1), tamp.get(tmp.getValue().get(tmp.getValue().size() - 1)) + 1);
				} else {
					Map<String, Integer> tamp = new TreeMap<>();
					tamp.put(tmp.getValue().get(tmp.getValue().size() - 1), 1);
					tempMap.put(i, tamp);
				}
			}

		}

		tempMap.forEach((e, v) -> {
			System.out.println(e + "-");
			v.forEach((d, f) -> {
				System.out.println(d + "-" + f);
			});
		});
		VBox vBox = new VBox();
		Label ta = new Label(newClassifiedObject.toString());
		DataUtils.initNode(ta);

		vBox.getChildren().add(ta);

		stage.setScene(new Scene(vBox, 100, 100));
		stage.setX(0);
		stage.setY(0);
		stage.show();

	}

	private void saveCSVFile() {
		try (PrintWriter writer = new PrintWriter(new File(fileName.substring(0, fileName.length() - 4) + ".csv"))) {

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < numOfCuts; i++) {
				sb.append("Cięcie nr: " + (i + 1));
				sb.append(',');
			}
			sb.append("Klasa");
			sb.append('\n');

			for (Entry<Integer, List<String>> tmp : mapToCSV.entrySet()) {
				for (int i = 0; i < tmp.getValue().size(); i++) {
					sb.append(tmp.getValue().get(i));
					sb.append(',');
				}
				sb.append('\n');
			}

			writer.write(sb.toString());

			System.out.println("Converting to CSV done");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	private void saveARFfile() {
		try (PrintWriter writer = new PrintWriter(new File(fileName.substring(0, fileName.length() - 4) + ".arff"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("@RELATION " + fileName.substring(0, fileName.length() - 4) + "\n");
			for (int i = 0; i < numOfCuts; i++) {
					sb.append("@ATTRIBUTE " + i + " NUMERIC\n");
			}
			sb.append("\n@DATA\n");
			mapToCSV.forEach((e, v) -> {
				System.out.println(v);
			});

			for (Entry<Integer, List<String>> tmp : mapToCSV.entrySet()) {
				for (int i = 0; i < tmp.getValue().size(); i++) {
					sb.append(tmp.getValue().get(i));
					if (i < tmp.getValue().size() - 1)
						sb.append(",");
				}
				sb.append('\n');
			}

			sb.append("%\n");
			sb.append("%\n");
			sb.append("%\n");
			writer.write(sb.toString());

			System.out.println("Converting to CSV done");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	private void make2dScatterChart(Stage stage, String text) {
		double minY, minX, maxY, maxX;

		double oldMin = Double.MAX_VALUE;
		double oldMax = Double.MIN_VALUE;
		for (int j = 0; j < savedRows; j++) {
			if (originalSavedData[j][1] > oldMax)
				oldMax = originalSavedData[j][1];
			if (originalSavedData[j][1] < oldMin)
				oldMin = originalSavedData[j][1];
		}

		minX = oldMin;
		maxX = oldMax;

		final NumberAxis xAxis = new NumberAxis(oldMin - oldMin * 0.1, oldMax + oldMax * 0.1, (oldMax - oldMin) / 100);

		oldMin = Double.MAX_VALUE;
		oldMax = Double.MIN_VALUE;
		for (int j = 0; j < savedRows; j++) {
			if (originalSavedData[j][2] > oldMax)
				oldMax = originalSavedData[j][2];
			if (originalSavedData[j][2] < oldMin)
				oldMin = originalSavedData[j][2];
		}

		final NumberAxis yAxis = new NumberAxis(oldMin - oldMin * 0.1, oldMax + oldMax * 0.1, (oldMax - oldMin) / 100);
		if (columnNames.isEmpty()) {
			xAxis.setLabel("X");
			yAxis.setLabel("Y");
		} else {
			xAxis.setLabel(columnNames.get(0));
			yAxis.setLabel(columnNames.get(1));
		}

		minY = oldMin;
		maxY = oldMax;

		final ScatterChart<Number, Number> bc = new ScatterChart<Number, Number>(xAxis, yAxis);

		bc.setTitle("System wspomagania danych (" + text + "), Plik: " + fileName.substring(0, fileName.length() - 4));
		globalCounter = 1;

		int max = Integer.MIN_VALUE;
		for (int i = 0; i < savedRows; i++) {
			if (originalSavedData[i][totalColumns - 1] > max)
				max = (int) originalSavedData[i][totalColumns - 1];
		}

		for (int i = 1; i <= max; i++) {
			makeScatter(bc, i);
		}

		Scene scene = new Scene(bc, 900, 800);

		for (Entry<Integer, Map<Integer, Double>> tmp : values.entrySet()) {
			for (Entry<Integer, Double> tmp2 : tmp.getValue().entrySet()) {
				makeScatterStrightLine(bc, tmp2.getKey(), tmp2.getValue(), (oldMax - oldMin) / 100);
			}
		}

		Stage newWindow = new Stage();
		newWindow.setTitle("Wykres");
		newWindow.setScene(scene);
		newWindow.setX(0);
		newWindow.setY(0);

		newWindow.show();
	}

	private void makeScatter(ScatterChart<Number, Number> sc, int i) {
		XYChart.Series series = new XYChart.Series();
		if (!finalColumnNames.isEmpty())
			series.setName(finalColumnNames.get(i - 1));
		for (int j = 0; j < savedRows; j++) {
			if (originalSavedData[j][totalColumns - 1] == (double) i)
				series.getData().add(new XYChart.Data(originalSavedData[j][1], originalSavedData[j][2]));
		}
		sc.getData().add(series);

	}

	private void makeScatterStrightLine(ScatterChart<Number, Number> sc, int pos, double val, double step) {
		XYChart.Series series = new XYChart.Series();

		int max = 40;

		if (pos == 1) {
			if (!columnNames.isEmpty())
				series.setName("Cięcie nr: " + (globalCounter++) + "(" + val + ") po X (" + columnNames.get(0) + ")");
			else
				series.setName("Cięcie nr: " + (globalCounter++) + "(" + val + ") po X");
			for (double j = 0; j < max; j = j + step) {
				series.getData().add(new XYChart.Data(val, j));
			}
		} else if (pos == 2) {
			if (!columnNames.isEmpty())
				series.setName("Cięcie nr: " + (globalCounter++) + "(" + val + ") po Y (" + columnNames.get(1) + ")");
			else
				series.setName("Cięcie nr: " + (globalCounter++) + "(" + val + ") po Y");
			for (double j = 0; j < max; j = j + step) {
				series.getData().add(new XYChart.Data(j, val));
			}
		}

		sc.getData().add(series);

	}

	public void printTable(double[][] data) {
		System.out.print("\n");
		for (int i = 0; i < totalRows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				System.out.print(data[i][j] + " ");
			}
			System.out.print("\n");
		}
	}

	public String printTableWithout0(double[][] data) {
		String s = "";
//		System.out.print("\n");
		for (int i = 0; i < data.length; i++) {
//			if (data[i][totalColumns] == 0.0)
//				continue;
			List<String> newList = new ArrayList<>();
			for (int j = 1; j < data[0].length; j++) {
				if (j == data[0].length - 2) {
					s += finalColumnNames.get((int) data[i][j] - 1) + " ";
				} else if (j == data[0].length - 1) {
					s += " (";
					for (int k = 1; k < data[i][j]; k++) {
						s += "0";
						newList.add("0");
					}
					if (data[i][totalColumns] != 0.0) {
						s += "1";
						newList.add("1");
					} else if (data[i][totalColumns] == 0.0) {
						deletedOnes++;
					}

					for (int k = (int) data[i][j]; k < numOfCuts; k++) {
						s += "0";
						newList.add("0");
					}
					s += ")";
					newList.add(finalColumnNames.get((int) data[i][j - 1] - 1));
					mapToCSV.put(i, newList);
				} else
					s += data[i][j] + " ";

//				System.out.print(data[i][j] + " ");
			}
//			System.out.print("\n");
			s += "\n";
		}
		return s;

	}

	private double[][] getFreshData2(double[][] data) {
		double[][] tempData = new double[(int) totalRows][(int) totalColumns + 1];
		for (int i = 0; i < totalRows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				tempData[i][j] = data[i][j];
			}
		}
		return tempData;
	}

	private double[][] getFreshData(double[][] data) {
		double[][] tempData = new double[(int) totalRows][(int) totalColumns];
		for (int i = 0; i < totalRows; i++) {
			for (int j = 0; j < totalColumns; j++) {
				tempData[i][j] = data[i][j];
			}
		}
		return tempData;
	}

	public void sortbyColumn(double arr[][], int col) {
		Arrays.sort(arr, new Comparator<double[]>() {

			public int compare(final double[] entry1, final double[] entry2) {
				if (entry1[col] > entry2[col])
					return 1;
				else if (entry1[col] < entry2[col])
					return -1;
				else
					return 0;
			}
		});
	}

	public void removeDuplicates() {
		for (int column = 1; column < totalColumns - 1; column++) {
			int lastColumn = (int) (totalColumns - 1);
			List<Integer> deleteRowsList = new ArrayList<>();
			for (int i = 0; i < (totalRows - 1); i++) {
				if ((originalData[i][column] == originalData[i + 1][column]) && (originalData[i][lastColumn] != originalData[i + 1][lastColumn])) {
					deleteRowsList.add((int) originalData[i + 1][0]);
					System.out.println("Element: " + originalData[i][0] + " " + originalData[i][1] + " " + originalData[i][2] + " " + originalData[i][3] + " ");
				}
			}
			for (int i = 0; i < deleteRowsList.size(); i++) {
				originalData = removerow(originalData, deleteRowsList.get(i));
			}
		}

	}

	public void cutData(double[][] data, int column) {
		Set<Integer> leftList = new LinkedHashSet<>();
		Set<Integer> rightList = new LinkedHashSet<>();
		int lastColumn = (int) (totalColumns - 1);
		int counterLeft = 1, counterRight = 1;

		if (totalRows <= 1) {
			isFinished = true;
			return;
		}

//		System.out.println();

		List<Double> leftLa = valsToSkip.get(column);
		double leftClass = 0;
		for (int i = 0; i < (totalRows - 1); i++) {
			if ((data[i][lastColumn] == data[i + 1][lastColumn]) || ((data[i][column] == data[i + 1][column]) && (data[i][lastColumn] == leftClass))) {
				if (leftLa.contains(data[i][column]))
					continue;
				counterLeft++;
				leftList.add((int) data[i][0]);
				leftList.add((int) data[i + 1][0]);
				leftClass = data[i][lastColumn];
			} else {
				break;
			}
		}

		if (leftList.size() == 0) {
			leftList.add((int) data[0][0]);
		}

		int lastElement = (int) (totalRows - 1);
		double rightClass = 0;
		for (int i = lastElement; i > 0; i--) {
			if ((data[i][lastColumn] == data[i - 1][lastColumn]) || ((data[i][column] == data[i - 1][column]) && (data[i][lastColumn] == rightClass))) {
				if (leftLa.contains(data[i][column]))
					continue;
				counterRight++;
				rightList.add((int) data[i][0]);
				rightList.add((int) data[i - 1][0]);
				rightClass = data[i][lastColumn];

			} else {
				break;
			}
		}

		if (rightList.size() == 0) {
			rightList.add((int) data[lastElement][0]);
		}

		int max = Integer.MIN_VALUE;
		int max2 = Integer.MIN_VALUE;

		// lewa strona

		List<Integer> leftListNormal = new ArrayList<>();
		for (Integer i : leftList) {
			leftListNormal.add(i);
		}
		double firstElementClass = originalSavedData[leftListNormal.get(0) - 1][totalColumns - 1];
		double valToRemoveLeft = 0.00005;
		for (Integer i : leftList) {
			if (originalSavedData[i - 1][lastColumn] != firstElementClass) {
				valToRemoveLeft = originalSavedData[i - 1][column];
//				System.out.println("Val to remove: " + valToRemoveLeft);
			}
		}

		List<Integer> leftIndexesToDelete = new ArrayList<>();
		int size = leftListNormal.size();
		for (int i = 0; i < size; i++) {
			if (originalSavedData[leftListNormal.get(i) - 1][column] == valToRemoveLeft) {
//				System.out.println(originalSavedData[leftListNormal.get(i) - 1][column] + " == " + valToRemoveLeft
//						+ ", Removing: " + leftListNormal.get(i));
				leftIndexesToDelete.add(leftListNormal.get(i));
			}
		}
//		for (int i = 0; i < leftIndexesToDelete.size(); i++) {
//			boolean check = leftListNormal.contains(leftIndexesToDelete.get(i));
//			if (check) {
//				System.out.println("Tak ma wartość: " + leftIndexesToDelete.get(i));
//			}
//		}

		leftList = new LinkedHashSet<>();
		for (int i = 0; i < size; i++) {
			leftList.add(leftListNormal.get(i));
		}

		List<Integer> rightListNormal = new ArrayList<>();
		for (Integer i : rightList) {
			rightListNormal.add(i);
		}

		// prawa strona

		firstElementClass = originalSavedData[rightListNormal.get(0) - 1][totalColumns - 1];
		double valToRemoveRight = 0.00005;
		for (Integer i : rightList) {
			if (originalSavedData[i - 1][lastColumn] != firstElementClass) {
				valToRemoveRight = originalSavedData[i - 1][column];
//				System.out.println("Val to remove: " + valToRemoveRight);
			}
		}

		List<Integer> rightIndexesToDelete = new ArrayList<>();
		size = rightListNormal.size();
		for (int i = 0; i < size; i++) {
			// int index = (int) originalSavedData[i][0];
			if (originalSavedData[rightListNormal.get(i) - 1][column] == valToRemoveRight) {
//				System.out.println(originalSavedData[rightListNormal.get(i) - 1][column] + " == " + valToRemoveRight
//						+ ", Removing: " + rightListNormal.get(i));
				rightIndexesToDelete.add(rightListNormal.get(i));
			}
		}

//		for(int i=0; i<leftIndexesToDelete.size(); i++) {
//			boolean check = rightListNormal.contains(leftIndexesToDelete.get(i));
//			if (check) {
//				System.out.println("Tak ma wartość: "+leftIndexesToDelete.get(i));
//			}
//		}

		rightList = new LinkedHashSet<>();
		for (int i = 0; i < size; i++) {
			boolean check = rightIndexesToDelete.contains(rightListNormal.get(i));
			if (!check)
				rightList.add(rightListNormal.get(i));
		}

		if (isSkipping)
			if (leftList.size() == 1 && rightList.size() == 1) {
				isFinished = true;
				return;
			}

		if (leftList.size() >= rightList.size()) {
			List<Double> leftL = valsToSkip.get(column);
			leftL.add(valToRemoveLeft);
			valsToSkip.put(column, leftL);
//			System.out.println("Z lewej można odciąć " + counterLeft + " elementów");
//			leftList.forEach(System.out::println);

			for (Entry<Integer, List<Integer>> tmp : extraValsToRemove.entrySet()) {
				if (tmp.getValue().size() > max2)
					max2 = tmp.getValue().size();
			}
			if (max2 < leftIndexesToDelete.size())
				extraValsToRemove.put(column, leftIndexesToDelete);

			for (Entry<Integer, Set<Integer>> tmp : map.entrySet()) {
				if (tmp.getValue().size() > max)
					max = tmp.getValue().size();
			}
			if (max < leftList.size())
				map.put(column, leftList);
		} else {
			List<Double> rightL = valsToSkip.get(column);
			rightL.add(valToRemoveRight);
			valsToSkip.put(column, rightL);
//			System.out.println("Z prawej można odciąć " + counterRight + " elementów");
//			rightList.forEach(System.out::println);

			for (Entry<Integer, List<Integer>> tmp : extraValsToRemove.entrySet()) {
				if (tmp.getValue().size() > max2)
					max2 = tmp.getValue().size();
			}
			if (max2 < rightIndexesToDelete.size())
				extraValsToRemove.put(column, rightIndexesToDelete);

			for (Entry<Integer, Set<Integer>> tmp : map.entrySet()) {
				if (tmp.getValue().size() > max)
					max = tmp.getValue().size();
			}
			if (max < rightList.size())
				map.put(column, rightList);
		}

	}

	public double[][] removerow(double mat[][], int ren) {
		int rengre = ren;
		double mat2[][] = new double[mat.length - 1][mat[0].length];
		int p = 0;
		for (int i = 0; i < mat.length; ++i) {
			if ((int) mat[i][0] == rengre) {
				continue;
			}
			int q = 0;
			for (int j = 0; j < mat[0].length; ++j) {
				mat2[p][q] = mat[i][j];
				++q;
			}
			++p;
		}
		totalRows = mat.length - 1;

		return mat2;
	}

	public double[][] removerow2(double mat[][], int ren) {
		boolean isFound = false;
		for (int i = 0; i < mat.length; ++i) {
			if (mat[i][0] == ren) {
				isFound = true;
			}
		}
		if (!isFound) {
			return mat;
		}
		int rengre = ren;
		double mat2[][] = new double[mat.length - 1][mat[0].length];
		int p = 0;
		for (int i = 0; i < mat.length; ++i) {
			if ((int) mat[i][0] == rengre) {
				continue;
			}
			int q = 0;
			for (int j = 0; j < mat[0].length; ++j) {
				mat2[p][q] = mat[i][j];
				++q;
			}
			++p;
		}
		totalRows = mat.length - 1;

		return mat2;
	}

}
