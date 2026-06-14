package com.agrovet.pos;

import android.app.Application;
import com.agrovet.pos.database.AppDatabase;

public class AgrovetApplication extends Application {
    
    public AppDatabase getDatabase() {
        return AppDatabase.getDatabase(this);
    }
}
