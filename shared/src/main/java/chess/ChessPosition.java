package chess;

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

    static public ChessPosition forward(ChessGame.TeamColor team) {
        if (team == ChessGame.TeamColor.BLACK) {
            return new ChessPosition(0, -1);
        }
        return new ChessPosition(0, 1);
    }

    static public ChessPosition backward(ChessGame.TeamColor team) {
        ChessPosition forward = forward(team);
        return new ChessPosition(forward.getRow(), -forward.getColumn());
    }

    static public ChessPosition left(ChessGame.TeamColor team) {
        if (team == ChessGame.TeamColor.BLACK) {
            return new ChessPosition(1, 0);
        }
        return new ChessPosition(-1, 0);
    }

    static public ChessPosition right(ChessGame.TeamColor team) {
        ChessPosition left = left(team);
        return new ChessPosition(-left.getRow(), left.getColumn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "{" + row + ", " + col + "}";
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
}
