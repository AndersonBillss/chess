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
        this.gameId = 1;
    }

    public ListGamesResult listGames(String authToken)
            throws DataAccessException, UnauthorizedException {
        if (authDao.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        return new ListGamesResult(gameDAO.getGames());
    }

    public CreateGameResult createGame(CreateGameRequest req, String authToken)
            throws DataAccessException, UnauthorizedException, BadRequestException {
        if (req.gameName() == null) {
            throw new BadRequestException();
        }
        if (authDao.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        GameData game = new GameData(
                0,
                null,
                null,
                req.gameName(),
                new ChessGame()
        );
        int newGameId = gameDAO.createGame(game);
        return new CreateGameResult(newGameId);
    }

    public void joinGame(JoinGameRequest req, String authToken)
            throws DataAccessException, UnauthorizedException, AlreadyTakenException, BadRequestException {
        if (!Objects.equals(req.playerColor(), "BLACK") && !Objects.equals(req.playerColor(), "WHITE")) {
            throw new BadRequestException();
        }
        if(req.gameID() < 1) {
            throw new BadRequestException();
        }
        var session = authDao.getAuth(authToken);
        if (session == null) {
            throw new UnauthorizedException();
        }
        String username = session.username();

        GameData data = gameDAO.getGame(req.gameID());
        if (data == null) {
            throw new BadRequestException();
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
