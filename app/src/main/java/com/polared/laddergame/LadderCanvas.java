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
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class LadderCanvas extends View {
    private boolean isStart;
    private boolean isDrawingX;
    private boolean isLeft;
    private boolean isTop;
    private boolean isDrawingDiagonal;
    boolean isAnimation;


    private int lineCount;
    private int lineNum;
    private int heightNum;
    private int participantNum;
    private int drawSpeed;

    private int startX;
    private int startY;
    private float stopX;
    private float stopY;

    private float moveX;
    private float moveY;

    private int[] ladder;
    private int[] nextLadder;
    private int[] ladderResult;

    private Paint ladderLinePaint = new Paint();
    private Paint animationLinePaint = new Paint();


    private Random random;

    private ArrayList<LadderLine> list;
    private ArrayList<DrawAnimationLocation> drawList;

    private CallbackLadderResult callbackLadderResult;

    private int[] colors;


    public LadderCanvas(Context context, int lineCount, CallbackLadderResult callbackLadderResult) {
        super(context);
        this.lineCount = lineCount;
        this.callbackLadderResult = callbackLadderResult;
        ladderLinePaint.setColor(Color.GRAY);
        ladderLinePaint.setStrokeWidth(20);
        colors = new int[]{ContextCompat.getColor(context, R.color.my_pink), ContextCompat.getColor(context, R.color.my_green), ContextCompat.getColor(context, R.color.my_orange)
                , ContextCompat.getColor(context, R.color.my_indigo), ContextCompat.getColor(context, R.color.my_yellow), ContextCompat.getColor(context, R.color.my_turquoise)
                , ContextCompat.getColor(context, R.color.my_purple), ContextCompat.getColor(context, R.color.my_sky), ContextCompat.getColor(context, R.color.my_brown)
                , ContextCompat.getColor(context, R.color.my_gray), ContextCompat.getColor(context, R.color.my_red), ContextCompat.getColor(context, R.color.my_beige) };

        drawSpeed = 5;
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

    public void ladderStart() {
        isStart = true;
        invalidate();
    }

    public void goAnimation(int position){
        drawList = new ArrayList<>();
        lineNum = position;
        participantNum = position;
        heightNum = 1;
        startX = 135+(lineNum*252);
        startY = 350;
        stopX =  135+(lineNum*252);
        stopY = 350;

        animationLinePaint.setColor(colors[participantNum]);
        animationLinePaint.setStrokeWidth(20);

        isAnimation = true;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);





        for (int i = 0; i < lineCount; i++) {
            canvas.drawLine(135+(i*252), 350, 135+(i*252), 1650, ladderLinePaint);
        }

        if (isStart) {
            random = new Random();
            createWidthLine();

            drawWidthLine(canvas);

            isStart = false;

        }


        if(isAnimation){
            drawWidthLine(canvas);

            for(int i = 0; i < drawList.size(); i++){
                canvas.drawLine(drawList.get(i).getStartX()
                        , drawList.get(i).getStartY()
                        , drawList.get(i).getStopX()
                        , drawList.get(i).getStopY(), animationLinePaint);
            }

            if (isDrawingX) {
                animationLocationX(canvas);
            } else if (isDrawingDiagonal){
                animationLocationDiagonal(canvas);
            } else {
                animationLocationY(canvas);
            }


        }



    }

    private void animationLocationX(Canvas canvas) {
        if(isLeft){
            if (stopX < 135+(252*(lineNum))) {
                isDrawingX = false;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = 135+(lineNum*252);
                stopX = 135+(lineNum*252);
                startY -= 10;

            } else {
                stopX += moveX;
            }
        }else{
            if (stopX > 135+(252*(lineNum))) {
                isDrawingX = false;
                drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                startX = 135+(lineNum*252);
                stopX = 135+(lineNum*252);
                startY -= 10;

            } else {
                stopX += moveX;
            }
        }



        canvas.drawLine(startX, startY, stopX, stopY, animationLinePaint);

        postInvalidateDelayed(drawSpeed);
    }


    private void animationLocationY(Canvas canvas){
        int position = 1;

        if (lineNum == 0) {
            position = 0;
        }

        if (stopY > 355+(heightNum*130)) {
            drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

            if (list.get(lineNum).getNextLocation()[heightNum] != 0) {

                startY = 350 + (130*heightNum);
                stopY = 350 + (130*heightNum);



                if(list.get(lineNum).getNextLocation()[heightNum] == heightNum+2){
                    isDrawingDiagonal = true;
                    isTop = false;
                    moveX = 9.7f;
                    moveY = 10.2f;
                    heightNum++;

                }else{
                    isDrawingX = true;
                    moveX = 15f;
                    isLeft = false;
                }

                lineNum++;

            } else if (list.get(lineNum-position).getNextLocation()[heightNum] == heightNum) {

                startY = 350 + (130*heightNum);
                stopY = 350 + (130*heightNum);

                moveX = -15f;


                isDrawingX = true;
                isLeft = true;

                lineNum--;

            }  else if(heightNum-2 > 0) {

                if (list.get(lineNum - position).getNextLocation()[heightNum - 2] == heightNum) {
                    startY = 350 + (130*heightNum);
                    stopY = 350 + (130*heightNum);

                    heightNum -= 3;
                    isDrawingDiagonal = true;
                    isTop = true;
                    moveX = -9.7f;
                    moveY = -10.2f;
                    lineNum--;
                }

            }
                heightNum++;


        } else {
            stopY += 10;
            canvas.drawLine(startX, startY, stopX, stopY, animationLinePaint);
        }

        if (stopY < 1650) {
            postInvalidateDelayed(drawSpeed);
        } else {
            callbackLadderResult.relayLadderResult(ladderResult);
        }



    }

    private void animationLocationDiagonal(Canvas canvas){

            if (isTop) {
                if (stopY < 350+(heightNum*130)) {
                    isDrawingDiagonal = false;
                    drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                    startX = 135+(lineNum*252);
                    stopX = 135+(lineNum*252);
                    startY = 350 + (130*heightNum);
                    stopY = 350 + (130*heightNum);

                    startY -= 10;
                    heightNum++;

                } else {
                    stopX += moveX;
                    stopY += moveY;
                }
            }else{
                if (stopY > 355+(heightNum*130)) {
                    isDrawingDiagonal = false;
                    drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                    startX = 135+(lineNum*252);
                    stopX = 135+(lineNum*252);
                    startY = 350 + (130*heightNum);
                    stopY = 350 + (130*heightNum);

                    heightNum++;



                } else {
                    stopX += moveX;
                    stopY += moveY;
                }
            }



        canvas.drawLine(startX, startY, stopX, stopY, animationLinePaint);
        postInvalidateDelayed(drawSpeed);
    }

    /**
     * 선 그리는곳
     */
    //가로 선 그리는곳
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

                canvas.drawLine(startX, startY, stopX, stopY, ladderLinePaint);

            }
        }
    }

    //가로 선 좌표 구하는곳
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

    //선이 0~1개면 추가로 3개 그려줌
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

    //사선 그리기
    private int obliqueLine(int ladderLine) {
        if(ladderLine > 5 || list.size() == 0){
            return ladderLine;
        }

        return ladderLine+2;
    }

    //내 왼쪽에 이미 가로선이 그려져있는지 확인
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

    public void clearDraw(){
        isStart = false;
        isAnimation = false;
        isDrawingX = false;
        isLeft = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);


        setMeasuredDimension(3000, height);


    }


    public int[] getLadderResult() {

        return ladderResult;
    }
}
