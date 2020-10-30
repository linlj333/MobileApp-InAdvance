package com.example.InAdvance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText fName, email1, password1, password2, phoneNum;
    private Button signupbutton;
    private TextView signup_title, userlogin;
    private RadioGroup radioGroup_signup;
    private RadioButton isUser, isBusinessOwner;
   // private database db;
   private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String userID;
    String TAG ="ABC";
    private ImageView logo;
    private Animation smalltobig,bottomtotop;
    private LinearLayout signup_linearlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupUIViews2();


        radioGroup_signup = findViewById(R.id.rg_signup);
        isUser = findViewById(R.id.rb_user);
        isBusinessOwner = findViewById(R.id.rd_bussinessOwner);
        signup_linearlayout = findViewById(R.id.ll_signup);
        signup_title = findViewById(R.id.tv_signup_title);

        smalltobig = AnimationUtils.loadAnimation(this,R.anim.small_to_big);
        bottomtotop = AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        logo = findViewById(R.id.im_signup_logo);
        logo.startAnimation(smalltobig);
        signup_title.startAnimation(smalltobig);
        signup_linearlayout.startAnimation(bottomtotop);

        isUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                return;
            }
        });
        isBusinessOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                return;
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullName = fName.getText().toString().trim();
                final String email = email1.getText().toString().trim();
                final String phone = phoneNum.getText().toString().trim();
                String pwd = password1.getText().toString().trim();
                String pwd1 = password2.getText().toString().trim();



                if(TextUtils.isEmpty(fullName)){
                    fName.setError("User Name is Required");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    phoneNum.setError("Phone is Required.");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    email1.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    password1.setError("Password is Required.");
                    return;
                }
                if(TextUtils.isEmpty(pwd1)){
                    password2.setError("Confirmed Password is Required.");
                    return;
                }

                if(!(isUser.isChecked() || isBusinessOwner.isChecked())){
                    Toast.makeText(SignUpActivity.this,"Select Account Type",Toast.LENGTH_SHORT).show();
                    return;
                }



                mAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){
                              Toast.makeText(SignUpActivity.this,"User Created",Toast.LENGTH_LONG).show();
                              userID = mAuth.getCurrentUser().getUid();
                              DocumentReference documentReference = firestore.collection("users").document(userID);
                              Map<String,Object> user = new HashMap<>();
                              user.put("fullName",fullName);
                              user.put("email",email);
                              user.put("phone",phone);
                              if(isUser.isChecked()){
                                  user.put("isUser","1");
                              }
                              if(isBusinessOwner.isChecked()){
                                  user.put("isBusinessOwner","1");
                              }

                              documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      Log.d(TAG, "onSuccess: user profile is created for" + userID);
                                  }
                              });
                             if(isUser.isChecked()){
                                 startActivity(new Intent (getApplicationContext(),SecondActivity.class));
                             }else if(isBusinessOwner.isChecked()){
                                 startActivity(new Intent (getApplicationContext(),BusinessOwner.class));
                             }

                          }else{
                              Toast.makeText(SignUpActivity.this,"Error!" +task.getException(),Toast.LENGTH_LONG).show();
                          }
                    }
                });
            }
        });

//        signupbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String user = username1.getText().toString().trim();
//                String email = email1.getText().toString().trim();
//                String pwd = password1.getText().toString().trim();
//                String pwd1 = password2.getText().toString().trim();
//
//                if(pwd.equals(pwd1)){
//                    long val = db.newUser(user, email, pwd);
//                    if(val > 0){
//                        Toast.makeText(SignUpActivity.this,"Successfully Signed up", Toast.LENGTH_SHORT).show();
//                        Intent moveToLogin = new Intent(SignUpActivity.this, MainActivity.class);
//                        startActivity(moveToLogin);
//                    }else{
//                        Toast.makeText(SignUpActivity.this,"Sign Up Error", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else{
//                    Toast.makeText(SignUpActivity.this,"Password Not matching", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        userlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupUIViews2(){
      //  db = new database(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fName =  findViewById(R.id.fullname);
        phoneNum = findViewById(R.id.phone);
        email1 =  findViewById(R.id.semail);
        password1 =  findViewById(R.id.spassword1);
        password2 =  findViewById(R.id.spassword2);
        signupbutton =  findViewById(R.id.bsignup);
        userlogin =  findViewById(R.id.signup_tv_signin);
    }


}