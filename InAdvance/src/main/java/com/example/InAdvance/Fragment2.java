package com.example.InAdvance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment implements RecyclerViewAdapter.RecyclerViewClickListener{
    //List<RecyclerViewRow> recyclerViewRowList ;
    List<RecyclerViewRow> recyclerViewRowList;
    SharedPreferences sharedPreferences;
    private Fragment2Listener listener;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
//    private RecyclerViewAdapter.RecyclerViewClickListener listener1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private  static Fragment Instance;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    private Fragment2() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance() {
        if(Fragment2.Instance!=null){
            return (Fragment2) Fragment2.Instance;
        }
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Fragment2.Instance= fragment;

        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        view=v;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
   }

    public void updateRank(List<HashMap<String, String>> hashMaps){
        List<RecyclerViewRow> recyclerViewRowList = new ArrayList<>();

        sharedPreferences = getActivity().getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);

        Collections.sort(hashMaps, new Comparator<HashMap<String,String>>() {
            @Override
            public int compare(final HashMap<String,String> lhs, HashMap<String,String> rhs) {
                double ratingOne = Double.parseDouble(lhs.get("rating"));
                double ratingTwo = Double.parseDouble(rhs.get("rating"));

                if (ratingOne == ratingTwo)
                    return 0;
                if (ratingOne > ratingTwo)
                    return -1;
                return 1;
            }
        });

        for (int i = 0; i < hashMaps.size(); i++) {

            HashMap<String, String> hashMapList = hashMaps.get(i);
            String name = hashMapList.get("name");
            String address = hashMapList.get("vicinity");
            if("".equals(address)){
                address="N/A";
            }
            String rating = "Rating: "+hashMapList.get("rating")+"/5";
            recyclerViewRowList.add(new RecyclerViewRow(name,address,rating));
        }

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(recyclerViewRowList,
                (RecyclerViewAdapter.RecyclerViewClickListener) RecyclerViewAdapter.RecyclerViewClickListener);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        return view;
    }

    @Override
    public void onClick(View view, int position) {
        recyclerViewRowList.get(position);
        Intent intent = new Intent(getActivity(), MenuActivity.class);
        startActivity(intent);
    }

    public interface Fragment2Listener{
        void onInputFragment2Sent(List<HashMap<String, String>> hashMaps);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Fragment2Listener) {
            listener = (Fragment2Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Fragment2Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}