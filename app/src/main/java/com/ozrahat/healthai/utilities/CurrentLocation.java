package com.ozrahat.healthai.utilities;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class CurrentLocation {

    private Timer timer;

    private LocationManager locationManager;
    private LocationResult locationResult;

    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;

    public boolean getLocation(Context context, LocationResult result) {
        //I use LocationResult callback class to pass location value from CurrentLocation to user code.
        locationResult = result;
        if(locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Exceptions will be thrown if provider is not permitted.
        try{
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception e) {
            e.printStackTrace();
        }

        try{
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception e){
            e.printStackTrace();
        }

        // Do not start listeners if no provider is enabled
        if(!isGpsEnabled && !isNetworkEnabled)
            return false;

        if(isGpsEnabled) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        if(isNetworkEnabled) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        timer = new Timer();
        timer.schedule(new GetLastLocation(), 20000);
        return true;
    }

    public void cancelListener(){
        timer.cancel();
        locationManager.removeUpdates(locationListenerGps);
        locationManager.removeUpdates(locationListenerNetwork);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);

            Location netLocation = null, gpsLocation = null;
            if(isGpsEnabled){
                try{
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            }

            if(isNetworkEnabled) {
                try {
                    netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

            // If there are both values use the latest one.
            if(gpsLocation != null && netLocation != null){
                if(gpsLocation.getTime() > netLocation.getTime())
                    locationResult.gotLocation(gpsLocation);
                else
                    locationResult.gotLocation(netLocation);
                return;
            }

            if(gpsLocation != null){
                locationResult.gotLocation(gpsLocation);
                return;
            }
            if(netLocation != null){
                locationResult.gotLocation(netLocation);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}