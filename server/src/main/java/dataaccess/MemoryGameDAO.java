package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> games;

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    public void createGame(GameData data) throws DataAccessException {
        games.put(data.gameID(), data);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public void editGame(GameData data) throws DataAccessException {
        games.remove(data.gameID());
        games.put(data.gameID(), data);
    }

    public Collection<GameData> getGames() throws DataAccessException {
        return games.values();
    }

    public void clear() {
        games = new HashMap<>();
    }
}
