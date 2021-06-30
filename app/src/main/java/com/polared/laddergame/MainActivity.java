package com.polared.laddergame;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_PARTICIPANT = 100;
    private static final int RESULT_BET = 200;

    private LinearLayout ladderLayout;
    private RelativeLayout relativeLayout;
    private Button btnParticipantInput, btnModifyBet, btnStart;

    private LadderCanvas ladderCanvas;
    private RelativeLayout unClicked;

    private String[] participantNames;
    private String[] betNames;

    private int[] ladderResult;

    private int participantNumber = 4;
    private int typePosition = 0;

    private boolean isStart;

    private LadderViewModel viewModel;

    private ExecutorService executorService;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    private int[] colors;
    private int[] borderColor = new int[] { R.drawable.border_dotted_pink, R.drawable.border_dotted_green
            , R.drawable.border_dotted_orange, R.drawable.border_dotted_indigo, R.drawable.border_dotted_yellow
            , R.drawable.border_dotted_turquoise, R.drawable.border_dotted_purple, R.drawable.border_dotted_sky
            , R.drawable.border_dotted_brown, R.drawable.border_dotted_gray, R.drawable.border_dotted_red
            , R.drawable.border_dotted_beige};


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

                        ladderCanvas.setLadderLine(participantNumber);

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

        colors = new int[]{ContextCompat.getColor(getApplicationContext(), R.color.my_pink), ContextCompat.getColor(getApplicationContext(), R.color.my_green), ContextCompat.getColor(getApplicationContext(), R.color.my_orange)
                , ContextCompat.getColor(getApplicationContext(), R.color.my_indigo), ContextCompat.getColor(getApplicationContext(), R.color.my_yellow), ContextCompat.getColor(getApplicationContext(), R.color.my_turquoise)
                , ContextCompat.getColor(getApplicationContext(), R.color.my_purple), ContextCompat.getColor(getApplicationContext(), R.color.my_sky), ContextCompat.getColor(getApplicationContext(), R.color.my_brown)
                , ContextCompat.getColor(getApplicationContext(), R.color.my_gray), ContextCompat.getColor(getApplicationContext(), R.color.my_red), ContextCompat.getColor(getApplicationContext(), R.color.my_beige) };


        viewModel = new LadderViewModel(new CallbackLadderResult() {
            @Override
            public void relayLadderResult(int resultNum, int participantNum) {
                ((TextView)ladderLayout.getChildAt(resultNum).findViewById(R.id.tvBetName)).setBackgroundResource(borderColor[participantNum]);
                ((TextView)ladderLayout.getChildAt(resultNum).findViewById(R.id.tvBetName)).setTextColor(colors[participantNum]);
            }

            @Override
            public void setClickable() {
                unClicked.setClickable(false);
            }
        });

        setObserver();

        ladderCanvas = new LadderCanvas(this, participantNumber, viewModel);

        findViewByIdFunc();

        relativeLayout.addView(ladderCanvas);


        eventHandlerFunc();

        for(int i = 0; i < participantNumber; i++){
            createView(i);
        }

    }

    private void setObserver() {
        Observer<Integer> participantResultObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                ((TextView)ladderLayout.getChildAt(position).findViewById(R.id.tvNumber)).setAlpha(0.5f);
            }
        };
        viewModel.getCurrentName().observe(this, participantResultObserver);
    }

    private void findViewByIdFunc() {
        ladderLayout = findViewById(R.id.ladderLayout);
        relativeLayout = findViewById(R.id.relativeLayout);
        unClicked = findViewById(R.id.unClicked);
        btnParticipantInput = findViewById(R.id.btnParticipantInput);
        btnModifyBet = findViewById(R.id.btnModifyBet);
        btnStart = findViewById(R.id.btnStart);
    }

    private void createView(int position) {
        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ladder_item, null, false);

        TextView tvNumber = addView.findViewById(R.id.tvNumber);
        TextView tvParticipantName = addView.findViewById(R.id.tvParticipantName);
        TextView tvBetName = addView.findViewById(R.id.tvBetName);

        String number = String.valueOf(position+1);
        String participantName = getString(R.string.participant)+number;
        String betName = getString(R.string.bet)+number;

        tvNumber.setText(number);
        tvNumber.setOnClickListener(v -> {
            tvParticipantName.setTextColor(colors[position]);
            ladderCanvas.init(LadderCanvas.ANIMATION, LadderCanvas.DRAW_ANIMATION_Y, position);
            unClicked.setClickable(true);
        });

        tvNumber.setClickable(false);


        tvParticipantName.setText(participantName);
        tvBetName.setText(betName);

        tvNumber.setBackgroundColor(colors[position]);

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
            setTvNumberClickable(true);
            isStart = true;

            ladderCanvas.init(LadderCanvas.START, 0, 0);

            btnStart.setVisibility(View.INVISIBLE);
            btnModifyBet.setText(getString(R.string.do_it_again));
            btnParticipantInput.setText(getString(R.string.overall_results));

        });
    }

    private void registerTvBetName() {
        for(int i = 0; i < participantNumber; i++){
            final int position = i;
            ladderLayout.getChildAt(i).findViewById(R.id.tvBetName).setOnClickListener(v -> {
                Toast.makeText(this, getString(R.string.participant)+ladderResult[position], Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void registerBtnModifyBet() {
        btnModifyBet.setOnClickListener(v -> {
            if(isStart){

                isStart = false;

                ladderCanvas.clearDraw();

                btnStart.setVisibility(View.VISIBLE);
                btnModifyBet.setText(getString(R.string.pix_bet));
                btnParticipantInput.setText(getString(R.string.enter_participants));

                setTvNumberClickable(false);
                allClear();


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

            if(isStart){

            }else{
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

    private void setTvNumberClickable(boolean isClick) {
        for (int i = 0; i < participantNumber; i++) {
            ladderLayout.getChildAt(i).findViewById(R.id.tvNumber).setClickable(isClick);
        }
    }

    private void allClear(){
        for (int i = 0; i < participantNumber; i++) {
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setBackgroundResource(R.drawable.border_dotted);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvParticipantName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvNumber)).setAlpha(1.0f);
        }
    }



}