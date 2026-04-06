package chess;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        Map<Integer, String> colToString = Map.of(
                1, "a",
                2, "b",
                3, "c",
                4, "d",
                5, "e",
                6, "f",
                7, "g",
                8, "h"
        );
        return colToString.get(col) + row;
    }

    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public ChessPosition add(ChessPosition position) {
        return new ChessPosition(row + position.row, col + position.col);
    }

    public ChessPosition mul(int n) {
        return new ChessPosition(row * n, col * n);
    }
}
