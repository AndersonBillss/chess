package client.ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.BoardDisplay;
import client.ClientContext;
import model.GameData;

import java.util.Map;
import java.util.Objects;

public class GameplayUI implements UI {
    public enum Mode {
        OBSERVER,
        PLAYER,
    }

    private ClientContext ctx;
    private Mode mode;
    private String username;

    public GameplayUI(
            ClientContext ctx, Mode mode, GameData game, String username
    ) {
        this.ctx = ctx;
        this.ctx.setGame(game);
        this.mode = mode;
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
        this.ctx.setColor(color);
    }

    public ChessGame.TeamColor getColor() {
        if (ctx.getColor() != null) {
            return ctx.getColor();
        }
        if (mode == Mode.OBSERVER) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, ctx.getGame().whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, ctx.getGame().blackUsername())) {
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

        return String.format("%s: %s", modeString, ctx.getGame().gameName());
    }

    @Override
    public UI handleInput(String[] input) {
        if (mode == Mode.OBSERVER) {
            return switch (input[0].toLowerCase()) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "highlight" -> highlight(input);
                default -> unknownCommand(input);
            };
        } else {
            return switch (input[0].toLowerCase()) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "highlight" -> highlight(input);
                case "move" -> move(input);
                case "resign" -> resign();
                default -> unknownCommand(input);
            };
        }
    }

    private UI redraw() {
        BoardDisplay.show(this.ctx.getGame().game(), getColor());
        return this;
    }

    private UI help() {
        if (mode == Mode.OBSERVER) {
            ctx.getPromptManager().println("  Redraw the board: \"redraw\"");
            ctx.getPromptManager().println("  Leave the game: \"leave\"");
            ctx.getPromptManager().println("  Highlight legal moves: \"highlight\"");
        } else {
            ctx.getPromptManager().println("  Redraw the board: \"redraw\"");
            ctx.getPromptManager().println("  Leave the game: \"leave\"");
            ctx.getPromptManager().println("  Highlight legal moves: \"highlight\"");
            ctx.getPromptManager().println("  Make a move: \"move\" <START> <END>");
            ctx.getPromptManager().println("  Resign from a game: \"resign\"");
        }
        return this;
    }

    private UI leave() {
        ctx.getPromptManager().println("Leaving game...");
        ctx.getWebSocketFacade().leaveGame(
                this.ctx.getServerFacade().getAuthToken(),
                this.ctx.getGame().gameID());
        return new PostloginUI(this.ctx, this.username);
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
            ctx.getPromptManager().println("Move takes two or three arguments");
            return this;
        }
        ChessPosition pos1 = parseMove(input[1]);
        if (pos1 == null) {
            ctx.getPromptManager().println(String.format("%s is not a valid move", input[1]));
            return this;
        }
        ChessPosition pos2 = parseMove(input[2]);
        if (pos2 == null) {
            ctx.getPromptManager().println(String.format("%s is not a valid move", input[2]));
            return this;
        }
        ChessPiece.PieceType promotionPiece = null;
        if (input.length == 4) {
            promotionPiece = parsePromotionPiece(input[3]);
            if (promotionPiece == null) {
                ctx.getPromptManager().println(String.format("%s is not a valid promotion piece", input[3]));
                return this;
            }
        }

        ChessMove move = new ChessMove(pos1, pos2, promotionPiece);
        ctx.getWebSocketFacade().makeMove(
                ctx.getServerFacade().getAuthToken(),
                ctx.getGame().gameID(),
                move);

        return this;
    }

    private UI resign() {
        ctx.getPromptManager().println("Not implemented!");
        return this;
    }

    private UI highlight(String[] input) {
        if (input.length != 2) {
            ctx.getPromptManager().println("Input takes one argument");
            return this;
        }
        ChessPosition pos = parseMove(input[1]);
        if (pos == null) {
            ctx.getPromptManager().println(String.format("%s is not a valid move", input[1]));
            return this;
        }
        if (ctx.getGame().game().getBoard().getPiece(pos) == null) {
            ctx.getPromptManager().println(String.format("%s does not have a piece",
                    input[1].toLowerCase().trim()));
            return this;
        }
        BoardDisplay.show(ctx.getGame().game(), ctx.getColor(), pos);

        return this;
    }

    private UI unknownCommand(String[] input) {
        ctx.getPromptManager().println("Unknown command: " + input[0]);
        return this;
    }
}
