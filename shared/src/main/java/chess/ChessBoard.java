package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String finalString = "";
        for (int i = 7; i > -1; i--) {
            var row = board[i];
            finalString += "|";
            for (var col : row) {
                if (col == null) {
                    finalString += " ";
                } else {
                    finalString += col;
                }
                ;
                finalString += "|";
            }
            finalString += "\n";
        }
        return finalString;
    }

    public ChessBoard() {
        board = new ChessPiece[8][8];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean inBounds(ChessPosition position) {
        return position.getRow() >= 1 && position.getColumn() >= 1 && position.getRow() <= 8 && position.getColumn() <= 8;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clearBoard();
        ChessPiece.PieceType[][] pieceTypes = {
                {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK},
                {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN}
        };

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, pieceTypes[i][j]);
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                int flippedIndexPosition = 7 - i;
                board[flippedIndexPosition][j] = new ChessPiece(ChessGame.TeamColor.BLACK, pieceTypes[i][j]);
            }
        }
    }
}
