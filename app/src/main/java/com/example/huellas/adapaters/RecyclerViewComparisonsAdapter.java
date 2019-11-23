package com.example.huellas.adapaters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huellas.R;
import com.example.huellas.models.Comparison;


import java.util.ArrayList;

public class RecyclerViewComparisonsAdapter extends RecyclerView.Adapter<RecyclerViewComparisonsAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewComparisons";



    private Context comparisonContext;
    private ArrayList<Comparison> arrayComparisons;

    public RecyclerViewComparisonsAdapter (Context context, ArrayList<Comparison> comparisons){
        comparisonContext = context;
        arrayComparisons  = comparisons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comparison_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comparison current = arrayComparisons.get(position);
        Log.d(TAG, "onBindViewHolder: Rendering Item");
        holder.comparisonTitle.setText(current.getTitle());
        holder.comparisonId.setText(current.getId());
        holder.comparison1.setText(current.getFirstFingerPrint());
        holder.comparison2.setText(current.getSecondFingerPrint());
        holder.percentage.setText(current.getCoincidencePercentage().toString() + "%");
    }

    @Override
    public int getItemCount() {
        return arrayComparisons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView comparisonTitle;
        TextView comparisonId;
        TextView comparison1;
        TextView comparison2;
        TextView percentage;
        CardView parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            comparisonTitle = itemView.findViewById(R.id.comparison_title);
            comparisonId = itemView.findViewById(R.id.comparison_id);
            parentLayout = itemView.findViewById(R.id.comparison_item_layout);
            comparison1 = itemView.findViewById(R.id.comparison1_name);
            comparison2 = itemView.findViewById(R.id.comparison2_name);
            percentage = itemView.findViewById(R.id.percentage_comparison);
        }
    }



}
