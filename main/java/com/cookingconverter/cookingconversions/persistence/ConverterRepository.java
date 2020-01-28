package com.cookingconverter.cookingconversions.persistence;


import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.cookingconverter.cookingconversions.async.DeleteAsyncTask;
import com.cookingconverter.cookingconversions.async.InsertAsyncTask;
import com.cookingconverter.cookingconversions.async.UpdateAsyncTask;
import com.cookingconverter.cookingconversions.models.Converter;
import com.cookingconverter.cookingconversions.persistence.ConverterDatabase;

import java.util.List;

public class ConverterRepository {

//
//calling the insert update and delete tasks with live data
//

    private ConverterDatabase mConverterDatabase;

    public ConverterRepository(Context context) {
        mConverterDatabase = ConverterDatabase.getInstance(context);
    }

    public void insertConverterTask(Converter converter){
        new InsertAsyncTask(mConverterDatabase.getConverterDao()).execute(converter);
    }

    public void updateConverterTask(Converter converter){
        new UpdateAsyncTask(mConverterDatabase.getConverterDao()).execute(converter);
    }

    public LiveData<List<Converter>> retrieveConvertersTask() {
        return mConverterDatabase.getConverterDao().getConverters();
    }

    public void deleteConverterTask(Converter converter){
        new DeleteAsyncTask(mConverterDatabase.getConverterDao()).execute(converter);
    }
}













