package com.polared.laddergame;

import android.content.Context;

import androidx.core.content.ContextCompat;

public class LGColors {
    private static int[] colors;

    public void setColors(Context context){
        colors = new int[]{ContextCompat.getColor(context, R.color.my_pink), ContextCompat.getColor(context, R.color.my_green), ContextCompat.getColor(context, R.color.my_orange)
                , ContextCompat.getColor(context, R.color.my_indigo), ContextCompat.getColor(context, R.color.my_yellow), ContextCompat.getColor(context, R.color.my_turquoise)
                , ContextCompat.getColor(context, R.color.my_purple), ContextCompat.getColor(context, R.color.my_sky), ContextCompat.getColor(context, R.color.my_brown)
                , ContextCompat.getColor(context, R.color.my_dark_purple), ContextCompat.getColor(context, R.color.my_red), ContextCompat.getColor(context, R.color.my_beige) };
    }

    public static int getColor(int number){
        return colors[number];
    }
}
