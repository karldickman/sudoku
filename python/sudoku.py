#!/usr/bin/env python2.6

"""An automated sudoku solver.  Reads a sudoku description from stdin and
attempts to solve it."""

from copy import deepcopy
from extensions.itertools import two_finger
import mapfile
from math import ceil, sqrt
from miscellaneous import main_function
from re import compile as Regex
from sys import stdin, stderr

SPACE = Regex("\s*")

@main_function()
def main(options, arguments):
    try:
        puzzle = load(stdin.read())
        puzzle.solve()
    except KeyboardInterrupt:
        return 1
    except InvalidPuzzleError, error:
        print >> stderr, error
        return 1
    except InconsistentPuzzleError, error:
        print >> stderr, "The puzzle is not consistent."
        return 1
    except mapfile.ParseError, error:
        print >> stderr, "Bad puzzle file."
        print >> stderr, error
        return 1
    print puzzle

def dims_to_regions(height, width):
    """Construct the indices for the squares in all the regions if the regions
    are of the given dimensions."""
    return [[i*width + j*width*height*height + k*width*height + l
             for l in xrange(width)
             for k in xrange(height)]
            for j in xrange(width)
            for i in xrange(height)]

def load(string):
    """Parse the puzzle description in the string.  May raise a mapfile.ParseError."""
    required = ["squares"]
    def squares_callback(mapping, line_number):
        values = load_values(mapping["values"]) if "values" in mapping else None
        try:
            mapping["squares"] = load_squares(mapping["squares"], values)
        except ParseError, error:
            raise ParseError(str(error), line_number)
    def regions_callback(mapping, line_number):
        try:
            mapping["regions"] = load_regions(mapping["regions"])
        except KeyError:
            mapping["regions"] = None
        except ParseError, error:
            raise ParseError(str(error), line_number)
    def values_callback(mapping, line_number):
        try:
            mapping["values"] = load_values(mapping["values"])
        except KeyError:
            mapping["values"] = None
    callbacks = {"regions": regions_callback, "squares": squares_callback, "values":
                 values_callback}
    mapping = mapfile.load(string, required, callbacks)
    return Puzzle(mapping["squares"], mapping["regions"], mapping["values"])

def load_regions(string):
    """Parse the given region description.  May raise a ParseError."""
    if len(string) == 0:
        return None
    try:
        if "x" in string:
            rheight, rwidth = string.split("x")
            return int(rheight), int(rwidth)
        return [[int(index) for index in SPACE.split(region.strip())]
                for region in string.split(",")]
    except ValueError, error:
        message = str(error)
        if message == "too many values to unpack":
            raise ParseError("Invalid region: \"%s\"" % string)
        if message[:15] == "invalid literal":
            raise ParseError("Invalid index: \"%s\"" % message[41:-1])

def load_squares(string, values):
    """Parse the given string into a list of squares.  All characters other
    than those in values are treated as whitespace.  May raise a ParseError."""
    if values:
        charset = set(letter for letter in "".join(values))
    else:
        charset = set(str(i) for i in xrange(10))
    blank = "."
    charset.add(blank)
    illegal = set(letter for letter in string) - charset
    for letter in illegal:
        string = string.replace(letter, " ")
    squares = [square if square != blank else None
               for square in SPACE.split(string)]
    width = int(sqrt(len(squares)))
    if values is None:
        values = EnhancedList(str(i) for i in xrange(1, max(10, width) + 1))
    try:
        return [values.index(square) if square is not None else None
                for square in squares]
    except ValueError, error:
        raise ParseError("\"%s\" is not a value available for this puzzle." %
                         str(error)[:-12])

def load_values(string):
    """Parse the given string into a list of values."""
    return EnhancedList(SPACE.split(string))

class Dumper(object):
    """Dumper class for the puzzle."""

    def __init__(self, puzzle):
        self.puzzle = puzzle
        self.rows = puzzle.rows
        self.values = puzzle.values
        self.vwidth = max(len(value) for value in puzzle.values)

    def __iter__(self):
        for row, nrow in two_finger(self.rows):
            yield self.row(row)
            divider = self.divider(row, nrow)
            if "-" in divider:
                yield divider
        yield self.row(nrow)

    def corner(self, square, rsquare, dsquare, csquare):
        selector = {(True, True, True, True): lambda: " ",
                    (True, True, True, False): lambda: " ",
                    (True, False, False, False): lambda: "-",
                    (False, True, False, True): lambda: "|"}
        try:
            return selector[self.puzzle.in_same_region(square, rsquare),
                            self.puzzle.in_same_region(square, dsquare),
                            self.puzzle.in_same_region(square, csquare),
                            self.puzzle.in_same_region(rsquare, csquare)]()
        except KeyError:
            return "+"

    def divider(self, row, nrow):
        return "".join(self.idivider(row, nrow))

    def idivider(self, row, nrow):
        seperator = ["-" * self.vwidth, " " * self.vwidth]
        for (square, dsquare), (rsquare, csquare) in two_finger(zip(row, nrow)):
            yield seperator[self.puzzle.in_same_region(square, dsquare)]
            yield self.corner(square, rsquare, dsquare, csquare)
        yield seperator[self.puzzle.in_same_region(square, dsquare)]

    def irow(self, row):
        for square, nsquare in two_finger(row):
            yield self.square(square)
            yield " " if self.puzzle.in_same_region(square, nsquare) else "|"
        yield self.square(nsquare)

    def row(self, row):
        return "".join(self.irow(row))

    def square(self, square):
        if len(square) > 1:
            return " " * self.vwidth
        return str.rjust(self.values[list(square)[0]], self.vwidth)

class EnhancedList(list):
    """Minor modification of builtin list to give more helpful error message
    for list.index()."""

    def index(self, value, *args, **kwargs):
        """L.index(value, [start, [stop]]) -> integer -- return first index of
        value.  Raises ValueError if the value is not present."""
        try:
            return super(EnhancedList, self).index(value, *args, **kwargs)
        except ValueError:
            raise ValueError("%s not in list" % repr(value))

class Puzzle(object):
    """Representation of a sudoku puzzle."""

    def __init__(self, squares, regions=None, values=None):
        """Raises an InvalidPuzzleError if the provided description does not
        describe a valid puzzle."""
        self.size = len(squares)
        self.width = int(sqrt(self.size))
        self.raw_squares = squares
        self.squares = [set(xrange(self.width)) if square is None
                        else set([square])
                        for square in squares]
        self.rows = [self.squares[i*self.width : (i+1)*self.width]
                      for i in xrange(self.width)]
        self.columns = [self.squares[i::self.width] for i in xrange(self.width)]
        if regions is None:
            rwidth = int(sqrt(self.width))
            regions = dims_to_regions(rwidth, rwidth)
        else:
            try:
                regions = dims_to_regions(regions[0], regions[1])
            except:
                pass
        self.region_indices = regions
        #Must check validity before proceeding further
        if values is None:
            self.values = [str(i) for i in xrange(1, self.width + 1)]
        else:
            self.values = values
        self.check_validity()
        self.regions = [[self.squares[index] for index in region]
                        for region in regions]

    def __eq__(self, other):
        """Ensures that every square with a known value is paired with the same
        value in the other puzzle."""
        try:
            if len(self.squares) != len(other.squares):
                return False
        except AttributeError:
            raise TypeError("Cannot compare Puzzle instance with %s." %
                            type(other))
        return all(s == o for s, o in zip(self.squares, other.squares)
                       if 1 in (len(s), len(o)))

    def __ne__(self, other):
        return not (self == other)

    def __repr__(self):
        squares = [list(square)[0] if len(square) == 1 else None
                   for square in self.squares]
        return "Puzzle(%s, %s, %s)" % (squares, self.region_indices, self.values)

    def __str__(self):
        return "\n".join(Dumper(self))

    def check_validity(self):
        """Check that the given puzzle is valid.  Returns a tuple (validity,
        problems)."""
        if self.width ** 2 != self.size:
            raise InvalidPuzzleError("A %d-puzzle cannot have %d squares." %
                                     (self.width, self.size))
        if len(self.values) != self.width:
            raise InvalidPuzzleError("A %d-puzzle cannot have %d values." %
                                     (self.width, len(self.values)))
        if any(square >= self.width for square in self.raw_squares):
            raise InvalidPuzzleError("A %d-puzzle cannot have %d values." %
                                     (self.width, max(self.raw_squares) + 1))
        if len(self.region_indices) != self.width:
            raise InvalidPuzzleError("A %d-puzzle cannot have %d regions." %
                                     (self.width, len(self.region_indices)))
        if any(len(region) != self.width for region in self.region_indices):
            raise InvalidPuzzleError("All regions must have %d squares." %
                                     self.width)
        if any(index >= self.size for index in sum(self.region_indices, [])):
            raise InvalidPuzzleError("Square index too large.")
        for index in xrange(self.size):
            if not any(index in region for region in self.region_indices):
                raise InvalidPuzzleError("Not all squares are members of a region.")

    @staticmethod
    def count_each_possibility(section):
        counters = [0] * len(section)
        for square in section:
            for option in square:
                counters[option] += 1
        return counters

    def count_possibilities(self):
        """The total number of possibilites in all the unknown squares on the
        board."""
        return sum(len(square) for square in self.squares if len(square) > 1)

    def easy_guesses(self, limit=2):
        """For each square with possibilities less than or equal to limit, try
        all possibilities and eliminate those which result in an inconsistent
        puzzle."""
        for i, square in enumerate(self.squares):
            if len(square) == limit:
                possible = self.guess_by_index(i)
                if len(possible) == 1:
                    square.__init__(possible)
                    self.prune()

    def find_singletons(self):
        """Check if there are any numbers which are possibilities in only one
        square in a row, column, or region."""
        for section in self.rows + self.columns + self.regions:
            self.find_singleton_in_section(section)

    @staticmethod
    def find_singleton_in_section(section):
        counters = Puzzle.count_each_possibility(section)
        for option, ocurrences in enumerate(counters):
            if ocurrences == 1:
                for square in section:
                    if option in square:
                        square.__init__([option])

    def fix_square(self, index, value):
        """Fix the square at the given index to the given value."""
        self.squares[index].__init__([value])
        self.prune_section(self.rows[index // self.width])
        self.prune_section(self.columns[index % self.width])
        for region in self.regions:
            for square in region:
                if square is self.squares[index]:
                    self.prune_section(region)
                    return

    def guess(self, square):
        """Try all possible values of the square at the and eliminate any
        possibilities that result in inconsistent puzzles."""
        return self.guess_by_index(self.square_index(square))

    def guess_by_index(self, index):
        """Try all possible values of the square at the given index and
        eliminate any possibilities that result in inconsistent puzzles."""
        possible = []
        for option in self.squares[index]:
            temp = deepcopy(self)
            #Because references to temp.squares[index] exist elsewhere, simple
            #assignment is unsatisfactory.
            temp.squares[index].__init__([option])
            temp.prune()
            if temp.is_solved():
                return [option]
            if temp.is_consistent():
                possible.append(option)
        return possible

    def in_same_region(self, square1, square2):
        """Check that square1 and square2 are in the same region."""
        for region in self.regions:
            for candidate in region:
                if square1 is candidate:
                    return any(square is square2 for square in region)

    def is_consistent(self):
        """Check that the puzzle is logically consistent based on its current
        state (that it has no squares with zero possibilities)."""
        return all(len(square) > 0 for square in self.squares)

    def is_solved(self):
        """Check if the puzzle is solved by ensuring that there is only one
        possiblity for each square."""
        return all(len(square) == 1 for square in self.squares)

    def prune(self):
        """Remove as many impossible choices from as many squares as
        possible."""
        possibilities = -1
        while possibilities != self.count_possibilities():
            possibilities = self.count_possibilities()
            self.prune_sections()
            self.find_singletons()

    @staticmethod
    def prune_section(section):
        """Remove impossible choices from the specified section."""
        unavailable = set(list(square)[0] for square in section
                          if len(square) == 1)
        for square in section:
            if len(square) > 1:
                square -= unavailable

    def prune_sections(self):
        """Remove impossible choices from the board."""
        possibilities = -1
        while possibilities != self.count_possibilities():
            possibilities = self.count_possibilities()
            for section in self.rows + self.columns + self.regions:
                self.prune_section(section)

    def solve(self):
        """Attempt to solve the puzzle, by pruning invalid choices and guessing
        at the rest.  In some rare cases, this method may not actually solve
        the puzzle."""
        self.prune()
        possibilities = self.count_possibilities()
        while not self.is_solved():
            if not self.is_consistent():
                raise InconsistentPuzzleError
            self.easy_guesses()
            if self.count_possibilities() == possibilities:
                break

    def square_index(self, square):
        """Find the index of the given square."""
        for index, candidate in enumerate(self.squares):
            if candidate is square:
                return candidate
        raise ValueError(square + " is not a member of this puzzle.")

    def state(self):
        """Return a detailed description of the progress on the puzzle."""
        return "\n".join(StateDumper(self.rows, self.values))

class StateDumper(object):
    """Generates a detailed description of the progress on the puzzle."""
    def __init__(self, rows, values):
        self.rows = rows
        self.width = len(rows[0])
        self.values = values
        self.cheight = 3
        self.cwidth = int(ceil(float(self.width) / self.cheight))
        if self.width % self.cwidth == 0:
            self.cheight = self.width / self.cwidth
        self.vwidth = max(len(value) for value in self.values)
        sep_length = self.width * self.cwidth * (self.vwidth + 1) - 1
        self.seperator = "-" * sep_length

    def __iter__(self):
        for row in self.irows():
            yield row
            yield self.seperator

    def ilines(self, row):
        for line in xrange(self.cheight):
            yield "|".join(self.isquarelines(row, line))

    def irows(self):
        """Generates each of the rows of the puzzle."""
        for row in self.rows:
            yield "\n".join(self.ilines(row))

    def isquarelines(self, row, line):
        for square in row:
            yield " ".join(self.ivalues(square, line))

    def ivalues(self, square, line):
        str_value = lambda index: str.rjust(self.values[index], self.vwidth)
        for j in xrange(self.cwidth):
            target = line * self.cwidth + j
            yield str_value(target) if target in square else " " * self.vwidth

class InconsistentPuzzleError(Exception): pass
class InvalidPuzzleError(Exception): pass
class ParseError(mapfile.ParseError):
    def __init__(self, *args, **kwargs):
        super(ParseError, self).__init__(*args, **kwargs)

if __name__ == "__main__":
    main()

#******************************************************************************
#********************************* UNIT TESTS *********************************
#******************************************************************************

from os import listdir
from py.test import raises

SUDOKU_DIR = "/home/karl/Programming/sudoku/"

puzzles = {}
bad_puzzles = {}
for puzzle_file in listdir(SUDOKU_DIR):
    puzzle_string = open(SUDOKU_DIR + puzzle_file).read()
    if ".sdk" in puzzle_file:
        if "bad" in puzzle_file:
            bad_puzzles[puzzle_file.split(".")[0]] = puzzle_string
        else:
            puzzles[puzzle_file.split(".")[0]] = puzzle_string

def test():
    for puzzle in puzzles.itervalues():
        puzzle = load(puzzle)
        puzzle.solve()
        assert puzzle.is_solved()
    for puzzle in bad_puzzles.itervalues():
        raises(InconsistentPuzzleError, load(puzzle).solve)

def test_dims_to_regions():
    wanted = [[0, 1, 2, 6, 7, 8], [3, 4, 5, 9, 10, 11],
              [12, 13, 14, 18, 19, 20], [15, 16, 17, 21, 22, 23],
              [24, 25, 26, 30, 31, 32], [27, 28, 29, 33, 34, 35]]
    wanted = [set(item) for item in wanted]
    found = [set(item) for item in dims_to_regions(2, 3)]
    for item in wanted:
        assert item in found
    assert 6 == len(dims_to_regions(2, 3))
    assert all(len(region) == 6 for region in dims_to_regions(2, 3))
    assert 12 == len(dims_to_regions(3, 4))
    assert all(len(region) == 12 for region in dims_to_regions(3, 4))

def test_load_regions():
    assert None == load_regions("")
    assert 2, 3 == load_regions("2x3")
    assert 3, 4 == load_regions("3 x  4")
    assert [[1, 2, 3], [4, 5, 6]] == load_regions("1 2 3, 4 5 6")

def test_load_squares():
    squares = """1 | . | .
                 --+---+--
                 2 | . | .
                 --+---+--
                 3 | . | ."""
    wanted = [0, None, None, 1, None, None, 2, None, None]
    assert wanted == load_squares(squares, ['1', '2', '3'])
    raises(ParseError, load_squares, "123456789", [str(i) for i in xrange(10)])

#Tests for Dumper

#Tests for Puzzle
def test_Puzzle_init():
    def npuzzle(width):
        return [i % width if i % width == i // width else None
                for i in xrange(width ** 2)]
    #These should succeed
    Puzzle([0])
    Puzzle(npuzzle(4))
    Puzzle(npuzzle(9))
    Puzzle([0], [1, 1])
    Puzzle([1, 0, 0, 1], [1, 2])
    Puzzle(npuzzle(3), [3, 1])
    Puzzle(npuzzle(4), [2, 2])
    Puzzle([1, 0, 0, 1], [[0, 3], [1, 2]])
    Puzzle(npuzzle(3), [[0, 4, 8], [1, 2, 6], [3, 5, 7]])
    #These should not
    raises(InvalidPuzzleError, Puzzle, [1])#Value too large
    raises(InvalidPuzzleError, Puzzle, [1, 2, 3])#Not square
    raises(InvalidPuzzleError, Puzzle, [1, 2, 3], [1, 1])#Not square
    raises(InvalidPuzzleError, Puzzle, [1, 0, 0, 1], [0, 0])#Region
    raises(InvalidPuzzleError, Puzzle, [1, 0, 0, 1], [2, 2])#Region
    raises(InvalidPuzzleError, Puzzle, [0], [[1]])#Region
    raises(InvalidPuzzleError, Puzzle, [1, 0, 0, 1], [[0, 1, 2], [3]])#Region
    raises(InvalidPuzzleError, Puzzle, [1, 0, 0, 1], [[0, 1], [1, 2]])#Region

def test_Puzzle_eq():
    puzzle1 = Puzzle([1] + [None]*4 + [2] + [None]*4 + [3] + [None]*4 + [0])
    puzzle1a = Puzzle([1] + [None]*4 + [2] + [None]*4 + [3] + [None]*4 + [0])
    assert puzzle1 == puzzle1a
    assert puzzle1a == puzzle1
    puzzle2 = Puzzle([1] + [None]*4 + [2] + [None]*4 + [3] + [None]*4 + [3])
    assert puzzle1 != puzzle2
    assert puzzle2 != puzzle1
    puzzle3 = Puzzle([1] + [None]*4 + [2] + [None]*4 + [3] + [None]*3 + [1, 0])
    assert puzzle3 != puzzle1
    assert puzzle1 != puzzle3

def test_Puzzle_count_possibilities():
    assert 64 == Puzzle([None] * 16).count_possibilities()
    puzzle = Puzzle([1] + [None]*4 + [2] + [None]*4 + [3] + [None]*4 + [0])
    assert 48 == puzzle.count_possibilities()
    assert 0 == Puzzle([0] * 16).count_possibilities()
    puzzle = Puzzle([2, None, None, 0, 1, None, None, None, None, None, None,
                     1, 0, None, None, 2])
    assert 40 == puzzle.count_possibilities()

def test_Puzzle_count_each_possibility():
    section = [set([5]), set([0]), set([1, 2, 3, 4]), set([2, 3, 4]),
                   set([1, 2, 3, 4]), set([1, 2])]
    counts = [1, 3, 4, 3, 3, 1]
    assert counts == Puzzle.count_each_possibility(section)

def test_Puzzle_find_singletons():
    wanted = [set([5]), set([2]), set([3]), set([0]), set([4]), set([1]),
              set([0]), set([4]), set([1]), set([2, 3]), set([2, 5]),
              set([2, 3, 5]), set([1]), set([0]), set([2, 4, 5]),
              set([2, 3, 4]), set([2, 5]), set([2, 3, 5]), set([2, 3, 4]),
              set([3, 5]), set([2, 4, 5]), set([2, 3, 4]), set([1]), set([0]),
              set([1, 2, 3, 4]), set([1, 3]), set([0, 2, 4]), set([5]),
              set([0]), set([2]), set([1, 2]), set([5]), set([0]), set([1]),
              set([3]), set([4])]
    puzzle = load(puzzles["6"])
    puzzle.prune_sections()
    puzzle.find_singletons()
    assert wanted == puzzle.squares

def test_Puzzle_find_singletons_in_section():
    starts = [[set([1]), set([0, 3, 5]), set([2, 3, 5]), set([2, 3, 5]),
              set([2, 3, 5]), set([4])],
             [set([5]), set([0]), set([1, 2, 3, 4]), set([2, 3, 4]),
              set([1, 2, 3, 4]), set([1, 2])],
             [set([ 2, 3, 4]), set([3, 5]), set([2, 4, 5]), set([2, 3, 4]),
              set([1]), set([0, 2, 3, 5])],
             [set([5]), set([0]), set([1, 2, 3, 4]), set([2, 3, 4]),
              set([2, 3, 4]), set([2, 3])]]
    wanteds = [[set([1]), set([0]), set([2, 3, 5]), set([2, 3, 5]),
               set([2, 3, 5]), set([4])],
              [set([5]), set([0]), set([1, 2, 3, 4]), set([2, 3, 4]),
               set([1, 2, 3, 4]), set([1, 2])],
              [set([2, 3, 4]), set([3, 5]), set([2, 4, 5]), set([2, 3, 4]),
               set([1]), set([0])],
              [set([5]), set([0]), set([1]), set([2, 3, 4]),
               set([2, 3, 4]), set([2, 3])]]
    for start, wanted in zip(starts, wanteds):
        Puzzle.find_singleton_in_section(start)
        assert wanted == start

def test_Puzzle_fix_square():
    puzzle = Puzzle([None] * 16)
    puzzle.fix_square(9, 2)
    wanted = [set([0, 1, 2, 3])] * 16
    wanted[9] = set([2])
    for i in (1, 5, 8, 10, 11, 12, 13):
        wanted[i] = set([0, 1, 3])
    assert wanted == puzzle.squares
    puzzle.fix_square(4, 1)
    puzzle.fix_square(14, 3)
    wanted = [None, None, 1, 2, 1, 2, 0, 3, None, None, 2, None, None, None, 3,
              None]
    for i, (ws, s) in enumerate(zip(wanted, puzzle.squares)):
        if ws is not None:
            print i, ws == list(s)[0], ws, s

def test_Puzzle_guess_by_index():
    puzzle = load(puzzles["16"])
    wanteds = [[12], [9], [1, 3, 4, 15], [6],
               [0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12], [13],
               [0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 13], [0, 1, 5, 10],
               [0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15],
               [0, 1, 2, 3, 4, 5, 7, 8, 9, 11, 12, 13, 14, 15],
               [0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15],
               [0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15], [14],
               [0, 3, 4, 7, 8, 12, 13, 14], [0],
               [0, 1, 5, 6, 8, 9, 10, 12, 14, 15]]
    assert all(found == wanted for found, wanted in
               zip((puzzle.guess_by_index(i) for i, square in
                    enumerate(puzzle.rows[0])), wanteds))

def test_Puzzle_in_same_region():
    puzzle = load(puzzles["6"])
    for region1 in puzzle.regions:
        for region2 in puzzle.regions:
            if region2 is region1:
                for square1 in region1:
                    for square2 in region2:
                        assert puzzle.in_same_region(square1, square2)
            else:
                for square1 in region1:
                    for square2 in region2:
                        assert not puzzle.in_same_region(square1, square2)

def test_Puzzle_is_consistent():
    assert Puzzle([0]).is_consistent()
    two = Puzzle([0, 1, 1, 0], [1, 2])
    assert two.is_consistent()
    two.squares[0].clear()
    assert not two.is_consistent()
    consistent = Puzzle([2, None, None, 0, 1, None, None, None, None, None, None,
                     1, 0, None, None, 2])
    consistent.prune()
    assert consistent.is_consistent()
    inconsistent = Puzzle([2, None, None, None, 0, 0, None, None, 1], [1, 3])
    inconsistent.prune()
    assert not inconsistent.is_consistent()

def test_Puzzle_is_solved():
    assert Puzzle([0]).is_solved()
    assert Puzzle([1, 0, 0, 1], [2, 1]).is_solved()
    assert not Puzzle([1, 0, 0, None], [2, 1]).is_solved()
    puzzle = Puzzle([2, None, None, 0, 1, None, None, None, None, None, None,
                     1, 0, None, None, 2])
    puzzle.solve()
    assert puzzle.is_solved()

def test_Puzzle_prune_section():
    target = [[None] * 9, [1, None, None, None, 2, None, 5, None, None],
               range(9)]
    rows = [[set(xrange(9)) if i is None else set([i]) for i in row]
                    for row in target]
    Puzzle.prune_section(rows[0])
    for square in rows[0]:
        assert set(xrange(9)) == square
    Puzzle.prune_section(rows[1])
    for wanted, square in zip(target[1], rows[1]):
        if wanted is None:
            assert set([0, 3, 4, 6, 7, 8]) == square
        else:
            assert set([wanted]) == square
    Puzzle.prune_section(rows[2])
    for i, square in enumerate(rows[2]):
        assert set([i]) == square

def test_Puzzle_prune_sections():
    wanted = [set([5]), set([2]), set([3]), set([0]), set([4]), set([1]),
              set([0]), set([4]), set([1]), set([2, 3]), set([2, 5]),
              set([2, 3, 5]), set([1, 2, 3, 4]), set([0]), set([2, 4, 5]),
              set([2, 3, 4]), set([2, 5]), set([2, 3, 5]), set([2, 3, 4]),
              set([3, 5]), set([2, 4, 5]), set([2, 3, 4]), set([1]),
              set([0, 2, 3, 5]), set([1, 2, 3, 4]), set([1, 3]), set([0, 2, 4]),
              set([5]), set([0, 2]), set([0, 2]), set([1, 2]), set([1, 5]),
              set([0, 2, 5]), set([1, 2]), set([3]), set([4])]
    puzzle = load(puzzles["6"])
    puzzle.prune_sections()
    assert wanted == puzzle.squares
