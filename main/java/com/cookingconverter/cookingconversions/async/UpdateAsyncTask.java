package com.cookingconverter.cookingconversions.async;

import android.os.AsyncTask;

import com.cookingconverter.cookingconversions.models.Converter;
import com.cookingconverter.cookingconversions.persistence.ConverterDao;

public class UpdateAsyncTask extends AsyncTask<Converter, Void, Void> {

    private ConverterDao mConverterDao;

    public UpdateAsyncTask(ConverterDao dao) {
        mConverterDao = dao;
    }

    @Override
    protected Void doInBackground(Converter... converters) {
        mConverterDao.updateConverters(converters);
        return null;
    }

}