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

import java.util.ArrayList;

public class ComparisonFragment extends Fragment {
    private static final String TAG = "ComparisonFragment";

    private ArrayList<String> comparisonNames = new ArrayList<String >();
    private ArrayList<String> comparisonTitles = new ArrayList<String>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comparisons,container,false);
        initializeComparison();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_comparisons);
        Context context = getContext();
        RecyclerViewComparisonsAdapter adapter = new RecyclerViewComparisonsAdapter(context,comparisonTitles,comparisonNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    private void initializeComparison(){
        comparisonNames.add("Huella Medio Izquierdo");
        comparisonTitles.add("Huella 1");

        comparisonNames.add("Huella Meñique  Izquierdo");
        comparisonTitles.add("Huella 2");

        comparisonNames.add("Huella Anular Derecho");
        comparisonTitles.add("Huella 3");

        comparisonNames.add("Huella Pulgar Izquierdo");
        comparisonTitles.add("Huella 4");

        comparisonNames.add("Huella Pulgar Derecho");
        comparisonTitles.add("Huella 5");
    }

    private void initializeRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_comparisons);
        Context context = getContext();
        RecyclerViewComparisonsAdapter adapter = new RecyclerViewComparisonsAdapter(context,comparisonTitles,comparisonNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}
