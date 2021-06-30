package com.polared.laddergame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LadderCanvas extends View {
    public static final int START = 1;
    public static final int ANIMATION = 2;
    public static final int LADDER_RESULT = 3;

    public static final int DRAW_ANIMATION_X = 10;
    public static final int DRAW_ANIMATION_Y = 11;
    public static final int DRAW_ANIMATION_DIAGONAL = 12;

    private boolean isLeft;
    private boolean isTop;

    private int type = 0;
    private int animationType = 0;

    private int lineCount;
    private int lineNum = 4;
    private int heightNum;
    private int participantNum;
    private int participantNumber;

    private int drawSpeed;

    private int ladderDistanceX;
    private int ladderDistanceY;

    private int startX;
    private int startY;
    private float stopX;
    private float stopY;

    private float moveX;
    private float moveY;

    private int[] ladder;
    private int[] nextLadder;

    private HashMap<Integer, ArrayList> ladderResult = new HashMap<>();

    private Paint ladderLinePaint = new Paint();
    private Paint animationLinePaint = new Paint();


    private Random random = new Random();

    private ArrayList<LadderLine> list;
    private ArrayList<DrawAnimationLocation> drawList;

    private int[] colors;

    private LadderViewModel ladderViewModel;



    public LadderCanvas(Context context, int lineCount, LadderViewModel ladderViewModel) {
        super(context);
        this.lineCount = lineCount;
        this.ladderViewModel = ladderViewModel;
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
    public void setLadderLine(int lineCount){
        this.lineCount = lineCount;
    }

    public void init(int type, int animationType, int position){
        this.type = type;
        this.animationType = animationType;
        lineNum = position;
        heightNum = 1;

        if (type == ANIMATION) {
            if(ladderResult.get(position) == null){
                drawList = new ArrayList<>();
            } else {
                drawList = ladderResult.get(position);
                this.type = LADDER_RESULT;
            }

            participantNum = position;
            participantNumber = position;

            setLadderDistanceX();

            startX = 135+(lineNum*252);
            startY = 350;
            stopX =  135+(lineNum*252);
            stopY = 350;

            animationLinePaint.setColor(colors[participantNumber]);
            animationLinePaint.setStrokeWidth(20);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < lineCount; i++) {
            canvas.drawLine(135+(i*252), 350, 135+(i*252), 1650, ladderLinePaint);
        }

        switch (type) {
            case START:
                createWidthLine();
                drawWidthLine(canvas);
                break;
            case ANIMATION:
                drawWidthLine(canvas);
                loadDrawData(canvas);
                drawAnimation(canvas);
                break;
            case LADDER_RESULT:
                drawWidthLine(canvas);
                loadDrawData(canvas);
                ladderViewModel.setClickable();
                break;
        }

    }

    private void drawAnimation(Canvas canvas){


        switch (animationType) {
            case DRAW_ANIMATION_X:
                animationLocationX(canvas);
                break;
            case DRAW_ANIMATION_Y:
                animationLocationY(canvas);
                break;
            case DRAW_ANIMATION_DIAGONAL:
                animationLocationDiagonal(canvas);
                break;
        }

    }

    private void loadDrawData(Canvas canvas) {
        for(int i = 0; i < drawList.size(); i++){
            canvas.drawLine(drawList.get(i).getStartX()
                    , drawList.get(i).getStartY()
                    , drawList.get(i).getStopX()
                    , drawList.get(i).getStopY(), animationLinePaint);
        }
    }

    private void animationLocationX(Canvas canvas) {
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
            if( heightNum == 10) {
                return;
            }
            startY = 350 + (130*heightNum);
            stopY = 350 + (130*heightNum);

            if (list.get(lineNum).getNextLocation()[heightNum] != 0) {


                if (list.get(lineNum).getNextLocation()[heightNum] == heightNum+2) {
                    animationType = DRAW_ANIMATION_DIAGONAL;

                    isTop = false;
                    moveX = 9.8f;
                    moveY = 10.2f;
                    heightNum++;
                    lineNum++;
                } else {
                    animationType = DRAW_ANIMATION_X;
                    lineNum++;
                    setLadderDistanceX();
                    moveX = 15f;
                    isLeft = false;
                }



            } else if (list.get(lineNum-position).getNextLocation()[heightNum] == heightNum) {

                moveX = -15f;

                animationType = DRAW_ANIMATION_X;
                isLeft = true;

                lineNum--;
                setLadderDistanceX();



            }  else if(heightNum-2 > 0) {

                if (list.get(lineNum - position).getNextLocation()[heightNum - 2] == heightNum) {

                    moveX = -9.9f;
                    moveY = -10.2f;

                    heightNum -= 3;
                    animationType = DRAW_ANIMATION_DIAGONAL;
                    isTop = true;
                    lineNum--;
                    setLadderDistanceX();
                }
            }
                heightNum++;


        } else {
            stopY += 20;
            canvas.drawLine(startX, startY, stopX, stopY, animationLinePaint);
        }

        if (stopY < 1650 || heightNum == 0) {
            postInvalidateDelayed(drawSpeed);
        } else {
            drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));
            ladderViewModel.ladderResult(lineNum, participantNum);
            ladderViewModel.setClickable();
            ladderResult.put(participantNum, drawList);
        }



    }

    private void setLadderDistanceX() {
        ladderDistanceX = 135+(lineNum*252);
    }


    private void animationLocationDiagonal(Canvas canvas){

            if(isTop){
                if (stopY < 355+(heightNum*130)) {
                    animationType = DRAW_ANIMATION_Y;
                    drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));

                    startX = ladderDistanceX;
                    stopX = ladderDistanceX;
                    startY = 350 + (130*heightNum);
                    stopY = 350 + (130*heightNum);

                    startY -= 5;
                    heightNum++;


                } else {
                    stopX += moveX;
                    stopY += moveY;
                }
            }else{
                if (stopY > 355+(heightNum*130)) {
                    animationType = DRAW_ANIMATION_Y;
                    drawList.add(new DrawAnimationLocation(startX, startY, stopX, stopY));
                    setLadderDistanceX();
                    startX = ladderDistanceX;
                    stopX = ladderDistanceX;
                    startY = 350 + (130*heightNum);
                    stopY = 350 + (130*heightNum);

                    startY -= 20;
                    heightNum++;



                } else {
                    stopX += moveX;
                    stopY += moveY;
                }
            }



        canvas.drawLine(startX, startY, stopX, stopY, animationLinePaint);
        postInvalidateDelayed(drawSpeed);
    }



    private void drawWidthLine(Canvas canvas) {

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

    public void clearDraw(){
        type = 0;
        animationType = 0;
        isLeft = false;
        ladderResult = new HashMap<>();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(3000, height);


    }
}
