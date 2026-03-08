package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> games;
    private int currId;

    public MemoryGameDAO() {
        games = new HashMap<>();
        currId = 1;
    }

    public int createGame(GameData data) throws DataAccessException {
        int gameId = getNextId();
        GameData newData = new GameData(
                gameId,
                data.whiteUsername(),
                data.blackUsername(),
                data.gameName(),
                data.game());
        games.put(gameId, newData);
        return gameId;
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

    private int getNextId() {
        return currId++;
    }
}
