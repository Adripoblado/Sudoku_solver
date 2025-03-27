package main;

import java.util.ArrayList;
import java.util.List;

public class Block {

	private int xaxis, yaxis;
	private String type;
	private List<Slot> slotList;

	public Block(int xaxis, int yaxis, String type) {
		this.xaxis = xaxis;
		this.yaxis = yaxis;
		this.type = type;
		this.slotList = new ArrayList<Slot>();
	}

	public Block(String type) {
		this.xaxis = -1;
		this.yaxis = -1;
		this.type = type;
		this.slotList = new ArrayList<Slot>();
	}

	public boolean containsSlot(String index) {
		for (Slot slot : slotList) {
			if (slot.getCoords().equals(index)) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> getAllValues() {
		List<Integer> values = new ArrayList<Integer>();

		for (Slot slot : slotList) {
			if (slot.getValue() > 0) {
				values.add(slot.getValue());
			}
		}

		return values;
	}
	
	public List<Slot> getSlots() {
		return this.slotList;
	}

	public String getType() {
		return this.type;
	}
	
	public int getX() {
		return this.xaxis;
	}
	
	public int getY() {
		return this.yaxis;
	}
}
