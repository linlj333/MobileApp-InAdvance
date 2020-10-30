package com.example.InAdvance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    public static Object RecyclerViewClickListener;
    private List<RecyclerViewRow> recyclerViewRowList;
    private RecyclerViewClickListener listener;


    public RecyclerViewAdapter(List<RecyclerViewRow> recyclerViewRowList, RecyclerViewClickListener listener) {
        this.recyclerViewRowList = recyclerViewRowList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RecyclerViewRow recyclerViewRow = recyclerViewRowList.get(position);

        holder.textViewName.setText(recyclerViewRow.getName());
        holder.textViewAddress.setText(recyclerViewRow.getAddress());
        holder.textViewRating.setText(recyclerViewRow.getRating());


    }

    @Override
    public int getItemCount() {
        return recyclerViewRowList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SharedPreferences sharedPreferences;
        public TextView textViewName;
        public TextView textViewAddress;
        public TextView textViewRating;
       // private RecyclerViewClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.tv_name);
            textViewAddress = (TextView) itemView.findViewById(R.id.tv_address);
            textViewRating = (TextView) itemView.findViewById(R.id.tv_rating);
            sharedPreferences = itemView.getContext().getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);
            itemView.setOnClickListener(this);

            listener = new RecyclerViewAdapter.RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                            sharedPreferences.edit().putString("restaurantname",textViewName.getText().toString()).apply();
                            sharedPreferences.edit().putString("restaurantaddress",textViewAddress.getText().toString()).apply();
                            Intent intent = new Intent(view.getContext(), MenuActivity.class);
                            view.getContext().startActivity((intent));
                        }
            };
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View view,int position);
    }
}
