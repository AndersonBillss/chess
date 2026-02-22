package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.DBService;

public class DBHandler {
    private final DBService service;

    public DBHandler(DBService service) {
        this.service = service;
    }

    public void clear(Context ctx) throws DataAccessException {
        service.clear();
    }
}
