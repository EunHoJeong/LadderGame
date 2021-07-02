package com.polared.laddergame;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class BetSetting extends BaseActivity {
    private static final int RESULT_BET = 200;



    private LinearLayout mainLayout;
    private GridLayout gridLayout;
    private TextView tvBetType;
    private Button btnCancel, btnInputComplete;

    private String[] betType;

    private String[] betNames;

    private String jsonBetNames;

    private int typePosition = 0;
    private int participantNum = 0;

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

        participantNum = getIntent().getIntExtra("participantNumber", 2);
        typePosition = getIntent().getIntExtra("typePosition", 0);

        findViewByIdFunc();

        eventHandlerFunc();

        for(int i = 0; i < participantNum; i++){
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
        ((EditText)gridLayout.getChildAt(participantNum -1).findViewById(R.id.edtBetName))
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
        if(participantNum > betNames.length){
            setToType(typePosition);
        }else{
            for(int i = 0; i < participantNum; i++){

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
                intent.putExtra("participantNumber", participantNum);
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
        betNames = new String[participantNum];

        for(int i = 0; i < participantNum; i++){
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
            public void onClick(DialogInterface dialog, int type) {

                if(typePosition != type){
                    setToType(type);
                    typePosition = type;
                    tvBetType.setText(betType[typePosition]);
                    setEdtBetName();
                }

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void setToType(int type) {

        switch(type){
            case BetData.NONE:
                betNames = nameForBetChoiceBet();
                break;
            case BetData.DRAW_BLANK:
            case BetData.DRAW_PRIZE:
                betNames = betData.nameForBetDrawBlankOrPrize(type, participantNum);
                break;

            case BetData.WHAT_EAT_TODAY:
                betNames = betData.nameForBetWhatEatToday(participantNum);
                break;
            default:
        }

    }

    private String[] nameForBetChoiceBet() {
        String[] betName = new String[participantNum];

        for(int i = 1; i <= participantNum; i++){
            betName[i-1] = getString(R.string.bet)+i;
        }
        return betName;
    }


    private void findViewByIdFunc() {

        mainLayout = findViewById(R.id.mainLayout);
        gridLayout = findViewById(R.id.gridLayout);

        tvBetType = findViewById(R.id.tvBetType);

        btnCancel = findViewById(R.id.btnCancel);
        btnInputComplete = findViewById(R.id.btnInputComplete);


    }

}