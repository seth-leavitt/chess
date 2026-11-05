package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class DatabaseService {
    DataAccess dataAccess;
    public DatabaseService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException{
        dataAccess.clear();
    }

}
