package service;

import chess.ChessGame;
import dataaccess.*;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import dto.JoinGameRequest;
import dto.ListGamesResult;
import model.GameData;

import java.util.Objects;

public class GameService {
    private final AuthDAO authDao;
    private final GameDAO gameDAO;
    private int gameId;

    public GameService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDAO = gameDao;
    }

    public ListGamesResult listGames(String authToken)
            throws DataAccessException, UnauthorizedException {
        if (authDao.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        return new ListGamesResult(gameDAO.getGames());
    }

    public CreateGameResult createGame(CreateGameRequest req, String authToken)
            throws DataAccessException, UnauthorizedException {
        if (authDao.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        int newGameId = generateGameId();
        GameData game = new GameData(
                newGameId,
                null,
                null,
                req.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(game);
        return new CreateGameResult(newGameId);
    }

    public void joinGame(JoinGameRequest req, String authToken)
            throws DataAccessException, UnauthorizedException, NotFoundException, AlreadyTakenException {
        var session = authDao.getAuth(authToken);
        if (session == null) {
            throw new UnauthorizedException();
        }
        String username = session.username();

        GameData data = gameDAO.getGame(req.gameID());
        if (data == null) {
            throw new NotFoundException();
        }

        String existingUser = Objects.equals(req.playerColor(), "BLACK") ?
                data.blackUsername() :
                data.whiteUsername();

        if (existingUser != null) {
            throw new AlreadyTakenException();
        }

        GameData newGame;
        if (Objects.equals(req.playerColor(), "BLACK")) {
            newGame = new GameData(
                    data.gameID(),
                    data.whiteUsername(),
                    username,
                    data.gameName(),
                    data.game());
        } else {
            newGame = new GameData(
                    data.gameID(),
                    username,
                    data.blackUsername(),
                    data.gameName(),
                    data.game());
        }

        gameDAO.editGame(newGame);
    }

    public int generateGameId() {
        return gameId++;
    }
}
