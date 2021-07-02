package com.polared.laddergame.draw;

public class DrawAnimationLocation {
    private int startX;
    private int startY;
    private float stopX;
    private float stopY;

    public DrawAnimationLocation(int startX, int startY, float stopX, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public float getStopX() {
        return stopX;
    }

    public float getStopY() {
        return stopY;
    }
}
