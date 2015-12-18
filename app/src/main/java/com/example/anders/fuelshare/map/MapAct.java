package com.example.anders.fuelshare.map;

import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.anders.fuelshare.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapAct extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        double myLat;
        double myLng;
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        // Add myLocation
        Location myLocation = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
        if (myLocation == null ) System.out.println("here");
        myLat = myLocation.getLatitude();
        myLng = myLocation.getLongitude();
        LatLng myLocationLatLng = new LatLng(myLat,myLng);
        //Add chargerMarkers


        //zoom before moving to location
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18)); //max zoom is 21
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocationLatLng));
    }

    private void AddChargerMarkers(GoogleMap map, double latitude, double longtitude, String chargerAdress, String snippet){
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longtitude))
                .title(chargerAdress)
                .snippet(snippet)
        );
    }
}
