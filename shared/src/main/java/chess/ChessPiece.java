package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        final Map<PieceType, String> pieceToString = Map.of(
                PieceType.KING, "k",
                PieceType.QUEEN, "q",
                PieceType.BISHOP, "b",
                PieceType.KNIGHT, "n",
                PieceType.ROOK, "r",
                PieceType.PAWN, "p");

        String pieceString = pieceToString.get(type);
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            pieceString = pieceString.toUpperCase();
        }
        return pieceString;
    }

    private ChessPosition forward() {
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            return new ChessPosition(-1, 0);
        }
        return new ChessPosition(1, 0);
    }

    private ChessPosition backward() {
        ChessPosition forward = forward();
        return new ChessPosition(-forward.getRow(), forward.getColumn());
    }

    private ChessPosition left() {
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            return new ChessPosition(0, -1);
        }
        return new ChessPosition(0, 1);
    }

    private ChessPosition right() {
        ChessPosition left = left();
        return new ChessPosition(left.getRow(), -left.getColumn());
    }


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var piece = board.getPiece(myPosition);
        if (piece.type == PieceType.ROOK) {
            return rookMoves(board, myPosition, piece.getTeamColor());
        } else if (piece.type == PieceType.BISHOP) {
            return bishopMoves(board, myPosition, piece.getTeamColor());
        } else if (piece.type == PieceType.QUEEN) {
            return queenMoves(board, myPosition, piece.getTeamColor());
        } else if (piece.type == PieceType.KING) {
            return kingMoves(board, myPosition, piece.getTeamColor());
        } else if (piece.type == PieceType.KNIGHT) {
            return knightMoves(board, myPosition, piece.getTeamColor());
        } else if (piece.type == PieceType.PAWN) {
            return pawnMoves(board, myPosition, piece.getTeamColor());
        }
        return List.of();
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] directions = {
                forward(),
                backward(),
                left(),
                right(),
        };

        for (var direction : directions) {
            moves.addAll(moveLine(board, position, teamColor, direction));
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] directions = {
                forward().add(right()),
                forward().add(left()),
                backward().add(right()),
                backward().add(left()),
        };

        for (var direction : directions) {
            moves.addAll(moveLine(board, position, teamColor, direction));
        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = bishopMoves(board, position, teamColor);
        moves.addAll(rookMoves(board, position, teamColor));
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] directions = {
                forward().add(right()),
                forward().add(left()),
                backward().add(right()),
                backward().add(left()),
                forward(),
                backward(),
                left(),
                right(),
        };

        for (var direction : directions) {
            var newPosition = position.add(direction);
            if (canOvertake(board, newPosition, teamColor)) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] directions = {
                forward().mul(2).add(left()),
                forward().mul(2).add(right()),
                backward().mul(2).add(right()),
                backward().mul(2).add(left()),
                left().mul(2).add(forward()),
                left().mul(2).add(backward()),
                right().mul(2).add(forward()),
                right().mul(2).add(backward()),
        };

        for (var direction : directions) {
            var newPosition = position.add(direction);
            if (canOvertake(board, newPosition, teamColor)) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition forwardOne = position.add(forward());
        if (board.inBounds(forwardOne) && board.getPiece(forwardOne) == null) {
            moves.addAll(pawnPromotion(board, position, forwardOne, teamColor));
        }
        ChessPosition backwardOne = position.add(backward());
        ChessPosition backwardTwo = position.add(backward().mul(2));
        ChessPosition forwardTwo = position.add(forward().mul(2));
        var canMoveTwo = !board.inBounds(backwardTwo) && board.inBounds(backwardOne);
        if (canMoveTwo && board.getPiece(forwardTwo) == null && board.getPiece(forwardOne) == null) {
            moves.add(new ChessMove(position, forwardTwo, null));
        }

        var forwardLeft = position.add(forward().add(left()));
        var forwardRight = position.add(forward().add(right()));
        if (canOvertake(board, forwardLeft, teamColor) && board.getPiece(forwardLeft) != null) {
            moves.addAll(pawnPromotion(board, position, forwardLeft, teamColor));
        }
        if (canOvertake(board, forwardRight, teamColor) && board.getPiece(forwardRight) != null) {
            moves.addAll(pawnPromotion(board, position, forwardRight, teamColor));
        }
        return moves;
    }

    private Collection<ChessMove> pawnPromotion(ChessBoard board, ChessPosition oldPosition, ChessPosition newPosition, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        var forwardTwo = newPosition.add(forward().mul(2));
        var canPromote = !board.inBounds(forwardTwo);
        if (canPromote) {
            moves.add(new ChessMove(oldPosition, newPosition, PieceType.QUEEN));
            moves.add(new ChessMove(oldPosition, newPosition, PieceType.BISHOP));
            moves.add(new ChessMove(oldPosition, newPosition, PieceType.ROOK));
            moves.add(new ChessMove(oldPosition, newPosition, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(oldPosition, newPosition, null));
        }
        return moves;
    }

    private boolean canOvertake(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        if (!board.inBounds(position)) {
            return false;
        }
        var piece = board.getPiece(position);
        if (piece == null) {
            return true;
        }
        return piece.getTeamColor() != teamColor;
    }

    private Collection<ChessMove> moveLine(
            ChessBoard board,
            ChessPosition position,
            ChessGame.TeamColor teamColor, ChessPosition direction) {
        Collection<ChessMove> moves = new ArrayList<>();

        var newMove = position.add(direction);
        if (!board.inBounds(newMove)) {
            return moves;
        }
        while (canOvertake(board, newMove, teamColor)) {
            moves.add(new ChessMove(position, newMove, null));
            if (!board.inBounds(newMove) || board.getPiece(newMove) != null) {
                break;
            }
            newMove = newMove.add(direction);
        }
        return moves;
    }
}
