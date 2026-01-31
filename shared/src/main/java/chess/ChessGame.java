package chess;

import java.lang.reflect.Array;
import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        // White starts first
        teamTurn = TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        var opposingTeam = piece.getTeamColor() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            Collection<ChessMove> kingMoves = piece.pieceMoves(board, startPosition);
            ArrayList<ChessMove> validKingMoves = new ArrayList<>();
            var opposingTeamMoves = getAllMoves(opposingTeam);
            for (var move : kingMoves) {
                if (!opposingTeamMoves.contains(move.getEndPosition())) {
                    validKingMoves.add(move);
                }
            }
            return validKingMoves;
        } else {
            return piece.pieceMoves(board, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var valid = validMoves(move.getStartPosition());
        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        }
        var movingPiece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), movingPiece);
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingTeam = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
        Set<ChessPosition> opposingTeamPotentialMoves = getAllMoves(opposingTeam);
        var king = findPieces(teamColor, ChessPiece.PieceType.KING).getFirst();
        return opposingTeamPotentialMoves.contains(king);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        TeamColor opposingTeam = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
        Set<ChessPosition> opposingTeamPotentialMoves = getAllMoves(opposingTeam);
        var kingPos = findPieces(teamColor, ChessPiece.PieceType.KING).getFirst();
        var king = board.getPiece(kingPos);
        ArrayList<ChessPosition> potentialKingPositions = new ArrayList<>();
        potentialKingPositions.add(kingPos);
        var kingMoves = king.pieceMoves(board, kingPos);
        for (var move : kingMoves) {
            potentialKingPositions.add(move.getEndPosition());
        }

        for (var pos : potentialKingPositions) {
            if (!opposingTeamPotentialMoves.contains(pos)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<ChessPosition> findPieces(TeamColor teamColor, ChessPiece.PieceType type) {
        ArrayList<ChessPosition> pieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition cell = new ChessPosition(i, j);
                var piece = board.getPiece(cell);
                if (piece.getPieceType() == type && piece.getTeamColor() == teamColor) {
                    pieces.add(cell);
                }
            }
        }
        return pieces;
    }

    private Set<ChessPosition> getAllMoves(TeamColor teamColor) {
        Set<ChessPosition> allMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var piecePos = new ChessPosition(i, j);
                var piece = board.getPiece(piecePos);
                if (piece.getTeamColor() == teamColor) {
                    var pieceMoves = piece.pieceMoves(board, piecePos);
                    for (var move : pieceMoves) {
                        allMoves.add(move.getEndPosition());
                    }
                }
            }
        }
        return allMoves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
