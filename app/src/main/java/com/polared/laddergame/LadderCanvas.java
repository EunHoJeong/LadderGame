package com.polared.laddergame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class LadderCanvas extends View {
    private boolean isStart;
    private boolean isGo;

    private int lineNum;

    private int[] ladder;
    private int[] nextLadder;

    private Random random;

    private ArrayList<LadderLine> list;




    public LadderCanvas(Context context, int lineNum) {
        super(context);
        this.lineNum = lineNum;
        Log.d("Test", "LadderCanvas"+this.lineNum);
    }

    public LadderCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LadderCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int position){
        invalidate();
        lineNum = position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Test", "onDraw");





        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(20);

        canvas.drawLine(135, 350, 135, 1650, paint);

        for(int i = 1; i < lineNum; i++){

            canvas.drawLine(135+(i*252), 350, 135+(i*252), 1650, paint);
        }

        if (isStart){
            random = new Random();
            createWidthLine();


                    for(int i = 0; i < lineNum-1; i++){
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



    }



    private void createWidthLine() {
        list = new ArrayList<>();
        ladder = new int[10];
        nextLadder = new int[10];

        LadderLine ladderLine;


        for(int i = 0; i < lineNum-1; i++){
            int ladderSize = 0;
            int position = 0;
            int count = 0;
            if(i > 0){
                position = 1;
            }

            for(int j = 1; j < 9; j++){

                boolean isCreate = random.nextBoolean();
                if(isCreate && ladderSize >= j){

                    continue;
                }
                count++;

                if(count > 4){
                    break;
                }

                if(hasLine((i-position), j)){
                    continue;
                }

                if(random.nextInt(5) < 2){
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
        if(count < 1 ){
            for(int i = 0; i < 3; i++){
                int randomNum = random.nextInt(8)+1;
                if(hasLine(position,randomNum)){
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
        int line = ladderLine;
        int randomNum = 0;
        int i = 0;
        while(i != 10){
            i++;

            randomNum = random.nextInt(3)+2;


            line += randomNum;


            if(line > 8){
                line = ladderLine;
                continue;
            }


            break;
        }


        return line;
    }

    private boolean hasLine(int position, int ladderLine) {
        if(list.size() == 0){
            return false;
        }
        boolean hasLine = false;
        for(int i = 0; i < 10; i++){
            if(list.get(position).getNextLocation()[i] == ladderLine){
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = lineNum * 252;
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        Log.d("Test", "onMeasure = " + width);

        setMeasuredDimension(3000, height);


    }



}
