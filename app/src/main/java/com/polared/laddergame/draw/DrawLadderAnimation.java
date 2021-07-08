package com.polared.laddergame.draw;

import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

public class DrawLadderAnimation {
    public static final int NONE = -1;
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

    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    private int xDistance;
    private int yDistance;
    private int ladderXDistance;
    private int ladderYDistance;

    private int reachPointX;
    private int reachPointY;
    private float endPoint;

    private float leftMoveX;
    private float rightMoveX;
    private float topDiagonalMoveX;
    private float topDiagonalMoveY;
    private float bottomDiagonalMoveX;
    private float bottomDiagonalMoveY;
    private float bottomMoveY;

    private Paint animationLinePaint;

    private ArrayList<LadderLine> list;
    private ArrayList<DrawAnimationLocation> drawList = new ArrayList<>();


    public DrawLadderAnimation(int lineNumber, int participantNumber, ArrayList<LadderLine> list, Paint animationLinePaint, int xDistance, int yDistance, int ladderXDistance, int ladderYDistance, float x, float y, float endPoint) {
        this.lineNumber = lineNumber;
        this.participantNumber = participantNumber;
        this.list = list;
        this.animationLinePaint = animationLinePaint;

        this.xDistance = xDistance;
        this.yDistance = yDistance;
        this.ladderXDistance = ladderXDistance;
        this.ladderYDistance = ladderYDistance;

        this.startX = x;
        this.startY = y;
        this.stopX = x;
        this.stopY = y;

        this.endPoint = endPoint;


        setReachPointX();
        setReachPointY();

        leftMoveX = -ladderXDistance/20;
        rightMoveX = ladderXDistance/20;
        bottomMoveY = ladderYDistance/10;
        topDiagonalMoveX = -ladderXDistance/20;
        topDiagonalMoveY = -ladderYDistance/10;
        bottomDiagonalMoveX = ladderXDistance/20;
        bottomDiagonalMoveY = ladderYDistance/10;
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
            if (stopX < reachPointX) {
                stopX = reachPointX;
                animationType = DRAW_ANIMATION_Y;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = reachPointX;

                startY -= 10;

            } else {
                stopX += leftMoveX;
            }
        }else{
            if (stopX > reachPointX) {
                animationType = DRAW_ANIMATION_Y;
                stopX = reachPointX;

                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = reachPointX;

                startY -= 10;

            } else {
                stopX += rightMoveX;
            }
        }


    }


    private void animationLocationY(){
        int position = 1;

        if (lineNumber == 0) {
            position = 0;
        }

        if (stopY > reachPointY) {
            drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

            stopY = reachPointY;
            startY = reachPointY;

            if (list.get(lineNumber).getNextLocation()[heightNumber] != 0) {


                if (list.get(lineNumber).getNextLocation()[heightNumber] == heightNumber+2) {
                    animationType = DRAW_ANIMATION_DIAGONAL;

                    isTop = false;
                    heightNumber++;
                    lineNumber++;
                    setReachPointX();
                } else {
                    animationType = DRAW_ANIMATION_X;
                    lineNumber++;
                    setReachPointX();
                    isLeft = false;
                    startX -= 10;
                }



            } else if (list.get(lineNumber-position).getNextLocation()[heightNumber] == heightNumber) {


                animationType = DRAW_ANIMATION_X;
                isLeft = true;

                lineNumber--;
                startX += 10;
                setReachPointX();



            }  else if(heightNumber-2 > 0) {

                if (list.get(lineNumber - position).getNextLocation()[heightNumber - 2] == heightNumber) {


                    heightNumber -= 3;
                    animationType = DRAW_ANIMATION_DIAGONAL;
                    isTop = true;
                    stopY -= 5;
                    lineNumber--;
                    setReachPointX();
                }
            }
            heightNumber++;
            setReachPointY();


        } else {
            stopY += bottomMoveY;
        }

        if (stopY > endPoint) {
            stopY = endPoint+1;
            drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));
            animationType = NONE;
        }

    }

    private void setReachPointX() {
        reachPointX = xDistance+(lineNumber*ladderXDistance);
    }

    private void setReachPointY() {
        reachPointY = yDistance+(heightNumber*ladderYDistance);
    }


    private void animationLocationDiagonal(){

        if(isTop){
            if (stopY < reachPointY) {
                animationType = DRAW_ANIMATION_Y;
                stopX = reachPointX;
                stopY = reachPointY;

                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = reachPointX;
                startY = reachPointY-5;



                heightNumber++;
                setReachPointY();

            } else {
                stopX += topDiagonalMoveX;
                stopY += topDiagonalMoveY;
            }
        }else{
            if (stopY > reachPointY) {
                animationType = DRAW_ANIMATION_Y;

                stopX = reachPointX;
                stopY = reachPointY;

                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = reachPointX;
                startY = reachPointY-20;


                heightNumber++;
                setReachPointY();



            } else {
                stopX += bottomDiagonalMoveX;
                stopY += bottomDiagonalMoveY;
            }
        }


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

    public void setDrawList(ArrayList<DrawAnimationLocation> drawList) {
        this.drawList = drawList;
    }
}
