package com.example.huellas.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huellas.R;
import com.example.huellas.models.Huella;
import com.example.huellas.ui.views.ScanActivity;
import com.example.huellas.ui.views.ShowFingerprintActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FingerprintsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        List<Huella> list = new ArrayList<>();
        list.add(new Huella("Huella1", "Esta es la huella1" ,R.drawable.h1));
        list.add(new Huella("Huella2", "Esta es la huella2" ,R.drawable.h2));
        list.add(new Huella("Huella3", "Esta es la huella3" ,R.drawable.h3));
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        FloatingActionButton fb = view.findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getContext(), ScanActivity.class));
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerViewAdapter(list));

        return view;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private CardView mCardView;
        private TextView mTextView;
        private ImageView image;

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.card_view, container, false));
            mCardView = itemView.findViewById(R.id.card_container);
            mTextView = itemView.findViewById(R.id.text_holder);
            image = itemView.findViewById(R.id.huella_img_id);

        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Huella> mlist;

        public RecyclerViewAdapter(List<Huella> list) {
            this.mlist = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
            holder.mTextView.setText(mlist.get(position).getHuella());
            holder.image.setImageResource(mlist.get(position).getThumbnail());

            //listeners

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShowFingerprintActivity.class);
                    intent.putExtra("Fingerprint", mlist.get(position).getHuella());
                    intent.putExtra("Description", mlist.get(position).getDescripcion());
                    intent.putExtra("Thumbnail", mlist.get(position).getThumbnail());
                    getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mlist.size();
        }
    }
}

