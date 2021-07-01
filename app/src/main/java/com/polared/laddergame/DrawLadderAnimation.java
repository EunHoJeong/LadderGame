package com.polared.laddergame;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class DrawLadderAnimation {
    public static final int DRAW_ANIMATION_X = 10;
    public static final int DRAW_ANIMATION_Y = 11;
    public static final int DRAW_ANIMATION_DIAGONAL = 12;

    private boolean isLeft;
    private boolean isTop;
    private boolean isEnd;

    private int lineNumber;
    private int heightNumber = 1;
    private int participantNumber;
    private int animationType = DRAW_ANIMATION_Y;

    private int startX;
    private int startY;
    private float stopX;
    private float stopY;

    private int ladderDistanceX;
    private int ladderDistanceY;

    private float moveX;
    private float moveY;

    private Paint animationLinePaint;

    private ArrayList<LadderLine> list;
    private ArrayList<DrawAnimationLocation> drawList = new ArrayList<>();


    public DrawLadderAnimation(int lineNumber, int participantNumber, ArrayList<LadderLine> list, Paint animationLinePaint) {
        this.lineNumber = lineNumber;
        this.participantNumber = participantNumber;
        this.list = list;
        this.animationLinePaint = animationLinePaint;

        startX = 135 + (lineNumber * 252);
        startY = 350;
        stopX = 135 + (lineNumber * 252);
        stopY = 350;

        setLadderDistanceX();
    }

    public void drawAnimation(){


        switch (animationType) {
            case DRAW_ANIMATION_X:
                animationLocationX();
                break;
            case DRAW_ANIMATION_Y:
                animationLocationY();
                break;
            case DRAW_ANIMATION_DIAGONAL:
                animationLocationDiagonal();
                break;
        }

    }

    private void animationLocationX() {
        if(isLeft){
            if (stopX < ladderDistanceX) {
                animationType = DRAW_ANIMATION_Y;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = ladderDistanceX;
                stopX = ladderDistanceX;
                startY -= 10;

            } else {
                stopX += moveX;
            }
        }else{
            if (stopX > ladderDistanceX) {
                animationType = DRAW_ANIMATION_Y;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = ladderDistanceX;
                stopX = ladderDistanceX;
                startY -= 10;

            } else {
                stopX += moveX;
            }
        }


    }


    private void animationLocationY(){
        int position = 1;

        if (lineNumber == 0) {
            position = 0;
        }

        if (stopY > 355+(heightNumber*130)) {
            drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

            startY = 350 + (130*heightNumber);
            stopY = 350 + (130*heightNumber);

            if (list.get(lineNumber).getNextLocation()[heightNumber] != 0) {


                if (list.get(lineNumber).getNextLocation()[heightNumber] == heightNumber+2) {
                    animationType = DRAW_ANIMATION_DIAGONAL;

                    isTop = false;
                    moveX = 9.8f;
                    moveY = 10.2f;
                    heightNumber++;
                    lineNumber++;
                } else {
                    animationType = DRAW_ANIMATION_X;
                    lineNumber++;
                    setLadderDistanceX();
                    moveX = 15f;
                    isLeft = false;
                }



            } else if (list.get(lineNumber-position).getNextLocation()[heightNumber] == heightNumber) {

                moveX = -15f;

                animationType = DRAW_ANIMATION_X;
                isLeft = true;

                lineNumber--;
                setLadderDistanceX();



            }  else if(heightNumber-2 > 0) {

                if (list.get(lineNumber - position).getNextLocation()[heightNumber - 2] == heightNumber) {

                    moveX = -9.9f;
                    moveY = -10.2f;

                    heightNumber -= 3;
                    animationType = DRAW_ANIMATION_DIAGONAL;
                    isTop = true;
                    lineNumber--;
                    setLadderDistanceX();
                }
            }
            heightNumber++;


        } else {
            stopY += 20;
        }

        if (stopY > 1650) {
            drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));
            animationType = -1;
        }

    }

    private void setLadderDistanceX() {
        ladderDistanceX = 135+(lineNumber*252);
    }


    private void animationLocationDiagonal(){

        if(isTop){
            if (stopY < 355+(heightNumber*130)) {
                animationType = DRAW_ANIMATION_Y;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = ladderDistanceX;
                stopX = ladderDistanceX;
                startY = 350 + (130*heightNumber);
                stopY = 350 + (130*heightNumber);

                startY -= 5;
                heightNumber++;


            } else {
                stopX += moveX;
                stopY += moveY;
            }
        }else{
            if (stopY > 355+(heightNumber*130)) {
                animationType = DRAW_ANIMATION_Y;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));
                setLadderDistanceX();
                startX = ladderDistanceX;
                stopX = ladderDistanceX;
                startY = 350 + (130*heightNumber);
                stopY = 350 + (130*heightNumber);

                startY -= 20;
                heightNumber++;



            } else {
                stopX += moveX;
                stopY += moveY;
            }
        }


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

    public int getLineNumber() {
        return lineNumber;
    }

    public int getParticipantNumber() {
        return participantNumber;
    }

    public Paint getAnimationLinePaint() {
        return animationLinePaint;
    }

    public void setAnimationType(int animationType) {
        this.animationType = animationType;
    }

    public ArrayList<DrawAnimationLocation> getDrawList() {
        return drawList;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
