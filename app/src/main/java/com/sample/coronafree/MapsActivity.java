package com.sample.coronafree;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databsereference;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    private EditText editTextlatitude;
    private EditText editTextlongitutude;
    private Button showDataBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

//        editTextlatitude = findViewById(R.id.lat);
//        editTextlongitutude = findViewById(R.id.lon);
        showDataBtn = findViewById(R.id.showData);

        databsereference = FirebaseDatabase.getInstance("https://coronafree-77bad-default-rtdb.firebaseio.com/").getReference("user");
        databsereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{


                   //dataSnapshot.child("latitude").getValue().toString()
                    //dataSnapshot.child("longitude").getValue().toString()




                    String databaseLatitudeString = dataSnapshot.child("latitude").getValue().toString().substring(1, dataSnapshot.child("latitude").getValue().toString().length() - 1);
                    String databaseLongitudeString = dataSnapshot.child("longitude").getValue().toString().substring(1, dataSnapshot.child("longitude").getValue().toString().length() - 1);






                    String[] stringLat = databaseLatitudeString.split(",");
                    Arrays.sort(stringLat);
                    String latitude = stringLat[stringLat.length - 1].split("=")[1];

                    String[] stringLon = databaseLongitudeString.split(",");
                    Arrays.sort(stringLon);
                    String longitude = stringLon[stringLon.length - 1].split("=")[1];

                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query affectedPersonQuery = FirebaseDatabase.getInstance("https://coronafree-77bad-default-rtdb.firebaseio.com/").getReference("user")
                .orderByChild("infected").equalTo(true);

        showDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                affectedPersonQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnap: dataSnapshot.getChildren()){
                            Log.d("user location latitude", dataSnap.child("latitude").getValue().toString());
                            Log.d("user location longitude", dataSnap.child("longitude").getValue().toString());

                            String lat = dataSnap.child("latitude").getValue().toString();
                            String lon = dataSnap.child("longitude").getValue().toString();

                            LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                            MarkerOptions options = new MarkerOptions().position(latlng);
                            mMap.addMarker(options);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uuid =  user.getUid();

                    databsereference.child(uuid).child("latitude").setValue(Double.toString(location.getLatitude()));
                    databsereference.child(uuid).child("longitude").setValue(Double.toString(location.getLongitude()));


//                    databsereference.child("lat").push().setValue(Double.toString(location.getLatitude()));
//                    databsereference.child("long").push().setValue(Double.toString(location.getLongitude()));
//                    editTextlatitude.setText(Double.toString(location.getLatitude()));
//                    editTextlongitutude.setText(Double.toString(location.getLongitude()));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        locationManager = (LocationManager) getSystemService((LOCATION_SERVICE));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME,
                    MIN_DIST,
                    locationListener);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME,
                    MIN_DIST,
                    locationListener);
        }catch (Exception e){
            e.printStackTrace();
        }


    }




}