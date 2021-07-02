package com.polared.laddergame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class ParticipantSetting extends AppCompatActivity {
    private static final int RESULT_PARTICIPANT = 100;
    private static final int ADD = 1;
    private static final int NONE = 2;

    private LinearLayout mainLayout;
    private GridLayout gridLayout;
    private TextView tvCurrentHeadcount;
    private ImageButton ibMinusMember, ibPlusMember;
    private Button btnCancel, btnInputComplete;

    private String jsonParticipantNames;
    private String[] participantNames;
    private int participantNumber = 0;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_setting);

        View customView = LayoutInflater.from(this).inflate(R.layout.toolbar, null, false);
        ((TextView)customView.findViewById(R.id.tvTitle)).setText(getString(R.string.enter_participants));
        customView.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView);

        participantNumber = getIntent().getIntExtra("participantNumber", 2);

        findViewByIdFunc();

        eventHandlerFunc();

        for(int i = 0; i < participantNumber; i++){
            createView(i);
        }

        setLastEdtParticipantNameImeOption(NONE);

        jsonParticipantNames = getIntent().getStringExtra("jsonParticipantNames");

        if(jsonParticipantNames != null){
            jsonFromStringArray();
            setEdtParticipantName();
        }


        if (participantNumber == 2) {
            ibMinusMember.setClickable(false);
            ibMinusMember.setAlpha(0.3f);
        } else if (participantNumber == 12) {
            ibPlusMember.setClickable(false);
            ibPlusMember.setAlpha(0.3f);
        }


    }

    private void setLastEdtParticipantNameImeOption(int type) {

        switch (type) {
            case ADD:
                ((EditText)gridLayout.getChildAt(participantNumber-2).findViewById(R.id.edtParticipantName))
                        .setImeOptions(EditorInfo.IME_ACTION_NEXT);
                break;
        }

        ((EditText)gridLayout.getChildAt(participantNumber-1).findViewById(R.id.edtParticipantName))
                .setImeOptions(EditorInfo.IME_ACTION_DONE);
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

        edtParticipantName.setText(getString(R.string.participant)+(position+1));

        edtParticipantName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edtParticipantName.setSelection(edtParticipantName.length());
                }
            }
        });


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
        tvNumber.setBackgroundColor(LGColors.getColor(position));
    }


    @SuppressLint("SetTextI18n")
    private void eventHandlerFunc() {

        tvCurrentHeadcount.setText(getString(R.string.participant)
                +getString(R.string.blank)
                +participantNumber
                +getString(R.string.person));

        registerIbMinusMember();

        registerIbPlusMember();

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
            tvCurrentHeadcount.setText(getString(R.string.participant)
                    +getString(R.string.blank)
                    +participantNumber
                    +getString(R.string.person));

            createView(participantNumber-1);
            setLastEdtParticipantNameImeOption(ADD);
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
            tvCurrentHeadcount.setText(getString(R.string.participant)
                    +getString(R.string.blank)
                    +participantNumber
                    +getString(R.string.person));
            gridLayout.removeViewAt(participantNumber);
            setLastEdtParticipantNameImeOption(NONE);


        });

    }

    private boolean checkData() {
        participantNames = new String[participantNumber];

        for(int i = 0; i < participantNumber; i++){
            EditText participantName = ((EditText)gridLayout.getChildAt(i).findViewById(R.id.edtParticipantName));
            if(participantName.length() > 0){
                participantNames[i] = participantName.getText().toString();
            }else{
                participantName.requestFocus();
                Toast.makeText(this, "참여자를 입력해주세요.", Toast.LENGTH_SHORT).show();

                return false;
            }

        }

        return true;
    }

    private void findViewByIdFunc() {
        mainLayout = findViewById(R.id.mainLayout);
        gridLayout = findViewById(R.id.gridLayout);

        tvCurrentHeadcount = findViewById(R.id.tvCurrentHeadcount);

        ibMinusMember = findViewById(R.id.ibMinusMember);
        ibPlusMember = findViewById(R.id.ibPlusMember);

        btnCancel = findViewById(R.id.btnCancel);
        btnInputComplete = findViewById(R.id.btnInputComplete);
    }



}