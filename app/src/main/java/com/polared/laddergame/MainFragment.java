package com.polared.laddergame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import static android.app.Activity.RESULT_CANCELED;

public class MainFragment extends Fragment {
    private static final int RESULT_PARTICIPANT = 100;
    private static final int RESULT_BET = 200;

    private static MainFragment mainFragment;

    private LinearLayout ladderLayout;
    private RelativeLayout relativeLayout;
    private Button btnParticipantInput, btnModifyBet, btnStart;

    private LadderCanvas ladderCanvas;
    private RelativeLayout unClicked;

    private String[] participantNames;
    private String[] betNames;


    private int participantNum = 4;
    private int endCount = 0;
    private int typePosition = 0;

    private boolean isStart;

    private LadderViewModel viewModel;

    View view;

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

                    int beforeNumber = participantNum;
                    participantNum = result.getData().getIntExtra("participantNumber", 4);

                    if(result.getResultCode() == RESULT_PARTICIPANT){

                        String jsonParticipantNames = result.getData().getStringExtra("jsonParticipantNames");

                        participantNames = jsonFromStringArray(jsonParticipantNames);

                        setParticipantView(beforeNumber);

                        setParticipantName();

                        ladderCanvas.setLadderLine(participantNum);

                    }else if(result.getResultCode() == RESULT_BET){

                        String jsonBetNames = result.getData().getStringExtra("jsonBetNames");
                        typePosition = result.getData().getIntExtra("typePosition", 0);
                        betNames = jsonFromStringArray(jsonBetNames);

                        setBetName();
                    }


                }
            });

    private void setParticipantView(int number) {
        if(participantNum == number){
            return;
        }

        if(participantNum > number){
            for(int i = number; i < participantNum; i++){
                createView(i);
            }

        }else{
            number--;

            for(int i = number; i >= participantNum; i--){
                ladderLayout.removeViewAt(i);
            }

        }
    }

    public static Fragment getInstance(){
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        return mainFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity().getSupportFragmentManager().findFragmentByTag("result") == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main, container, false);
            colors = new int[]{ContextCompat.getColor(getContext(), R.color.my_pink), ContextCompat.getColor(getContext(), R.color.my_green), ContextCompat.getColor(getContext(), R.color.my_orange)
                    , ContextCompat.getColor(getContext(), R.color.my_indigo), ContextCompat.getColor(getContext(), R.color.my_yellow), ContextCompat.getColor(getContext(), R.color.my_turquoise)
                    , ContextCompat.getColor(getContext(), R.color.my_purple), ContextCompat.getColor(getContext(), R.color.my_sky), ContextCompat.getColor(getContext(), R.color.my_brown)
                    , ContextCompat.getColor(getContext(), R.color.my_dark_purple), ContextCompat.getColor(getContext(), R.color.my_red), ContextCompat.getColor(getContext(), R.color.my_beige) };


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

                @Override
                public void ladderGameEnd() {
                    endCount++;
                    if (endCount == participantNum) {
                        switchScreen();
                    }
                }
            });

            setObserver();

            ladderCanvas = new LadderCanvas(getContext(), participantNum, viewModel);
            findViewByIdFunc(view);

            relativeLayout.addView(ladderCanvas);


            eventHandlerFunc();

            for(int i = 0; i < participantNum; i++){
                createView(i);
            }
        }





        return view;
    }

    private void setObserver() {
        Observer<Integer> participantResultObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                ((TextView)ladderLayout.getChildAt(position).findViewById(R.id.tvNumber)).setAlpha(0.5f);
            }
        };
        viewModel.getCurrentName().observe(getViewLifecycleOwner(), participantResultObserver);
    }

    private void switchScreen(){
        FragmentManager fm = getActivity().getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.main_frameLayout, new LadderResultFragment(), "result");
        ft.addToBackStack(null);
        ft.commit();

    }

    private void findViewByIdFunc(View view) {
        ladderLayout = view.findViewById(R.id.ladderLayout);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        unClicked = view.findViewById(R.id.unClicked);
        btnParticipantInput = view.findViewById(R.id.btnParticipantInput);
        btnModifyBet = view.findViewById(R.id.btnModifyBet);
        btnStart = view.findViewById(R.id.btnStart);
    }

    private void createView(int position) {
        View addView = LayoutInflater.from(getContext()).inflate(R.layout.ladder_item, null, false);

        TextView tvNumber = addView.findViewById(R.id.tvNumber);
        TextView tvParticipantName = addView.findViewById(R.id.tvParticipantName);
        TextView tvBetName = addView.findViewById(R.id.tvBetName);

        String number = String.valueOf(position+1);
        String participantName = getString(R.string.participant)+number;
        String betName = getString(R.string.bet)+number;

        tvNumber.setText(number);
        tvNumber.setOnClickListener(v -> {
            tvParticipantName.setTextColor(colors[position]);
            ladderCanvas.init(LadderCanvas.ANIMATION, position);
            unClicked.setClickable(true);
        });

        tvNumber.setClickable(false);


        tvParticipantName.setText(participantName);
        tvBetName.setText(betName);

        tvNumber.setBackgroundColor(colors[position]);

        ladderLayout.addView(addView);
    }

    private void setBetName() {

        for(int i = 0; i < participantNum; i++){
            ((TextView) ladderLayout.getChildAt(i).findViewById(R.id.tvBetName))
                    .setText(betNames[i]);
        }
    }

    private void setParticipantName() {
        for(int i = 0; i < participantNum; i++){
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

            ladderCanvas.init(LadderCanvas.START, 0);

            btnStart.setVisibility(View.INVISIBLE);
            btnModifyBet.setText(getString(R.string.do_it_again));
            btnParticipantInput.setText(getString(R.string.overall_results));

        });
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
                endCount = 0;

            }else{

                Intent intent = new Intent(getContext(), BetSetting.class);
                intent.putExtra("participantNumber", participantNum);

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
            Log.d("Test", "edtCount = " + endCount);
            if(endCount == participantNum) {
                switchScreen();
                return;
            }

            if(isStart){
                ladderCanvas.allResult();
            }else{
                Intent intent = new Intent(getContext(), ParticipantSetting.class);
                intent.putExtra("participantNumber", participantNum);

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
        for (int i = 0; i < participantNum; i++) {
            ladderLayout.getChildAt(i).findViewById(R.id.tvNumber).setClickable(isClick);
        }
    }

    private void allClear(){
        for (int i = 0; i < participantNum; i++) {
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setBackgroundResource(R.drawable.border_dotted);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvParticipantName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvNumber)).setAlpha(1.0f);
        }
    }


}
