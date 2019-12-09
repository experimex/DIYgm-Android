package android.kaviles.bletutorial;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        t = (TextView) findViewById(R.id.count_text);
        t.setText("750");

        final Button markButton = (Button) findViewById(R.id.mark_button);
        markButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addMarker();
            }
        });

        final Button undoButton = (Button) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                undoMarker();
            }
        });

        final Button removeAllButton = (Button) findViewById(R.id.removeall_button);
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeAllMarkers();
            }
        });

        final Button hideButton = (Button) findViewById(R.id.hide_button);
        hideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.bluetooth_layout);
                layout.setVisibility(LinearLayout.GONE);
            }
        });

        builder = new AlertDialog.Builder(this);

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
                            ((TextView) findViewById(R.id.marker_count_text)).setText("Marker Count: " + markers.size());
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
        ((TextView) findViewById(R.id.marker_count_text)).setText("Marker Count: " + markers.size());
    }

    void removeAllMarkers() {
        builder.setMessage("Are you sure you want to remove all markers?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (int i = 0; i < markers.size(); i++) {
                    mMap.clear();
                    markers.clear();
                    ((TextView) findViewById(R.id.marker_count_text)).setText("Marker Count: 0");
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

}