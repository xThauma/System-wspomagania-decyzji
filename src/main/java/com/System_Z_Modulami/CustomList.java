package com.System_Z_Modulami;

import java.util.ArrayList;
import java.util.List;

public class CustomList {
	int column;
	List<Double> list;
	
	public CustomList(int column) {
		super();
		this.column = column;
		this.list = new ArrayList<>();
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public List<Double> getList() {
		return list;
	}
	public void setList(List<Double> list) {
		this.list = list;
	}
	
	

}
