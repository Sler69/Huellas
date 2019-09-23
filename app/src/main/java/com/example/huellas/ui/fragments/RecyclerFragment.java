package com.example.huellas.ui.fragments;

import android.content.Intent;
import android.nfc.cardemulation.CardEmulation;
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

import com.example.huellas.Huella;
import com.example.huellas.R;
import com.example.huellas.ui.views.ShowFingerprintActivity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerFragment extends Fragment {

    public static Fragment newInstance() {
        return new RecyclerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);

        List<Huella> list = new ArrayList<>();
        list.add(new Huella("Huella1", "Esta es la huella1" ,R.drawable.h1));
        list.add(new Huella("Huella2", "Esta es la huella2" ,R.drawable.h2));
        list.add(new Huella("Huella3", "Esta es la huella3" ,R.drawable.h3));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerViewAdapter(list));

        return view;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private CardView mCardView;
        private TextView mTextView;
        private ImageView image;

        public RecyclerViewHolder(View itemView){
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.card_view, container, false));
            mCardView = itemView.findViewById(R.id.card_container);
            mTextView = itemView.findViewById(R.id.text_holder);
            image = itemView.findViewById(R.id.huella_img_id);

        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{

        private List<Huella> mlist;
        public RecyclerViewAdapter(List<Huella> list){
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