package handlers.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final SessionManager sessions;
    private final GameDAO gameDao;
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public WebSocketHandler(GameDAO gameDao, UserDAO userDao, AuthDAO authDao) {
        this.sessions = new SessionManager();
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.authDao = authDao;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        var gson = new Gson();
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, ctx);
            case MAKE_MOVE -> makeMove(
                    gson.fromJson(ctx.message(), MakeMoveCommand.class),
                    ctx.session
            );
            case LEAVE -> leave(command, ctx.session);
            case RESIGN -> resign(command, ctx.session);
        }
    }

    private void connect(UserGameCommand command, WsMessageContext ctx)
            throws DataAccessException, IOException {
        var user = getUser(command.getAuthToken());
        var game = gameDao.getGame(command.getGameID());
        if (authDao.getAuth(command.getAuthToken()) == null) {
            throw new WebSocketException("Unauthorized");
        }
        if (game == null) {
            throw new WebSocketException("Game does not exist");
        }
        LoadGameMessage serverMessage = new LoadGameMessage(
                game
        );
        var message = new Gson().toJson(serverMessage);
        ctx.session.getRemote().sendString(message);
        ChessGame.TeamColor color = getInitialColor(user, game);
        sessions.addSession(command.getGameID(), ctx.session, color);
        String roleString = color == null ? "an observer" : color.toString().toLowerCase();

        sessions.broadcast(
                command.getGameID(),
                ctx.session,
                String.format("%s has joined as %s",
                        user.username(),
                        roleString));
    }

    private void makeMove(MakeMoveCommand command, Session session)
            throws DataAccessException, InvalidMoveException, IOException {
        UserData user = getUser(command.getAuthToken());
        var game = getGame(command.getGameID());
        if (game.game().isGameOver()) {
            throw new WebSocketException("Game is finished");
        }
        ChessGame.TeamColor userColor = getPlayerColor(game, session);
        if (userColor == null) {
            throw new WebSocketException("Not a player in the game");
        }
        ChessGame.TeamColor opposingColor = userColor ==
                ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK :
                ChessGame.TeamColor.WHITE;

        if (game.game().getTeamTurn() != userColor) {
            throw new WebSocketException("Not your turn");
        }

        game.game().makeMove(command.getMove());
        gameDao.editGame(game);
        sessions.broadcast(game);
        sessions.broadcast(
                game.gameID(),
                session,
                String.format("%s made move: %s",
                        user.username(),
                        command.getMove().toString()
                )
        );
        if (game.game().isInCheckmate(opposingColor)) {
            sessions.broadcast(game.gameID(), null,
                    String.format("%s player is in checkmate!", opposingColor));
        } else if (game.game().isInCheck(opposingColor)) {
            sessions.broadcast(game.gameID(), null,
                    String.format("%s player is in check!", opposingColor));
        } else if (game.game().isInStalemate(opposingColor)) {
            sessions.broadcast(game.gameID(), null,
                    "%s player cannot move. Game ends in stalemate!"
            );
        }
    }

    private void leave(UserGameCommand command, Session session)
            throws DataAccessException, IOException {
        UserData user = getUser(command.getAuthToken());
        GameData game = getGame(command.getGameID());
        ChessGame.TeamColor userColor = getPlayerColor(game, session);
        if (userColor == ChessGame.TeamColor.WHITE) {
            game = new GameData(game.gameID(),
                    null,
                    game.blackUsername(),
                    game.gameName(),
                    game.game());
        } else {
            game = new GameData(game.gameID(),
                    game.whiteUsername(),
                    null,
                    game.gameName(),
                    game.game());
        }
        gameDao.editGame(game);
        sessions.removeSession(game.gameID(), session);
        sessions.broadcast(
                game.gameID(),
                session,
                String.format("%s left the game", user.username())
        );
    }

    private void resign(UserGameCommand command, Session session)
            throws DataAccessException, IOException {
        UserData user = getUser(command.getAuthToken());
        GameData game = getGame(command.getGameID());
        ChessGame.TeamColor playerColor = getPlayerColor(game, session);
        if (playerColor == null) {
            throw new WebSocketException("You are not playing in this game");
        }
        if (game.game().isGameOver()) {
            throw new WebSocketException("Game is finished");
        }
        game.game().setGameOver(true);
        gameDao.editGame(game);
        sessions.broadcast(game.gameID(), null,
                String.format("%s resigned!", user.username())
        );
    }

    private ChessGame.TeamColor getPlayerColor(
            GameData game, Session session) {
        SessionData sessionData = sessions.findSession(game.gameID(), session);
        if (sessionData.color() == ChessGame.TeamColor.WHITE) {
            return ChessGame.TeamColor.WHITE;
        } else if (sessionData.color() == ChessGame.TeamColor.BLACK) {
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    private ChessGame.TeamColor getInitialColor(
            UserData user, GameData game) {
        boolean isWhitePlayer = Objects.equals(game.whiteUsername(), user.username());
        boolean hasWhitePlayer = sessions.hasPlayer(game.gameID(), ChessGame.TeamColor.WHITE);
        boolean isBlackPlayer = Objects.equals(game.blackUsername(), user.username());
        boolean hasBlackPlayer = sessions.hasPlayer(game.gameID(), ChessGame.TeamColor.BLACK);
        if (hasWhitePlayer && hasBlackPlayer) {
            return null;
        }
        if (isWhitePlayer && hasWhitePlayer) {
            return ChessGame.TeamColor.BLACK;
        }
        if (isBlackPlayer && hasBlackPlayer) {
            return ChessGame.TeamColor.WHITE;
        }
        if (isBlackPlayer) {
            return ChessGame.TeamColor.BLACK;
        }
        if (isWhitePlayer) {
            return ChessGame.TeamColor.WHITE;
        }

        return null;
    }

    private UserData getUser(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new WebSocketException("Session not found");
        }
        UserData user = userDao.getUser(authData.username());
        if (user == null) {
            throw new WebSocketException("User not found");
        }
        return user;
    }

    private GameData getGame(int gameId) throws DataAccessException {
        GameData game = gameDao.getGame(gameId);
        if (game == null) {
            throw new WebSocketException("Game not found");
        }
        return game;
    }
}
