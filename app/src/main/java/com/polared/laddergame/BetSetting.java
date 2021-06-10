package com.polared.laddergame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

public class BetSetting extends AppCompatActivity {
    private static final int RESULT_BET = 200;

    private GridLayout gridLayout;
    private TextView tvBetType;
    private Button btnCancel, btnInputComplete;

    private String[] betType = new String[]{"내기를 선택해주세요", "꽝뽑기", "당첨뽑기", "오늘 뭐 먹지"};
    private String[] betNames;

    private String jsonBetNames;

    private int typePosition = 0;
    private int participantNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_setting);

        participantNumber = getIntent().getIntExtra("participantNumber", 2);

        findViewByIdFunc();

        eventHandlerFunc();

        for(int i = 0; i < participantNumber; i++){
            createView(i);
        }

        jsonBetNames = getIntent().getStringExtra("jsonBetNames");

        if(jsonBetNames != null){
            jsonFromStringArray();
            setEdtBetName();
        }

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
        for(int i = 0; i < participantNumber; i++){
            ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtBetName))
                    .setText(betNames[i]);
        }
    }

    private void eventHandlerFunc() {

        registerTvBetType();

        registerBtnCancel();

        registerBtnInputComplete();

    }

    private void registerBtnInputComplete() {

        btnInputComplete.setOnClickListener(v -> {

            if(checkData()){
                stringArrayFromJson();

                Intent intent = new Intent();
                intent.putExtra("jsonBetNames", jsonBetNames);
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
                }
                typePosition = which;

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setToType(int which) {

    }

    private void findViewByIdFunc() {

        gridLayout = findViewById(R.id.gridLayout);

        tvBetType = findViewById(R.id.tvBetType);

        btnCancel = findViewById(R.id.btnCancel);
        btnInputComplete = findViewById(R.id.btnInputComplete);


    }
}