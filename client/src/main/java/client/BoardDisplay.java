package client;

import chess.*;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BoardDisplay {
    private static final String BORDER_ESC = String.format("%s%s%s",
            EscapeSequences.SET_BG_COLOR_WHITE,
            EscapeSequences.SET_TEXT_COLOR_BLACK,
            EscapeSequences.SET_TEXT_BOLD);
    private static final String CELL_WHITE_ESC = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String CELL_BLACK_ESC = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String HIGHLIGHT_ESC = EscapeSequences.SET_BG_COLOR_YELLOW;
    private static final String MOVE_LIGHT_ESC = EscapeSequences.SET_BG_COLOR_GREEN;
    private static final String MOVE_DARK_ESC = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    private static final String RESET_ESC = String.format("%s%s%s",
            EscapeSequences.RESET_TEXT_COLOR,
            EscapeSequences.RESET_BG_COLOR,
            EscapeSequences.RESET_TEXT_BOLD_FAINT);
    private static final String BORDER_ROW = String.format(
            "%s  %sa %sb %sc %sd %se %sf %sg %sh     %s",
            BORDER_ESC,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            RESET_ESC
    );
    private static final String BORDER_ROW_REVERSE = String.format(
            "%s  %sh %sg %sf %se %sd %sc %sb %sa     %s",
            BORDER_ESC,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            EscapeSequences.EMPTY,
            RESET_ESC
    );

    public static void show(ChessGame game, ChessGame.TeamColor color) {
        show(game, color, null);
    }

    public static void show(
            ChessGame game,
            ChessGame.TeamColor color,
            ChessPosition revealPosition) {

        Collection<ChessPosition> possiblePositions = new ArrayList<>();
        if (revealPosition != null) {
            var possibleMoves = game.validMoves(revealPosition);
            for (var move : possibleMoves) {
                possiblePositions.add(move.getEndPosition());
            }
        }
        if (color == ChessGame.TeamColor.WHITE) {
            System.out.println(BORDER_ROW);
        } else {
            System.out.println(BORDER_ROW_REVERSE);
        }

        for (int i = 0; i < 16; i++) {
            boolean onRowTop = i % 2 == 0;
            int row = i / 2;
            if (color == ChessGame.TeamColor.WHITE) {
                row = 8 - row - 1;
            }
            String borderCell = String.format(
                    "%s %d %s",
                    BORDER_ESC,
                    row + 1,
                    RESET_ESC
            );
            if (onRowTop) {
                borderCell = String.format(
                        "%s   %s",
                        BORDER_ESC,
                        RESET_ESC);
            }
            System.out.print(borderCell);
            for (int j = 0; j < 8; j++) {
                int col = j;
                if (color == ChessGame.TeamColor.BLACK) {
                    col = 8 - col - 1;
                }
                int rowPattern = row % 2 == 0 ? 1 : -1;
                int colPattern = col % 2 == 0 ? 1 : -1;
                boolean isWhiteCell = rowPattern * colPattern == -1;
                ChessPosition currPosition = new ChessPosition(row + 1, col + 1);
                String cellEsc = isWhiteCell ? CELL_WHITE_ESC : CELL_BLACK_ESC;
                boolean isHighlighted = Objects.equals(currPosition, revealPosition);
                boolean isRevealed = possiblePositions.contains(currPosition);
                if (isHighlighted) {
                    cellEsc = HIGHLIGHT_ESC;
                } else if (isRevealed) {
                    cellEsc = isWhiteCell ? MOVE_LIGHT_ESC : MOVE_DARK_ESC;
                }
                System.out.printf("%s ", cellEsc);
                ChessPiece piece = game.getBoard().getPiece(currPosition);
                if (onRowTop) {
                    System.out.print(EscapeSequences.EMPTY);
                } else {
                    printCell(piece, isHighlighted, isRevealed, isWhiteCell);
                }
                System.out.print(" ");
            }
            System.out.println(borderCell);
        }

        if (color == ChessGame.TeamColor.WHITE) {
            System.out.println(BORDER_ROW);
        } else {
            System.out.println(BORDER_ROW_REVERSE);
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private static void printCell(ChessPiece piece,
                                  boolean isHighlighted,
                                  boolean isRevealed,
                                  boolean isWhiteCell) {
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            String esc = switch (piece.getPieceType()) {
                case ChessPiece.PieceType.PAWN -> EscapeSequences.BLACK_PAWN;
                case ChessPiece.PieceType.ROOK -> EscapeSequences.BLACK_ROOK;
                case ChessPiece.PieceType.KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ChessPiece.PieceType.BISHOP -> EscapeSequences.BLACK_BISHOP;
                case ChessPiece.PieceType.KING -> EscapeSequences.BLACK_KING;
                case ChessPiece.PieceType.QUEEN -> EscapeSequences.BLACK_QUEEN;
            };
            System.out.printf("%s%s", EscapeSequences.SET_TEXT_COLOR_BLACK, esc);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            String esc = switch (piece.getPieceType()) {
                case ChessPiece.PieceType.PAWN -> EscapeSequences.WHITE_PAWN;
                case ChessPiece.PieceType.ROOK -> EscapeSequences.WHITE_ROOK;
                case ChessPiece.PieceType.KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ChessPiece.PieceType.BISHOP -> EscapeSequences.WHITE_BISHOP;
                case ChessPiece.PieceType.KING -> EscapeSequences.WHITE_KING;
                case ChessPiece.PieceType.QUEEN -> EscapeSequences.WHITE_QUEEN;
            };
            if (isHighlighted || (isRevealed && isWhiteCell)) {
                System.out.printf("%s%s", EscapeSequences.SET_TEXT_COLOR_DARK_GREY, esc);
            } else {
                System.out.printf("%s%s", EscapeSequences.SET_TEXT_COLOR_WHITE, esc);
            }
        }
    }
}
