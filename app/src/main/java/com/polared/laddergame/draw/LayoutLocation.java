package com.polared.laddergame.draw;

public class LayoutLocation {
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    public LayoutLocation(float startX, float startY, float stopX, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getStopX() {
        return stopX;
    }

    public float getStopY() {
        return stopY;
    }
}
