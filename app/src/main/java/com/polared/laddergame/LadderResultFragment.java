package com.polared.laddergame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LadderResultFragment extends Fragment {
    private Button btnPreviousScreen, btnReStart;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_ladder_result, container, false);

        btnPreviousScreen = view.findViewById(R.id.btnPreviousScreen);
        btnReStart = view.findViewById(R.id.btnReStart);

        btnPreviousScreen.setOnClickListener(v -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.main_frameLayout, MainFragment.getInstance());

            ft.commit();
        });


        return view;
    }
}
