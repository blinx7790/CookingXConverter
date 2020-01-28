package com.cookingconverter.cookingconversions.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cookingconverter.cookingconversions.models.Converter;

@Database(entities = {Converter.class}, version = 1)
public abstract class ConverterDatabase extends RoomDatabase {

//
//creating the database name and calling build
//

    public static final String DATABASE_NAME = "conversion_db";

    private static ConverterDatabase instance;

    static ConverterDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ConverterDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract ConverterDao getConverterDao();
}