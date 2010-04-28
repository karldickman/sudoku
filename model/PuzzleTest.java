package sudoku.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sudoku.io.InvalidPuzzleError;
import sudoku.io.PuzzleFactory;

public class PuzzleTest {

	private Puzzle four, six, nine, twelve, sixteen, twentyFive, squiggly;

	@Before
	public void setup() throws InvalidPuzzleError {
		four = PuzzleFactory.makePuzzle(new int[] { 3, 0, 0, 1, 2, 0, 0, 0, 0,
				0, 0, 2, 1, 0, 0, 3 });
		six = PuzzleFactory.makePuzzle(new int[] { 6, 3, 0, 1, 0, 0, 0, 0, 2,
				0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 6, 0, 0,
				0, 0, 1, 0, 4, 5 }, 2, 3);
		nine = PuzzleFactory.makePuzzle(new int[] { 0, 0, 0, 2, 6, 9, 0, 0, 0,
				8, 0, 0, 5, 0, 0, 0, 2, 0, 0, 0, 7, 0, 1, 0, 6, 0, 0, 0, 0, 6,
				0, 0, 5, 0, 0, 0, 5, 4, 0, 0, 0, 0, 0, 9, 1, 0, 0, 0, 3, 0, 0,
				2, 0, 0, 0, 0, 2, 0, 9, 0, 3, 0, 0, 0, 7, 0, 0, 0, 2, 0, 0, 4,
				0, 0, 0, 4, 5, 6, 0, 0, 0 });
		twelve = PuzzleFactory.makePuzzle(new int[] { 1, 0, 0, 0, 11, 5, 0, 0,
				0, 3, 8, 0, 11, 5, 0, 0, 0, 12, 4, 8, 0, 0, 1, 10, 0, 0, 8, 0,
				0, 0, 0, 0, 5, 9, 0, 0, 0, 10, 0, 3, 12, 4, 2, 0, 0, 0, 0, 0,
				9, 0, 4, 0, 6, 0, 8, 0, 12, 0, 10, 7, 12, 0, 0, 0, 0, 0, 0, 0,
				0, 4, 11, 0, 0, 11, 1, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 8, 0, 7,
				0, 1, 0, 9, 0, 2, 0, 4, 0, 0, 0, 0, 0, 2, 7, 10, 11, 0, 6, 0,
				0, 0, 7, 11, 0, 0, 0, 0, 0, 5, 0, 0, 8, 6, 0, 0, 4, 10, 3, 0,
				0, 0, 7, 9, 0, 4, 5, 0, 0, 0, 9, 6, 0, 0, 0, 8 }, 3, 4);
		sixteen = PuzzleFactory.makePuzzle(new int[] { 13, 10, 0, 7, 0, 14, 0,
				0, 0, 0, 0, 0, 15, 0, 1, 0, 15, 0, 0, 1, 0, 0, 0, 10, 0, 5, 11,
				0, 13, 9, 0, 2, 16, 0, 3, 0, 0, 0, 0, 0, 0, 4, 14, 0, 0, 0, 10,
				11, 0, 0, 4, 5, 0, 9, 0, 7, 0, 0, 16, 0, 0, 0, 0, 0, 6, 7, 13,
				0, 2, 0, 0, 9, 5, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 10, 5, 3, 11,
				12, 7, 0, 0, 0, 0, 0, 0, 1, 11, 16, 0, 0, 0, 13, 10, 0, 15, 12,
				0, 0, 0, 7, 0, 0, 12, 0, 0, 0, 0, 8, 0, 4, 0, 0, 0, 13, 0, 0,
				15, 3, 7, 2, 0, 0, 8, 0, 0, 0, 14, 0, 3, 0, 0, 0, 0, 5, 0, 0,
				11, 0, 0, 0, 4, 15, 0, 9, 5, 0, 0, 0, 14, 13, 4, 0, 0, 0, 0, 0,
				0, 3, 11, 13, 7, 8, 12, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 5, 16, 0,
				0, 4, 0, 15, 3, 7, 0, 0, 0, 0, 0, 6, 0, 0, 8, 0, 13, 0, 5, 4,
				0, 0, 1, 8, 0, 0, 0, 2, 16, 0, 0, 0, 0, 0, 0, 6, 0, 9, 10, 0,
				12, 6, 0, 4, 5, 0, 1, 0, 0, 0, 11, 0, 0, 8, 0, 4, 0, 16, 0, 0,
				0, 0, 0, 0, 6, 0, 2, 0, 13, 10 });
		twentyFive = PuzzleFactory.makePuzzle(new int[] { 6, 0, 0, 0, 0, 0, 0,
				0, 25, 5, 0, 0, 0, 0, 24, 21, 12, 0, 14, 15, 0, 7, 0, 0, 0, 20,
				7, 0, 4, 0, 16, 0, 8, 0, 0, 21, 23, 3, 0, 1, 0, 17, 0, 19, 5,
				0, 0, 0, 14, 0, 11, 0, 0, 0, 0, 0, 7, 3, 4, 0, 20, 0, 22, 0, 5,
				0, 23, 0, 0, 0, 0, 0, 0, 19, 15, 0, 17, 18, 0, 24, 0, 0, 12, 0,
				0, 0, 0, 0, 0, 10, 0, 0, 0, 9, 0, 16, 22, 23, 25, 5, 16, 0, 23,
				0, 0, 21, 17, 18, 0, 0, 0, 0, 12, 14, 11, 6, 8, 0, 0, 4, 0, 0,
				0, 0, 0, 1, 0, 2, 9, 3, 0, 0, 23, 0, 0, 0, 0, 17, 18, 19, 15,
				11, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 1, 20, 0, 0, 0, 0,
				16, 2, 22, 0, 24, 21, 0, 18, 0, 0, 0, 17, 13, 14, 0, 0, 0, 18,
				14, 0, 6, 0, 13, 4, 0, 20, 23, 8, 0, 5, 0, 0, 0, 0, 24, 0, 12,
				0, 0, 24, 0, 0, 0, 19, 14, 12, 11, 8, 15, 0, 6, 0, 0, 0, 20, 1,
				0, 22, 0, 0, 0, 16, 18, 0, 0, 23, 16, 22, 25, 0, 0, 0, 0, 0,
				14, 0, 11, 13, 0, 0, 7, 0, 0, 0, 0, 0, 2, 3, 9, 0, 0, 0, 3, 2,
				9, 0, 16, 0, 0, 0, 0, 0, 0, 0, 19, 0, 11, 12, 13, 14, 10, 0, 0,
				7, 9, 0, 6, 12, 0, 0, 10, 0, 0, 3, 19, 1, 16, 2, 22, 14, 0, 0,
				0, 0, 0, 24, 0, 0, 0, 0, 10, 11, 7, 13, 4, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 22, 19, 15, 21, 12, 0, 0, 0, 0, 17, 0, 0, 0,
				0, 0, 14, 4, 10, 6, 7, 8, 1, 0, 0, 2, 0, 0, 16, 5, 0, 22, 25,
				0, 0, 23, 22, 19, 24, 21, 0, 17, 0, 0, 0, 0, 0, 0, 0, 10, 0, 7,
				9, 1, 0, 0, 0, 3, 4, 1, 0, 0, 0, 0, 0, 16, 0, 0, 25, 24, 0, 17,
				0, 0, 0, 0, 0, 8, 14, 10, 7, 0, 0, 9, 10, 0, 0, 0, 25, 0, 20,
				2, 0, 0, 0, 16, 0, 18, 14, 24, 21, 17, 13, 0, 0, 0, 12, 0, 0,
				15, 0, 12, 0, 0, 0, 0, 7, 0, 9, 1, 6, 0, 22, 25, 0, 20, 0, 18,
				19, 0, 0, 0, 18, 19, 24, 0, 0, 0, 15, 0, 6, 13, 0, 4, 10, 20,
				0, 0, 0, 0, 16, 2, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 17,
				21, 12, 15, 14, 0, 0, 0, 0, 4, 0, 0, 3, 9, 1, 0, 2, 0, 0, 0, 0,
				0, 23, 0, 0, 5, 16, 17, 22, 19, 0, 0, 0, 0, 14, 15, 11, 0, 0,
				4, 0, 10, 12, 3, 14, 10, 6, 0, 22, 0, 0, 0, 2, 0, 0, 0, 0, 0,
				0, 25, 0, 0, 17, 0, 19, 15, 0, 7, 13, 0, 0, 0, 0, 0, 0, 10, 0,
				23, 0, 25, 0, 20, 0, 22, 19, 5, 0, 0, 0, 0, 0, 21, 0, 18, 0, 0,
				0, 11, 14, 0, 15, 0, 7, 0, 4, 10, 6, 0, 0, 9, 0, 20, 0, 25, 0,
				5, 16, 0, 0, 0, 5, 0, 17, 18, 0, 21, 24, 11, 0, 0, 0, 0, 7, 4,
				0, 0, 0, 0, 0, 0, 0, 20 });
		squiggly = PuzzleFactory.makePuzzle(new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1, 0, 0, 0, 5, 3, 4, 2, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 7, 2,
				8, 0, 0, 0, 0, 0, 9, 0, 0, 2, 0, 9, 0, 8, 0, 0, 5, 0, 0, 0, 0,
				0, 1, 9, 6, 0, 0, 0, 0, 0, 8, 5, 0, 0, 0, 3, 5, 1, 7, 0, 0, 0,
				2, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, new int[][] {
				{ 0, 1, 9, 10, 18, 19, 27, 28, 36 },
				{ 2, 3, 4, 11, 12, 13, 20, 21, 22 },
				{ 5, 6, 7, 8, 14, 15, 16, 17, 23 },
				{ 24, 25, 26, 32, 33, 34, 35, 42, 43 },
				{ 29, 30, 31, 39, 40, 41, 49, 50, 51 },
				{ 37, 38, 45, 46, 47, 48, 54, 55, 56 },
				{ 57, 63, 64, 65, 66, 72, 73, 74, 75 },
				{ 58, 59, 60, 67, 68, 69, 76, 77, 78 },
				{ 44, 52, 53, 61, 62, 70, 71, 79, 80 } });
	}

	@Test
	public void countCandidates() throws InvalidPuzzleError {
		assertEquals(64, PuzzleFactory.makePuzzle(
				new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 })
				.countCandidates());
		assertEquals(48, PuzzleFactory.makePuzzle(
				new int[] { 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 4 })
				.countCandidates());
		assertEquals(0, PuzzleFactory.makePuzzle(
				new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 })
				.countCandidates());
		assertEquals(40, PuzzleFactory.makePuzzle(
				new int[] { 3, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 3 })
				.countCandidates());
	}

	@Test
	public void countEachPossibility() {
		int[][] startValues = new int[][] { { 6 }, { 1 }, { 2, 3, 4, 5 },
				{ 3, 4, 5 }, { 2, 3, 4, 5 }, { 2, 3 } };
		Square[] section = new Square[startValues.length];
		for (int i = 0; i < section.length; i++) {
			section[i] = new Square(startValues[i]);
		}
		int[] counts = new int[] { 0, 1, 3, 4, 3, 3, 1 };
		assertArrayEquals(counts, Puzzle.countEachPossibility(section));
	}

	@Test
	public void equals() throws InvalidPuzzleError {
		Puzzle puzzle1 = PuzzleFactory.makePuzzle(new int[] { 1, 0, 0, 0, 0, 2,
				0, 0, 0, 0, 3, 0, 0, 0, 0, 4 });
		Puzzle puzzle1a = PuzzleFactory.makePuzzle(new int[] { 1, 0, 0, 0, 0,
				2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 4 });
		assertTrue(puzzle1.equals(puzzle1a));
		assertTrue(puzzle1a.equals(puzzle1));
		Puzzle puzzle2 = PuzzleFactory.makePuzzle(new int[] { 1, 0, 0, 0, 0, 2,
				0, 0, 0, 0, 3, 0, 0, 0, 0, 5 });
		assertFalse(puzzle1.equals(puzzle2));
		assertFalse(puzzle2.equals(puzzle1));
		Puzzle puzzle3 = PuzzleFactory.makePuzzle(new int[] { 1, 0, 0, 0, 0, 2,
				0, 0, 0, 0, 3, 0, 0, 0, 1, 4 });
		assertFalse(puzzle3.equals(puzzle1));
		assertFalse(puzzle1.equals(puzzle3));
	}

	@Test
	public void findSingletonInSection() {
		int[][][] startValues = new int[][][] {
				{ { 2 }, { 1, 4, 6 }, { 3, 4, 6 }, { 3, 4, 6 }, { 3, 4, 6 },
						{ 5 } },
				{ { 6 }, { 1 }, { 2, 3, 4, 5 }, { 3, 4, 5 }, { 2, 3, 4, 5 },
						{ 2, 3 } },
				{ { 3, 4, 5 }, { 4, 6 }, { 3, 5, 6 }, { 3, 4, 5 }, { 2 },
						{ 1, 3, 4, 6 } },
				{ { 6 }, { 1 }, { 2, 3, 4, 5 }, { 3, 4, 5 }, { 3, 4, 5 },
						{ 3, 4 } } };
		int[][][] wantedValues = new int[][][] {
				{ { 2 }, { 1 }, { 3, 4, 6 }, { 3, 4, 6 }, { 3, 4, 6 }, { 5 } },
				{ { 6 }, { 1 }, { 2, 3, 4, 5 }, { 3, 4, 5 }, { 2, 3, 4, 5 },
						{ 2, 3 } },
				{ { 3, 4, 5 }, { 4, 6 }, { 3, 5, 6 }, { 3, 4, 5 }, { 2 }, { 1 } },
				{ { 6 }, { 1 }, { 2 }, { 3, 4, 5 }, { 3, 4, 5 }, { 3, 4 } } };
		Square[][] starts = new Square[startValues.length][];
		Square[][] targets = new Square[wantedValues.length][];
		for (int i = 0; i < startValues.length; i++) {
			starts[i] = new Square[startValues[i].length];
			targets[i] = new Square[wantedValues[i].length];
			for (int j = 0; j < startValues[i].length; j++) {
				starts[i][j] = new Square(startValues[i][j]);
				targets[i][j] = new Square(wantedValues[i][j]);
			}
		}
		for (int i = 0; i < starts.length; i++) {
			Puzzle.findSingletonInSection(starts[i]);
			for (int j = 0; j < starts[i].length; j++) {
				assertTrue(starts[i][j].equals(targets[i][j]));
			}
		}
	}

	@Test
	public void findSingletons() throws InvalidPuzzleError {
		int[][] wantedValues = new int[][] { { 6 }, { 3 }, { 4 }, { 1 }, { 5 },
				{ 2 }, { 1 }, { 5 }, { 2 }, { 3, 4 }, { 3, 6 }, { 3, 4, 6 },
				{ 2 }, { 1 }, { 3, 5, 6 }, { 3, 4, 5 }, { 3, 6 }, { 3, 4, 6 },
				{ 3, 4, 5 }, { 4, 6 }, { 3, 5, 6 }, { 3, 4, 5 }, { 2 }, { 1 },
				{ 4 }, { 2 }, { 3, 5 }, { 6 }, { 1 }, { 3 }, { 2, 3 }, { 6 },
				{ 1 }, { 2 }, { 4 }, { 5 } };
		Square[] wanted = new Square[wantedValues.length];
		for (int i = 0; i < wantedValues.length; i++) {
			wanted[i] = new Square(wantedValues[i]);
		}
		Puzzle puzzle = PuzzleFactory.makePuzzle(new int[] { 6, 3, 0, 1, 0, 0,
				0, 0, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
				6, 0, 0, 0, 0, 1, 0, 4, 5 }, 2, 3);
		puzzle.pruneSections();
		puzzle.findSingletons();
		for (int i = 0; i < wanted.length; i++) {
			assertTrue(wanted[i].equals(puzzle.getSquares()[i]));
		}
	}

	@Test
	public void guessByIndex() throws InvalidPuzzleError {
		Square[] squares = sixteen.getSquares();
		int[][] wanted = new int[][] { {}, {}, { 2, 4, 5, 16 }, {},
				{ 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13 }, {},
				{ 1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 14 }, { 1, 2, 6, 11 },
				{ 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16 },
				{ 1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 13, 14, 15, 16 },
				{ 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16 },
				{ 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, {},
				{ 1, 4, 5, 8, 9, 13, 14, 15 }, {},
				{ 1, 2, 6, 7, 9, 10, 11, 13, 15, 16 } };
		for (int i = 0; i < wanted.length; i++) {
			if (!squares[i].isFixed()) {
				List<Integer> guesses = sixteen.guessByIndex(i);
				for (int j = 0; j < wanted[i].length; j++) {
					assertTrue(guesses.contains(wanted[i][j]));
				}
			}
		}
	}

	@Test
	public void inSameRegion() {
		for (int i = 0; i < 6; i++) {
			assertTrue(six.inSameRegion(six.getSquares()[i],
					six.getSquares()[i + 6]));
			assertFalse(six.inSameRegion(six.getSquares()[i],
					six.getSquares()[i + 12]));
			if ((i + 1) % 3 == 0) {
				assertFalse(six.inSameRegion(six.getSquares()[i], six
						.getSquares()[i + 1]));
			} else {
				assertTrue(six.inSameRegion(six.getSquares()[i], six
						.getSquares()[i + 1]));
			}
		}
	}

	@Test
	public void isConsistent() throws InvalidPuzzleError {
		assertTrue(PuzzleFactory.makePuzzle(new int[] { 0 }).isConsistent());
		Puzzle two = PuzzleFactory.makePuzzle(new int[] { 0, 2, 3, 4 }, 1, 2);
		assertTrue(two.isConsistent());
		two.getSquares()[0].getCandidates().clear();
		assertFalse(two.isConsistent());
		four.prune();
		assertTrue(four.isConsistent());
		Puzzle inconsistent = PuzzleFactory.makePuzzle(new int[] { 3, 0, 0, 0,
				1, 1, 0, 0, 2 }, 3, 1);
		inconsistent.prune();
		assertFalse(inconsistent.isConsistent());
	}

	@Test
	public void isSolved() throws InvalidPuzzleError, InconsistentPuzzleError {
		assertTrue(PuzzleFactory.makePuzzle(new int[] { 1 }).isSolved());
		assertFalse(PuzzleFactory.makePuzzle(new int[] { 0 }).isSolved());
		assertTrue(PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4 }, 2, 1)
				.isSolved());
		assertFalse(PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 0 }, 2, 1)
				.isSolved());
		assertFalse(PuzzleFactory.makePuzzle(
				new int[] { 3, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 1 })
				.isSolved());
	}

	@Test
	public void prune() throws InvalidPuzzleError {
		six.prune();
		assertTrue(six.isSolved());
	}

	@Test
	public void pruneSection() {
		int[] original = new int[] { 2, 0, 0, 0, 3, 0, 6, 0, 0 };
		Square.setValues(9);
		Square[][] sections = new Square[3][9];
		for (int i = 0; i < sections[0].length; i++) {
			sections[0][i] = new Square();
			if (original[i] == 0) {
				sections[1][i] = new Square();
			} else {
				sections[1][i] = new Square(original[i]);
			}
			sections[2][i] = new Square(i);
		}
		Puzzle.pruneSection(sections[0]);
		for (Square square : sections[0]) {
			assertTrue(new Square().equals(square));
		}
		Square wanted = new Square(new int[] { 1, 4, 5, 7, 8, 9 });
		Puzzle.pruneSection(sections[1]);
		for (int i = 0; i < sections[1].length; i++) {
			if (original[i] == 0) {
				assertTrue(wanted.equals(sections[1][i]));
			} else {
				assertTrue(new Square(original[i]).equals(sections[1][i]));
			}
		}
		Puzzle.pruneSection(sections[2]);
		for (int i = 0; i < sections[2].length; i++) {
			assertTrue(new Square(i).equals(sections[2][i]));
		}
	}

	@Test
	public void pruneSections() throws InvalidPuzzleError {
		int[][] wantedValues = new int[][] { { 6 }, { 3 }, { 4 }, { 1 }, { 5 },
				{ 2 }, { 1 }, { 5 }, { 2 }, { 3, 4 }, { 3, 6 }, { 3, 4, 6 },
				{ 2, 3, 4, 5 }, { 1 }, { 3, 5, 6 }, { 3, 4, 5 }, { 3, 6 },
				{ 3, 4, 6 }, { 3, 4, 5 }, { 4, 6 }, { 3, 5, 6 }, { 3, 4, 5 },
				{ 2 }, { 1, 3, 4, 6 }, { 2, 3, 4, 5 }, { 2, 4 }, { 3, 5 },
				{ 6 }, { 1, 3 }, { 1, 3 }, { 2, 3 }, { 2, 6 }, { 1 }, { 2, 3 },
				{ 4 }, { 5 } };
		Square[] wanted = new Square[wantedValues.length];
		for (int i = 0; i < wantedValues.length; i++) {
			wanted[i] = new Square(wantedValues[i]);
		}
		six.pruneSections();
		for (int i = 0; i < wanted.length; i++) {
			assertTrue(wanted[i].equals(six.getSquares()[i]));
		}
	}

	@Test
	public void solve() throws InvalidPuzzleError, InconsistentPuzzleError {
		four.solve();
		assertTrue(four.isSolved());
		six.solve();
		assertTrue(six.isSolved());
		nine.solve();
		assertTrue(nine.isSolved());
		twelve.solve();
		assertTrue(twelve.isSolved());
		sixteen.solve();
		assertTrue(sixteen.isSolved());
		twentyFive.solve();
		assertTrue(twentyFive.isSolved());
		squiggly.solve();
		assertTrue(squiggly.isSolved());
	}

	@Test
	public void testClone() throws InvalidPuzzleError {
		Puzzle start = PuzzleFactory.makePuzzle(new int[] { 0 });
		Puzzle clone = start.clone();
		assertTrue(start.equals(clone));
		assertNotSame(start, clone);
		assertNotSame(start.getSquares()[0], clone.getSquares()[0]);
		for (Square square : clone.getSquares()) {
			assertNotNull(square);
		}
		for (Square[] section : clone.getSections()) {
			for (Square square : section) {
				assertNotNull(square);
			}
		}
		start = PuzzleFactory.makePuzzle(new int[] { 1 });
		clone = start.clone();
		assertTrue(start.equals(clone));
		assertNotSame(start, clone);
		assertNotSame(start.getSquares()[0], clone.getSquares()[0]);
		start = PuzzleFactory.makePuzzle(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16 });
		clone = start.clone();
		assertTrue(start.equals(clone));
		assertNotSame(start, clone);
		assertNotSame(start.getSquares()[0], clone.getSquares()[0]);
		start = PuzzleFactory.makePuzzle(new int[] { 6, 3, 0, 1, 0, 0, 0, 0, 2,
				0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 6, 0, 0,
				0, 0, 1, 0, 4, 5 }, 2, 3);
		clone = start.clone();
		assertTrue(start.equals(clone));
		assertNotSame(start, clone);
		assertNotSame(start.getSquares()[0], clone.getSquares()[0]);
	}
}