package com.polared.laddergame.result;

public class LadderResultData {
    private int number;
    private String participantName;
    private String betName;

    public LadderResultData(int number, String participantName, String betName) {
        this.number = number;
        this.participantName = participantName;
        this.betName = betName;
    }

    public int getNumber() {
        return number;
    }

    public String getParticipantName() {
        return participantName;
    }

    public String getBetName() {
        return betName;
    }


}
