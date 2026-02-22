package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData data) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> getGames() throws DataAccessException;
}
