package com.polared.laddergame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
    public BetData betData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        setContentView(R.layout.activity_base);

        String[] foods = new String[]{ getString(R.string.food_black_soybean_sauce_noodle), getString(R.string.food_stew), getString(R.string.food_pork_cutlet)
                , getString(R.string.food_jjambbong), getString(R.string.food_noodle), getString(R.string.food_pasta), getString(R.string.food_hamburger)
                , getString(R.string.food_korean_cuisine), getString(R.string.food_raw_fish), getString(R.string.food_pizza), getString(R.string.food_stir_fried_rice_cake)
                , getString(R.string.food_meat), getString(R.string.food_ramen), getString(R.string.food_rice_with_soy_sauce_egg), getString(R.string.food_kimchi_fried_rice)
                , getString(R.string.food_starving)};

        String exclamationFail = getString(R.string.fail_exclamation_mark);
        String fail = getString(R.string.fail);
        String exclamationPass = getString(R.string.prize_exclamation_mark);
        String pass = getString(R.string.pass);

        betData = new BetData(foods, exclamationFail, fail, exclamationPass, pass);

    }
}