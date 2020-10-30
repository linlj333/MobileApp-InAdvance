package com.example.InAdvance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;




public class QR_CodeActivity extends AppCompatActivity {

    final String TAG = "ABC";
    public final static int QRCodeWidth = 500;
    Bitmap bitmap;
    private TextView tv_displayOrderDetail, tv_displayOrderId;
    private Button download, generate, readOrders, share_qr;
    private ImageView iv;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    String price, orderTime ,restaurantName, restaurantAddress, orderList, priceWithSign, userID, orderID,orderDetail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r__code);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        download = findViewById(R.id.download);
        download.setVisibility(View.INVISIBLE);
        generate = findViewById(R.id.generate);
        iv = findViewById(R.id.image);
        readOrders = findViewById(R.id.qr_readOrders);
        readOrders.setVisibility(View.INVISIBLE);
        share_qr = findViewById(R.id.qr_share);
        share_qr.setVisibility(View.INVISIBLE);
        tv_displayOrderDetail = findViewById(R.id.tv);
        tv_displayOrderId = findViewById(R.id.tv_orderId);
        tv_displayOrderId.setVisibility(View.VISIBLE);

        displayOrder();
        saveOrderToDatabase();


        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderID.length() == 0) {
                    Toast.makeText(QR_CodeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        bitmap = textToImageEncode(orderID);
                        iv.setImageBitmap(bitmap);
                        Log.d(TAG,"Your Current Order ID:  "+ orderID);
                        generate.setVisibility(View.INVISIBLE);
                        //updated the order ID into firebase
                        updateOrderID(orderID);
                        // pass orderID to MeFragment
                        SharedPreferences preferences = getSharedPreferences("myKey", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("orderID",orderID);
                        editor.apply();

                        download.setVisibility(View.VISIBLE);
                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "code_scanner"
                                        , null);
                                Toast.makeText(QR_CodeActivity.this, "Saved to Gallery", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

                        // return to order detail page
                        readOrders.setVisibility(View.VISIBLE);
                        readOrders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent;
                                intent = new Intent(QR_CodeActivity.this, OrderDetail.class);
                                startActivity(intent);
                            }
                        });

                        //share qr code
                        share_qr.setVisibility(View.VISIBLE);
                        share_qr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareQR_Code();
                            }
                        });

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    // share QR Code
    private void shareQR_Code() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        bitmap = drawable.getBitmap();
        File file = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name) + (orderID)+ ".png");
        Intent shareInt;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.setType("image/*");
            shareInt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(shareInt, "Share QR Code via"));
    }

    //display order detail
    private void displayOrder() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.InAdvance", MODE_PRIVATE);
        price = sharedPreferences.getString("priceTotal", "");
        priceWithSign = "$ " + price;
        restaurantName = sharedPreferences.getString("restaurantname", "");
        restaurantAddress = sharedPreferences.getString("restaurantaddress", "");
        orderList = "";
        orderTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        String storedHashMapString = sharedPreferences.getString("hashString", "");
        java.lang.reflect.Type type = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();
        LinkedHashMap<String, String> HashMap2 = gson.fromJson(storedHashMapString, type);

        Set<String> keys = HashMap2.keySet();
        int i = 1;
        for (String key : keys) {
            orderList += "#" + i + ". Food Name: " + key + "     Amount: " + HashMap2.get(key) + ";\n";
            i++;
        }

        orderDetail = " Restaurant: " + restaurantName + "\n Address: " + restaurantAddress + "\n" + "\n"  + " Order Time: " + orderTime + "\n" + " Order: \n"  + orderList + "\n"+ " Total: $ " + price + "\n";
        tv_displayOrderDetail.setText(orderDetail);

    }

    // upload order detail to database
    private void saveOrderToDatabase() {

        userID = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();
        // create a new document under collection "orders"
        documentReference = firestore.collection("orders").document();
        String temID = "";
        Map<String, Object> order = new HashMap<>();
        order.put("userId", userID);
        order.put("orderTime", orderTime);
        order.put("priceTotal", priceWithSign);
        order.put("restaurantName", restaurantName);
        order.put("restaurantAddress", restaurantAddress);
        order.put("orderList", orderList);
        order.put("userAccount", userEmail);
        order.put("orderID",temID);

        firestore.collection("orders")
                .add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                orderID = documentReference.getId();
                tv_displayOrderId.append(" Order ID: " + orderID);
            }
        });

//        documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                //Log.d(TAG, "onSuccess: order info is saved" + orderID);
//            }
//        });
//        CollectionReference collectionReference = firestore.collection("orders");
//        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if(e != null){
//                    return;
//                }
//
//                for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
//                    DocumentSnapshot documentSnapshot = dc.getDocument();
//                    orderID= documentSnapshot.getId();
//                    if(dc.getType() == DocumentChange.Type.ADDED){
//                        textView.append("this is test:::::  " + orderID);
//                    }
//
//                }
//            }
//        });

       // orderID = firestore.collection("orders").document().getId();
       // Log.d(TAG, "************************************** ORDER ID: " + orderID);
        //Toast.makeText(getApplicationContext(), "Saved the Order info" + orderID, Toast.LENGTH_LONG).show();

    }

    private void updateOrderID(String orderID) {
        firestore.collection("orders").document(orderID)
                .update("orderID",orderID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"onSuccess: added orderid into firebase");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: ",e);
            }
        });
    }
}

