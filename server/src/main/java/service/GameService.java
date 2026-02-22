package service;

import chess.ChessGame;
import dataaccess.*;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import model.GameData;

public class GameService {
    private final AuthDAO authDao;
    private final GameDAO gameDAO;
    private int gameId;

    public GameService() {
        this.authDao = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
    }

    public CreateGameResult createGame(CreateGameRequest req, String authToken)
            throws DataAccessException, UnauthorizedException {
        if (authDao.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        int gameId = generateGameId();
        GameData game = new GameData(
                gameId,
                null,
                null,
                req.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(game);
        return new CreateGameResult(gameId);
    }

    public int generateGameId() {
        return gameId++;
    }
}
