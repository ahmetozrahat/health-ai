package com.ozrahat.healthai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ozrahat.healthai.R;
import com.ozrahat.healthai.models.Place;
import com.ozrahat.healthai.models.PlaceType;
import com.ozrahat.healthai.utilities.CurrentLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity{

    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;

    private final int LOCATION_PERM_REQUEST_CODE = 1000;
    private final int CAMERA_ZOOM_CONSTANT = 15;

    private ArrayList<Place> places;

    private CurrentLocation currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        places = new ArrayList<>();

        // Setup components.
        setupComponents();
    }

    private void setupComponents() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);

        // Request runtime permissions for using location.
        requestLocationPermission();

        // Get the current location and find nearby places.
        getCurrentLocation();
    }

    private void requestLocationPermission(){
        // Check for Location permissions.
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted.
            // Get the current location.
            getCurrentLocation();
        }else {
            // Permissions not granted, ask user for location permissions.
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERM_REQUEST_CODE);
        }
    }

    private void getCurrentLocation() {
        CurrentLocation.LocationResult locationResult = new CurrentLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                Toast.makeText(MapsActivity.this, "Lat:"+location.getLatitude()+", Long"+location.getLongitude(), Toast.LENGTH_LONG).show();

                supportMapFragment.getMapAsync(map -> {
                    // When map is ready, we have a callback.
                    googleMap = map;

                    // Get PlaceType from intent extra.
                    // Then get the nearby places corresponding to this type.
                    PlaceType placeType = (PlaceType) getIntent().getSerializableExtra("place");

                    getNearbyPlaces(placeType, location);
                });
            }
        };

        currentLocation = new CurrentLocation();
        currentLocation.getLocation(this, locationResult);
    }

    /**
     *  This function uses Places API.
     *  In order to use this API, you should enable billing account on Google Cloud.
     *  And get an API key.
     *  Look https://developers.google.com/places/web-service/overview
     *  for more detailed information.
     */

    private void getNearbyPlaces(PlaceType placeType, Location location) {
        // Construct the Places API url.
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + location.getLatitude() + "," + location.getLongitude()
                + "&radius=5000"
                + "&types=" + placeType.id
                + "&sensor=true"
                + "&key=" + getResources().getString(R.string.API_KEY);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // We got the places JSON file.
                    // Let's parse it.
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        // We get the results JSON Array.
                        // Then loop through it an get each place.
                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                        for(int i=0; i<jsonArray.length(); i++){
                            // We got the Place information.
                            // Put them into ArrayList.
                            JSONObject placeObject = jsonArray.getJSONObject(i);

                            String placeName = placeObject.getString("name");
                            String placeLatitude = placeObject.getJSONObject("geometry")
                                    .getJSONObject("location").getString("lat");
                            String placeLongitude = placeObject.getJSONObject("geometry")
                                    .getJSONObject("location").getString("lng");

                            // We create a Place object and insert it to the ArrayList.
                            places.add(new Place(placeName, Double.parseDouble(placeLatitude), Double.parseDouble(placeLongitude)));
                        }

                        // Now we need to add markers to each Place item.
                        for(int i = 0; i<places.size(); i++){
                            Place place = places.get(i);

                            LatLng latLng = new LatLng(place.lat, place.lng);

                            MarkerOptions markerOptions = new MarkerOptions();

                            markerOptions.position(latLng);
                            markerOptions.title(place.name);
                            googleMap.addMarker(markerOptions);

                            if(i==places.size()-1){
                                // This is the last index of the list.
                                // We should animate camera here.
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(location.getLatitude(), location.getLongitude()), CAMERA_ZOOM_CONSTANT
                                ));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MapsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show());

        queue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERM_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted.
                getCurrentLocation();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // This will prevent the app crashing If user closes the activity before location found.
        currentLocation.cancelListener();
    }
}