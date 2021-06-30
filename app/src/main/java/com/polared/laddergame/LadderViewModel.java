package com.polared.laddergame;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class LadderViewModel extends ViewModel {
    private MutableLiveData<Integer> liveData;
    private CallbackLadderResult callbackLadderResult;

    public LadderViewModel(CallbackLadderResult callbackLadderResult) {
        this.callbackLadderResult = callbackLadderResult;
    }

    public MutableLiveData<Integer> getCurrentName() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void ladderResult(int resultNum, int participantNum) {
        liveData.setValue(participantNum);
        callbackLadderResult.relayLadderResult(resultNum, participantNum);
    }

    public void setClickable(){
        callbackLadderResult.setClickable();
    }
}
