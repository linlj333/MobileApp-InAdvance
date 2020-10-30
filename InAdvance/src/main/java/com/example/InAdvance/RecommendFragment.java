package com.example.InAdvance;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;



public class RecommendFragment extends Fragment {
    RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Order_RecyclerViewRow> oList;
    private RecommendRecyclerViewAdapter recommendRecyclerViewAdapter;
    public static Context mContext;
//    Context mContext;
    private  static Fragment Instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    static String TAG ="Recommend page";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static RecommendFragment newInstance() {
//        if(RecommendFragment.Instance!=null){
//            return (RecommendFragment) RecommendFragment.Instance;
//        }
//        RecommendFragment fragment = new RecommendFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//       // RecommendFragment.Instance= fragment;
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recommand, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_recommend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE);
//        id = sharedPreferences.getString("orderID","");
//        TextView textView1 = view.findViewById(R.id.textView1);
//        textView1.setText(text + ", welcome!");
        recyclerView.setHasFixedSize(true);
        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        clearAll();
        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        oList = new ArrayList<>();
        Query query = databaseReference.child("RecommendList");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getActivity() == null) {
                    return;
                }
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Order_RecyclerViewRow order_recyclerViewRow = new Order_RecyclerViewRow();
                    order_recyclerViewRow.setImageUrl(snapshot.child("image").getValue().toString());
                    order_recyclerViewRow.setName(snapshot.child("name").getValue().toString());

                    oList.add(order_recyclerViewRow);
                }

                recommendRecyclerViewAdapter = new RecommendRecyclerViewAdapter(mContext,oList);
                recyclerView.setAdapter(recommendRecyclerViewAdapter);
                recommendRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void clearAll(){
        if(oList != null){
            oList.clear();

            if(recommendRecyclerViewAdapter != null){
                recommendRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
        oList = new ArrayList<>();
    }
}