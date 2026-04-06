package client.ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ClientContext;
import model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameplayUI implements UI {
    public enum Mode {
        OBSERVER,
        PLAYER,
    }

    private ClientContext ctx;
    private Mode mode;
    private GameData game;
    private String username;
    private ChessGame.TeamColor color;

    public GameplayUI(
            ClientContext ctx, Mode mode, GameData game, String username
    ) {
        this.ctx = ctx;
        this.mode = mode;
        this.game = game;
        this.username = username;
        ctx.setColor(getColor());

        this.ctx.getWebSocketFacade().joinGame(
                this.ctx.getServerFacade().getAuthToken(),
                game.gameID());
    }

    public GameplayUI(
            ClientContext ctx, Mode mode, GameData game, String username, ChessGame.TeamColor color
    ) {
        this(ctx, mode, game, username);
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        if (color != null) {
            return color;
        }
        if (mode == Mode.OBSERVER) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return ChessGame.TeamColor.WHITE;
    }

    @Override
    public String pageIndicator() {
        String modeString = switch (this.mode) {
            case OBSERVER -> "Observing";
            case PLAYER -> "Playing";
        };

        return String.format("%s: %s", modeString, game.gameName());
    }

    @Override
    public UI handleInput(String[] input) {
        return switch (input[0].toLowerCase()) {
            case "help" -> help();
            case "leave" -> leave();
            case "move" -> move(input);
            case "resign" -> resign();
            case "highlight" -> highlight();
            default -> unknownCommand(input);
        };
    }

    private UI help() {
        System.out.println("  Redraw the board: \"redraw\"");
        System.out.println("  Leave the game: \"leave\"");
        System.out.println("  Make a move: \"move\" <START> <END>");
        System.out.println("  Resign from a game: \"resign\"");
        System.out.println("  Highlight legal moves: \"highlight\"");
        ctx.getPromptManager().printPrompt();
        return this;
    }

    private UI leave() {
        System.out.println("Not implemented!");
        ctx.getPromptManager().printPrompt();
        return this;
    }

    private ChessPosition parseMove(String input) {
        Map<Character, Integer> movesToInt = Map.of(
                'a', 1,
                'b', 2,
                'c', 3,
                'd', 4,
                'e', 5,
                'f', 6,
                'g', 7,
                'h', 8
        );
        String trimmed = input.trim().toLowerCase();
        Integer col = movesToInt.get(trimmed.charAt(0));
        if (col == null) {
            return null;
        }
        int row = trimmed.charAt(1) - '0';
        if (row < 1 || row > 8) {
            return null;
        }

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotionPiece(String input) {
        Map<String, ChessPiece.PieceType> movesToInt = Map.of(
                "queen", ChessPiece.PieceType.QUEEN,
                "bishop", ChessPiece.PieceType.BISHOP,
                "knight", ChessPiece.PieceType.KNIGHT,
                "rook", ChessPiece.PieceType.ROOK
        );

        ChessPiece.PieceType parsed = movesToInt.get(input.trim().toLowerCase());
        return parsed;
    }

    private UI move(String[] input) {
        if (input.length < 3 || input.length > 4) {
            System.out.println("Move takes two or three arguments");
            ctx.getPromptManager().printPrompt();
            return this;
        }
        ChessPosition pos1 = parseMove(input[1]);
        if (pos1 == null) {
            System.out.println(String.format("%s is not a valid move", input[1]));
            ctx.getPromptManager().printPrompt();
            return this;
        }
        ChessPosition pos2 = parseMove(input[2]);
        if (pos2 == null) {
            System.out.println(String.format("%s is not a valid move", input[2]));
            ctx.getPromptManager().printPrompt();
            return this;
        }
        ChessPiece.PieceType promotionPiece = null;
        if (input.length == 4) {
            promotionPiece = parsePromotionPiece(input[3]);
            if (promotionPiece == null) {
                System.out.println(String.format("%s is not a valid promotion piece", input[3]));
                ctx.getPromptManager().printPrompt();
                return this;
            }
        }

        ChessMove move = new ChessMove(pos1, pos2, promotionPiece);
        ctx.getWebSocketFacade().makeMove(
                ctx.getServerFacade().getAuthToken(),
                game.gameID(),
                move);

        return this;
    }

    private UI resign() {
        System.out.println("Not implemented!");
        ctx.getPromptManager().printPrompt();
        return this;
    }

    private UI highlight() {
        System.out.println("Not implemented!");
        ctx.getPromptManager().printPrompt();
        return this;
    }

    private UI unknownCommand(String[] input) {
        System.out.printf("Unknown command: %s\n", input[0]);
        ctx.getPromptManager().printPrompt();
        return this;
    }
}
