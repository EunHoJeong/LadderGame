package com.polared.laddergame;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_PARTICIPANT = 100;
    private static final int RESULT_BET = 200;

    private LinearLayout ladderLayout;
    private RelativeLayout relativeLayout;
    private Button btnParticipantInput, btnModifyBet, btnStart;

    private LadderCanvas ladderCanvas;

    private String[] participantNames;
    private String[] betNames;

    private int[] ladderResult;

    private int participantNumber = 4;
    private int typePosition = 0;

    private boolean isStart;

    private int[] colors = new int[]{R.color.my_pink, R.color.my_green, R.color.my_orange, R.color.my_indigo
            , R.color.my_yellow, R.color.my_turquoise, R.color.my_purple, R.color.my_sky
            , R.color.my_brown, R.color.my_gray, R.color.my_red, R.color.my_beige };

    private ActivityResultLauncher mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_CANCELED
                    || result.getData() == null){
                        return ;
                    }

                    int beforeNumber = participantNumber;
                    participantNumber = result.getData().getIntExtra("participantNumber", 4);

                    if(result.getResultCode() == RESULT_PARTICIPANT){

                        String jsonParticipantNames = result.getData().getStringExtra("jsonParticipantNames");

                        participantNames = jsonFromStringArray(jsonParticipantNames);

                        setParticipantView(beforeNumber);

                        setParticipantName();

                        ladderCanvas.init(participantNumber);

                    }else if(result.getResultCode() == RESULT_BET){

                        String jsonBetNames = result.getData().getStringExtra("jsonBetNames");
                        typePosition = result.getData().getIntExtra("typePosition", 0);
                        betNames = jsonFromStringArray(jsonBetNames);

                        setBetName();
                    }


                }
            });

    private void setParticipantView(int number) {
        if(participantNumber == number){
            return;
        }

        if(participantNumber > number){
            for(int i = number; i < participantNumber; i++){
                createView(i);
            }

        }else{
            number--;

            for(int i = number; i >= participantNumber; i--){
                ladderLayout.removeViewAt(i);
            }

        }
    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar);

        CallbackLadderResult callbackLadderResult = new CallbackLadderResult() {
            @Override
            public void relayLadderResult(int[] result) {
                ladderResult = result;
            }
        };

        ladderCanvas = new LadderCanvas(this, participantNumber, callbackLadderResult);


        ladderLayout = findViewById(R.id.ladderLayout);
        relativeLayout = findViewById(R.id.relativeLayout);



        relativeLayout.addView(ladderCanvas);


        btnParticipantInput = findViewById(R.id.btnParticipantInput);
        btnModifyBet = findViewById(R.id.btnModifyBet);
        btnStart = findViewById(R.id.btnStart);


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

        String number = String.valueOf(position+1);
        String participantName = "참여"+number;
        String betName = "내기"+number;

        tvNumber.setText(number);

        tvNumber.setOnClickListener(v -> {
            ladderCanvas.setIsAnimation(true);
            ladderCanvas.goAnimation(position);
        });

        tvParticipantName.setText(participantName);
        tvBetName.setText(betName);

        tvNumber.setBackgroundResource(colors[position]);

        ladderLayout.addView(addView);
    }

    private void setBetName() {

        for(int i = 0; i < participantNumber; i++){
            ((TextView) ladderLayout.getChildAt(i).findViewById(R.id.tvBetName))
                    .setText(betNames[i]);
        }
    }

    private void setParticipantName() {
        for(int i = 0; i < participantNumber; i++){
            ((TextView) ladderLayout.getChildAt(i).findViewById(R.id.tvParticipantName))
                    .setText(participantNames[i]);
        }
    }

    private void eventHandlerFunc() {

        registerBtnParticipantInput();

        registerBtnModifyBet();


        btnStart.setOnClickListener(v -> {
            registerTvBetName();

            isStart = true;

            btnStart.setVisibility(View.INVISIBLE);
            btnModifyBet.setText("다시하기");
            btnParticipantInput.setText("전체결과");

            ladderResult = ladderCanvas.getLadderResult();

        });
    }

    private void registerTvBetName() {
        for(int i = 0; i < participantNumber; i++){
            final int position = i;
            ladderLayout.getChildAt(i).findViewById(R.id.tvBetName).setOnClickListener(v -> {
                Toast.makeText(this, "참여"+ladderResult[position], Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void registerBtnModifyBet() {
        btnModifyBet.setOnClickListener(v -> {
            if(isStart){

                isStart = false;

                ladderCanvas.clearDraw();
                ladderCanvas.invalidate();

                btnStart.setVisibility(View.VISIBLE);
                btnModifyBet.setText("내기 수정하기");
                btnParticipantInput.setText("참여자 입력하기");


            }else{

                Intent intent = new Intent(this, BetSetting.class);
                intent.putExtra("participantNumber", participantNumber);

                if(betNames != null){
                    String jsonBetNames = stringArrayFromJson(betNames);
                    intent.putExtra("jsonBetNames", jsonBetNames);
                    intent.putExtra("typePosition", typePosition);
                }

                mStartForResult.launch(intent);

            }

        });
    }

    private void registerBtnParticipantInput() {
        btnParticipantInput.setOnClickListener(v -> {
            if (isStart) {
                ladderCanvas.allLadderResult();
            } else {
                Intent intent = new Intent(this, ParticipantSetting.class);
                intent.putExtra("participantNumber", participantNumber);

                if(participantNames != null){
                    String jsonParticipantNames = stringArrayFromJson(participantNames);
                    intent.putExtra("jsonParticipantNames", jsonParticipantNames);
                }

                mStartForResult.launch(intent);
            }

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