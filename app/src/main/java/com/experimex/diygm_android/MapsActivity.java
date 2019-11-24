package com.experimex.diygm_android;

import androidx.fragment.app.FragmentActivity;
import io.reactivex.disposables.Disposable;

import com.google.android.gms.location.FusedLocationProviderClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.tbruyelle.rxpermissions2.RxPermissions;

import android.os.ParcelUuid;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.experimex.diygm_android.R;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    RxBleClient rxBleClient;
    private GoogleMap mMap;
    TextView t;
    private FusedLocationProviderClient fusedLocationClient;
    int currentCount = 0;
    ArrayList<Marker> markers = new ArrayList<Marker>(0);
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("STARTUP", "YES");

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION) // ask single or multiple permission once
                .subscribe(granted -> {
                    if (granted) {
                        // All requested permissions are granted
                    } else {
                        // At least one permission is denied
                    }
                });

        //Context context = this;
        //rxBleClient = RxBleClient.create(context);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        t = (TextView) findViewById(R.id.count_text);
        t.setText("750");

        final Button markButton = findViewById(R.id.mark_button);
        markButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addMarker();
            }
        });

        final Button undoButton = findViewById(R.id.undo_button);
        undoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                undoMarker();
            }
        });

        final Button removeAllButton = findViewById(R.id.removeall_button);
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeAllMarkers();
            }
        });

        final Button hideButton = findViewById(R.id.hide_button);
        hideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LinearLayout layout = findViewById(R.id.bluetooth_layout);
                layout.setVisibility(LinearLayout.GONE);
            }
        });

        builder = new AlertDialog.Builder(this);

        //scan();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f));;
                        }
                    }
                });
    }

    void addMarker() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            markers.add(mMap.addMarker(new MarkerOptions().position(currentLocation).title(String.valueOf(currentCount))));
                            Log.d("MARKERS ADDED", Integer.toString(markers.size()));
                        }
                    }
                });

    }

    void undoMarker() {
        if (markers.size() > 0) {
            markers.get(markers.size()-1).remove();
            markers.remove(markers.size()-1);
        }
    }

    void removeAllMarkers() {
        builder.setMessage("Are you sure you want to remove all markers?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (int i = 0; i < markers.size(); i++) {
                    mMap.clear();
                    markers.clear();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    void scan() {
        Disposable scanSubscription = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                        .build()/*,

                new ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid.fromString("e3754285-8072-458b-a45b-94a0dab368ef"))
                        .build() // Optional, more than one ScanFilter may be passed as varargs.

                 */
        )
                .subscribe(
                        scanResult -> {
                            Log.d("SCAN_SUCCESS", "YES");
                            Log.d("SCAN_RESULT", scanResult.toString());
                        },
                        throwable -> {
                            Log.d("SCAN_SUCCESS", throwable.toString());
                        }
                );

        // When done, just unsubscribe.
        scanSubscription.dispose();
    }
}