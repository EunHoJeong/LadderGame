package com.polared.laddergame.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import androidx.annotation.Nullable;

import com.polared.laddergame.LGColors;
import com.polared.laddergame.LadderViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LadderCanvas extends View {
    public static final int START = 1;
    public static final int ANIMATION = 2;
    public static final int LADDER_RESULT = 3;


    private int type = 0;

    private int lineCount;
    private int lineNum;
    private int participantNumber;

    private int drawSpeed;

    private int[] ladder;
    private int[] nextLadder;

    private HashMap<Integer, ArrayList> ladderResult = new HashMap<>();

    private Paint ladderLinePaint = new Paint();
    private Paint animationLinePaint = new Paint();


    private Random random = new Random();

    private ArrayList<LadderLine> list;
    private ArrayList<DrawAnimationLocation> drawList;
    private ArrayList<DrawLadderAnimation> drawLadderAnimationsList;

    private LadderViewModel ladderViewModel;

    public LadderCanvas(Context context, int lineCount, LadderViewModel ladderViewModel) {
        super(context);
        this.lineCount = lineCount;
        this.ladderViewModel = ladderViewModel;
        ladderLinePaint.setColor(Color.GRAY);
        ladderLinePaint.setStrokeWidth(20);
        animationLinePaint.setStrokeWidth(20);

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

    public void init(int type, int position){
        this.type = type;
        lineNum = position;
        participantNumber = position;

        animationLinePaint.setColor(LGColors.getColor(participantNumber));

        if (type == ANIMATION) {
            if(ladderResult.get(position) == null){
                drawList = new ArrayList<>();
                drawLadderAnimationsList = new ArrayList<>();
                drawLadderAnimationsList.add(new DrawLadderAnimation(lineNum, participantNumber, list, animationLinePaint));


            } else {
                drawList = ladderResult.get(position);
                this.type = LADDER_RESULT;
            }


        }

        invalidate();
    }

    public void allResult(){
        type = ANIMATION;
        drawLadderAnimationsList = new ArrayList<>();
        for(int i = 0; i < lineCount; i++) {
            Paint paint = new Paint();
            paint.setStrokeWidth(20);
            paint.setColor(LGColors.getColor(i));
            drawLadderAnimationsList.add(new DrawLadderAnimation(i, i, list, paint));
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
                drawAnimation(canvas);
                break;
            case LADDER_RESULT:
                drawWidthLine(canvas);
                loadDrawData(canvas);
                ladderViewModel.setClickable();
                break;
        }

    }

    private void drawAnimation(Canvas canvas) {
        for (int i = 0; i < drawLadderAnimationsList.size(); i++) {

            for(int j = 0; j < drawLadderAnimationsList.get(i).getDrawList().size(); j++){
                canvas.drawLine(drawLadderAnimationsList.get(i).getDrawList().get(j).getStartX()
                        , drawLadderAnimationsList.get(i).getDrawList().get(j).getStartY()
                        , drawLadderAnimationsList.get(i).getDrawList().get(j).getStopX()
                        , drawLadderAnimationsList.get(i).getDrawList().get(j).getStopY()
                        , drawLadderAnimationsList.get(i).getAnimationLinePaint());
            }

            drawLadderAnimationsList.get(i).drawAnimation();

            canvas.drawLine(drawLadderAnimationsList.get(i).getStartX()
                    , drawLadderAnimationsList.get(i).getStartY()
                    , drawLadderAnimationsList.get(i).getStopX()
                    , drawLadderAnimationsList.get(i).getStopY()
                    ,drawLadderAnimationsList.get(i).getAnimationLinePaint());
        }


        for (int i = 0; i < drawLadderAnimationsList.size(); i++) {


            if (drawLadderAnimationsList.get(i).getStopY() > 1650
            && !drawLadderAnimationsList.get(i).isEnd()){

                drawLadderAnimationsList.get(i).setEnd(true);
                ladderViewModel.ladderResult(drawLadderAnimationsList.get(i).getLineNumber(), drawLadderAnimationsList.get(i).getParticipantNumber());
                ladderViewModel.setClickable();
                ladderResult.put(drawLadderAnimationsList.get(i).getParticipantNumber(), drawLadderAnimationsList.get(i).getDrawList());
                ladderViewModel.ladderGameEnd(drawLadderAnimationsList.get(i).getParticipantNumber());
                drawLadderAnimationsList.remove(i);
            }
        }

        if(drawLadderAnimationsList.size() != 0) {
            postInvalidateDelayed(drawSpeed);
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
        ladderResult = new HashMap<>();
        invalidate();
    }



}
