package com.polared.laddergame.result;

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
import com.polared.laddergame.R;
import com.polared.laddergame.main.MainFragment;
import com.polared.laddergame.result.LadderResultAdapter;
import com.polared.laddergame.result.LadderResultData;

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

        if (resultList.size()%3 == 1) {
            resultList.add(new LadderResultData(-1, "", ""));
        }

        ladderResultAdapter = new LadderResultAdapter(resultList);

        resultRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        resultRecyclerView.setAdapter(ladderResultAdapter);

        return view;
    }

    private void initButton() {

        btnPreviousScreen.setOnClickListener(v -> {
            replaceFragment("reload");
        });

        btnReStart.setOnClickListener(v -> {
            replaceFragment("restart");
        });
    }

    private void replaceFragment(String data){

        Bundle bundle = new Bundle();
        bundle.putString(data, data);
        MainFragment mainFragment = (MainFragment) getParentFragmentManager().findFragmentByTag("main");

        if (mainFragment != null) {
            mainFragment.setArguments(bundle);

            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.main_frameLayout, mainFragment);
            ft.commit();
        }


    }
}
