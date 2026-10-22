package handlers;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import service.DatabaseService;

public class DatabaseHandler {
    DatabaseService service;
    public DatabaseHandler(DatabaseService service) {
        this.service = service;
    }

    public void clear(Context context){
        service.clear();
    }
}
