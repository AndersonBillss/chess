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
        GameData newData = new GameData(
                getNextId(),
                data.whiteUsername(),
                data.blackUsername(),
                data.gameName(),
                data.game());
        games.put(data.gameID(), data);
        return newData.gameID();
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
