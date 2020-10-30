package com.example.InAdvance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


public class OrderDetail extends AppCompatActivity {

    String userID;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    DocumentReference documentReference;
    String TAG ="order detail page testing";
    private RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;
    Bitmap bitmap;
    public final static int QRCodeWidth = 500;
    Button btn_home;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.firestore_list);

        userID = mAuth.getCurrentUser().getUid();
        Query query = firestore.collection("orders").whereEqualTo("userId",userID);
        // RecycleOptions

        FirestoreRecyclerOptions<Order_RecyclerViewRow> options = new FirestoreRecyclerOptions.Builder<Order_RecyclerViewRow>()
                .setQuery(query, Order_RecyclerViewRow.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Order_RecyclerViewRow, OrdersViewHolder>(options) {
            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_display_orderlist,parent,false);
                return new OrdersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull Order_RecyclerViewRow model) {

                  holder.display_order_list.setText(model.getOrderList());
                  holder.display_resName.setText(model.getRestaurantName());
                  holder.display_resAddress.setText(model.getRestaurantAddress());
                  holder.display_price.setText(model.getPriceTotal());
//                  holder.display_order_time.setText((CharSequence) model.getOrderTime().toDate());
                  holder.display_order_time.setText(model.getOrderTime());
                try {
                    bitmap = textToImageEncode(model.getOrderID());
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                  holder.iv_qrCode.setImageBitmap(bitmap);
            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


//        FirebaseFirestore.getInstance().collection("orders")
//                .whereEqualTo("userId",userID)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                   @Override
//                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                       Log.d(TAG,"onSuccess: We are getting the data" );
//                       List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
//                       for(DocumentSnapshot snapshot:snapshotList){
//                           Log.d(TAG,"onSuccess: " + snapshot.getData().toString());
//                       }
//           }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG,"onFailure: ",e);
//            }
//        });

        btn_home = findViewById(R.id.btn_order_return);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(OrderDetail.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    private class OrdersViewHolder extends RecyclerView.ViewHolder {
        private TextView display_order_list, display_order_time, display_price,display_resName, display_resAddress,display_qr_code;
        ImageView iv_qrCode;
        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            display_resName = itemView.findViewById(R.id.tv_resName);
            display_resAddress = itemView.findViewById(R.id.tv_resAddress);
            display_order_list = itemView.findViewById(R.id.tv_order);
            display_price = itemView.findViewById(R.id.tv_price);
            display_order_time = itemView.findViewById(R.id.tv_time);
            iv_qrCode = itemView.findViewById(R.id.iv_display_qrcode);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


private Bitmap textToImageEncode(String value) throws WriterException {
    BitMatrix bitMatrix;
    try {
        bitMatrix = new MultiFormatWriter().encode(value,
                BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);
    } catch (IllegalArgumentException e) {
        return null;
    }

    int bitMatrixWidth = bitMatrix.getWidth();
    int bitMatrixHeight = bitMatrix.getHeight();
    int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

    for (int y = 0; y < bitMatrixHeight; y++) {
        int offSet = y * bitMatrixWidth;
        for (int x = 0; x < bitMatrixWidth; x++) {
            pixels[offSet + x] = bitMatrix.get(x, y) ?
                    getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
        }
    }
    Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
    return bitmap;
}
}