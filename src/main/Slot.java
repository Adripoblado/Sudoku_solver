package main;

import java.util.HashSet;
import java.util.Set;

public class Slot {

	private int xIndex, yIndex, value;
	private Set<Integer> possibleValues;
	
	public Slot(int xIndex, int yIndex, int value) {
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		this.value = value;
		this.possibleValues = new HashSet<Integer>();
	}
	
	public void assignValue(int value) {
		this.value = value;
		this.possibleValues = null;
	}
	
	public void updatePossibleValues(Set<Integer> possibleValues) {
		this.possibleValues.addAll(possibleValues);
	}
	
	public String printPossibleValues() {
		StringBuilder sb = new StringBuilder();
		
		for (int value : possibleValues) {
			sb.append(value + " ");
		}
		
		return sb.toString().trim();
	}
	
	public String printFormatNotes() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("|");
		for (int i = 1; i <= 9; i++) {
			if (possibleValues.contains(i)) {
				sb.append(" " + i + " ");
			} else {
				sb.append(" - ");
			}
			
			if (i % 3 == 0) {
				sb.append("|\n|");
			}
		}
		sb.append("---------|");
		
		return sb.toString();
	}
	
	public Set<Integer> getPossibleValues() {
		return this.possibleValues;
	}
	
	public String getCoords() {
		return String.valueOf(xIndex + "-" + yIndex);
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getX() {
		return this.xIndex;
	}
	
	public int getY() {
		return this.yIndex;
	}
}
