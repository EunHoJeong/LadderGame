package com.polared.laddergame;

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

    public void setNumber(int number) {
        this.number = number;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getBetName() {
        return betName;
    }

    public void setBetName(String betName) {
        this.betName = betName;
    }



}
