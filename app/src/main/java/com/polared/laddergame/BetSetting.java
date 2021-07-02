package com.polared.laddergame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class BetSetting extends AppCompatActivity {
    private static final int RESULT_BET = 200;

    private static final int CHOICE_BET = 0;
    private static final int DRAW_BLANK = 1;
    private static final int DRAW_PRIZE = 2;
    private static final int WHAT_EAT_TODAY = 3;

    private LinearLayout mainLayout;
    private GridLayout gridLayout;
    private TextView tvBetType;
    private Button btnCancel, btnInputComplete;

    private String[] betType;

    private String[] betNames;

    private String jsonBetNames;

    private int typePosition = 0;
    private int participantNumber = 0;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_setting);

        View customView = LayoutInflater.from(this).inflate(R.layout.toolbar, null, false);
        ((TextView)customView.findViewById(R.id.tvTitle)).setText(getString(R.string.enter_bet));
        customView.setLayoutParams(new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView);

        betType = new String[]{getString(R.string.choose_a_bet)
                , getString(R.string.draw_a_blank), getString(R.string.drawing_a_prize)
                , getString(R.string.what_should_i_eat_today)};

        participantNumber = getIntent().getIntExtra("participantNumber", 2);
        typePosition = getIntent().getIntExtra("typePosition", 0);

        findViewByIdFunc();

        eventHandlerFunc();

        for(int i = 0; i < participantNumber; i++){
            createView(i);
        }

        setLastEdtBetNameImeOption();

        jsonBetNames = getIntent().getStringExtra("jsonBetNames");

        if(jsonBetNames != null){
            jsonFromStringArray();
            setEdtBetName();
        }

    }

    private void setLastEdtBetNameImeOption() {
        ((EditText)gridLayout.getChildAt(participantNumber-1).findViewById(R.id.edtBetName))
                .setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private void jsonFromStringArray() {
        Gson gson = new Gson();
        betNames = gson.fromJson(jsonBetNames, String[].class);
    }

    private void stringArrayFromJson() {
        Gson gson = new Gson();
        jsonBetNames = gson.toJson(betNames);
    }



    private void createView(int position) {

        View addView = LayoutInflater.from(this).inflate(R.layout.bet_item, null, false);

        EditText edtBetName = addView.findViewById(R.id.edtBetName);

        ImageButton ibTextClear = addView.findViewById(R.id.ibTextClear);

        edtBetName.setText(getString(R.string.bet)+(position+1));

        edtBetName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edtBetName.setSelection(edtBetName.length());
                }
            }
        });

        ibTextClear.setOnClickListener(v -> {
            edtBetName.setText(R.string.blank);
        });

        gridLayout.addView(addView);

    }

    private void setEdtBetName() {
        if(participantNumber > betNames.length){
            setToType(typePosition);
        }else{
            for(int i = 0; i < participantNumber; i++){

                ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName))
                        .setText(betNames[i]);
            }
        }



    }

    private void eventHandlerFunc() {

        registerTvBetType();

        registerBtnCancel();

        registerBtnInputComplete();

        registerMainLayout();

    }

    private void registerMainLayout() {
        mainLayout.setOnClickListener(v -> {
            if(getCurrentFocus() == null){
                return;
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            getCurrentFocus().clearFocus();
        });
    }

    private void registerBtnInputComplete() {

        btnInputComplete.setOnClickListener(v -> {

            if(checkData()){
                stringArrayFromJson();

                Intent intent = new Intent();
                intent.putExtra("jsonBetNames", jsonBetNames);
                intent.putExtra("participantNumber", participantNumber);
                intent.putExtra("typePosition", typePosition);
                setResult(RESULT_BET, intent);

                finish();
            }


        });

    }



    private void registerBtnCancel() {

        btnCancel.setOnClickListener(v -> {
            finish();
        });

    }

    private void registerTvBetType() {
        tvBetType.setText(betType[typePosition]);

        tvBetType.setOnClickListener(v -> {

            setAlertDialog();

        });

    }

    private boolean checkData() {
        betNames = new String[participantNumber];

        for(int i = 0; i < participantNumber; i++){
            EditText betName = ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName));
            if(betName.length() > 0){
                betNames[i] = betName.getText().toString();

            }else{
                betName.requestFocus();
                Toast.makeText(this, "내기를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void setAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setSingleChoiceItems(betType, typePosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(typePosition != which){
                    setToType(which);
                    typePosition = which;
                    tvBetType.setText(betType[typePosition]);
                }

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setToType(int which) {

        switch(which){
            case CHOICE_BET:
                nameForBetChoiceBet();
                break;

            case DRAW_BLANK:
            case DRAW_PRIZE:
                nameForBetDrawBlankOrPrize(which);
                break;

            case WHAT_EAT_TODAY:
                nameForBetWhatEatToday();
                break;
        }

    }

    private void nameForBetWhatEatToday() {
        String[] foods = new String[]{ getString(R.string.food_black_soybean_sauce_noodle), getString(R.string.food_stew), getString(R.string.food_pork_cutlet)
                , getString(R.string.food_jjambbong), getString(R.string.food_noodle), getString(R.string.food_pasta), getString(R.string.food_hamburger)
                , getString(R.string.food_korean_cuisine), getString(R.string.food_raw_fish), getString(R.string.food_pizza), getString(R.string.food_stir_fried_rice_cake)
                , getString(R.string.food_meat), getString(R.string.food_ramen), getString(R.string.food_rice_with_soy_sauce_egg), getString(R.string.food_kimchi_fried_rice)
                , getString(R.string.food_starving)};

        boolean[] isUsed = new boolean[foods.length];


        for(int i = 0; i < participantNumber; i++){
            int randomNumber = (int)(Math.random()*foods.length);

            if(isUsed[randomNumber]) {
                i--;
                continue;
            }else{
                isUsed[randomNumber] = true;

                ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName))
                        .setText(foods[randomNumber]);
            }

        }

    }


    private void nameForBetDrawBlankOrPrize(int which) {
        String draw = null;
        String message = null;

        if(which == DRAW_BLANK){
            draw = getString(R.string.fail_exclamation_mark);
            message = getString(R.string.pass);
        }else if(which == DRAW_PRIZE){
            draw = getString(R.string.prize_exclamation_mark);
            message = getString(R.string.fail);
        }
        int randomNumber = (int)(Math.random()*participantNumber);

        for(int i = 0; i < participantNumber; i++){
            ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName))
                    .setText(message);
        }

        ((EditText)gridLayout.getChildAt(randomNumber).findViewById(R.id.edtBetName))
                .setText(draw);

    }

    private void nameForBetChoiceBet() {

        for(int i = 0; i < participantNumber; i++){
            ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName))
                    .setText(getString(R.string.bet)+(i+1));
        }

    }

    private void findViewByIdFunc() {

        mainLayout = findViewById(R.id.mainLayout);
        gridLayout = findViewById(R.id.gridLayout);

        tvBetType = findViewById(R.id.tvBetType);

        btnCancel = findViewById(R.id.btnCancel);
        btnInputComplete = findViewById(R.id.btnInputComplete);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        gridLayout.removeAllViews();
    }
}