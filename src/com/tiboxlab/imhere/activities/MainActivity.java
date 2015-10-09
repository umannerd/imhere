package com.tiboxlab.imhere.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tiboxlab.imhere.R;
import com.tiboxlab.imhere.R.id;
import com.tiboxlab.imhere.R.layout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Resources : 
 * http://www.rdcworld-android.blogspot.in/2012/01/get-current-location-coordinates-city.html
 * http://stackoverflow.com/questions/14222152/androids-onstatuschanged-not-working
 * https://github.com/barbeau/gpstest/blob/master/GPSTest/src/main/java/com/android/gpstest/GpsTestActivity.java
 * 
 * @author TiboxLab
 *
 */

public class MainActivity extends Activity implements LocationListener, android.location.GpsStatus.Listener
{
    private static final String TAG = "LocationActivity";
    TextView textLocation = null;
    TextView textGps = null;
    TextView textSatelittes = null;
    TextView textSend = null;

    LocationManager locationManager = null;
    Location location = null;
    GpsStatus status = null;

    //    Geocoder geocoder;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLocation = (TextView) this.findViewById(R.id.textLocation);
        textGps = (TextView) this.findViewById(R.id.textGps);
        textSatelittes = (TextView) this.findViewById(R.id.textSatellites);
        textSend = (TextView) this.findViewById(R.id.buttonSend);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        
//        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        this.onLocationChanged(location);
        
        textSend.setVisibility(View.GONE);
        textSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                sendLocation();
            }
        });

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location receivedLocation)
    {
        if (receivedLocation == null) return;

        location = receivedLocation;
        textSend.setVisibility(View.VISIBLE);

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(location.getTime()));
        
        Log.d(TAG, "BLABLA onLocationChanged with location " + location.toString());
        int ago = Math.round(age_ms(location) /1000);
        String text = String.format("Lat:\t %f\nLong:\t %f\nAlt:\t %.2f m\nBearing:\t %.1f\nAccuracy:\t %.2f m \nDate:\t %s\n%d seconds ago", location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getBearing(), location.getAccuracy(), formattedDate, ago);
        this.textLocation.setText(text);

    }

    public long age_ms(Location last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return age_ms_api_17(last);
        return age_ms_api_pre_17(last);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private long age_ms_api_17(Location last) {
        return (SystemClock.elapsedRealtimeNanos() - last
                .getElapsedRealtimeNanos()) / 1000000;
    }

    private long age_ms_api_pre_17(Location last) {
        return System.currentTimeMillis() - last.getTime();
    }
    
    @Override
    public void onProviderDisabled(String provider)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onGpsStatusChanged(int event)
    {
        status = locationManager.getGpsStatus(status);

        switch (event)
        {
        case GpsStatus.GPS_EVENT_FIRST_FIX:
            textGps.setText("GPS_EVENT_FIRST_FIX");
            break;
        case GpsStatus.GPS_EVENT_STARTED:
            textGps.setText("GPS_EVENT_START");
            break;
        case GpsStatus.GPS_EVENT_STOPPED:
            textGps.setText("GPS_EVENT_STOP");
            break;
        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
            textGps.setText("GPS_EVENT_SATELLITE_STATUS");

            break;
        default:
            break;
        }

        double timeToFirstFix = (status.getTimeToFirstFix() / 1000);
        int satellitesSize = 0;
        int satellitesUsedInFixSize = 0;

        for (GpsSatellite sat : status.getSatellites())
        {
            satellitesSize++;
            if (sat.usedInFix()) satellitesUsedInFixSize++;
        }
        textSatelittes.setText(String.format("FirstFix :\t%.2f \nSatellites :\t%d \nSat used in fix : \t%d", timeToFirstFix, satellitesSize, satellitesUsedInFixSize));
    }
    
    private void sendLocation()
    {
        if (location != null)
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
            String strLocation = "Je suis là, pouvez-vous venir me chercher ?\nhttp://maps.google.com/maps?geocode=&q=" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
            intent.putExtra("sms_body", strLocation);
            startActivity(intent);
        }
    }


}
