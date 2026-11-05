package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.DatabaseService;

public class DatabaseHandler {
    DatabaseService service;
    public DatabaseHandler(DatabaseService service) {
        this.service = service;
    }

    public void clear(Context context) throws DataAccessException{
        service.clear();
    }
}
