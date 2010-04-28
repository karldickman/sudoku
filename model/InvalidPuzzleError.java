package sudoku.model;

public class InvalidPuzzleError extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidPuzzleError(String message) {
		super(message);
	}

}
