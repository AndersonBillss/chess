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
        }
        return List.of();
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] directions = {
                ChessPosition.forward(teamColor),
                ChessPosition.backward(teamColor),
                ChessPosition.left(teamColor),
                ChessPosition.right(teamColor),
        };

        for (var direction : directions) {
            moves.addAll(moveLine(board, position, teamColor, direction));
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] directions = {
                ChessPosition.forward(teamColor).add(ChessPosition.right(teamColor)),
                ChessPosition.forward(teamColor).add(ChessPosition.left(teamColor)),
                ChessPosition.backward(teamColor).add(ChessPosition.right(teamColor)),
                ChessPosition.backward(teamColor).add(ChessPosition.left(teamColor)),
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
                ChessPosition.forward(teamColor).add(ChessPosition.right(teamColor)),
                ChessPosition.forward(teamColor).add(ChessPosition.left(teamColor)),
                ChessPosition.backward(teamColor).add(ChessPosition.right(teamColor)),
                ChessPosition.backward(teamColor).add(ChessPosition.left(teamColor)),
                ChessPosition.forward(teamColor),
                ChessPosition.backward(teamColor),
                ChessPosition.left(teamColor),
                ChessPosition.right(teamColor),
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
                ChessPosition.forward(teamColor).add(ChessPosition.forward(teamColor).add(ChessPosition.left(teamColor))),
                ChessPosition.forward(teamColor).add(ChessPosition.forward(teamColor).add(ChessPosition.right(teamColor))),
                ChessPosition.backward(teamColor).add(ChessPosition.backward(teamColor).add(ChessPosition.right(teamColor))),
                ChessPosition.backward(teamColor).add(ChessPosition.backward(teamColor).add(ChessPosition.left(teamColor))),
                ChessPosition.left(teamColor).add(ChessPosition.left(teamColor).add(ChessPosition.forward(teamColor))),
                ChessPosition.left(teamColor).add(ChessPosition.left(teamColor).add(ChessPosition.backward(teamColor))),
                ChessPosition.right(teamColor).add(ChessPosition.right(teamColor).add(ChessPosition.forward(teamColor))),
                ChessPosition.right(teamColor).add(ChessPosition.right(teamColor).add(ChessPosition.backward(teamColor))),
        };

        for (var direction : directions) {
            var newPosition = position.add(direction);
            if (canOvertake(board, newPosition, teamColor)) {
                moves.add(new ChessMove(position, newPosition, null));
            }
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
