package com.System_Z_Modulami;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.collections.FXCollections;
import javafx.scene.control.Control;

public class DataUtils {
	public final static int MAX_WIDTH = 170;

	public static void initNode(Control control) {
		control.setMinWidth(MAX_WIDTH);
		control.setMaxWidth(MAX_WIDTH);
		control.setStyle("-fx-alignment: CENTER;");
	}



}
