package com.System_Z_Modulami;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Sets;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.tree.Leaf;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class DecisionTree {

	private double[][] data;
	private double totalColumns, totalRows;
	private List<String> columnNames;
	private Map<Integer, Map<Double, Map<Double, Double>>> mapWithOccurances;
	private Map<Double, Integer> mapWithInformationGains;
	private Map<Integer, Map<Double, Double>> mapWithEntropies;
	private Map<Integer, Map<Double, Double>> mapWithClasses;
	private int sections = 3;
	private double value = 0.0;
	private List<Double> values;
	private double sumA = 0.0;
	private double sumB = 0.0;
	private double sumC = 0.0;
	private List<Double> sumsList;
	private List<List<Double>> temp2list;
	private int indexToDelete;
	private List<Double> listForEntropies;
	private int rootVal = 0;
	private double[][] tempData;
	private double entropy = 0;
	private int rowCounter = 0;
	private Boolean isPrint = false;
	private Map<Integer, Integer> mapToDeleteElements;
	private List<Integer> listWithColumns;
	String rootElement = "";

	public void initDecisionTree(double[][] data, List<String> columnNames, int totalColumns, int totalRows) throws Exception {
		this.data = data;
		this.columnNames = columnNames;
		this.totalColumns = totalColumns;
		this.totalRows = totalRows;
		System.out.println("Rows: " + totalRows + ", columns: " + (totalColumns - 2));
		System.out.print("Columns names: ");
		Arrays.asList(columnNames).forEach(System.out::print);
		System.out.println();
		Stage stage = new Stage();
		stage.setTitle("Drzewa decyzyjne");

		setDiscreteValues(15);
//		Node<String> root = createTreeFor2Descretes();
//		printTree(root, "  ");

		for (int i = 2; i <= 5; i++) {
			System.out.println("Dla deskretyzacji: " + i);
			generateViaWeka(6);
		}

		initQucikDt();
//		generateRandomTree2Sections();
//		calculateTree();
//		printTable();

		GridPane gridPane = new GridPane();
		stage.setScene(new Scene(gridPane, 600, 600));
		stage.setX(0);
		stage.setY(0);
		stage.show();
	}

	public void printTable() {
		System.out.print("\n");
		for (int i = 0; i < totalRows; i++) {
			for (int j = 1; j < totalColumns; j++) {
				if (j == 5) {
					if (data[i][j] == 1.0) {
						System.out.print("setosa");
					} else if (data[i][j] == 2.0) {
						System.out.print("virginica");
					} else if (data[i][j] == 3.0) {
						System.out.print("versicolor");
					}
				} else
					System.out.print(data[i][j] + ",");
			}
			System.out.print("\n");
		}
	}

	public void setDiscreteValues(int sections) {
		mapWithOccurances = new TreeMap<>();
		for (int j = 1; j < (totalColumns - 1); j++)
			discretizeData(sections, j);
	}

	public void initQucikDt() {
		int goodPredictionsCounter = 0;
		System.out.print("\n");
		final Set<Instance> instances = Sets.newHashSet();
		for (int q = 0; q < totalRows; q++) {
			for (int i = 0; i < totalRows; i++) {
				if (i == q)
					continue;
				String word = "";
				if (data[i][5] == 1.0) {
					word = "SETOSA";
				} else if (data[i][5] == 2.0) {
					word = "VIRGINIC";
				} else if (data[i][5] == 3.0) {
					word = "VERSICOL";
				}
				instances.add(HashMapAttributes.create("LISDLG", data[i][1], "LISSZE", data[i][2], "PLADLG", data[i][3], "PLASZE", data[i][4]).classification(word));
			}
			TreeBuilder treeBuilder = new TreeBuilder();
			Tree tree = treeBuilder.buildPredictiveModel(instances);
//		tree.dump(System.out);
			Leaf leaf = (Leaf) tree.node.getLeaf(HashMapAttributes.create("LISDLG", data[q][1], "LISSZE", data[q][2], "PLADLG", data[q][3], "PLASZE", data[q][4]));
			String word = "";
			if (data[q][5] == 1.0) {
				word = "SETOSA";
			} else if (data[q][5] == 2.0) {
				word = "VIRGINIC";
			} else if (data[q][5] == 3.0) {
				word = "VERSICOL";
			}
			if (leaf.getBestClassification().equals("SETOSA")) {
				if (data[q][5] == 1.0) {
					goodPredictionsCounter++;
//					System.out.println("Correct prediction: Setosa");
				} else {
					System.out.println("Bad prediction: Setosa, correct prediction: " + word);
				}
			} else if (leaf.getBestClassification().equals("VIRGINIC")) {
				if (data[q][5] == 2.0) {
					goodPredictionsCounter++;
//					System.out.println("Correct prediction: VIRGINIC");
				} else {
					System.out.println("Bad prediction: VIRGINIC, correct prediction: " + word);
				}
			} else {
				if (data[q][5] == 3.0) {
					goodPredictionsCounter++;
//					System.out.println("Correct prediction: VERSICOL");
				} else {
					System.out.println("Bad prediction: VERSICOL, correct prediction: " + word);
				}
			}
		}
		double prediction = (((double) goodPredictionsCounter / totalRows) * 100);
		System.out.println("Dobre wyniki: " + goodPredictionsCounter + "/" + totalRows + ", skuteczność: " + formatDouble(prediction) + "%");
	}

	public void calculateTree() {
		listWithColumns = new ArrayList<>();
		listWithColumns.add(1);
		listWithColumns.add(2);
		listWithColumns.add(3);
		listWithColumns.add(4);
		mapWithOccurances.forEach((e, v) -> System.out.println(e + "=" + v));

		rootVal = getHighestInformationGain(mapWithOccurances, data, isPrint, (int) totalRows);
		System.out.println("\nHighest information gain column: " + rootVal);
		// root = new Tree<String>(columnNames.get(rootVal - 1));

		for (int i = 0; i < listWithColumns.size(); i++) {
			if (listWithColumns.get(i) == rootVal) {
				listWithColumns.remove(i);
			}
		}
		for (int i = 1; i <= sections; i++) {
			getSubTreesInformationGains(rootVal, i);
		}
	}

	public String getRandomElement() {
		Random rand = new Random();
		String randomElement = columnNames.get(rand.nextInt(columnNames.size() - 1));
		columnNames.remove(randomElement);
		return randomElement;
	}

	public void generateViaWeka(int i) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Kamil\\Documents\\Systemy wspomagania decyzji\\System_Z_Modulami\\iris" + i + ".arff"));

		// Get the data
		Instances data = new Instances(reader);
		reader.close();

		// Setting class attribute
		data.setClassIndex(data.numAttributes() - 1);

		// Make tree
		J48 tree = new J48();
		String[] options = new String[1];
		options[0] = "-U";
		tree.setOptions(options);
		tree.buildClassifier(data);

		// Print tree
		System.out.println(tree);
	}
//	public void generateRandomTree2Sections() {
//		root = new Tree<String>(getRandomElement());
//		// left
//		Node<String> leftChild = new Node<String>();
//		leftChild.setParent(root);
//
//	}

	private Node<String> createTree() {
		Node<String> root = new Node<>("root");

		Node<String> node1 = root.addChild(new Node<String>("node 1"));

		Node<String> node11 = node1.addChild(new Node<String>("node 11"));
		Node<String> node111 = node11.addChild(new Node<String>("node 111"));
		Node<String> node112 = node11.addChild(new Node<String>("node 112"));

		Node<String> node12 = node1.addChild(new Node<String>("node 12"));

		Node<String> node2 = root.addChild(new Node<String>("node 2"));

		Node<String> node21 = node2.addChild(new Node<String>("node 21"));
		Node<String> node211 = node2.addChild(new Node<String>("node 22"));
		return root;
	}

	public void buildTree() {
		java.util.Collections.shuffle(columnNames);

	}

	public void buildNode(List<String> permutation, List<String> columnNames, int depth) {
	}

	private Node<String> createTreeFor2Descretes() {
		String rootElement = getRandomElement();
		Node<String> root = new Node<>(rootElement);

		String nd2 = getRandomElement();
		String nd3 = getRandomElement();
		String nd4 = getRandomElement();

		Node<String> node1 = root.addChild(new Node<String>(nd2));

		Node<String> node11 = node1.addChild(new Node<String>(nd3));
		Node<String> node111 = node11.addChild(new Node<String>(nd4));
		Node<String> node112 = node11.addChild(new Node<String>(nd4));

		Node<String> node12 = node1.addChild(new Node<String>(nd3));

		Node<String> node2 = root.addChild(new Node<String>(nd2));

		Node<String> node21 = node2.addChild(new Node<String>(nd3));
		Node<String> node211 = node21.addChild(new Node<String>(nd4));
		Node<String> node212 = node21.addChild(new Node<String>(nd4));

		Node<String> node22 = node2.addChild(new Node<String>(nd3));

		return root;
	}

	private static <T> void printTree(Node<T> node, String appender) {
		System.out.println(appender + node.getData());
		node.getChildren().forEach(each -> printTree(each, appender + appender));
	}

	public void getSubTreesInformationGains(int column, int k) {
		mapWithOccurances = new TreeMap<>();
		tempData = getFreshData();
		rowCounter = 0;
		for (int i = 1; i < (totalColumns - 1); i++) {
			if (listWithColumns.contains(i)) {
				discretizeDataWithValues(i, tempData, column, (double) k);
			}
		}
		int a = getHighestInformationGain(mapWithOccurances, tempData, isPrint, rowCounter);

		if (a == 0) {
			System.out.println("Single leaf for value: " + (double) k + " for column: " + columnNames.get(column - 1));
			listWithColumns = new ArrayList<>();
			listWithColumns.add(1);
			listWithColumns.add(2);
			listWithColumns.add(3);
//			listWithColumns.add(4);
		} else {
			System.out.println("Not single leaf for value: " + (double) k + " for column: " + columnNames.get(column - 1) + ", win value: " + columnNames.get(a - 1));
			for (int i = 0; i < listWithColumns.size(); i++) {
				if (listWithColumns.get(i) == a) {
					listWithColumns.remove(i);
				}
			}
			getSubTreesInformationGains(a, k);
		}

	}

	private double[][] getFreshData() {
		double[][] tempData = new double[(int) totalRows][(int) totalColumns];
		for (int i = 0; i < totalRows; i++) {
			for (int j = 1; j < totalColumns; j++) {
				tempData[i][j] = data[i][j];
			}
		}
		return tempData;
	}

	private int getHighestInformationGain(Map<Integer, Map<Double, Map<Double, Double>>> mapWithOccurances, double data[][], boolean isPrint, int totalRows) {
		mapWithClasses = new TreeMap<>();
		listForEntropies = new ArrayList<>();
		mapWithEntropies = new TreeMap<>();
		mapWithInformationGains = new TreeMap<>();
		mapWithOccurances.forEach((e, v) -> {
			sumsList = new ArrayList<>();
			temp2list = new ArrayList<>();
			Map<Double, Double> mapWithClassTemp = new TreeMap<>();
			sumA = 0;
			sumB = 0;
			sumC = 0;
			value = 0.0;
			if (isPrint)
				System.out.println("Column: " + e);
			v.forEach((ee, vv) -> {
				values = new ArrayList<>();
				if (isPrint)
					System.out.print("Attributes: " + ee + ". Classes: ");
				vv.forEach((eee, vvv) -> {
					values.add(vvv);
				});

				double sum = 0;
				for (int i = 0; i < 3; i++) {
					if (values.size() < 3) {
						values.add(0.0);

					}
					sum += values.get(i);
				}

				getH(values, 1, isPrint);
				if (isPrint)
					System.out.print(". Calculating: " + "(" + sum + "/" + totalRows + "). E: " + formatDouble(getH2(values, sum)) + ", Entropy: " + formatDouble((sum / totalRows) * getH2(values, sum)));
				if (isPrint)
					System.out.println();
				sumsList.add(sum);
				temp2list.add(values);
				if (mapWithEntropies.containsKey(e)) {
					Map<Double, Double> tmp = mapWithEntropies.get(e);
					tmp.put(ee, getH2(values, sum));
					mapWithEntropies.put(e, tmp);

				} else {
					Map<Double, Double> tmp = new TreeMap<>();
					tmp.put(ee, getH2(values, sum));
					mapWithEntropies.put(e, tmp);
				}

			});
			List<Double> tempList = new ArrayList<>();
			tempList.add(sumA);
			tempList.add(sumB);
			tempList.add(sumC);
			entropy = 0.0;
			for (int i = 0; i < sumsList.size(); i++) {

				entropy += ((sumsList.get(i) / totalRows) * getH(temp2list.get(i), sumsList.get(i), isPrint));
//				System.out.println(getH2(temp2list.get(i), sumsList.get(i)) + "-----------" + entropy);
			}
			if (isPrint)
				System.out.println("\nTotal E: " + formatDouble(entropy) + ". Information gain: " + formatDouble(getH2(tempList, totalRows)) + " - " + formatDouble(entropy) + " = "
						+ formatDouble(getH(tempList, totalRows, isPrint) - entropy));
			mapWithInformationGains.put(getH2(tempList, totalRows) - entropy, e);
		});
		indexToDelete = 0;
		double max = Double.MIN_VALUE;
		for (Entry<Double, Integer> tempMap : mapWithInformationGains.entrySet()) {
			if (tempMap.getKey() > max) {
				max = tempMap.getKey();
				indexToDelete = tempMap.getValue();
//				System.out.println(indexToDelete);
			}
		}

		Map<Double, Double> tempMapToCheckIfSame = new TreeMap<>();
		mapWithInformationGains.forEach((e, v) -> {
			tempMapToCheckIfSame.put(e, e);
		});
		if (tempMapToCheckIfSame.size() == 1)
			return 0;

		return indexToDelete;
	}

	public double getH(List<Double> list, double sum, boolean isPrint) {
		double a = list.get(0);
		double b = list.get(1);
		double c = list.get(2);
		sumA += a;
		sumB += b;
		sumC += c;
		if (isPrint)
			System.out.print("(" + a + "," + b + "," + c + ")");
		double val = -((a / sum) * log2(a / sum)) - ((b / sum) * log2(b / sum)) - ((c / sum) * log2(c / sum));
		if (Double.isNaN(val) || Double.isInfinite(val))
			return 0.0;
		return val;
	}

	public double getH2(List<Double> list, double sum) {
		double a = list.get(0);
		double b = list.get(1);
		double c = list.get(2);
		double val = -((a / sum) * log2(a / sum)) - ((b / sum) * log2(b / sum)) - ((c / sum) * log2(c / sum));
		if (Double.isNaN(val) || Double.isInfinite(val))
			return 0.0;
		return val;
	}

	public static double log2(double x) {
		double val = (Math.log(x) / Math.log(2));
		if (Double.isInfinite(val))
			return 0.0000000000001;
		return val;
	}

	public String formatDouble(double item) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(item);
	}

	public void discretizeData(int sections, int column) {
		Map<Double, Double> secondInnerMap;
		Map<Double, Map<Double, Double>> firstInnerMap = new TreeMap<>();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int j = 0; j < totalRows; j++) {
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

		for (int j = 0; j < totalRows; j++) {
			for (int k = 0; k < sections; k++) {
				if (data[j][column] <= valSections[k][column]) {
					data[j][column] = k + 1;
					Double currColumn = data[j][column];
					Double lastColumn = data[j][(int) (totalColumns - 1)];
					if (firstInnerMap.containsKey(currColumn)) {
						secondInnerMap = firstInnerMap.get(currColumn);
						if (secondInnerMap.containsKey(lastColumn)) {
							secondInnerMap.put(lastColumn, secondInnerMap.get(lastColumn) + 1.0);
						} else {
							secondInnerMap.put(lastColumn, 1.0);
						}

					} else {
						secondInnerMap = new TreeMap<>();
						for (int q = 1; q <= sections; q++) {
							if (firstInnerMap.containsKey(currColumn)) {

							} else {
								secondInnerMap.put((double) q, 0.0);
							}
						}

						secondInnerMap.put(lastColumn, 1.0);
						firstInnerMap.put(currColumn, secondInnerMap);
					}
					break;
				}

			}
		}
		mapWithOccurances.put(column, firstInnerMap);

	}

	public void discretizeData2(int sections, int column) {
		Map<Double, Double> secondInnerMap;
		Map<Double, Map<Double, Double>> firstInnerMap = new TreeMap<>();

		for (int j = 0; j < totalRows; j++) {
			Double currColumn = data[j][column];
			Double lastColumn = data[j][(int) (totalColumns - 1)];
			if (firstInnerMap.containsKey(currColumn)) {
				secondInnerMap = firstInnerMap.get(currColumn);
				if (secondInnerMap.containsKey(lastColumn)) {
					secondInnerMap.put(lastColumn, secondInnerMap.get(lastColumn) + 1.0);
				} else {
					secondInnerMap.put(lastColumn, 1.0);
				}

			} else {
				secondInnerMap = new TreeMap<>();
				secondInnerMap.put(lastColumn, 1.0);
				firstInnerMap.put(currColumn, secondInnerMap);
			}

		}

		mapWithOccurances.put(column, firstInnerMap);

	}

	public void discretizeDataWithValues(int column, double[][] data, int columnValue, double atribute) {
		Map<Double, Double> secondInnerMap;
		Map<Double, Map<Double, Double>> firstInnerMap = new TreeMap<>();

		for (int j = 0; j < totalRows; j++) {
			if (data[j][columnValue] == atribute) {
				rowCounter++;
				Double currColumn = data[j][column];
				Double lastColumn = data[j][(int) (totalColumns - 1)];
				if (firstInnerMap.containsKey(currColumn)) {

					secondInnerMap = firstInnerMap.get(currColumn);
					if (secondInnerMap.containsKey(lastColumn)) {
						secondInnerMap.put(lastColumn, secondInnerMap.get(lastColumn) + 1.0);
					} else {
						secondInnerMap.put(lastColumn, 1.0);
					}

				} else {
					rowCounter++;
					secondInnerMap = new TreeMap<>();
					secondInnerMap.put(lastColumn, 1.0);
					firstInnerMap.put(currColumn, secondInnerMap);
				}

			}
		}

		mapWithOccurances.put(column, firstInnerMap);

	}

}
