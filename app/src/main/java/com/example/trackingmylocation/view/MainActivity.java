package com.example.trackingmylocation.view;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trackingmylocation.R;
import com.example.trackingmylocation.viewmodel.LocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    ArrayList<Location> locationArrayList = new ArrayList<>();
    private TextView locationText;
    private Context context;
    private LocationViewModel locationViewModel;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        setupViews();
        setupViewModel();
    }

    private void setupViewModel() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
    }

    public void checkLocationPermission() {
        int hasWriteStoragePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteStoragePermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
                return;
            }
            subscribeToLocationUpdate();
        } else {
            subscribeToLocationUpdate();
        }
    }

    private void setupViews() {
        locationText = findViewById(R.id.locationText);
        Button startLocation = findViewById(R.id.button_start_location);
        Button stopLocation = findViewById(R.id.button_stop_location);

        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });

        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
            }
        });

        //Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    private void stopLocationUpdates() {
        locationViewModel.getLocationHelper(context).stopLocationUpdates();
    }

    private void subscribeToLocationUpdate() {
        locationViewModel.getLocationHelper(context).observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if (location != null) {
                    locationText.setText(String.valueOf("Current Location: " +
                            location.getLatitude() + ", " +
                            location.getLongitude()));
                    plotMarkers(location);
                }
            }
        });
    }

    private void plotMarkers(Location locationObj) {
        if (map != null) {
            LatLng latLng = new LatLng(locationObj.getLatitude(), locationObj.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Position")
                    .snippet("Latitude: " + locationObj.getLatitude() + ", " +
                            "Longitude: " + locationObj.getLongitude()));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            locationArrayList.add(locationObj);
            Log.d("Location list", "plotMarkers: " + locationArrayList.size());

            //Draw Line
            ArrayList<LatLng> points = new ArrayList<>();
            if (locationArrayList != null) {
                for (int i = 0; i < locationArrayList.size(); i++) {
                    double latitude = locationArrayList.get(i).getLatitude();
                    double longitude = locationArrayList.get(i).getLongitude();
                    points.add(new LatLng(latitude, longitude));
                    map.addPolyline(new PolylineOptions().
                            addAll(points)
                            .width(5)
                            .color(Color.BLUE)
                            .geodesic(true));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    subscribeToLocationUpdate();
                }

            } else {
                // permission denied
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
    }
}