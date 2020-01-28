package com.cookingconverter.cookingconversions.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.cookingconverter.cookingconversions.models.Converter;

import java.util.List;

//
//all the information for adding updating and deleting from the database
//

@Dao
public interface ConverterDao {

    @Insert
    long[] insertConverters(Converter... converters);

    @Query("SELECT * FROM converters")
    LiveData<List<Converter>> getConverters();

    @Delete
    int delete(Converter... converters);

    @Update
    int updateConverters(Converter... converters);
}
