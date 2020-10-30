package com.example.InAdvance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class SecondActivity extends AppCompatActivity implements GmapFragment.Fragment1Listener, Fragment2.Fragment2Listener{
    private static String TAG = "Second Activity";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GmapFragment fragment1;
    private Fragment2 fragment2;
    private RecommendFragment fragment3;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    private TextView fullName;
    ImageView profileImage;
    String userID, name;
    Uri imageUri;
    int THE_POSITION;
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // setup drawer
        setNavDrawer();
        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        tabLayout =  findViewById(R.id.tabLayout);
        viewPager =  findViewById(R.id.viewPager);

        fragment1 = new GmapFragment();
        fragment2 = Fragment2.newInstance();
        // setup here for switch the drawer, so cannot use fragment3 in setViewPager()
        fragment3 = new RecommendFragment();

        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }



    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(fragment1, "Search");
        viewPagerAdapter.addFragment(fragment2, "List");
        viewPagerAdapter.addFragment(new RecommendFragment(), "Recommend");
        viewPager.setAdapter(viewPagerAdapter);

    }


    @Override
    public void onInputFragment1Sent(List<HashMap<String, String>> hashMaps) {
       // Fragment2 fragment20 = Fragment2.newInstance();
        fragment2.updateRank(hashMaps);
    }

    @Override
    public void onInputFragment2Sent(List<HashMap<String, String>> hashMaps) {

    }

    private void setNavDrawer(){

        toolbar = findViewById(R.id.tooBar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        // display user's full name in drawer
        showHeaderNameAndImage();


        // drawer- select menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
          @Override
           public boolean onNavigationItemSelected(MenuItem menuItem) {
              if (menuItem.isChecked()) {

                 menuItem.setChecked(false);
              } else {
                 menuItem.setChecked(true);
              }
                drawerLayout.closeDrawers();

                  switch (menuItem.getItemId()) {
                   case R.id.logout:
                       FirebaseAuth.getInstance().signOut();
                       finish();
                       startActivity(new Intent(getApplicationContext(),MainActivity.class));
                       break;
                    case R.id.login:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                        break;

                   case R.id.aboutMe:
                       intent = new Intent(SecondActivity.this,AboutMe.class);
                       startActivity(intent);
                       break;

                    case R.id.settings:
                          intent = new Intent(SecondActivity.this, SettingsActivity.class);
                          startActivity(intent);
                          break;
                    case R.id.home:
                    case R.id.map:
                          THE_POSITION = 0;
                          viewPager.setCurrentItem(THE_POSITION);
                          showMapFragment();
                          break;
                    case R.id.recommend:
                          THE_POSITION = 2;
                          viewPager.setCurrentItem(THE_POSITION);
                          showRecommendFragment();
                          break;

                    case R.id.orderDetail:
                          intent = new Intent(SecondActivity.this,OrderDetail.class);
                          startActivity(intent);
                          finish();
                          break;
                   default:
                          break;
                  }
              return true;
             }
        });
    }

    private void showHeaderNameAndImage() {
        View header = navigationView.getHeaderView(0);
        profileImage = header.findViewById(R.id.profpic);
        fullName = header.findViewById(R.id.header_fName);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        // setup drawer - display the user's full name
        userID = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(userID).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if( documentSnapshot != null){
                    name = documentSnapshot.getString("fullName");
                    fullName.setText(" Welcome！ "+ name);
                }else{
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    // switch - helper
    private void showRecommendFragment() {
        if (this.fragment3 == null) {
            this.addFragmentToStack(this.fragment3);
        }
    }
    private void showMapFragment() {
        if (this.fragment1 == null) {
            this.addFragmentToStack(this.fragment1);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else{
            finish();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.me) {
////            FirebaseAuth.getInstance().signOut();
////            finish();
////            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void addFragmentToStack(Fragment fragment){
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK ){
                imageUri = data.getData();
                uploadImageToFirebase();
            }
        }


    private void uploadImageToFirebase() {
        // upload image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(SecondActivity.this,"Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SecondActivity.this, "Failed.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
