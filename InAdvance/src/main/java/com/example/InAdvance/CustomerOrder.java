package com.example.InAdvance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;


public class CustomerOrder extends AppCompatActivity {

    String passOrderId;
    TextView tv_displayList, tv_displayOrderId;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    String orderID;
    String TAG ="ABC";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        if (NavUtils.getParentActivityName(CustomerOrder.this) != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        tv_displayOrderId = findViewById(R.id.tv_display_orderId);
        tv_displayList = findViewById(R.id.tv_display_list);

        Intent intent = getIntent();
        passOrderId = intent.getStringExtra("String");
        tv_displayOrderId.setText(" Order ID: " + passOrderId);
        orderID = passOrderId;

        FirebaseFirestore.getInstance().collection("orders").document(orderID).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String cusOrderList = documentSnapshot.getString("orderList");
                    String orderPrice = documentSnapshot.getString("priceTotal");
                    String orderTime = documentSnapshot.getString("orderTime");
                    String resAddress = documentSnapshot.getString("restaurantAddress");
                    String resName = documentSnapshot.getString("restaurantName");
                    String userAccount = documentSnapshot.getString("userAccount");


                    tv_displayList.append(" Order Time: "+ orderTime + "\n" + " User Account Name: " +userAccount + "\n"+" Restaurant Detail: " + resName + ",  " + resAddress + "\n" +" Order List: \n "+ cusOrderList +"\n Order Price: " + orderPrice);
                }
            }
        });

//        FirebaseFirestore dr = FirebaseFirestore.getInstance().collection("orders").document(orderID).get();
//        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                          @Override
//                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                                          }
//                                      });

//        loadData();
      //  DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        CollectionReference citiesRef = firestore.collection("orders");
//
//// Create a query against the collection.
//        String tempOrderID = firestore.collection("orders").document().getId();
//        Query query = citiesRef.whereEqualTo(tempOrderID, orderID);
//        tv_displayList.setText(String.valueOf(query));


//        DatabaseReference animalsRef = rootRef.child("orders");
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String search = "orderList";
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String orderList = ds.child(orderID).getValue(String.class);
//                    Toast.makeText(getApplicationContext(),"orderList" +orderList, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        animalsRef.addListenerForSingleValueEvent(eventListener);



//        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("orders").child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mDatabase.child("orders").child(orderID).child("username").getValue("name");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//            });


//        Query query = mFirebaseDatabaseReference.child("userTasks").orderByChild("title").equalTo("#Yahoo");
//        query.addValueEventListener(valueEventListener);
//        DocumentReference dr = FirebaseFirestore.getInstance().collection("orders").document(orderID);
//        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
////                if(documentSnapshot.getString("orderList") != null){
//                    tv_displayList.setText(documentSnapshot.getString("orderList"));
////                }else if(documentSnapshot.getString("isBusinessOwner") != null){
////                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
////                }
//            }
//        });
    //}
//    private void loadData(){
//        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//
//        Query query = dbRef.child("orders").equalTo(orderID);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // dataSnapshot is the "issue" node with all children with id 0
//                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
//                        // do something with the individual "issues"
//                        String orderList = issue.child(orderID).getValue(String.class);
//                    Toast.makeText(getApplicationContext(),"orderList" +orderList, Toast.LENGTH_SHORT).show();
//
//                    }
//                }else{
//                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
