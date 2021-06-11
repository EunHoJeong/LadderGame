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

    private final String[] betType = new String[]{"내기를 선택해주세요", "꽝뽑기", "당첨뽑기", "오늘 뭐 먹지"};

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
        ((TextView)customView.findViewById(R.id.tvTitle)).setText("내기 입력");
        customView.setLayoutParams(new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView);

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

        edtBetName.setText("내기"+(position+1));

        ibTextClear.setOnClickListener(v -> {
            edtBetName.setText("");
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
            if(((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName)).length() > 0){
                betNames[i] = ((EditText)gridLayout
                            .getChildAt(i)
                            .findViewById(R.id.edtBetName))
                            .getText()
                            .toString();

            }else{
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
        String[] foods = new String[]{"짜장", "찌개", "돈까스", "짬뽕"
                                    , "국수", "파스타", "햄버거", "정식"
                                    , "회", "피자", "떡볶이", "고기", "라면"
                                    , "간계밥", "김치볶음밥", "굶기"};

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
            draw = "꽝!!";
            message = "통과";
        }else if(which == DRAW_PRIZE){
            draw = "당첨!!";
            message = "꽝";
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
                    .setText("내기"+(i+1));
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