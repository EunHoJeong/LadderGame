package com.polared.laddergame;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_PARTICIPANT = 100;
    private static final int RESULT_BET = 200;

    private LinearLayout ladderLayout;
    private Button participantInput, modifyBet;

    private String[] participantNames;
    private String[] betNames;


    private int participantNumber = 4;

    private ActivityResultLauncher mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_CANCELED
                    || result.getData() == null){
                        return ;
                    }

                    int number = result.getData().getIntExtra("participantNumber", 4);

                    if(number > participantNumber){
                        for(int i = number-participantNumber+1; i < number; i++){
                            createView(i);
                        }
                    }else{
                        
                    }

                    if(result.getResultCode() == RESULT_PARTICIPANT){

                        participantNumber = result.getData().getIntExtra("participantNumber", 4);
                        String jsonParticipantNames = result.getData().getStringExtra("jsonParticipantNames");

                        participantNames = jsonFromStringArray(jsonParticipantNames);

                        setParticipantName();

                    }else if(result.getResultCode() == RESULT_BET){

                        String jsonBetNames = result.getData().getStringExtra("jsonBetNames");

                        betNames = jsonFromStringArray(jsonBetNames);

                        setBetName();
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ladderLayout = findViewById(R.id.ladderLayout);

        participantInput = findViewById(R.id.participantInput);

        modifyBet = findViewById(R.id.modifyBet);

        eventHandlerFunc();

        for(int i = 0; i < participantNumber; i++){
            createView(i);
        }

    }

    private void createView(int position) {
        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ladder_item, null, false);

        TextView tvNumber = addView.findViewById(R.id.tvNumber);
        TextView tvParticipantName = addView.findViewById(R.id.tvParticipantName);
        TextView tvBetName = addView.findViewById(R.id.tvBetName);

        tvNumber.setText(""+(position+1));
        tvParticipantName.setText("참여"+(position+1));
        tvBetName.setText("내기"+(position+1));

        ladderLayout.addView(addView);
    }

    private void setBetName() {

        for(int i = 0; i < participantNumber; i++){
            ((TextView) ladderLayout.getChildAt(i).findViewById(R.id.tvParticipantName))
                    .setText(participantNames[i]);
        }
    }

    private void setParticipantName() {
        for(int i = 0; i < participantNumber; i++){
            ((TextView) ladderLayout.getChildAt(i).findViewById(R.id.tvBetName))
                    .setText(betNames[i]);
        }
    }

    private void eventHandlerFunc() {

        participantInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParticipantSetting.class);
            intent.putExtra("participantNumber", participantNumber);

            if(participantNames != null){
                String jsonParticipantNames = stringArrayFromJson(participantNames);
                intent.putExtra("jsonParticipantNames", jsonParticipantNames);
            }

            mStartForResult.launch(intent);
        });

        modifyBet.setOnClickListener(v -> {
            Intent intent = new Intent(this, BetSetting.class);
            intent.putExtra("participantNumber", participantNumber);

            if(betNames != null){
                String jsonBetNames = stringArrayFromJson(betNames);
                intent.putExtra("jsonBetNames", jsonBetNames);
            }

            mStartForResult.launch(intent);
        });
    }

    private String stringArrayFromJson(String[] arrayData){
        Gson gson = new Gson();
        return gson.toJson(arrayData);

    }

    private String[] jsonFromStringArray(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, String[].class);
    }
}