package com.example.InAdvance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;


public class RecommendRecyclerViewAdapter extends RecyclerView.Adapter<RecommendRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = "RECOMMEND";
    private ArrayList<Order_RecyclerViewRow> oList;
    private Context mContext;

    public RecommendRecyclerViewAdapter(Context mContext, ArrayList<Order_RecyclerViewRow> oList) {
        this.mContext = mContext;
        this.oList = oList;
    }

    @NonNull
    @Override
    public RecommendRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommend_single_item, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RecommendRecyclerViewAdapter.MyViewHolder holder, int position) {

        Order_RecyclerViewRow order_recyclerViewRow = oList.get(position);
        mContext = holder.itemView.getContext();
        holder.tv_name.setText(order_recyclerViewRow.getName());
        Glide.with(mContext).load(order_recyclerViewRow.getImageUrl())
                .into(holder.iv_url);
    }

    @Override
    public int getItemCount() {
        return oList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public ImageView iv_url;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_recommend_title);
            iv_url = itemView.findViewById(R.id.iv_recommend);
        };
    }
}
