package com.cookingconverter.cookingconversions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cookingconverter.cookingconversions.models.Converter;
import com.cookingconverter.cookingconversions.persistence.ConverterRepository;
import com.cookingconverter.cookingconversions.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ConverterActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher {

    private static final String TAG = "ConverterActivity";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;
    private LinedEditText mLinedEditText;
    private EditText mEditTitle, number;
    private TextView mViewTitle, answer;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;
    public Spinner leftSpinner, rightSpinner;
    private Button calculate;
    private boolean mIsNewConverter;
    private Converter mConverterInitial;
    private GestureDetector mGestureDetector;
    private int mMode;
    private ConverterRepository mConverterRepository;
    private Converter mConverterFinal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        mLinedEditText = findViewById(R.id.converter_text);
        mEditTitle = findViewById(R.id.converter_edit_title);
        mViewTitle = findViewById(R.id.converter_text_title);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        leftSpinner = findViewById(R.id.leftSpinner);
        rightSpinner = findViewById(R.id.rightSpinner);
        number = findViewById(R.id.number);
        answer = findViewById(R.id.answer);
        calculate = findViewById(R.id.calculate);

        //
        //setting the list of items for the spinners
        //

        List<String> spinnerList = new ArrayList<>();
        spinnerList.add("Cup");
        spinnerList.add("Gallon");
        spinnerList.add("Liter");
        spinnerList.add("Milliliter");
        spinnerList.add("Ounce");
        spinnerList.add("Pint");
        spinnerList.add("Quart");
        spinnerList.add("Tablespoon");
        spinnerList.add("Teaspoon");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner lItems = findViewById(R.id.leftSpinner);
        leftSpinner.setSelection(0);
        lItems.setAdapter(adapter);


        Spinner rItems = findViewById(R.id.rightSpinner);
        rightSpinner.setSelection(0);
        rItems.setAdapter(adapter);


        mConverterRepository = new ConverterRepository(this);

        setListeners();

        if (getIncomingIntent()) {
            setNewConverterProperties();
            enableEditMode();
        } else {
            setConverterProperties();
            disableContentInteraction();
        }

        //
        //calling the calculate method on click
        //

        calculate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                calculate();
            }

        });


    }


    //save the changes
    private void saveChanges() {
        if (mIsNewConverter) {
            saveNewConverter();
        } else {
            updateConverter();
        }
    }

    public void updateConverter() {
        mConverterRepository.updateConverterTask(mConverterFinal);
    }

    public void saveNewConverter() {
        mConverterRepository.insertConverterTask(mConverterFinal);
    }

    private void setListeners() {
        mGestureDetector = new GestureDetector(this, this);
        mLinedEditText.setOnTouchListener(this);
        mCheck.setOnClickListener(this);
        mViewTitle.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
    }

    //get a selected conversion
    private boolean getIncomingIntent() {
        if (getIntent().hasExtra("selected_converter")) {
            mConverterInitial = getIntent().getParcelableExtra("selected_converter");

            mConverterFinal = new Converter();
            mConverterFinal.setTitle(mConverterInitial.getTitle());
            mConverterFinal.setLeftSpinner((mConverterInitial.getLeftSpinner()));
            mConverterFinal.setRightSpinner(mConverterInitial.getRightSpinner());
            mConverterFinal.setContent(mConverterInitial.getContent());
            mConverterFinal.setNumber(mConverterInitial.getNumber());
            mConverterFinal.setAnswer(mConverterInitial.getAnswer());
            mConverterFinal.setTimestamp(mConverterInitial.getTimestamp());
            mConverterFinal.setId(mConverterInitial.getId());

            enableEditMode();

            mMode = EDIT_MODE_ENABLED;
            mIsNewConverter = false;
            return false;
        }
        mMode = EDIT_MODE_ENABLED;
        mIsNewConverter = true;
        return true;
    }

    private void disableContentInteraction() {
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }

    private void enableContentInteraction() {
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }

    //make the title editable

    private void enableEditMode() {
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }

    //make the title not editable after another action

    private void disableEditMode() {
        Log.d(TAG, "disableEditMode: called.");
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        // Make sure the conversion has a title (Default title is New Conversion)
        String temp = mEditTitle.getText().toString();
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");
        if (temp.length() > 0) {
            mConverterFinal.setTitle(mEditTitle.getText().toString());
            mConverterFinal.setLeftSpinner(leftSpinner.getSelectedItemPosition());
            mConverterFinal.setRightSpinner(rightSpinner.getSelectedItemPosition());
            mConverterFinal.setContent(mLinedEditText.getText().toString());
            mConverterFinal.setNumber(number.getText().toString());
            mConverterFinal.setAnswer(answer.getText().toString());
            String timestamp = Utility.getCurrentTimeStamp();
            mConverterFinal.setTimestamp(timestamp);

            Log.d(TAG, "disableEditMode: initial: " + mConverterInitial.toString());
            Log.d(TAG, "disableEditMode: final: " + mConverterFinal.toString());


            //Save if there are any changes to the conversion
            if (!mConverterFinal.getContent().equals(mConverterInitial.getContent())
                    || !mConverterFinal.getTitle().equals(mConverterInitial.getTitle())
                    || !mConverterFinal.getNumber().equals(mConverterInitial.getNumber())
                    || mConverterFinal.getRightSpinner() != mConverterInitial.getRightSpinner()
                    || mConverterFinal.getLeftSpinner() != mConverterInitial.getLeftSpinner()
                    || mConverterFinal.getAnswer().equals(mConverterInitial.getAnswer())) {
                Log.d(TAG, "disableEditMode: called?");
                saveChanges();
            }
        }
    }

    //start of a new conversion set titles and spinners default values
    private void setNewConverterProperties() {
        mViewTitle.setText("New Conversion");
        mEditTitle.setText("New Conversion");
        leftSpinner.setSelection(0);
        rightSpinner.setSelection(0);

        mConverterFinal = new Converter();
        mConverterInitial = new Converter();
        mConverterInitial.setTitle("New Conversion");
    }

    //get the information from a previously saved conversion
    private void setConverterProperties() {
        mViewTitle.setText(mConverterInitial.getTitle());
        leftSpinner.setSelection(mConverterInitial.getLeftSpinner());
        rightSpinner.setSelection(mConverterInitial.getRightSpinner());
        mEditTitle.setText(mConverterInitial.getTitle());
        mLinedEditText.setText(mConverterInitial.getContent());
        number.setText(mConverterInitial.getNumber());
        answer.setText(mConverterInitial.getAnswer());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }



    //enable editing for the title and the text at the bottom if it is double clicked
    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Log.d(TAG, "onDoubleTap: double tapped.");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //save and exit if back arrow is clicked
            case R.id.toolbar_back_arrow: {
                finish();
                break;
            }
            //disable editing if check is clicked
            case R.id.toolbar_check: {
                disableEditMode();
                break;
            }
            //enable editing if title is clicked
            case R.id.converter_text_title: {
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (mMode == EDIT_MODE_ENABLED) {

            onClick(mCheck);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if (mMode == EDIT_MODE_ENABLED) {
            enableEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mViewTitle.setText(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //
    //Method that does all of the calculations based on what is selected on the spinner
    //


    public void calculate() {


        if (number.getText().length() > 0) {

            //if both sides are ==
            if (leftSpinner.getSelectedItem() == rightSpinner.getSelectedItem()) {

                answer.setText(number.getText());

            }

            //
            //Start if left side == Cup
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 16;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 4.227;

                answer.setText(String.format("%.2f", num));

            }


            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 236.588;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) * 8;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) / 2;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) / 4;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 16;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Cup") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 48;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Cup
            //
            //
            //


            //
            //Start if left side == Gallon
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Cup")) {

                double num = (Double.parseDouble(number.getText().toString())) * 16;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 3.785;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 3785.412;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) * 128;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) * 8;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) * 4;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 256;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Gallon") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 768;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Gallon
            //
            //
            //


            //
            //Start if left side == Liter
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 3.785;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) * 4.227;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 1000;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) * 33.814;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) * 2.113;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) * 1.057;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 67.628;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Liter") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 202.884;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Liter
            //
            //
            //


            //
            //Start if left side == Milliliter
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 3785.412;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 1000;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) / 236.588;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) / 29.574;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) / 473.176;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) / 946.353;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) / 14.787;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Milliliter") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) / 4.929;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Milliliter
            //
            //
            //


            //
            //Start if left side == Ounce
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 128;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 33.814;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 29.574;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) / 8;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) / 16;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) / 32;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 2;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Ounce") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 6;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Ounce
            //
            //
            //


            //
            //Start if left side == Pint
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 8;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 2.113;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 473.176;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) * 16;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) * 2;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) / 2;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 32;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Pint") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 96;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Pint
            //
            //
            //


            //
            //Start if left side == Quart
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 4;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 1.057;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 946.353;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) * 32;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) * 2;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) * 4;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 64;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Quart") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 192;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Quart
            //
            //
            //


            //
            //Start if left side == Tablespoon
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 256;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 67.628;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 14.787;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) / 2;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) / 32;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) / 64;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) / 16;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Tablespoon") && rightSpinner.getSelectedItem().equals("Teaspoon")) {


                double num = (Double.parseDouble(number.getText().toString())) * 3;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Tablespoon
            //
            //
            //


            //
            //Start if left side == Teaspoon
            //
            //
            //

            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Gallon")) {

                double num = (Double.parseDouble(number.getText().toString())) / 768;


                answer.setText(String.format("%.2f", num));
            }


            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Liter")) {


                double num = (Double.parseDouble(number.getText().toString())) / 202.884;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Milliliter")) {


                double num = (Double.parseDouble(number.getText().toString())) * 4.929;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Ounce")) {


                double num = (Double.parseDouble(number.getText().toString())) / 6;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Pint")) {


                double num = (Double.parseDouble(number.getText().toString())) / 96;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }


            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Quart")) {


                double num = (Double.parseDouble(number.getText().toString())) / 192;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Tablespoon")) {


                double num = (Double.parseDouble(number.getText().toString())) / 3;


                answer.setText(String.valueOf(String.format("%.2f", num)));


            }


            if (leftSpinner.getSelectedItem().equals("Teaspoon") && rightSpinner.getSelectedItem().equals("Cup")) {


                double num = (Double.parseDouble(number.getText().toString())) / 48;

                answer.setText(String.valueOf(String.format("%.2f", num)));

            }

            //
            //
            //
            //End if left side is Teaspoon
            //
            //
            //

            Double answerCheck = Double.parseDouble(answer.getText().toString());


            if (answerCheck > 0.00) {
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(ConverterActivity.this).create();
                alertDialog.setTitle("Small answer");
                alertDialog.setMessage("The answer is very small less than 0.00 \n" + "Please enter a larger number.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(ConverterActivity.this).create();
            alertDialog.setTitle("Nothing Entered");
            alertDialog.setMessage("Please enter a number.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }


    }

}













