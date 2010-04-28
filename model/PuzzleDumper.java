package sudoku.model;

public class PuzzleDumper {
	private Puzzle puzzle;
	private Square[][] rows;
	private int vwidth;

	public PuzzleDumper(Puzzle puzzle, Square[][] rows, int width) {
		this.puzzle = puzzle;
		this.rows = rows;
		if (width < 10) {
			vwidth = 1;
		} else {
			vwidth = 2;
		}
	}

	/** Determine which character to print. */
	public String corner(Square square, Square rsquare, Square dsquare,
			Square csquare) {
		boolean r = puzzle.inSameRegion(square, rsquare);
		boolean d = puzzle.inSameRegion(square, dsquare);
		boolean c = puzzle.inSameRegion(square, csquare);
		boolean rs = puzzle.inSameRegion(rsquare, csquare);
		if (r && d && c) {
			return " ";
		}
		if (r && !(d || c || rs)) {
			return "-";
		}
		if (d && rs && !(r && c)) {
			return "|";
		}
		return "+";
	}

	/** Determine which divider to use. */
	public String divider(Square[] row, Square[] nextRow) {
		String result = "";
		Square square = null, rsquare, csquare, dsquare = null;
		for (int i = 0; i < row.length - 1; i++) {
			square = row[i];
			rsquare = row[i + 1];
			dsquare = nextRow[i];
			csquare = nextRow[i + 1];
			result += seperator(square, dsquare);
			result += corner(square, rsquare, dsquare, csquare);
		}
		result += seperator(square, dsquare);
		return result;
	}

	/** Dump the puzzle. */
	public String dump() {
		String result = "";
		for (int i = 0; i < rows.length - 1; i++) {
			result += formatRow(rows[i]) + "\n";
			String divider = divider(rows[i], rows[i + 1]);
			if (divider.contains("-")) {
				result += divider + "\n";
			}
		}
		return result + formatRow(rows[rows.length - 1]);
	}

	/** Format the rows. */
	public String formatRow(Square[] row) {
		String result = "";
		int i;
		for (i = 0; i < row.length - 1; i++) {
			result += formatSquare(row[i]);
			if (puzzle.inSameRegion(row[i], row[i + 1])) {
				result += " ";
			} else {
				result += "|";
			}
		}
		return result + formatSquare(row[i]);
	}

	/** Format the squares. */
	public String formatSquare(Square square) {
		String result = "";
		if (square.isFixed()) {
			for (int i = 0; i < vwidth; i++) {
				result += " ";
			}
		}
		return "" + square.getValue();
	}

	/** Determine which seperator to use. */
	private String seperator(Square square, Square dsquare) {
		String result = "";
		if (puzzle.inSameRegion(square, dsquare)) {
			for (int j = 0; j < vwidth; j++) {
				result += " ";
			}
		} else {
			for (int j = 0; j < vwidth; j++) {
				result += "-";
			}
		}
		return result;
	}
}
