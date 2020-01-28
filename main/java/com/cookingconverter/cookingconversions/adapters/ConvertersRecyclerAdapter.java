package com.cookingconverter.cookingconversions.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cookingconverter.cookingconversions.R;
import com.cookingconverter.cookingconversions.models.Converter;
import com.cookingconverter.cookingconversions.util.Utility;


import java.util.ArrayList;

//
//Adapter class for the recycler
//


public class ConvertersRecyclerAdapter extends RecyclerView.Adapter<ConvertersRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ConvertersRecyclerAdapter";

    private ArrayList<Converter> mConverters;
    private OnConverterListener mOnConverterListener;

    public ConvertersRecyclerAdapter(ArrayList<Converter> mConverters, OnConverterListener onConverterListener) {
        this.mConverters = mConverters;
        this.mOnConverterListener = onConverterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_converter_list_item, parent, false);
        return new ViewHolder(view, mOnConverterListener);
    }

    //
    //Items that are shown in the recycler view
    //for this one it is the title and the date
    //

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try{
            String month = mConverters.get(position).getTimestamp().substring(0, 2);
            month = Utility.getMonthFromNumber(month);
            String year = mConverters.get(position).getTimestamp().substring(3);
            String timestamp = month + " " + year;
            holder.timestamp.setText(timestamp);
            holder.title.setText(mConverters.get(position).getTitle());
        }catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return mConverters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView timestamp, title;
        OnConverterListener mOnConverterListener;

        public ViewHolder(View itemView, OnConverterListener onConverterListener) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.converter_timestamp);
            title = itemView.findViewById(R.id.converter_title);
            mOnConverterListener = onConverterListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            mOnConverterListener.onConverterClick(getAdapterPosition());
        }
    }

    public interface OnConverterListener{
        void onConverterClick(int position);
    }

    public void updateList(ArrayList<Converter> newList){

        mConverters = newList;
        notifyDataSetChanged();
    }

}
















