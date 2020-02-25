package com.System_Z_Modulami;

import java.util.Arrays;

public class Point {
	double[] axis;
	int oldIndex, newIndex;
	Double minDistance;
	
	
	
	
	
	
	
	
	
	public Point(double[] axis, int oldIndex) {
		super();
		this.axis = axis;
		this.oldIndex = oldIndex;
		this.newIndex = oldIndex;
		this.minDistance = 0.0;
	}
	@Override
	public String toString() {
		return "Point [axis=" + Arrays.toString(axis) + ", oldIndex=" + oldIndex + ", newIndex=" + newIndex + ", minDistance=" + minDistance + "]";
	}
	public double[] getAxis() {
		return axis;
	}
	public void setAxis(double[] axis) {
		this.axis = axis;
	}
	public int getOldIndex() {
		return oldIndex;
	}
	public void setOldIndex(int oldIndex) {
		this.oldIndex = oldIndex;
	}
	public int getNewIndex() {
		return newIndex;
	}
	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
	}
	public Double getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(Double minDistance) {
		this.minDistance = minDistance;
	}
	
	
	
	
	

}
