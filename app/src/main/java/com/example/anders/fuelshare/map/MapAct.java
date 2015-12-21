package com.example.anders.fuelshare.map;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.anders.fuelshare.PEDO.PEDOact;
import com.example.anders.fuelshare.R;

import com.example.anders.fuelshare.data.AsyncLocationsDatabase;
import com.example.anders.fuelshare.data.Logic;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapAct extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    double myLat;
    double myLng;
    Button pedoBtn;
    boolean latLngSet = false;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //PEDO button listener
        pedoBtn = (Button) findViewById(R.id.pedo_btn);
        pedoBtn.setOnClickListener(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add chargerMarkers
        AsyncLocationsDatabase aLocD = new AsyncLocationsDatabase(this);
        aLocD.execute();
        //zoom to position
        moveToLoc();
        drawCircle();
    }

    public void addChargerMarkers(double latitude, double longtitude, String chargerAdress){
        GoogleMap map = this.mMap;
        map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longtitude))
                        .title(chargerAdress)
        );
    }
    private void moveToLoc(){
        GoogleMap map = this.mMap;

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        Location myLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        myLat = myLocation.getLatitude();
        myLng = myLocation.getLongitude();
        LatLng myLocationLatLng = new LatLng(myLat,myLng);

        map.moveCamera(CameraUpdateFactory.zoomTo(15)); //max zoom is 21
        map.moveCamera(CameraUpdateFactory.newLatLng(myLocationLatLng));

        latLngSet = true;
    }

    private void drawCircle(){
        GoogleMap map = this.mMap;
        double mRadius = Logic.instance.getRemainingDistance()*1000;
        if(latLngSet && !(mRadius<0)) {
            Circle circle = map.addCircle(new CircleOptions()
                    .center(new LatLng(myLat,myLng))
                    .radius(mRadius)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
            circle.setVisible(true);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, PEDOact.class);
        this.startActivity(i);
        finish();
    }
}
