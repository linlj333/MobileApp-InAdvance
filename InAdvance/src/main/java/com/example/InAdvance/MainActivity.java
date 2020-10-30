package com.example.InAdvance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private EditText email, password;
    private Button loginbutton;
   // private database db;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private Animation smalltobig, bottomtotop;
    private ImageView logo;
    private TextView signin_title, usersignup;
    private LinearLayout signin_linearlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setTitle("Login Page");

      //  db = new database(this);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginbutton =  findViewById(R.id.blogin);
        usersignup = findViewById(R.id.main_tv_signup);
        signin_linearlayout = findViewById(R.id.ll_signin);
        smalltobig = AnimationUtils.loadAnimation(this,R.anim.small_to_big);
        bottomtotop = AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        logo = findViewById(R.id.im_signin_logo);
        signin_title = findViewById(R.id.tv_signin_title);
        logo.startAnimation(smalltobig);
        signin_title.startAnimation(smalltobig);
        signin_linearlayout.startAnimation(bottomtotop);
      //  sharedPreferences = this.getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // String user = username.getText().toString().trim();
                String email1 = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();
               // Boolean res = db.checkUser(user, pwd);


                if(TextUtils.isEmpty(email1)){
                    email.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    password.setError("Password is Required.");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email1,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Logged In successfully",Toast.LENGTH_LONG).show();
                            DocumentReference dr = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getString("isUser") != null){
                                        startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                                        finish();
                                    }else if(documentSnapshot.getString("isBusinessOwner") != null){
                                        startActivity(new Intent(getApplicationContext(), BusinessOwner.class));
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                            });

                        }else{
                            Toast.makeText(MainActivity.this,"Error!" +task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });



//                if(res == true){
//                   // progressBar = findViewById(R.id.progressBar);
//                    Toast.makeText(MainActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
//
//                    EditText username = (EditText) findViewById(R.id.username);
//                    String text = username.getText().toString();
//                    sharedPreferences.edit().putString("Username",text).apply();
//
//                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(MainActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
//                }


        usersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}