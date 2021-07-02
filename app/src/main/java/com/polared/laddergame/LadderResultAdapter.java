package com.polared.laddergame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LadderResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<LadderResultData> resultList;

    public LadderResultAdapter(ArrayList<LadderResultData> resultList) {
        this.resultList = resultList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_result_ladder, parent, false);

        return new LadderResult(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LadderResult) {
            LadderResult result = (LadderResult) holder;

            int number = resultList.get(holder.getAdapterPosition()).getNumber();

            String participantName = resultList.get(holder.getAdapterPosition()).getParticipantName();
            String betName = resultList.get(holder.getAdapterPosition()).getBetName();

            result.result_participant_number.setText(String.valueOf(number+1));
            result.result_participant_number.setBackgroundColor(LGColors.getColor(number));

            result.result_participant_name.setText(participantName);

            result.result_bet_name.setText(betName);
            result.result_bet_name.setTextColor(LGColors.getColor(number));
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class LadderResult extends RecyclerView.ViewHolder{
        private TextView result_participant_number;
        private TextView result_participant_name;
        private TextView result_bet_name;

        public LadderResult(View itemView) {
            super(itemView);
            result_participant_number = itemView.findViewById(R.id.result_participant_number);
            result_participant_name = itemView.findViewById(R.id.result_participant_name);
            result_bet_name = itemView.findViewById(R.id.result_bet_name);
        }
    }
}
