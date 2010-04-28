package sudoku.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Square {

	/** Set the possible values for a square. */
	public static void setValues(int numValues) {
		VALUES = new Integer[numValues];
		for (int i = 0; i < numValues; i++) {
			VALUES[i] = i + 1;
		}
	}

	private Set<Integer> candidates;
	private int value;

	private static Integer[] VALUES;

	public Square() {
		try {
			candidates = new HashSet<Integer>(Arrays.asList(VALUES));
		} catch (NullPointerException error) {
			throw (new NullPointerException(
					"Square.VALUES has not been initialized."));
		}
	}

	public Square(int value) {
		candidates = null;
		this.value = value;
	}

	protected Square(int[] candidates) {
		if (candidates.length == 1) {
			fix(candidates[0]);
		} else {
			this.candidates = new HashSet<Integer>();
			for (int item : candidates) {
				this.candidates.add(item);
			}
		}
	}

	/** Make a copy of the square. */
	public Square clone() {
		if (isFixed()) {
			return new Square(value);
		}
		Square newSquare = new Square();
		newSquare.candidates = new HashSet<Integer>(candidates);
		return newSquare;
	}

	/** Count the number of candidates the square has. */
	public int countCandidates() {
		if (candidates == null) {
			return 1;
		}
		return candidates.size();
	}

	/**
	 * Check that the squares have the same value or an identical list of
	 * candidates.
	 */
	public boolean equals(Square that) {
		if (isFixed() != that.isFixed()) {
			return false;
		}
		if (isFixed()) {
			return value == that.value;
		}
		return candidates.equals(that.candidates);
	}

	/** Fix the square to contain value. */
	public void fix(int value) {
		candidates = null;
		this.value = value;
	}

	public Set<Integer> getCandidates() {
		return candidates;
	}

	public int getValue() {
		return value;
	}

	public boolean hasCandidate(int candidate) {
		if (candidates == null) {
			return false;
		}
		return candidates.contains(candidate);
	}

	/** Check if the square is fixed. */
	public boolean isFixed() {
		return candidates == null;
	}

	/** Remove the unavailable candidates from the squares list of candidates. */
	public void remove(Collection<Integer> unavailable) {
		candidates.removeAll(unavailable);
		if (candidates.size() == 1) {
			for (int item : candidates) {
				fix(item);
			}
		}
	}

	/** Set the square to contain the given list of candidates. */
	protected void setCandidates(int[] candidates) {
		if (candidates.length == 1) {
			fix(candidates[0]);
		} else {
			this.candidates = new HashSet<Integer>();
			for (int item : candidates) {
				this.candidates.add(item);
			}
		}
	}

	public void setCandidates(List<Integer> candidates) {
		if (candidates.size() == 1) {
			fix(candidates.get(0));
		} else {
			this.candidates = new HashSet<Integer>(candidates);
		}
	}

	public String toString() {
		if (isFixed()) {
			return "" + value;
		}
		String result = "[";
		for (int candidate : candidates) {
			result += candidate + ", ";
		}
		return result + "]";
	}
}