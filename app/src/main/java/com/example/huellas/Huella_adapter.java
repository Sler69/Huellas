package com.example.huellas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Huella_adapter extends RecyclerView.Adapter<Huella_adapter.MyViewHolder> {

    private Context mContext;
    private List<Huella> mData;

    public Huella_adapter(Context mContext, List<Huella> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.huella.setText(mData.get(position).getHuella());
        holder.img_huella_thumbnail.setImageResource(mData.get(position).getThumbnail());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView huella;
        ImageView img_huella_thumbnail;

        public MyViewHolder(View itemView){
            super(itemView);

            huella = (TextView) itemView.findViewById(R.id.text_holder);
            img_huella_thumbnail = (ImageView) itemView.findViewById(R.id.huella_img_id);
        }
    }

}
