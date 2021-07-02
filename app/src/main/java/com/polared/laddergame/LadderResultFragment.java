package com.polared.laddergame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polared.laddergame.main.MainFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LadderResultFragment extends Fragment {
    private Button btnPreviousScreen, btnReStart;
    private RecyclerView resultRecyclerView;
    private ArrayList<LadderResultData> resultList;
    private LadderResultAdapter ladderResultAdapter;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_ladder_result, container, false);

        btnPreviousScreen = view.findViewById(R.id.btnPreviousScreen);
        btnReStart = view.findViewById(R.id.btnReStart);
        resultRecyclerView = view.findViewById(R.id.resultRecyclerView);

        initButton();

        String jsonArrayResultDataList = getArguments().getString("jsonArrayResultDataList");

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<LadderResultData>>(){}.getType();

        resultList = gson.fromJson(jsonArrayResultDataList, listType);

        ladderResultAdapter = new LadderResultAdapter(resultList);



        resultRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        resultRecyclerView.setAdapter(ladderResultAdapter);






        return view;
    }

    private void initButton() {

        btnPreviousScreen.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("reload", "OK");
            MainFragment mainFragment = (MainFragment) MainFragment.getInstance();
            mainFragment.setArguments(bundle);
            replaceFragment(mainFragment);
        });

        btnReStart.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("clear", "OK");
            MainFragment mainFragment = (MainFragment) MainFragment.getInstance();
            mainFragment.setArguments(bundle);
            replaceFragment(mainFragment);
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.main_frameLayout, fragment);
        ft.commit();
    }
}
