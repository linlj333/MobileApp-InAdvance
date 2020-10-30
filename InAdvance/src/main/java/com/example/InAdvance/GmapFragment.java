package com.example.InAdvance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private View view;
    private Fragment1Listener listener;
    FusedLocationProviderClient client;
    SupportMapFragment fragment;
    EditText type;
    String address;
    Button find;
    Button go;
    GoogleMap google_map;
    Integer retry;

    double currentLat = 0, currentLong = 0;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_gmap, container, false);
        mMapView = view.findViewById(R.id.google_map);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        type = (EditText) view.findViewById(R.id.et_type);
        find = (Button) view.findViewById(R.id.bt_find);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            retry = 0;
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry = 0;
                address = type.getText().toString();
                if (address.equals("")) {
                    Toast.makeText(getActivity(), " Can't be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder googlePlacesUrl =
                            new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    googlePlacesUrl.append("location=").append(currentLat).append(",").append(currentLong);
                    googlePlacesUrl.append("&radius=").append(1000);
                    googlePlacesUrl.append("&keyword=").append(address);
                    googlePlacesUrl.append("&sensor=true");
                    googlePlacesUrl.append("&key=AIzaSyAlS4OUGpiDtl5ziIqCSkb3EcPb58Z3JZw");

                    google_map.clear(); //clear the last address markers including the current location
                    getCurrentLocation();
                    PlaceTask task = new PlaceTask();
                    task.execute(googlePlacesUrl.toString());
                }
            }
        });
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showSnackBar("Enable Location and try again.", true);
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    final LatLng currentLatLng = new LatLng(currentLat, currentLong);
                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            google_map = googleMap;
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            googleMap.setMyLocationEnabled(true);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            MarkerOptions current = new MarkerOptions();
                            current.position(currentLatLng);
                            google_map.addMarker(current).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            google_map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 10
                            ));
                        }
                    });
                } else if(retry<10){
                    retry++;
                    getCurrentLocation();
                }
                else  if(retry>=10){
                    showSnackBar("No connection", true);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }


    private class PlaceTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        SharedPreferences sharedPreferences;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            String data = null;
            List<HashMap<String, String>> mapList = null;

            try {
                data = downloadUrl(strings[0]);
                JsonParser jsonParser = new JsonParser();
                JSONObject object = null;
                try {

                    object = new JSONObject(data);
                    mapList = jsonParser.parseResult(object);

//                    EditText username = (EditText) findViewById(R.id.username);
//                    String text = username.getText().toString();
                    sharedPreferences = getActivity().getSharedPreferences("com.example.InAdvance", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(String.valueOf(jsonParser),data).apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                showSnackBar("Error while fetching results.", true);
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            listener.onInputFragment1Sent(hashMaps);
            if(hashMaps.size()>0){
                showSnackBar(hashMaps.size() + " results nearby.", true);
            }else{
                showSnackBar("No results", true);
            }
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                google_map.addMarker(options).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                if (i == 0) {
                    google_map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
            }

        }
    }

    public interface Fragment1Listener{
        void onInputFragment1Sent(List<HashMap<String, String>> hashMaps);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Fragment1Listener) {
            listener = (Fragment1Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Fragment1Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine())!= null){
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public void showSnackBar(String message, boolean error) {
        hideKeyboard(getActivity());
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (!error)
            textView.setTextColor(Color.RED);
        else
            textView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
