package sudoku.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class SdkReaderTest {

	@Test
	public void loadRegionDims() throws SdkReadError {
		assertNull(SdkReader.loadRegionDims(""));
		assertArrayEquals(new int[] { 2, 3 }, SdkReader.loadRegionDims("2x3"));
		assertArrayEquals(new int[] { 3, 4 }, SdkReader
				.loadRegionDims("3 x  4"));
		try {
			SdkReader.loadRegionDims("4");
			SdkReader.loadRegionDims("2x3x4");
			fail("These should not succeed.");
		} catch (SdkReadError error) {

		}
	}

	@Test
	public void loadRegions() {
		assertNull(SdkReader.loadRegions(""));
		int[][] wanteds = new int[][] { { 1, 2, 3 }, { 4, 5, 6 } };
		List<List<Integer>> result = SdkReader.loadRegions("1  2 3, 4 5 6");
		assertEquals(wanteds.length, result.size());
		for (int i = 0; i < wanteds.length; i++) {
			assertEquals(wanteds[i].length, result.get(i).size());
			for (int j = 0; j < wanteds[i].length; j++) {
				assertEquals(wanteds[i][j], result.get(i).get(j).intValue());
			}
		}
	}

	@Test
	public void loadSquares() {
		String squares = "1 | . | .--+---+--2 | . | .--+---+--3 | . | .";
		int[] wanted = { 1, 0, 0, 2, 0, 0, 3, 0, 0 };
		List<Integer> result = SdkReader.loadSquares(squares);
		assertEquals(wanted.length, result.size());
		for (int i = 0; i < wanted.length; i++) {
			assertEquals(wanted[i], result.get(i).intValue());
		}
	}
}
