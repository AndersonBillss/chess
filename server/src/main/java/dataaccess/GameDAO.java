package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData data) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void editGame(GameData data) throws DataAccessException;

    Collection<GameData> getGames() throws DataAccessException;

    void clear() throws DataAccessException;
}
