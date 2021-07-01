package com.polared.laddergame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private long backButtonTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_frameLayout, MainFragment.getInstance());
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();
        long getTime = currentTimeMillis - backButtonTime;


        if(getTime >= 0 && getTime <= 2000) {
            super.onBackPressed();
        }else{
            backButtonTime = currentTimeMillis;
            Toast.makeText(getApplicationContext(), "뒤로가기를 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}