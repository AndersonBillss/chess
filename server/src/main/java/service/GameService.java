package service;

import chess.ChessGame;
import dataaccess.*;
import dto.CreateGameRequest;
import model.GameData;

public class GameService {
    private final AuthDAO authDao;
    private final GameDAO gameDAO;
    private int gameId;

    public GameService() {
        this.authDao = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
    }

    public void createGame(CreateGameRequest req, String authToken)
            throws DataAccessException, UnauthorizedException {
        if (authDao.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        GameData game = new GameData(
                generateGameId(),
                null,
                null,
                req.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(game);
    }

    public int generateGameId() {
        return gameId++;
    }
}
