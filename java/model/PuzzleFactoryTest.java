package sudoku.model;

import static java.util.Arrays.sort;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class PuzzleFactoryTest {

	@Test
	public void dimsToRegionSizes() {
		assertEquals(0, PuzzleFactory.dimsToRegion(0, 0).length);
		assertEquals(6, PuzzleFactory.dimsToRegion(2, 3).length);
		assertEquals(9, PuzzleFactory.dimsToRegion(3, 3).length);
		assertEquals(12, PuzzleFactory.dimsToRegion(3, 4).length);
		assertEquals(16, PuzzleFactory.dimsToRegion(4, 4).length);
	}

	@Test
	public void dimsToRegionsSizes() {
		assertEquals(0, PuzzleFactory.dimsToRegions(0, 0).length);
		assertEquals(6, PuzzleFactory.dimsToRegions(2, 3).length);
		assertEquals(9, PuzzleFactory.dimsToRegions(3, 3).length);
		assertEquals(12, PuzzleFactory.dimsToRegions(3, 4).length);
		assertEquals(16, PuzzleFactory.dimsToRegions(4, 4).length);
	}

	@Test
	public void dimsToRegionsValues() {
		int[][] regions = PuzzleFactory.dimsToRegions(2, 3);
		int[][] wanted = { { 0, 1, 2, 6, 7, 8 }, { 3, 4, 5, 9, 10, 11 },
				{ 12, 13, 14, 18, 19, 20 }, { 15, 16, 17, 21, 22, 23 },
				{ 24, 25, 26, 30, 31, 32 }, { 27, 28, 29, 33, 34, 35 } };
		// Ensure that all regions are sorted, so the comparison will work.
		for (int[] region : regions) {
			sort(region);
		}
		for (int[] item : wanted) {
			if (!itemInRegions(item, regions)) {
				fail(itemNotInRegion(item, regions));
			}
		}
	}

	@Test
	public void dimsToRegionValues() {
		assertArrayEquals(new int[] {}, PuzzleFactory.dimsToRegion(0, 0));
		assertArrayEquals(new int[] { 0, 1, 4, 5 }, PuzzleFactory.dimsToRegion(
				2, 2));
		assertArrayEquals(new int[] { 0, 1, 2, 6, 7, 8 }, PuzzleFactory
				.dimsToRegion(2, 3));
		assertArrayEquals(new int[] { 0, 1, 2, 9, 10, 11, 18, 19, 20 },
				PuzzleFactory.dimsToRegion(3, 3));
		assertArrayEquals(new int[] { 0, 1, 6, 7, 12, 13 }, PuzzleFactory
				.dimsToRegion(3, 2));
		assertArrayEquals(new int[] { 2, 3, 6, 7 }, PuzzleFactory.dimsToRegion(
				2, 2, 2));
	}

	public boolean itemInRegions(int[] item, int[][] regions) {
		for (int[] region : regions) {
			if (regionsEqual(region, item)) {
				return true;
			}
		}
		return false;
	}

	public String itemNotInRegion(int[] item, int[][] regions) {
		String message = "The item [";
		for (int index : item) {
			message += index + ", ";
		}
		message += "] could not be found in [";
		for (int[] region : regions) {
			message += "[";
			for (int index : region) {
				message += index + ", ";
			}
			message += "]";
		}
		return message + "].";
	}

	public boolean regionsEqual(int[] region1, int[] region2) {
		if (region1.length != region2.length) {
			return false;
		}
		for (int i = 0; i < region1.length; i++) {
			if (region1[i] != region2[i]) {
				return false;
			}
		}
		return true;
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMakePuzzle() throws InvalidPuzzleError {
		// These should succeed
		PuzzleFactory.makePuzzle(new int[] { 0 });
		PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
				12, 13, 14, 15, 16 });
		PuzzleFactory.makePuzzle(new int[] { 0 }, 1, 1);
		PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4 }, 1, 2);
		PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 3, 1);
		PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
				12, 13, 14, 15, 16 });
		PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
				12, 13, 14, 15, 16 }, 2, 2);
		Puzzle puzzle = PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4 },
				new int[][] { { 0, 3 }, { 1, 2 } });
		int[][] targets = new int[][] { { 1, 2 }, { 3, 4 }, { 1, 3 }, { 2, 4 },
				{ 1, 4 }, { 2, 3 } };
		Square[][] sections = puzzle.getSections();
		for (int i = 0; i < sections.length; i++) {
			for (int j = 0; j < sections[i].length; j++) {
				assertEquals(targets[i][j], sections[i][j].getValue());
			}
		}
		targets = new int[][] { { 6, 3, 0, 1, 0, 0 }, { 0, 0, 2, 0, 0, 0 },
				{ 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 2, 0 },
				{ 0, 0, 0, 6, 0, 0 }, { 0, 0, 1, 0, 4, 5 },

				{ 6, 0, 0, 0, 0, 0 }, { 3, 0, 1, 0, 0, 0 },
				{ 0, 2, 0, 0, 0, 1 }, { 1, 0, 0, 0, 6, 0 },
				{ 0, 0, 0, 2, 0, 4 }, { 0, 0, 0, 0, 0, 5 },

				{ 6, 3, 0, 0, 0, 2 }, { 1, 0, 0, 0, 0, 0 },
				{ 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 2, 0 },
				{ 0, 0, 0, 0, 0, 1 }, { 6, 0, 0, 0, 4, 5 } };
		Puzzle six = PuzzleFactory.makePuzzle(new int[] { 6, 3, 0, 1, 0, 0, 0,
				0, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 6,
				0, 0, 0, 0, 1, 0, 4, 5 }, 2, 3);
		sections = six.getSections();
		for (int i = 12; i < sections.length; i++) {
			for (int j = 0; j < sections[i].length; j++) {
				assertEquals(targets[i][j], sections[i][j].getValue());

			}
		}
		new Puzzle(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new int[][] {
				{ 0, 4, 8 }, { 1, 2, 6 }, { 3, 5, 7 } });
		// These should not
		expectInvalidPuzzleError(new int[] { 1, 2, 3 });
		expectInvalidPuzzleError(new int[] { 1, 2, 3 }, 1, 1);
		expectInvalidPuzzleError(new int[] { 1, 2, 3, 4 }, 0, 0);
		expectInvalidPuzzleError(new int[] { 1, 2, 3, 4 }, 2, 2);
		expectInvalidPuzzleError(new int[] { 1 }, new int[][] { { 1 } });
		expectInvalidPuzzleError(new int[] { 1, 2, 3, 4 }, new int[][] {
				{ 0, 1, 2 }, { 3 } });
		expectInvalidPuzzleError(new int[] { 1, 2, 3, 4 }, new int[][] {
				{ 0, 1 }, { 1, 2 } });
	}

	public void expectInvalidPuzzleError(int[] squares) {
		try {
			PuzzleFactory.makePuzzle(squares);
			fail("Expected an InvalidPuzzleError.");
		} catch (InvalidPuzzleError error) {
		}
	}

	public void expectInvalidPuzzleError(int[] squares, int rheight, int rwidth) {
		try {
			PuzzleFactory.makePuzzle(squares, rheight, rwidth);
			fail("Expected an InvalidPuzzleError.");
		} catch (InvalidPuzzleError error) {
		}
	}

	public void expectInvalidPuzzleError(int[] squares, int[][] regions) {
		try {
			PuzzleFactory.makePuzzle(squares, regions);
			fail("Expected an InvalidPuzzleError.");
		} catch (InvalidPuzzleError error) {
		}
	}

}
