package com.polared.laddergame.bet;

import android.util.Log;
import android.widget.EditText;

public class BetData {
    public static final int NONE = 0;
    public static final int DRAW_BLANK = 1;
    public static final int DRAW_PRIZE = 2;
    public static final int WHAT_EAT_TODAY = 3;

    private String[] foods;
    private String exclamationFail;
    private String fail;
    private String exclamationPass;
    private String pass;

    public BetData(String[] foods, String exclamationFail, String fail, String exclamationPass, String pass) {
        this.foods = foods;
        this.exclamationFail = exclamationFail;
        this.fail = fail;
        this.exclamationPass = exclamationPass;
        this.pass = pass;
    }




    public String[] nameForBetWhatEatToday(int participantNumber) {

        boolean[] isUsed = new boolean[foods.length];
        String[] betName = new String[participantNumber];

        for(int i = 0; i < participantNumber; i++){
            int randomNumber = (int)(Math.random()*foods.length);

            if(isUsed[randomNumber]) {
                i--;
                continue;
            }else{
                isUsed[randomNumber] = true;
                betName[i] = foods[randomNumber];
            }

        }
        return betName;
    }


    public String[] nameForBetDrawBlankOrPrize(int type, int participantNumber) {

        String draw = null;
        String message = null;

        String[] betName = new String[participantNumber];

        if(type == DRAW_BLANK){

            draw = exclamationFail;
            message = pass;
        }else if(type == DRAW_PRIZE){
            draw = exclamationPass;
            message = fail;
        }
        int randomNumber = (int)(Math.random()*participantNumber);

        for(int i = 0; i < participantNumber; i++){
            betName[i] = message;
        }

        betName[randomNumber] = draw;

        return betName;
    }


}
