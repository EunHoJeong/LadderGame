package com.polared.laddergame.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.polared.laddergame.bet.BetData;
import com.polared.laddergame.bet.BetSetting;
import com.polared.laddergame.utils.LGColors;
import com.polared.laddergame.draw.LadderCanvas;
import com.polared.laddergame.result.LadderResultData;
import com.polared.laddergame.result.LadderResultFragment;
import com.polared.laddergame.LadderViewModel;
import com.polared.laddergame.participant.ParticipantSetting;
import com.polared.laddergame.R;
import com.polared.laddergame.draw.LayoutLocation;
import com.polared.laddergame.utils.TransactionJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;

public class MainFragment extends Fragment {
    private static final int RESULT_PARTICIPANT = 100;
    private static final int RESULT_BET = 200;
    private static final int SWITCH_SCREEN = 10;


    private LinearLayout ladderLayout;
    private RelativeLayout unClicked;
    private RelativeLayout canvasContainer;
    private Button btnParticipantInput, btnModifyBet, btnStart;

    private LadderCanvas ladderCanvas;

    private String[] participantNames;
    private String[] betNames;

    private ArrayList<LadderResultData> resultList;


    private int participantNum = 4;
    private int endCount = 0;
    private int typePosition = 0;

    private boolean isStart;

    private LadderViewModel viewModel;
    private View view;


    private Handler handler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SWITCH_SCREEN:
                    switchScreen();
                    break;
            }

        }
    };

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
                    participantNum = result.getData().getIntExtra("participantNumber", participantNum);



                    if(result.getResultCode() == RESULT_PARTICIPANT){

                        String jsonParticipantNames = result.getData().getStringExtra("jsonParticipantNames");

                        participantNames = TransactionJson.jsonFromStringArray(jsonParticipantNames);
                        betNames = new String[participantNum];

                        setParticipantView(beforeNumber);

                        setParticipantName();

                        getBetName();

                        ladderCanvas.setLadderLine(participantNum);

                        if (beforeNumber > participantNum) {
                            for (int i = beforeNumber-1; i > participantNum; i--) {
                                ladderCanvas.removeLocation(i);
                            }

                        }

                    }else if(result.getResultCode() == RESULT_BET){

                        String jsonBetNames = result.getData().getStringExtra("jsonBetNames");
                        typePosition = result.getData().getIntExtra("typePosition", 0);
                        betNames = TransactionJson.jsonFromStringArray(jsonBetNames);

                        setBetName();
                    }

                    if (beforeNumber != participantNum) {
                        setToType(typePosition);
                        setBetName();
                    }



                }
            });

    public void setToType(int type) {

        switch(type){

            case BetData.DRAW_BLANK:
            case BetData.DRAW_PRIZE:
                betNames = ((MainActivity)getContext()).betData.nameForBetDrawBlankOrPrize(type, participantNum);
                break;

            case BetData.WHAT_EAT_TODAY:
                betNames = ((MainActivity)getContext()).betData.nameForBetWhatEatToday(participantNum);
                break;
            default:
        }

    }

    private void getBetName(){
        for (int i = 0; i < participantNum; i++) {
            betNames[i] = ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).getText().toString();
        }
    }

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

    public static Fragment newInstance(){
        return new MainFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main, container, false);

            setViewModel();

            setObserver();

            findViewByIdFunc(view);

            ladderCanvas = new LadderCanvas(getContext(), participantNum, viewModel);
            canvasContainer.addView(ladderCanvas);

            eventHandlerFunc();

            participantNames = new String[participantNum];
            betNames = new String[participantNum];

            for(int i = 0; i < participantNum; i++){
                createView(i);
            }

            resultList = new ArrayList<>();
        }




        if (getArguments() != null && getArguments().getString("clear") != null) {
            initLadderGame();
        }





        return view;
    }

    private void setViewModel() {
        viewModel = new LadderViewModel(new CallbackLadderResult() {
            @Override
            public void relayLadderResult(int resultNum, int participantNum) {
                ((TextView)ladderLayout.getChildAt(resultNum).findViewById(R.id.tvBetName)).setBackgroundResource(borderColor[participantNum]);
                ((TextView)ladderLayout.getChildAt(resultNum).findViewById(R.id.tvBetName)).setTextColor(LGColors.getColor(participantNum));
                resultList.add(new LadderResultData(participantNum, participantNames[participantNum], betNames[resultNum]));
            }

            @Override
            public void setClickable() {
                unClicked.setClickable(false);
            }

            @Override
            public void ladderGameEnd(int lastPosition) {
                endCount++;
                if (endCount == participantNum) {
                    ladderCanvas.init(LadderCanvas.ANIMATION, lastPosition);
                    Message message = new Message();
                    message.what = SWITCH_SCREEN;
                    handler.sendMessageDelayed(message, 700);
                }
            }
        });
    }

    private void setObserver() {

        Observer<Integer> participantResultObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {

                ((TextView)ladderLayout.getChildAt(position).findViewById(R.id.tvNumber)).setAlpha(0.5f);
            }
        };

        viewModel.getCurrentName().observeForever(participantResultObserver);
    }

    private void switchScreen(){

        Collections.sort(resultList, new Comparator<LadderResultData>() {
            @Override
            public int compare(LadderResultData o1, LadderResultData o2) {
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        });

        Gson gson = new Gson();
        String jsonArrayResultDataList = gson.toJson(resultList);

        Bundle bundle = new Bundle();
        bundle.putString("jsonArrayResultDataList", jsonArrayResultDataList);

        LadderResultFragment ladderResultFragment = new LadderResultFragment();
        ladderResultFragment.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.main_frameLayout, ladderResultFragment, "result");
        ft.addToBackStack("main");

        ft.commit();

    }

    private void findViewByIdFunc(View view) {
        ladderLayout = (LinearLayout) view.findViewById(R.id.ladderLayout);

        canvasContainer = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        unClicked = (RelativeLayout) view.findViewById(R.id.unClicked);

        btnParticipantInput = (Button) view.findViewById(R.id.btnParticipantInput);
        btnModifyBet = (Button) view.findViewById(R.id.btnModifyBet);
        btnStart = (Button) view.findViewById(R.id.btnStart);
    }

    private void createView(int position) {
        View addView = LayoutInflater.from(getContext()).inflate(R.layout.ladder_item, null, false);

        TextView tvNumber = addView.findViewById(R.id.tvNumber);
        TextView tvParticipantName = addView.findViewById(R.id.tvParticipantName);
        TextView tvBetName = addView.findViewById(R.id.tvBetName);

        String number = String.valueOf(position+1);
        String participantName = getString(R.string.participant)+number;
        String betName = getString(R.string.bet)+number;

        participantNames[position] = participantName;
        betNames[position] = betName;


        tvNumber.setOnClickListener(v -> {
            tvParticipantName.setTextColor(LGColors.getColor(position));
            ladderCanvas.init(LadderCanvas.ANIMATION, position);
            unClicked.setClickable(true);
        });

        tvNumber.setText(number);
        tvNumber.setClickable(false);
        tvNumber.setBackgroundColor(LGColors.getColor(position));

        tvParticipantName.setText(participantName);

        tvBetName.setText(betName);

        ladderLayout.addView(addView);

        getViewLocation(addView);

    }

    private void getViewLocation(View addView) {
        addView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                addView.getViewTreeObserver().removeOnPreDrawListener(this);
                LinearLayout itemLayout = addView.findViewById(R.id.itemLayout);
                TextView tvParticipantName = addView.findViewById(R.id.tvParticipantName);

                float left = itemLayout.getLeft();
                float right = itemLayout.getRight();
                float x = (left+right) / 2;
                float bottom = tvParticipantName.getBottom()+20;
                float top = itemLayout.getBottom() - getResources().getDimension(R.dimen.ladder_line_height);

                ladderCanvas.addLocation(new LayoutLocation(x, bottom, x, top));

                return true;
            }
        });
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

        registerBtnStart();

    }

    private void registerBtnModifyBet() {
        btnModifyBet.setOnClickListener(v -> {
            if(isStart){

                initLadderGame();

            }else{

                Intent intent = new Intent(getContext(), BetSetting.class);
                intent.putExtra("participantNumber", participantNum);

                if(betNames != null){
                    String jsonBetNames = TransactionJson.stringArrayFromJson(betNames);
                    intent.putExtra("jsonBetNames", jsonBetNames);
                    intent.putExtra("typePosition", typePosition);
                }

                mStartForResult.launch(intent);

            }

        });
    }


    private void registerBtnParticipantInput() {
        btnParticipantInput.setOnClickListener(v -> {
            if(endCount == participantNum) {
                switchScreen();
                return;
            }

            if(isStart){

                allResult();

            }else{
                Intent intent = new Intent(getContext(), ParticipantSetting.class);
                intent.putExtra("participantNumber", participantNum);

                if(participantNames != null){
                    String jsonParticipantNames = TransactionJson.stringArrayFromJson(participantNames);
                    intent.putExtra("jsonParticipantNames", jsonParticipantNames);
                }

                mStartForResult.launch(intent);
            }

        });
    }

    private void registerBtnStart() {
        btnStart.setOnClickListener(v -> {
            setTvNumberClickable(true);
            isStart = true;

            ladderCanvas.init(LadderCanvas.START, 0);

            btnStart.setVisibility(View.INVISIBLE);
            btnModifyBet.setText(getString(R.string.do_it_again));
            btnParticipantInput.setText(getString(R.string.overall_results));
            shuffleBetName();

        });
    }


    private void shuffleBetName() {
        Random random = new Random();

        for (int i = 0; i < betNames.length; i++) {
            int shuffle = random.nextInt(betNames.length);
            String temp = betNames[i];
            betNames[i] = betNames[shuffle];
            betNames[shuffle] = temp;
        }

        setBetName();
    }

    private void allResult() {
        ladderCanvas.allResult();
        resultList = new ArrayList<>();
        endCount = 0;
        unClicked.setClickable(true);

        for (int i = 0; i < participantNum; i++) {
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvParticipantName)).setTextColor(LGColors.getColor(i));
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvNumber)).setAlpha(1.0f);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setBackgroundResource(R.drawable.border_dotted);
        }
    }


    private void setTvNumberClickable(boolean isClick) {
        for (int i = 0; i < participantNum; i++) {
            ladderLayout.getChildAt(i).findViewById(R.id.tvNumber).setClickable(isClick);
        }
    }

    private void initLadderGame(){
        isStart = false;
        endCount = 0;
        resultList = new ArrayList<>();

        ladderCanvas.clearDraw();

        btnStart.setVisibility(View.VISIBLE);
        btnModifyBet.setText(getString(R.string.pix_bet));
        btnParticipantInput.setText(getString(R.string.enter_participants));


        for (int i = 0; i < participantNum; i++) {
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setBackgroundResource(R.drawable.border_dotted);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvBetName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvParticipantName)).setTextColor(Color.BLACK);
            ((TextView)ladderLayout.getChildAt(i).findViewById(R.id.tvNumber)).setAlpha(1.0f);
        }

        setTvNumberClickable(false);
    }




}
