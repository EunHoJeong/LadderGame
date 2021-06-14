package com.polared.laddergame;

public class LadderLine {
    int[] location;
    int[] nextLocation;

    public LadderLine(int[] location, int[] nextLocation) {
        this.location = location;
        this.nextLocation = nextLocation;
    }

    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

    public int[] getNextLocation() {
        return nextLocation;
    }

    public void setNextLocation(int[] nextLocation) {
        this.nextLocation = nextLocation;
    }
}
