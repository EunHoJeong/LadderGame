package com.polared.laddergame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class ParticipantSetting extends AppCompatActivity {
    private static final int RESULT_PARTICIPANT = 100;

    private GridLayout gridLayout;
    private TextView tvCurrentHeadcount;
    private ImageButton ibMinusMember, ibPlusMember;
    private Button btnCancel, btnInputComplete;

    private String jsonParticipantNames;
    private String[] participantNames;
    private int participantNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_setting);

        participantNumber = getIntent().getIntExtra("participantNumber", 2);

        findViewByIdFunc();

        eventHandlerFunc();

        for(int i = 0; i < participantNumber; i++){
            createView(i);
        }

        jsonParticipantNames = getIntent().getStringExtra("jsonParticipantNames");

        if(jsonParticipantNames != null){
            jsonFromStringArray();
            setEdtParticipantName();
        }



    }

    private void jsonFromStringArray() {
        Gson gson = new Gson();
        participantNames = gson.fromJson(jsonParticipantNames, String[].class);
    }

    private void stringArrayFromJson(){
        Gson gson = new Gson();
        jsonParticipantNames = gson.toJson(participantNames);
    }

    @SuppressLint("SetTextI18n")
    private void createView(int position) {
        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.participan_item, null, false);

        TextView tvNumber = addView.findViewById(R.id.tvNumber);
        EditText edtParticipantName = addView.findViewById(R.id.edtParticipantName);
        ImageButton ibTextClear = addView.findViewById(R.id.ibTextClear);

        setTvNumber(tvNumber, position);

        edtParticipantName.setText("참여"+(position+1));

        ibTextClear.setOnClickListener(v -> {
            edtParticipantName.setText("");
        });

        gridLayout.addView(addView);

    }

    private void setEdtParticipantName() {
        for(int i = 0; i < participantNumber; i++){
            ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtParticipantName))
                    .setText(participantNames[i]);
        }
    }

    private void setTvNumber(TextView tvNumber, int position) {
        tvNumber.setText(""+(position+1));
        tvNumber.setBackgroundColor(Color.BLACK);
    }


    @SuppressLint("SetTextI18n")
    private void eventHandlerFunc() {

        tvCurrentHeadcount.setText("참여 "+participantNumber+"명");

        registerIbMinusMember();

        registerIbPlusMember();

        registerBtnCancel();

        registerBtnInputComplete();

    }

    private void registerBtnInputComplete() {
        btnInputComplete.setOnClickListener(v -> {



            if(checkData()){

                stringArrayFromJson();

                Intent intent = new Intent();
                intent.putExtra("jsonParticipantNames", jsonParticipantNames);
                intent.putExtra("participantNumber", participantNumber);
                setResult(RESULT_PARTICIPANT, intent);

                finish();
            }

        });
    }

    private void registerBtnCancel() {
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerIbPlusMember() {
        ibPlusMember.setOnClickListener(v -> {
            if(participantNumber == 11){
                ibPlusMember.setClickable(false);
                ibPlusMember.setAlpha(0.3f);
            }

            if(!ibMinusMember.isClickable()){
                ibMinusMember.setClickable(true);
                ibMinusMember.setAlpha(1.0f);
            }

            participantNumber++;
            tvCurrentHeadcount.setText("참여 " + participantNumber + "명");
            createView(participantNumber-1);
        });
    }

    private void registerIbMinusMember() {

        ibMinusMember.setOnClickListener(v -> {
            if(participantNumber == 3){
                ibMinusMember.setClickable(false);
                ibMinusMember.setAlpha(0.3f);
            }

            if(!ibPlusMember.isClickable()){
                ibPlusMember.setClickable(true);
                ibPlusMember.setAlpha(1.0f);
            }

            participantNumber--;
            tvCurrentHeadcount.setText("참여 " + participantNumber + "명");
            gridLayout.removeViewAt(participantNumber);


        });

    }

    private boolean checkData() {
        participantNames = new String[participantNumber];

        for(int i = 0; i < participantNumber; i++){
            if(((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtParticipantName)).length() > 0){
                participantNames[i] = ((EditText)gridLayout
                                     .getChildAt(i)
                                     .findViewById(R.id.edtParticipantName))
                                     .getText()
                                     .toString();
            }else{
                return false;
            }

        }

        return true;
    }

    private void findViewByIdFunc() {
        gridLayout = findViewById(R.id.gridLayout);

        tvCurrentHeadcount = findViewById(R.id.tvCurrentHeadcount);

        ibMinusMember = findViewById(R.id.ibMinusMember);
        ibPlusMember = findViewById(R.id.ibPlusMember);

        btnCancel = findViewById(R.id.btnCancel);
        btnInputComplete = findViewById(R.id.btnInputComplete);
    }
}