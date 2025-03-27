package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
	static BufferedReader br;
	static Set<String> freeSlots;
	static Map<String, List<Integer>> possibleValuesPerSlot;
	static int[][] field;

	public static void main(String[] args) {
		field = new int[9][9];
		freeSlots = new HashSet<String>();
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Introduce the sudoku field: ");

//		populateField(field);

//		field = new int[][] { { 0, 0, 0, 9, 2, 6, 0, 4, 5 }, { 0, 0, 9, 8, 0, 0, 7, 2, 0 },
//				{ 2, 0, 6, 4, 0, 3, 8, 0, 1 }, { 7, 6, 0, 0, 0, 0, 0, 3, 0 }, { 0, 9, 8, 0, 0, 0, 1, 6, 0 },
//				{ 1, 0, 0, 0, 0, 5, 4, 7, 9 }, { 0, 0, 0, 0, 6, 8, 9, 0, 3 }, { 0, 1, 5, 0, 4, 0, 0, 0, 0 },
//				{ 6, 0, 3, 0, 0, 0, 0, 5, 4 } };

//		field = new int[][] { { 0, 0, 2, 0, 0, 6, 0, 0, 0 }, { 0, 4, 0, 0, 1, 8, 0, 9, 6 },
//				{ 0, 0, 6, 7, 0, 0, 0, 8, 0 }, { 2, 0, 0, 0, 0, 0, 8, 0, 1 }, { 0, 8, 0, 5, 3, 0, 6, 0, 0 },
//				{ 0, 0, 0, 0, 2, 0, 5, 4, 0 }, { 0, 7, 0, 9, 0, 0, 0, 1, 5 }, { 0, 0, 9, 0, 0, 0, 0, 0, 0 },
//				{ 5, 6, 0, 0, 0, 4, 0, 0, 0 } };

		field = new int[][] { { 0, 0, 8, 0, 0, 0, 0, 0, 4 }, { 0, 0, 0, 7, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 5, 2, 3, 0, 6 }, { 6, 0, 0, 9, 0, 0, 0, 3, 0 }, { 0, 0, 4, 0, 0, 1, 0, 0, 0 },
				{ 2, 0, 9, 0, 3, 5, 0, 0, 0 }, { 9, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 6, 0, 0, 9, 4, 0 },
				{ 4, 0, 0, 0, 0, 0, 0, 0, 2 } };

		List<Block> blockList = arrangeValues();

		while (freeSlots.size() > 0) {
			Tools.printField(field, -1, -1);
			solve(blockList);

			blockList = arrangeValues();
		}

		long time = System.nanoTime();

		System.out.println("AC: " + (System.nanoTime() - time));
		Tools.printField(field, -1, -1);
	}

	private synchronized static List<Block> arrangeValues() {
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (field[x][y] == 0) {
					int xIndex = x / 3;
					int yIndex = (y / 3) * 3;

					freeSlots.add(x + "-" + y + "-" + String.valueOf(xIndex + yIndex));
				}
			}
		}

		List<Block> blockList = new ArrayList<Block>();
		int xsum = 0, ysum = 0;
		for (int blockIndex = 0; blockIndex < 9; blockIndex++) {
			int xaxis = blockIndex % 3;
			int yaxis = blockIndex / 3;
			Block block = new Block(xaxis, yaxis, "B");

			ysum = blockIndex / 3;
			if (xsum == 9) {
				xsum = 0;
			}

			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					int yIndex = y + (ysum * 3);
					int xIndex = x + xsum;

					Slot slot = new Slot(xIndex, yIndex, field[xIndex][yIndex]);
					block.getSlots().add(slot);
				}
			}
			xsum += 3;
			blockList.add(block);
		}

		int yaxis = 0;
		for (int x = 0; x < 9; x++) {
			Block block = new Block("X", x, yaxis);
			for (int y = 0; y < 9; y++) {
				block.getSlots().add(new Slot(x, y, field[x][y]));
				yaxis = y;
			}
			blockList.add(block);
		}

		int xaxis = 0;
		for (int y = 0; y < 9; y++) {
			Block block = new Block("Y", xaxis, y);
			for (int x = 0; x < 9; x++) {
				block.getSlots().add(new Slot(x, y, field[x][y]));
				xaxis = x;
			}
			blockList.add(block);
		}

		return blockList;
	}

	private synchronized static void solve(List<Block> blockList) {
		for (Block block : blockList) {
			for (Slot slot : block.getSlots()) {
				if (slot.getValue() < 1) {
					slot.updatePossibleValues(calculateNotes(slot, blockList));
				}
			}
		}

		for (Block block : blockList) {
			for (Slot slot : block.getSlots()) {
				cleanNotes(slot, blockList);
			}
		}

//		cleanLines(blockList);

		for (Block block : blockList) {
			removeNakedPairs(block, blockList);
//			checkNaked(block, 2);
//			checkNaked(block, 3);
		}

		int assignedValues = 0;
		for (Block block : blockList) {
			for (Slot slot : block.getSlots()) {
				int fetchedValue = fetchUniqueValues(slot, block);
				if (fetchedValue > 0) {
					slot.assignValue(fetchedValue);
					field[slot.getX()][slot.getY()] = fetchedValue;
					assignedValues++;
				}
			}
		}

		if (assignedValues == 0) {
			Tools.printNotes(blockList, 0);
			System.exit(0);
		}
	}

	private synchronized static void cleanLines(List<Block> blockList) {
		List<Block> squareBlocks = new ArrayList<Block>();

		for (Block block : blockList) {
			if (block.getType().equals("B")) {
				squareBlocks.add(block);
			}
		}

		List<String> lineList = new ArrayList<String>();

		for (int i = 1; i <= 9; i++) {
			for (Block block : squareBlocks) {
				int occurrences = getOccurrences(i, block);

				if (occurrences == 2 || occurrences == 3) {
					String line = getLineIndex(i, block);

					if (line != null) {
						System.out.println("Line with 2 or 3 occurrences: " + line);
						lineList.add(line);
					}
				}
			}
		}
//		If it doesn't contain any lines, skip
		if (lineList.size() > 0) {
			System.out.println("Some lines to remove...");
			for (String line : lineList) {
				int index = Integer.parseInt(line.substring(0, 1));
				int blockIndex = index / 3;
				String axis = line.substring(1, 2);
				int value = Integer.parseInt(line.substring(2, 3));

				int xIndex = Integer.parseInt(line.substring(3, 4));
				int yIndex = Integer.parseInt(line.substring(4));
				for (Block block : squareBlocks) {
					if (block.getX() == xIndex && block.getY() == yIndex) {
						continue;
					}

					if (axis.equals("X") && block.getX() == blockIndex) {
						for (Slot slot : block.getSlots()) {
							if (slot.getX() == index) {
//								System.out.println(slot.getCoords() + ": " + slot.printPossibleValues());
//								System.out.println("Removing value: " + value);
//								slot.getPossibleValues().remove(value);
//								System.err.println(slot.printPossibleValues());
							}
						}
					}

					if (axis.equals("Y") && block.getY() == blockIndex) {
						for (Slot slot : block.getSlots()) {
							if (slot.getY() == index) {
//								System.out.println(slot.getCoords() + ": " + slot.printPossibleValues());
//								System.out.println("Removing value: " + value);
//								slot.getPossibleValues().remove(value);
//								System.err.println(slot.printPossibleValues());
							}
						}
					}
				}
			}
		}
	}

	private synchronized static String getLineIndex(int value, Block block) {
//		Initialize both axis on -1 to identify whether they have been assigned a value or not
		int xaxis = -1;
		int yaxis = -1;
		Slot s = null;
//		Go through all slots checking if it contains the desired value
		for (Slot slot : block.getSlots()) {
			if (slot.getPossibleValues().contains(value)) {
//				Skip first coincidence
				s = slot;
				if (xaxis == -1) {
					xaxis = slot.getX();
					yaxis = slot.getY();
					continue;
				}
//				If axis changes set value to -2
				if (xaxis != slot.getX()) {
					xaxis = -2;
				}

				if (yaxis != slot.getY()) {
					yaxis = -2;
				}
			}
		}

		String line = null;
//		Use line String to put 1.- axis index, 2.- axis identification, 3.- slot value, 4.- block X axis and 5.- block Y axis
		if (xaxis != -2) {
			line = String.valueOf(xaxis + "X" + value + "" + block.getX() + "" + block.getY());
		}

		if (yaxis != -2) {
			line = String.valueOf(yaxis + "Y" + value + "" + block.getX() + "" + block.getY());
		}

		return line;
	}

	private synchronized static int getOccurrences(int possibleValue, Block block) {
		int occurrences = 0;
		for (Slot s : block.getSlots()) {
			if (s.getPossibleValues() == null) {
				continue;
			}

			if (s.getPossibleValues().contains(possibleValue)) {
				occurrences++;
			}
		}

		return occurrences;
	}

	private synchronized static void checkNaked(Block block, int n) {
//		Create a Set of integers to temporally store the possible values of a slot
		Set<Integer> pairs = new HashSet<Integer>();
		for (Slot slot : block.getSlots()) {
//			Ignore solved slots
			if (slot.getValue() > 0) {
				continue;
			}
//			Get only n-sized slots (pairs or triples)
			if (slot.getPossibleValues().size() == n) {
				pairs.addAll(slot.getPossibleValues());
//				Go through slots again, ignoring current Slot
				for (Slot compareSlot : block.getSlots()) {
					if (slot.equals(compareSlot) || compareSlot.getValue() > 0) {
						continue;
					}
//					Check if possible values of each slot match with current slot possible values
					if (compareSlot.getPossibleValues().size() == n
							&& compareSlot.getPossibleValues().equals(slot.getPossibleValues())) {
						System.err.println("Detected coincidence on " + n + " possible values on slot: "
								+ slot.getCoords() + "(" + slot.printPossibleValues() + ") with slot "
								+ compareSlot.getCoords() + " (" + compareSlot.printPossibleValues() + ")");
//						Go through all slots again once we have a match and delete those values
						for (Slot slotToClean : block.getSlots()) {
							if (slotToClean.equals(slot) || slotToClean.equals(compareSlot)
									|| slotToClean.getValue() > 0) {
								continue;
							}

							StringBuilder sb = new StringBuilder();
							for (int pv : pairs) {
								sb.append(pv + " ");
							}

							System.out.println("Removing " + sb.toString() + " from slot " + slotToClean.getCoords());
							slotToClean.getPossibleValues().removeAll(pairs);
						}

						return;
					}
				}
			}
		}
	}

	private synchronized static void removeNakedPairs(Block block, List<Block> blockList) {
		List<Slot> nakedPairs = new ArrayList<Slot>();

		boolean exit = false;
		for (Slot slot : block.getSlots()) {
			if (slot.getPossibleValues().size() != 2) {
				continue;
			}
			for (Slot compareSlot : block.getSlots()) {
				if (compareSlot.equals(slot) || compareSlot.getPossibleValues().size() != 2) {
					continue;
				}

				if (slot.getPossibleValues().equals(compareSlot.getPossibleValues())) {
					nakedPairs.add(slot);
					nakedPairs.add(compareSlot);
					exit = true;
					break;
				}
			}
			if (exit) {
				break;
			}
		}

		if (exit) {
			StringBuilder sb = new StringBuilder();
			for (int pv : nakedPairs.get(0).getPossibleValues()) {
				sb.append(pv + " ");
			}
			
			List<String> affectedCoords = new ArrayList<String>();
			for (Block b : blockList) {
				if (b.containsSlot(nakedPairs.get(0).getCoords()) && b.containsSlot(nakedPairs.get(1).getCoords())) {
					for (Slot slot : b.getSlots()) {
						if (!slot.getPossibleValues().equals(nakedPairs.get(0).getPossibleValues())) {
							affectedCoords.add(slot.getCoords());
//							slot.getPossibleValues().removeAll(nakedPairs.get(0).getPossibleValues());
						}
					}
				}
			}
			
			for (Block b : blockList) {
				for (Slot s : b.getSlots()) {
					if (affectedCoords.contains(s.getCoords())) {
						s.getPossibleValues().removeAll(nakedPairs.get(0).getPossibleValues());
					}
				}
			}
		}
	}

	private synchronized static int fetchUniqueValues(Slot slot, Block block) {
		if (slot.getPossibleValues() == null) {
			return 0;
		}

		Map<Integer, Integer> occurrences = new HashMap<Integer, Integer>();

		for (Slot s : block.getSlots()) {
			if (s.getPossibleValues() == null) {
				continue;
			}

			for (int value : s.getPossibleValues()) {
				occurrences.put(value, occurrences.getOrDefault(value, 0) + 1);
			}
		}

		for (int value : slot.getPossibleValues()) {
			if (occurrences.get(value) == 1) {
				System.out.println("Unique Value on slot " + slot.getCoords() + ": " + value);
				return (value);
			}
		}

		return 0;
	}

	private synchronized static void cleanNotes(Slot slot, List<Block> blockList) {
		List<Block> relatedBlocks = getBlocks(slot.getCoords(), blockList);

		Set<Integer> possibleValues = slot.getPossibleValues();

		if (possibleValues == null) {
			return;
		}

		for (Block block : relatedBlocks) {
			Block compareBlock = new Block(block.getX(), block.getY(), "N");
			compareBlock.getSlots().addAll(block.getSlots());

			compareBlock.getSlots().remove(slot);

			possibleValues.removeAll(compareBlock.getAllValues());
		}
	}

	private synchronized static Set<Integer> calculateNotes(Slot slot, List<Block> blockList) {
		Set<Integer> options = new HashSet<Integer>();
		for (int i = 1; i <= 9; i++) {
			options.add(i);
		}

		List<Block> relatedBlocks = getBlocks(slot.getCoords(), blockList);

		for (Block block : relatedBlocks) {
			for (int i = 1; i <= 9; i++) {
				if (block.getAllValues().contains(i)) {
					options.remove(i);
				}
			}
		}

		return options;
	}

	private synchronized static List<Block> getBlocks(String index, List<Block> blockList) {
		List<Block> sortedBlockList = new ArrayList<Block>();

		for (Block block : blockList) {
			if (block.containsSlot(index)) {
				sortedBlockList.add(block);
			}
		}

		return sortedBlockList;
	}

	@SuppressWarnings("unused")
	private synchronized static void populateField(int[][] field) {
		int add = 0;
		boolean newParcel = false;
		for (int y = 0; y <= 9; y++) {
			for (int x = 0; x < 3; x++) {
				if (y > 0 && x % 3 == 0 && y % 3 == 0) {
					add += 3;
					y -= 3;
				}

				if (newParcel) {
					add = 0;
					newParcel = false;
				}

				assignValue(x + add, y, true);

				if (y > 0 && (y + 1) % 3 == 0 && x + add == 8) {
					newParcel = true;
					y += 3;
				}
			}
		}
	}

	private synchronized static void assignValue(int xaxis, int yaxis, boolean print) {
		if (print) {
			Tools.printField(field, xaxis, yaxis);
		}

		int value = 0;

		System.err.println("Possible Options: ");
		List<Integer> possibleOptions = null;// TODO: calculateOptions(xaxis, yaxis);

		if (possibleOptions.size() == 0) {
			System.err.println("There are no possible combinations with this field.");
			System.exit(0);
		}

		for (int val : possibleOptions) {
			System.err.print(val + " ");
		}

		while ((value < 1 || value > 9)) {
			try {
				String input = br.readLine();
				if (input.isEmpty() || input == "") {
					value = 0;
					freeSlots.add(xaxis + "-" + yaxis);
					return;
				}
				value = Integer.parseInt(input);
			} catch (NumberFormatException | IOException e) {
			}
		}

		if (!possibleOptions.contains(value)) {
			assignValue(xaxis, yaxis, false);
			return;
		}

		field[xaxis][yaxis] = value;
	}
}
