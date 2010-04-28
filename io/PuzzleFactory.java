package sudoku.io;

import static java.lang.Math.sqrt;
import sudoku.model.Puzzle;

public class PuzzleFactory {

	/** Convert the dimensions of the sub-regions to a list of square indices. */
	protected static int[] dimsToRegion(int height, int width) {
		int size = width * height;
		int[] region = new int[size];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				region[i * width + j] = j + i * size;
			}
		}
		return region;
	}

	protected static int[] dimsToRegion(int height, int width, int start) {
		int[] region = dimsToRegion(height, width);
		for (int i = 0; i < width * height; i++) {
			region[i] += start;
		}
		return region;
	}

	protected static int[][] dimsToRegions(int height, int width) {
		int[][] regions = new int[width * height][];
		int rindex = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				regions[rindex] = dimsToRegion(height, width, width
						* (i * height * height + j));
				rindex++;
			}
		}
		return regions;
	}

	public static Puzzle makePuzzle(int[] squares) throws InvalidPuzzleError {
		return makePuzzle(squares, (int) sqrt(sqrt(squares.length)),
				(int) sqrt(sqrt(squares.length)));
	}

	public static Puzzle makePuzzle(int[] squares, int rheight, int rwidth)
			throws InvalidPuzzleError {
		return makePuzzle(squares, dimsToRegions(rheight, rwidth));
	}

	public static Puzzle makePuzzle(int[] squares, int[][] regions)
			throws InvalidPuzzleError {
		checkValidity(squares, regions);
		return new Puzzle(squares, regions);
	}

	/** Make sure that the puzzle description is a valid puzzle. */
	protected static void checkValidity(int[] squares, int[][] regions)
			throws InvalidPuzzleError {
		// Check that width makes sense
		int width = (int) sqrt(squares.length);
		int size = width * width;
		if (size != squares.length) {
			throw (new InvalidPuzzleError("A " + width + "-puzzle cannot have "
					+ squares.length + " squares."));
		}
		// Check for the correct number of regions
		if (regions.length != width) {
			throw (new InvalidPuzzleError("There must be exactly " + width
					+ " regions."));
		}
		for (int i = 0; i < width; i++) {
			// Check that the region is the right size
			if (regions[i].length != width) {
				throw (new InvalidPuzzleError("All regions must have exactly "
						+ width + " squares."));
			}
			for (int j = 0; j < width; j++) {
				if (regions[i][j] >= width * width) {
					throw (new InvalidPuzzleError("There is no square number "
							+ regions[i][j] + "."));
				}
			}
		}
		// Check that all squares are members of one region.
		for (int target = 0; target < squares.length; target++) {
			boolean found = false;
			for (int[] region : regions) {
				for (int candidate : region) {
					if (candidate == target) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
			if (!found) {
				throw (new InvalidPuzzleError(
						"Not all squares are members of a region."));
			}
		}
	}
}
