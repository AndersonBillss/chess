package service;

import chess.ChessGame;
import dataaccess.*;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import dto.ListGamesResult;
import model.GameData;

public class GameService {
    private final AuthDAO authDao;
    private final GameDAO gameDAO;
    private int gameId;

    public GameService() {
        this.authDao = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
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

    public int generateGameId() {
        return gameId++;
    }
}
