package com.example.huellas.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huellas.R;
import com.example.huellas.adapaters.RecyclerViewComparisonsAdapter;
import com.example.huellas.models.Comparison;

import java.util.ArrayList;

public class ComparisonFragment extends Fragment {
    private static final String TAG = "ComparisonFragment";
    private ArrayList<Comparison> comparisons = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comparisons,container,false);
        initializeComparison();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_comparisons);
        Context context = getContext();
        RecyclerViewComparisonsAdapter adapter = new RecyclerViewComparisonsAdapter(context, comparisons);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    private void initializeComparison(){
        Comparison comp0 = new Comparison("H447-123AD-AFGV2", "Huella  14", "Huella 22", 40,"Comp 4" );
        Comparison comp1 = new Comparison("32QW-12A2D-WS432", "Huella  12", "Huella 68", 61 , "Comparacion 5" );
        Comparison comp2 = new Comparison("Q445-3432D-12342", "Huella  12", "Huella 31", 45 , "Comparacion 2");
        Comparison comp3 = new Comparison("1266-ASD2D-AS132", "Huella  24", "Huella 4", 62, "Comparacion 3" );
        Comparison comp4 = new Comparison("KLQW-12TER-GFDS2", "Huella  45", "Huella 6", 16, "Comparacion 1" );

        comparisons.add(comp0);
        comparisons.add(comp1);
        comparisons.add(comp2);
        comparisons.add(comp3);
        comparisons.add(comp4);
    }

}
