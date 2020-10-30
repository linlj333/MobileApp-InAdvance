package com.example.InAdvance;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;

import java.util.LinkedHashMap;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    LinkedHashMap<String, String> HashMap = new LinkedHashMap<String, String>();
    String data1[], data2[], number;
    float total=0;
    int image[];
    Context context;


    public CartAdapter(Context ct,String s1[], String s2[], int img[], String num){
        context = ct;
        data1 = s1;
        data2 = s2;
        image = img;
        number = num;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_cart_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.myText1.setText(data1[position]);
        holder.myText2.setText(data2[position]);
        holder.myImage.setImageResource(image[position]);
        holder.price=Float.parseFloat(data2[position]);

    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        SharedPreferences sharedPreferences;
        TextView myText1, myText2;
        ImageView myImage;
        ElegantNumberButton button;
        float price=0;
        float oldnum = 0;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.text_food_name);
            myText2 = itemView.findViewById(R.id.text_food_price);
            myImage = itemView.findViewById(R.id.image_cart);
            sharedPreferences = itemView.getContext().getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);

            button = (ElegantNumberButton) itemView.findViewById(R.id.number_button);
            button.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String num = button.getNumber();
                    float num1 =Integer.parseInt(num);
                    total-=oldnum*price;
                    total+=num1*price;
                    oldnum=num1;
                    TextView price_total = (TextView) itemView.getRootView().findViewById(R.id.price_total);
                    price_total.setText(String.valueOf(total));
                    HashMap.put(myText1.getText().toString(),num);
                    Gson gson = new Gson();
                    String hashMapString = gson.toJson(HashMap);
//                    sharedPreferences = itemView.getContext().getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("hashString", hashMapString).apply();
                }
            });
        }
    }
}