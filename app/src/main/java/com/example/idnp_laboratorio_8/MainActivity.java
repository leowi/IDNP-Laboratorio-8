package com.example.idnp_laboratorio_8;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS=123;
    private TextView latitude;
    private TextView longitude;
    private CheckBox fineAcc;
    private Button choose;
    private TextView provText;
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    private MapsFragment mf;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        provText = (TextView) findViewById(R.id.prov);
        fineAcc = (CheckBox) findViewById(R.id.fineAccuracy);
        choose = (Button) findViewById(R.id.chooseRadio);
        //mf= (MapsFragment) findViewById(R.id.fragment);
        // user defines the criteria
        choose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fineAcc.isChecked()) {
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    //choice.setText("fine accuracy selected");
                } else {
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    //choice.setText("coarse accuracy selected");
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }else {
            // Get the location manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the location provider
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default
            criteria.setCostAllowed(false);
            // get the best provider depending on the criteria
            provider = locationManager.getBestProvider(criteria, false);

            Location location = locationManager.getLastKnownLocation(provider);

            mylistener = new MyLocationListener();

            if (location != null) {
                mylistener.onLocationChanged(location);
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            // location updates: at least 1 meter and 200millsecs change
            locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
        }

    }

    private class MyLocationListener implements LocationListener {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
            longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
            provText.setText(provider + " provider has been selected.");

            Toast.makeText(MainActivity.this,  "Location changed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(MainActivity.this, provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}