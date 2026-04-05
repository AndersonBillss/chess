package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

public class BoardDisplay {
    public static void show(ChessBoard board, ChessGame.TeamColor color) {
        String borderEsc = String.format("%s%s%s",
                EscapeSequences.SET_BG_COLOR_GREEN,
                EscapeSequences.SET_TEXT_COLOR_BLACK,
                EscapeSequences.SET_TEXT_BOLD);
        String cellWhiteEsc = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String cellBlackEsc = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        String resetEsc = String.format("%s%s%s",
                EscapeSequences.RESET_TEXT_COLOR,
                EscapeSequences.RESET_BG_COLOR,
                EscapeSequences.RESET_TEXT_BOLD_FAINT);
        String borderRow = String.format(
                "%s  %sa %sb %sc %sd %se %sf %sg %sh     %s",
                borderEsc,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                resetEsc
        );
        String borderRowReverse = String.format(
                "%s  %sh %sg %sf %se %sd %sc %sb %sa     %s",
                borderEsc,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                resetEsc
        );
        if (color == ChessGame.TeamColor.WHITE) {
            System.out.println(borderRow);
        } else {
            System.out.println(borderRowReverse);
        }

        for (int i = 0; i < 16; i++) {
            boolean onRowTop = i % 2 == 0;
            int row = i / 2;
            if (color == ChessGame.TeamColor.WHITE) {
                row = 8 - row - 1;
            }
            String borderCell = String.format(
                    "%s %d %s",
                    borderEsc,
                    row + 1,
                    resetEsc
            );
            if (onRowTop) {
                borderCell = String.format(
                        "%s   %s",
                        borderEsc,
                        resetEsc);
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
                String cellEsc = isWhiteCell ? cellWhiteEsc : cellBlackEsc;
                System.out.printf("%s ", cellEsc);
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (onRowTop) {
                    System.out.print(EscapeSequences.EMPTY);
                } else {
                    printCell(piece);
                }
                System.out.print(" ");
            }
            System.out.println(borderCell);
        }

        if (color == ChessGame.TeamColor.WHITE) {
            System.out.println(borderRow);
        } else {
            System.out.println(borderRowReverse);
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private static void printCell(ChessPiece piece) {
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
            System.out.printf("%s%s", EscapeSequences.SET_TEXT_COLOR_WHITE, esc);
        }
    }
}
