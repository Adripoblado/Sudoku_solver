package main;

import java.util.ArrayList;
import java.util.List;

public class Tools {

	public synchronized static void printField(int[][] field, int xaxis, int yaxis) {
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
	
	public synchronized static void printNotes(List<Block> blockList, int value) {
		List<Block> blocks = new ArrayList<Block>();

		for (Block block : blockList) {
			if (block.getType().equals("Y")) {
				blocks.add(block);
			}
		}

		List<Slot> arrangedSlots = new ArrayList<Slot>();
		for (Block block : blocks) {
			arrangedSlots.addAll(block.getSlots());
		}

		int count = 0;
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		for (int i = 1; i <= 3; i++) {
			int x = 1;
			for (Slot slot : arrangedSlots) {
				if (count % 9 == 0) {
					sb1.append("\n|");
				}
				sb1.append(slot.printFormatNotes(i, value));
				sb1.append("|");
				if(x % 3 == 0) {
					sb1.append("|");
				}
				
				count++;
				x++;
			}
		}

		for (int i = 4; i <= 6; i++) {
			int x = 1;
			for (Slot slot : arrangedSlots) {
				if (count % 9 == 0) {
					sb2.append("\n|");
				}
				sb2.append(slot.printFormatNotes(i, value));
				sb2.append("|");
				if(x % 3 == 0) {
					sb2.append("|");
				}
				
				count++;
				x++;
			}
		}

		for (int i = 7; i <= 9; i++) {
			int x = 1;
			for (Slot slot : arrangedSlots) {
				if (count % 9 == 0) {
					sb3.append("\n|");
				}
				sb3.append(slot.printFormatNotes(i, value));
				sb3.append("|");
				if(x % 3 == 0) {
					sb3.append("|");
				}
				
				count++;
				x++;
			}
		}

		String[] first = sb1.toString().split("\n");
		String[] second = sb2.toString().split("\n");
		String[] third = sb3.toString().split("\n");
		
		for (int i = 0; i <= 9; i++) {
			System.out.println(first[i]);
			System.out.println(second[i]);
			System.out.println(third[i]);
			if (i % 3 == 0) {
				System.out.println("==============================================================================================");
			} else {
				System.out.println("----------------------------------------------------------------------------------------------");
			}
		}
	}
}
