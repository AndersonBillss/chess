package chess;

import java.io.Console;
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
        this.board = new ChessBoard();
        board.resetBoard();
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
        var pieceMoves = piece.pieceMoves(board, startPosition);
        var teamColor = piece.getTeamColor();
        ArrayList<ChessMove> result = new ArrayList<>();
        for (var move : pieceMoves) {
            var boardCopy = new ChessBoard(board);
            var movingPiece = boardCopy.getPiece(move.getStartPosition());
            boardCopy.addPiece(move.getStartPosition(), null);
            boardCopy.addPiece(move.getEndPosition(), movingPiece);
            if (!isInCheck(teamColor, boardCopy)) {
                result.add(move);
            }
        }
        pieceMoves.addAll(castlingMoves(startPosition));
        return result;
    }

    private Collection<ChessMove> castlingMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        var king = board.getPiece(position);
        if (king.getPieceType() != ChessPiece.PieceType.KING) {
            return moves;
        }
        if (king.getTimesMoved() > 0) {
            return moves;
        }

        var leftRookPos = position.add(new ChessPosition(0, 4));
        var leftRook = board.getPiece(leftRookPos);
        // The two spaces to the left should be clear
        ChessPosition[] clearSpacesLeft = new ChessPosition[]{
                position.add(new ChessPosition(0, -1)),
                position.add(new ChessPosition(0, -2)),
                position.add(new ChessPosition(0, -3)),
        };
        ChessPosition[] positionsNotInCheckLeft = new ChessPosition[]{
                position,
                position.add(new ChessPosition(0, -1)),
                position.add(new ChessPosition(0, -2)),
                position.add(new ChessPosition(0, -3)),
                leftRookPos,
        };
        if (checkCastleRules(clearSpacesLeft, positionsNotInCheckLeft, leftRook)) {
            moves.add(new ChessMove(position, position.add(new ChessPosition(0, -2)), null));
        }

        var rightRookPos = position.add(new ChessPosition(0, 4));
        var rightRook = board.getPiece(leftRookPos);
        // The two spaces to the left should be clear
        ChessPosition[] clearSpacesRight = new ChessPosition[]{
                position.add(new ChessPosition(0, 1)),
                position.add(new ChessPosition(0, 2)),
        };
        ChessPosition[] positionsNotInCheckRight = new ChessPosition[]{
                position,
                position.add(new ChessPosition(0, 1)),
                position.add(new ChessPosition(0, 2)),
                rightRookPos,
        };
        if (checkCastleRules(clearSpacesRight, positionsNotInCheckRight, rightRook)) {
            moves.add(new ChessMove(position, position.add(new ChessPosition(0, 2)), null));
        }

        return moves;
    }

    private boolean checkCastleRules(ChessPosition[] clearSpaces, ChessPosition[] positionsNotInCheck, ChessPiece piece) {
        if (piece.getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }
        if (piece.getTimesMoved() > 0) {
            return false;
        }
        if (!isClear(clearSpaces)) {
            return false;
        }
        var opposingTeam = piece.getTeamColor() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        var allOpposingMoves = getAllMovePositions(opposingTeam, board);
        for (var pos : positionsNotInCheck) {
            if (allOpposingMoves.contains(pos)) {
                return false;
            }
        }
        return true;
    }

    private boolean isClear(ChessPosition[] positions) {
        for (var pos : positions) {
            if (board.getPiece(pos) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (move == null) {
            throw new InvalidMoveException("Null move");
        }
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Cannot find piece at starting position");
        }
        var valid = validMoves(move.getStartPosition());
        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        var movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Wrong turn");
        }
        // Reset all pawns that can be taken by en passant
        var opposingPawns = findPieces(movingPiece.getTeamColor(), ChessPiece.PieceType.PAWN);
        for (var pawnPos : opposingPawns) {
            movingPiece.setTimesMoved(movingPiece.getTimesMoved() + 1);
            var pawnPiece = board.getPiece(pawnPos);
            pawnPiece.setCanBeTakenWithEnPassant(false);
        }

        // If the piece is a pawn moving forward two, it can be taken with en passant
        boolean pawnMoveForwardTwo = movingPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && move.getEndPosition().equals(move.getStartPosition().add(movingPiece.forward().mul(2)));
        if (pawnMoveForwardTwo) {
            movingPiece.setCanBeTakenWithEnPassant(true);
        }
        boolean isEnPassantMove = movingPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && move.getEndPosition().getRow() != move.getStartPosition().getRow()
                && move.getEndPosition().getColumn() != move.getStartPosition().getColumn()
                && board.getPiece(move.getEndPosition()) == null;
        if (isEnPassantMove) {
            board.addPiece(move.getEndPosition(), movingPiece);
            board.addPiece(move.getEndPosition().add(movingPiece.backward()), null);
        } else if (move.getPromotionPiece() != null) {
            ChessPiece newPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), newPiece);
        } else {
            board.addPiece(move.getEndPosition(), movingPiece);
        }
        board.addPiece(move.getStartPosition(), null);

        // Next player's turn
        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, this.board);
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        TeamColor opposingTeam = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
        Set<ChessPosition> opposingTeamPotentialMoves = getAllMovePositions(opposingTeam, board);
        var king = findPieces(teamColor, ChessPiece.PieceType.KING, board).getFirst();
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
        var kingPos = findPieces(teamColor, ChessPiece.PieceType.KING).getFirst();

        Set<ChessPosition> opposingTeamMoves = getAllMovePositions(opposingTeam, board);
        if (!opposingTeamMoves.contains(kingPos)) {
            return false;
        }

        var blackMoves = getAllMoves(teamColor, board);
        for (var move : blackMoves) {
            // Make the move on a copy of the board
            var boardCopy = new ChessBoard(board);
            var movingPiece = boardCopy.getPiece(move.getStartPosition());
            boardCopy.addPiece(move.getStartPosition(), null);
            boardCopy.addPiece(move.getEndPosition(), movingPiece);
            opposingTeamMoves = getAllMovePositions(opposingTeam, boardCopy);

            kingPos = findPieces(teamColor, ChessPiece.PieceType.KING, boardCopy).getFirst();

            if (!opposingTeamMoves.contains(kingPos)) {
                return false;
            }
        }

        return true;
    }

    private ArrayList<ChessPosition> findPieces(TeamColor teamColor) {
        return findPieces(teamColor, null, this.board);
    }

    private ArrayList<ChessPosition> findPieces(TeamColor teamColor, ChessPiece.PieceType type) {
        return findPieces(teamColor, type, this.board);
    }

    private ArrayList<ChessPosition> findPieces(TeamColor teamColor, ChessPiece.PieceType type, ChessBoard board) {
        ArrayList<ChessPosition> pieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition cell = new ChessPosition(i, j);
                var piece = board.getPiece(cell);
                if (piece == null) {
                    continue;
                }
                boolean correctTeamColor = piece.getTeamColor() == teamColor;
                if (type == null && correctTeamColor) {
                    pieces.add(cell);
                    continue;
                }
                if (piece.getPieceType() == type && correctTeamColor) {
                    pieces.add(cell);
                }
            }
        }
        return pieces;
    }

    private Set<ChessMove> getAllMoves(TeamColor teamColor, ChessBoard board) {
        Set<ChessMove> allMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var piecePos = new ChessPosition(i, j);
                var piece = board.getPiece(piecePos);
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() == teamColor) {
                    var pieceMoves = piece.pieceMoves(board, piecePos);
                    allMoves.addAll(pieceMoves);
                }
            }
        }
        return allMoves;
    }

    private Set<ChessPosition> getAllMovePositions(TeamColor teamColor, ChessBoard board) {
        Set<ChessMove> allMoves = getAllMoves(teamColor, board);
        Set<ChessPosition> allMovePositions = new HashSet<>();
        for (var move : allMoves) {
            allMovePositions.add(move.getEndPosition());
        }
        return allMovePositions;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor != this.teamTurn) {
            return false;
        }
        var pieces = findPieces(teamColor);
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (var piece : pieces) {
            moves.addAll(validMoves(piece));
        }

        if (moves.isEmpty()) {
            var check = isInCheck(teamColor);
            return !check;
        }
        return false;
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
