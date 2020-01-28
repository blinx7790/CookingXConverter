package com.cookingconverter.cookingconversions;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.cookingconverter.cookingconversions.adapters.ConvertersRecyclerAdapter;
import com.cookingconverter.cookingconversions.models.Converter;
import com.cookingconverter.cookingconversions.persistence.ConverterRepository;
import com.cookingconverter.cookingconversions.util.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class ConvertersListActivity extends AppCompatActivity implements
        ConvertersRecyclerAdapter.OnConverterListener,
        FloatingActionButton.OnClickListener
{



    private RecyclerView mRecyclerView;
    private EditText spinner;
    private ArrayList<Converter> mConverters = new ArrayList<>();
    private ConvertersRecyclerAdapter mConverterRecyclerAdapter;
    private ConverterRepository mConverterRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converters_list);
        mRecyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinner);
        spinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            //Search changes the recycler view based on what is searched as it is typed
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });






        findViewById(R.id.fab).setOnClickListener(this);

        initRecyclerView();
        mConverterRepository = new ConverterRepository(this);
        retrieveConverters();








        setSupportActionBar((Toolbar)findViewById(R.id.converters_toolbar));
        setTitle("CookingXConversions");
    }

    //
    //Search filter
    //

    private void filter(String text){

        ArrayList<Converter> filteredList = new ArrayList<>();


            for (Converter item : mConverters) {


                if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }

            }

        mConverterRecyclerAdapter.updateList(filteredList);
            if(text.isEmpty()){
                mConverterRecyclerAdapter.updateList(mConverters);
            }

    }







    //Add all items to the list

    public void retrieveConverters() {
        mConverterRepository.retrieveConvertersTask().observe(this, new Observer<List<Converter>>() {
            @Override
            public void onChanged(@Nullable List<Converter> converters) {
                if(mConverters.size() > 0){
                    mConverters.clear();
                }
                if(converters != null){
                    mConverters.addAll(converters);
                }
                mConverterRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }




    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mConverterRecyclerAdapter = new ConvertersRecyclerAdapter(mConverters, this);
        mRecyclerView.setAdapter(mConverterRecyclerAdapter);
    }


    @Override
    public void onConverterClick(int position) {
        Intent intent = new Intent(this, ConverterActivity.class);
        intent.putExtra("selected_converter", mConverters.get(position));
        startActivity(intent);
    }

    //Start Converter activity on click
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, ConverterActivity.class);
        startActivity(intent);
    }

    //Delete an item from the list
    private void deleteConverter(Converter converter) {
        mConverters.remove(converter);
        mConverterRecyclerAdapter.notifyDataSetChanged();

        mConverterRepository.deleteConverterTask(converter);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        //Delete called on swiped
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            deleteConverter(mConverters.get(viewHolder.getAdapterPosition()));
          mConverterRecyclerAdapter.updateList(mConverters);

        }
    };


}


















