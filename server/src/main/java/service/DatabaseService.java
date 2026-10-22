package service;

import dataaccess.DataAccess;

public class DatabaseService {
    DataAccess dataAccess;
    public DatabaseService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear(){
        dataAccess.clear();
    }

}
