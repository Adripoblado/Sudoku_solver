package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {

	static BufferedReader br;
	static Set<String> freeSlots;
	static Map<String, List<Integer>> possibleValuesPerSlot;

	public static void main(String[] args) {
		int[][] field = new int[9][9];
		freeSlots = new HashSet<String>();
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Introduce the sudoku field: ");

//		populateField(field);

		field = new int[][] { 
			{ 0, 0, 0, 9, 2, 6, 0, 4, 5 }, 
			{ 0, 0, 9, 8, 0, 0, 7, 2, 0 },
			{ 2, 0, 6, 4, 0, 3, 8, 0, 1 }, 
			{ 7, 6, 0, 0, 0, 0, 0, 3, 0 }, 
			{ 0, 9, 8, 0, 0, 0, 1, 6, 0 },
			{ 1, 0, 0, 0, 0, 5, 4, 7, 9 }, 
			{ 0, 0, 0, 0, 6, 8, 9, 0, 3 }, 
			{ 0, 1, 5, 0, 4, 0, 0, 0, 0 },
			{ 6, 0, 3, 0, 0, 0, 0, 5, 4 } };

		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (field[x][y] == 0) {
					freeSlots.add(x + "-" + y);
				}
			}
		}

		printField(field, -1, -1);
		
		long time = System.nanoTime();

		Set<String> slotsToRemove = new HashSet<String>();
		while (freeSlots.size() > 0) {
			possibleValuesPerSlot = new HashMap<String, List<Integer>>();
			
			for (String slot : freeSlots) {
				int xaxis = Integer.parseInt(slot.split("-")[0]);
				int yaxis = Integer.parseInt(slot.split("-")[1]);
				possibleValuesPerSlot.put(slot, calculateOptions(field, xaxis, yaxis));
			}

			Map<String, List<Integer>> sortedPVPS = sortByListSize(possibleValuesPerSlot, true);

			if (sortedPVPS.size() > 0) {
				for (String coords : sortedPVPS.keySet()) {
					int xaxis = Integer.parseInt(coords.split("-")[0]);
					int yaxis = Integer.parseInt(coords.split("-")[1]);

					field[xaxis][yaxis] = sortedPVPS.get(coords).get(0);
					slotsToRemove.add(coords);
				}
			} else {
				printField(field, -1, -1);
				System.err.println("No more unique values for any field");
				System.exit(0);
			}

			freeSlots.removeAll(slotsToRemove);
			slotsToRemove.clear();
		}

		System.out.println("AC: " + (System.nanoTime() - time));
		printField(field, -1, -1);
	}

	private static List<Integer> calculateOptions(int[][] field, int xaxis, int yaxis) {
		List<Integer> options = new ArrayList<Integer>();

		List<Integer> valuesOnXaxis = new ArrayList<Integer>();
		List<Integer> valuesOnYaxis = new ArrayList<Integer>();

		for (int x = 0; x < 9; x++) {
			options.add(x + 1);
			valuesOnYaxis.add(field[x][yaxis]);
		}

		for (int y = 0; y < 9; y++) {
			valuesOnXaxis.add(field[xaxis][y]);
		}

		List<Integer> valuesOnBlock = new ArrayList<Integer>();

		int startXaxis = (((xaxis / 3) + 1) * 3) - 2;
		int startYaxis = (((yaxis / 3) + 1) * 3) - 2;

		for (int y = startYaxis - 1; y < startYaxis + 2; y++) {
			for (int x = startXaxis - 1; x < startXaxis + 2; x++) {
				valuesOnBlock.add(field[x][y]);
			}
		}

		options.removeAll(valuesOnBlock);
		options.removeAll(valuesOnXaxis);
		options.removeAll(valuesOnYaxis);

		return options;
	}

	private static void populateField(int[][] field) {
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

				assignValue(field, x + add, y, true);

				if (y > 0 && (y + 1) % 3 == 0 && x + add == 8) {
					newParcel = true;
					y += 3;
				}
			}
		}
	}

	private static void assignValue(int[][] field, int xaxis, int yaxis, boolean print) {
		if (print) {
			printField(field, xaxis, yaxis);
		}

		int value = 0;

		System.err.println("Possible Options: ");
		List<Integer> possibleOptions = calculateOptions(field, xaxis, yaxis);

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
			assignValue(field, xaxis, yaxis, false);
			return;
		}

		field[xaxis][yaxis] = value;
	}

	private static void printField(int[][] field, int xaxis, int yaxis) {
		System.out.println("-------------------------------");
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (x == 0) {
					System.out.print("|");
				}

				if (x == xaxis && y == yaxis) {
					System.out.print(" X ");
				} else if (field[x][y] == 0) {
					System.out.print(" - ");
				} else {
					System.out.print(" " + field[x][y] + " ");
				}

				if (x == 2 || x == 5) {
					System.out.print("|");
				}

				if (x == 8) {
					System.out.print("|\n");
				}
			}
			if (y == 2 || y == 5) {
				System.out.println("-------------------------------");
			}
		}
		System.out.println("-------------------------------");
	}

	public static Map<String, List<Integer>> sortByListSize(Map<String, List<Integer>> map, boolean onlyUniqueValues) {
		List<Map.Entry<String, List<Integer>>> list = new ArrayList<>(map.entrySet());
		list.sort(Comparator.comparingInt(entry -> entry.getValue().size()));

		Map<String, List<Integer>> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, List<Integer>> entry : list) {
			if (onlyUniqueValues && entry.getValue().size() > 1) {
				return sortedMap;
			}
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
