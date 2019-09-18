package com.example.huellas.adapaters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huellas.R;

import java.util.ArrayList;

public class RecyclerViewComparisonsAdapter extends RecyclerView.Adapter<RecyclerViewComparisonsAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewComparisons";

    private ArrayList<String> arrayComparisonNames = new ArrayList<String>();
    private ArrayList<String> arrayComparisonTitles = new ArrayList<String>();
    private Context comparisonContext;

    public RecyclerViewComparisonsAdapter (Context context, ArrayList<String> titles, ArrayList<String> names){
        arrayComparisonNames = names;
        arrayComparisonTitles = titles;
        comparisonContext = context;
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
        Log.d(TAG, "onBindViewHolder: Rendering Item");
        holder.comparisonTitle.setText(arrayComparisonTitles.get(position));
        holder.comparisonName.setText(arrayComparisonNames.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayComparisonNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView comparisonTitle;
        TextView comparisonName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            comparisonTitle = itemView.findViewById(R.id.comparison_title);
            comparisonName = itemView.findViewById(R.id.comparison_name);
            parentLayout = itemView.findViewById(R.id.comparison_item_layout);

        }
    }



}
