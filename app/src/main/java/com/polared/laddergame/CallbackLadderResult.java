package com.polared.laddergame;

public interface CallbackLadderResult {
    void relayLadderResult(int resultNum, int participantNum);
    void setClickable();
    void ladderGameEnd(int lastPosition);
}
