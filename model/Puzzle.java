package sudoku.model;

import static java.lang.Math.sqrt;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import mapfile.ReadError;
import sudoku.io.SdkReader;

public class Puzzle {

	/** Count the number of times each possible value occurs in the section. */
	protected static int[] countEachPossibility(Square[] section) {
		int[] counters = new int[section.length + 1];
		for (Square square : section) {
			if (!square.isFixed()) {
				for (int option : square.getCandidates()) {
					counters[option] += 1;
				}
			} else {
				counters[square.getValue()] += 1;
			}
		}
		return counters;
	}

	/** Search for squares in the section with only one possible value. */
	protected static void findSingletonInSection(Square[] section) {
		int[] counters = countEachPossibility(section);
		for (int i = 1; i < counters.length; i++) {
			if (counters[i] == 1) {
				for (Square square : section) {
					if (square.hasCandidate(i)) {
						square.fix(i);
					}
				}
			}
		}
	}

	/** Read a puzzle from stdin and attempt to solve it. */
	public static void main(String[] args) throws InvalidPuzzleError,
			InconsistentPuzzleError, ReadError, FileNotFoundException {
		Scanner STDIN = new Scanner(System.in);
		String file = "";
		while (true) {
			try {
				String line = STDIN.nextLine();
				file += line + "\n";
			} catch (NoSuchElementException error) {
				break;
			}
		}
		Puzzle puzzle = new SdkReader(file).read();
		puzzle.solve();
		System.out.println(puzzle);
	}

	/** Prune all the impossible values from the section. */
	protected static void pruneSection(Square[] section) {
		Set<Integer> unavailable = new HashSet<Integer>();
		for (Square square : section) {
			if (square.isFixed()) {
				unavailable.add(square.getValue());
			}
		}
		for (Square square : section) {
			if (!square.isFixed()) {
				square.remove(unavailable);
			}
		}
	}

	/** All the squares in the puzzle. */
	private Square[] squares;
	/** All the rows in the puzzle. */
	private Square[][] rows;
	/** All the columns in the puzzle. */
	private Square[][] columns;
	/** All the regions in the puzzle. */
	private Square[][] regions;
	private int[][] regionIndices;
	/** All rows, columns, and regions in the puzzle. */
	private Square[][] sections;
	/** Number of squares. */
	private int size;
	/** Width of the puzzle. */
	private int width;

	// Only around for the sake of the clone method
	public Puzzle() {
	}

	public Puzzle(int[] squares, int[][] regions) {
		width = (int) sqrt(squares.length);
		size = width * width;
		Square.setValues(width);
		this.squares = new Square[width * width];
		// Initialize squares
		for (int i = 0; i < squares.length; i++) {
			if (squares[i] == 0) {
				this.squares[i] = new Square();
			} else {
				this.squares[i] = new Square(squares[i]);
			}
		}
		// Initialize sections (rows, columns, regions)
		rows = new Square[width][width];
		columns = new Square[width][width];
		this.regionIndices = regions;
		this.regions = new Square[width][width];
		sections = new Square[width * 3][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				rows[i][j] = this.squares[i * width + j];
				sections[i][j] = rows[i][j];
				columns[i][j] = this.squares[j * width + i];
				sections[i + width][j] = columns[i][j];
				this.regions[i][j] = this.squares[regions[i][j]];
				sections[i + 2 * width][j] = this.regions[i][j];
			}
		}
	}

	/** Create a copy of the puzzle. */
	public Puzzle clone() {
		Puzzle clone = new Puzzle();
		clone.width = width;
		clone.size = size;
		clone.squares = new Square[width * width];
		for (int i = 0; i < squares.length; i++) {
			clone.squares[i] = squares[i].clone();
		}
		clone.rows = new Square[width][width];
		clone.columns = new Square[width][width];
		clone.regions = new Square[width][width];
		clone.sections = new Square[width * 3][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				clone.sections[i][j] = clone.rows[i][j] = clone.squares[i
						* width + j];
				clone.sections[i + width][j] = clone.columns[i][j] = clone.squares[j
						* width + i];
				clone.sections[i + 2 * width][j] = clone.regions[i][j] = clone.squares[regionIndices[i][j]];
			}
		}
		return clone;
	}

	/**
	 * Count the total number of candidate values in the puzzle. If the puzzle
	 * is solved, this should be exactly equal to the number of sqares.
	 */
	protected int countCandidates() {
		int sum = 0;
		for (Square square : squares) {
			if (!square.isFixed()) {
				sum += square.countCandidates();
			}
		}
		return sum;
	}

	/**
	 * Check if all the squares with known values are equal. Ignore squares with
	 * multiple candidates.
	 */
	public boolean equals(Puzzle that) {
		if (squares.length != that.squares.length) {
			return false;
		}
		for (int i = 0; i < squares.length; i++) {
			if (squares[i].isFixed() != that.squares[i].isFixed()) {
				return false;
			} else if (squares[i].isFixed()
					&& (!squares[i].equals(that.squares[i]))) {
				return false;
			}
		}
		return true;
	}

	/** Find all squares with only one candidate. */
	protected void findSingletons() {
		for (Square[] section : sections) {
			findSingletonInSection(section);
		}
	}

	public Square[][] getSections() {
		return sections;
	}

	public Square[] getSquares() {
		return squares;
	}

	/**
	 * Try all possibilities in the square at index. Eliminate any that result
	 * in an inconsistent puzzle.
	 */
	protected List<Integer> guessByIndex(int index) {
		List<Integer> possible = new LinkedList<Integer>();
		for (int option : squares[index].getCandidates()) {
			Puzzle temp = clone();
			temp.squares[index].fix(option);
			temp.prune();
			if (temp.isConsistent()) {
				possible.add(option);
			}
		}
		return possible;
	}

	/** Check if a square index is in the region. */
	protected boolean inRegion(int j, int[] region) {
		for (int candidate : region) {
			if (candidate == j) {
				return true;
			}
		}
		return false;
	}

	/** Check if a square is in the region. */
	protected boolean inRegion(Square j, Square[] region) {
		for (Square candidate : region) {
			if (candidate == j) {
				return true;
			}
		}
		return false;
	}

	/** Check if two squares are in the same region. */
	protected boolean inSameRegion(Square i, Square j) {
		for (Square[] region : regions) {
			for (Square candidate : region) {
				if (i == candidate) {
					return inRegion(j, region);
				}
			}
		}
		return false;
	}

	/**
	 * Returns false if any square has zero possiblities. Otherwise returns
	 * true.
	 */
	public boolean isConsistent() {
		for (Square square : squares) {
			if (square.countCandidates() == 0) {
				return false;
			}
		}
		return true;
	}

	/** Check if the puzzle has been solved. */
	public boolean isSolved() {
		for (Square square : squares) {
			if (!square.isFixed()) {
				return false;
			}
		}
		return true;
	}

	/** Prune down possiblities as much as feasible. */
	protected void prune() {
		int oldCount = -1;
		while (oldCount != countCandidates()) {
			oldCount = countCandidates();
			pruneSections();
			findSingletons();
		}
	}

	/** Prune all the sections. */
	protected void pruneSections() {
		int oldCount = -1;
		while (oldCount != countCandidates()) {
			oldCount = countCandidates();
			for (Square[] section : sections) {
				pruneSection(section);
			}
		}
	}

	/** Attempt to solve the puzzle. */
	public void solve() throws InconsistentPuzzleError {
		prune();
		while (!isSolved()) {
			if (!isConsistent()) {
				throw (new InconsistentPuzzleError());
			}
			int candidates = countCandidates();
			for (int i = 0; i < squares.length; i++) {
				if (squares[i].countCandidates() == 2) {
					List<Integer> possible = guessByIndex(i);
					squares[i].setCandidates(possible);
					if (possible.size() == 1) {
						prune();
					}
				}
			}
			if (countCandidates() == candidates) {
				break;
			}
		}
	}

	public String toString() {
		return new PuzzleDumper(this, rows, width).dump();
	}
}