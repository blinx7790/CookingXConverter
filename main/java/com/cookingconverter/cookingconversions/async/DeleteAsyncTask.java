package com.cookingconverter.cookingconversions.async;

import android.os.AsyncTask;

import com.cookingconverter.cookingconversions.models.Converter;
import com.cookingconverter.cookingconversions.persistence.ConverterDao;

public class DeleteAsyncTask extends AsyncTask<Converter, Void, Void> {

    private ConverterDao mConverterDao;

    public DeleteAsyncTask(ConverterDao dao) {
        mConverterDao = dao;
    }

    @Override
    protected Void doInBackground(Converter... converters) {
        mConverterDao.delete(converters);
        return null;
    }

}