package sudoku.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import mapfile.MapfileReader;
import sudoku.model.InvalidPuzzleError;
import sudoku.model.Puzzle;
import sudoku.model.PuzzleFactory;

public class SdkReader {

	/** Load the region dimensions. */
	protected static int[] loadRegionDims(String string) throws SdkReadError {
		if (string.length() == 0) {
			return null;
		}
		String[] stringDims = string.split("x", 2);
		int[] dims = new int[2];
		try {
			dims[0] = Integer.parseInt(stringDims[0].trim());
			dims[1] = Integer.parseInt(stringDims[1].trim());
		} catch (ArrayIndexOutOfBoundsException error) {
			throw (new SdkReadError(string + " does not define a region."));
		}
		return dims;
	}

	/** Load the regions. */
	protected static List<List<Integer>> loadRegions(String string) {
		if (string.length() == 0) {
			return null;
		}
		List<List<Integer>> regions = new LinkedList<List<Integer>>();
		for (String line : string.split(",")) {
			List<Integer> region = new LinkedList<Integer>();
			regions.add(region);
			for (String index : line.trim().split(" ")) {
				if (index.length() > 0) {
					region.add(Integer.parseInt(index));
				}
			}
		}
		return regions;
	}

	/** Load the squares. */
	protected static List<Integer> loadSquares(String string) {
		List<Character> charset = Arrays.asList(' ', '.', '0', '1', '2', '3',
				'4', '5', '6', '7', '8', '9');
		Set<Character> illegal = new HashSet<Character>();
		for (char letter : string.toCharArray()) {
			if (!charset.contains(letter)) {
				illegal.add(letter);
			}
		}
		for (char letter : illegal) {
			try {
				string = string.replaceAll("" + letter, " ");
			} catch (PatternSyntaxException error) {
				string = string.replaceAll("\\" + letter, " ");
			}
		}
		string = string.replaceAll("\\|", " ");
		List<Integer> squares = new LinkedList<Integer>();
		for (String square : string.split("\\s*")) {
			if (square.length() > 0) {
				if (square.charAt(0) == ".".charAt(0)) {
					squares.add(0);
				} else {
					squares.add(Integer.parseInt(square));
				}
			}
		}
		return squares;
	}

	MapfileReader mapfileReader;

	public SdkReader(File file) throws FileNotFoundException {
		mapfileReader = new MapfileReader(file);
	}

	public SdkReader(String fileName) throws FileNotFoundException {
		mapfileReader = new MapfileReader(fileName);
	}

	/** Parse the file into a Puzzle instance. */
	public Puzzle read() throws InvalidPuzzleError, mapfile.ReadError {
		Map<String, String> dictionary = mapfileReader.read();
		List<Integer> squareList = loadSquares(dictionary.get("squares"));
		int[] squares = new int[squareList.size()];
		int index = 0;
		for (int square : squareList) {
			squares[index] = square;
			index++;
		}
		if (dictionary.containsKey("regions")) {
			String regionDescription = dictionary.get("regions");
			if (regionDescription.contains("x")) {
				int[] regions = loadRegionDims(regionDescription);
				if (regions == null) {
					return PuzzleFactory.makePuzzle(squares);
				}
				return PuzzleFactory
						.makePuzzle(squares, regions[0], regions[1]);
			}
			List<List<Integer>> regionList = loadRegions(dictionary
					.get("regions"));
			int[][] regions = new int[regionList.size()][];
			index = 0;
			for (List<Integer> region : regionList) {
				int[] newRegion = new int[region.size()];
				int squareIndex = 0;
				for (int square : region) {
					newRegion[squareIndex] = square;
					squareIndex++;
				}
				regions[index] = newRegion;
				index++;
			}

			if (regions == null) {
				return PuzzleFactory.makePuzzle(squares);
			}
			return PuzzleFactory.makePuzzle(squares, regions);
		}
		return PuzzleFactory.makePuzzle(squares);
	}

}