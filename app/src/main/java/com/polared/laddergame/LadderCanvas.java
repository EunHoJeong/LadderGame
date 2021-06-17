package com.polared.laddergame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class LadderCanvas extends View {
    private boolean isStart;
    private boolean isGo;

    private int lineCount;
    private int lineNum;
    private int heightNum;

    private int startX;
    private int startY;
    private int stopX;
    private int stopY;
    private int[] ladder;
    private int[] nextLadder;
    private int[] ladderResult;

    private Paint paint;
    private Path path = new Path();

    private Random random;

    private ArrayList<LadderLine> list;
    private ArrayList<DrawAnimationLocation> drawList;

    private CallbackLadderResult callbackLadderResult;

    boolean isAnimation;







    public LadderCanvas(Context context, int lineCount, CallbackLadderResult callbackLadderResult) {
        super(context);
        this.lineCount = lineCount;
        this.callbackLadderResult = callbackLadderResult;
        drawList = new ArrayList<>();
        Log.d("Test", "LadderCanvas"+this.lineCount);
    }

    public LadderCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LadderCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int position){
        lineCount = position;
        invalidate();
    }

    public void goAnimation(int position){
        lineNum = position;
        heightNum = 1;
        startX = 135+((lineNum-1)*252);
        startY = 350;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Test", "onDraw");

        paint = new Paint();




        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(20);

        canvas.drawLine(135, 350, 135, 1650, paint);

        for (int i = 1; i < lineCount; i++) {
            canvas.drawLine(135+(i*252), 350, 135+(i*252), 1650, paint);
        }

        if (isStart) {
            random = new Random();
            createWidthLine();

            drawWidthLine(canvas);

            setLadderResult();

            callbackLadderResult.relayLadderResult(ladderResult);
            isStart = false;

        }


        if(isAnimation){
            drawWidthLine(canvas);

            paint.setColor(Color.RED);

            animationLocation(canvas);

        }



    }

    private void animationLocation(Canvas canvas){
        for(int i = 0; i < drawList.size(); i++){
            canvas.drawLine(drawList.get(i).getStartX()
                    , drawList.get(i).getStartY()
                    , drawList.get(i).getStopX()
                    , drawList.get(i).getStopY(), paint);
        }

        if (list.get(lineNum).getNextLocation()[heightNum] != 0 && stopY > 480) {
            heightNum = list.get(lineNum).getNextLocation()[heightNum];
        }
    }



    private void setLadderResult() {
        for (int i = 0; i < lineCount; i++) {
            int line = i;

            for(int j = 0; j < 10; j++){
                if(i == 0) {
                    Log.d("Test", "j = " + j);
                }
                int position = 1;
                if(line == 0){
                    position = 0;
                }
                int before = list.get(line-position).getNextLocation()[j];
                int after = list.get(line).getNextLocation()[j];

                if(i == 0) {
                    Log.d("Test", "after = " + after + " before = " + before);
                }

                if (after != 0) {
                    line++;
                    j = after;
                } else if (before != 0) {
                    line--;
                    j = getLine(line, before);
                }
                if(i == 0) {
                    Log.d("Test", "line = " + line);
                }
            }//end of for

            ladderResult[line] = i+1;
        }
    }

    private void drawWidthLine(Canvas canvas) {
        ladderResult = new int[lineCount];

        for (int i = 0; i < lineCount -1; i++) {
            for(int j = 0; j < 10; j++){
                int heightLine = list.get(i).location[j];
                if(heightLine == 0){
                    continue;
                }
                int nextLine = list.get(i).nextLocation[j];

                int startX = 135+(i*252);
                int startY = 350+(j*130);
                int stopX = 135+((i+1)*252);
                int stopY = 350+(nextLine*130);

                canvas.drawLine(startX, startY, stopX, stopY, paint);

            }
        }
    }

    private int getLine(int position, int before) {
        int[] location = list.get(position).getNextLocation();
        for(int i = 0; i < 10; i++){
            if(location[i] == before){
                return i;
            }
        }
        return before;
    }

    private void createWidthLine() {
        list = new ArrayList<>();
        ladder = new int[10];
        nextLadder = new int[10];

        LadderLine ladderLine;

        for(int i = 0; i < lineCount -1; i++){
            int ladderSize = 0;
            int position = 0;
            int count = 0;

            if (i > 0) {
                position = 1;
            }

            for(int j = 1; j < 9; j++){

                boolean isCreate = random.nextBoolean();
                if(isCreate || ladderSize >= j){

                    continue;
                }
                count++;

                if(count > 4){
                    break;
                }

                if(hasLine((i-position), j)){
                    continue;
                }

                if(random.nextInt(5) < 1){
                    ladder[j] = 1;
                    ladderSize = obliqueLine(j);
                    nextLadder[j] = ladderSize;

                }else{
                    ladder[j] = 1;
                    nextLadder[j] = j;
                }


            }


            checkLineNum(i-position, ladderSize);

            ladderLine = new LadderLine(ladder, nextLadder);
            list.add(ladderLine);
            ladder = new int[10];
            nextLadder = new int[10];

        }

        list.add(new LadderLine(new int[10], new int[10]));

    }

    private void checkLineNum(int position, int ladderSize){
        if(list.size() == 0){
            return;
        }

        int count = 0;

        for(int i = 0; i < 10; i++){
            if(ladder[i] == 1){
                count++;
            }
        }
        if(count < 2 ){
            for(int i = 0; i < 3; i++){
                int randomNum = random.nextInt(8)+1;
                if (ladderSize >= randomNum && ladderSize-2 <= randomNum
                || hasLine(position,randomNum)) {
                    i--;
                    continue;
                }
                ladder[randomNum] = 1;
                nextLadder[randomNum] = randomNum;
            }
        }
    }

    private int obliqueLine(int ladderLine) {
        if(ladderLine > 5 || list.size() == 0){
            return ladderLine;
        }

        return ladderLine+2;
    }

    private boolean hasLine(int position, int ladderLine) {
        if (list.size() == 0) {
            return false;
        }

        boolean hasLine = false;

        for (int i = 0; i < 10; i++) {
            if (list.get(position).getNextLocation()[i] == ladderLine) {
                hasLine = true;

                break;
            }
        }

        return hasLine;
    }

    public void setIsStart(boolean isStart){
        Log.d("Test", "setIsStart = " + isStart);
        this.isStart = isStart;
    }

    public void setIsAnimation(boolean animation){
        isAnimation = animation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = lineCount * 252;
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        Log.d("Test", "onMeasure = " + width);

        setMeasuredDimension(3000, height);


    }


    public int[] getLadderResult() {

        return ladderResult;
    }
}
