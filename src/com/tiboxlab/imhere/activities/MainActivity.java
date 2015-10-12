package com.tiboxlab.imhere.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tiboxlab.imhere.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Resources :
 * http://www.rdcworld-android.blogspot.in/2012/01/get-current-location-
 * coordinates-city.html
 * http://stackoverflow.com/questions/14222152/androids-onstatuschanged-not-
 * working
 * https://github.com/barbeau/gpstest/blob/master/GPSTest/src/main/java/com/
 * android/gpstest/GpsTestActivity.java
 * 
 * @author TiboxLab
 *
 */

public class MainActivity extends Activity implements LocationListener, android.location.GpsStatus.Listener
{
	TextView textAccuracy = null;
	TextView textLatitude = null;
	TextView textLongitude = null;
	TextView textAltitude = null;
	TextView textDate = null;
	TextView textSend = null;
	TextView textGpsStatus = null;
	TextView textSatellitesFound = null;
	TextView textSatellitesUsed = null;
	LinearLayout layoutCoordinates = null;

	LocationManager locationManager = null;
	Location location = null;
	GpsStatus status = null;

	// Geocoder geocoder;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textAccuracy = (TextView) this.findViewById(R.id.textAccuracy);
		textLatitude = (TextView) this.findViewById(R.id.textLatitude);
		textLongitude = (TextView) this.findViewById(R.id.textLongitude);
		textAltitude = (TextView) this.findViewById(R.id.textAltitude);
		textDate = (TextView) this.findViewById(R.id.textDate);
		textGpsStatus = (TextView) this.findViewById(R.id.textGpsStatus);
		textSatellitesUsed = (TextView) this.findViewById(R.id.textSatellitesUsed);
		textSatellitesFound = (TextView) this.findViewById(R.id.textSatellitesFound);
		textSend = (TextView) this.findViewById(R.id.buttonSend);
		layoutCoordinates = (LinearLayout) this.findViewById(R.id.layoutCoordinates);

		this.textDate.setText("");
		this.textAccuracy.setText("");
		this.textLatitude.setText("");
		this.textLongitude.setText("");
		this.textAltitude.setText("");
		this.textGpsStatus.setText("");
		this.textSatellitesUsed.setText("0");
		this.textSatellitesFound.setText("0");
		this.textGpsStatus.setText("");

		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);

		layoutCoordinates.setVisibility(View.GONE);

		// location =
		// locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// this.onLocationChanged(location);

		textSend.setVisibility(View.GONE);
		textSend.setOnClickListener(new OnClickListener()
		{
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

	public void vibrate()
	{
		Vibrator vibrator;
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
	}

	@Override
	public void onLocationChanged(Location receivedLocation)
	{
		if (receivedLocation == null) return;

		if (location == null) vibrate();

		location = receivedLocation;
		textSend.setVisibility(View.VISIBLE);

		String formattedDate = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE).format(new Date(location.getTime()));

		this.textDate.setText(formattedDate);
		this.textAccuracy.setText(String.format("%.0f m", location.getAccuracy()));
		this.textLatitude.setText(String.format("%f", location.getLatitude()));
		this.textLongitude.setText(String.format("%f", location.getLongitude()));
		this.textAltitude.setText(String.format("%.0f m", location.getAltitude()));

		layoutCoordinates.setVisibility(View.VISIBLE);
	}

	public long age_ms(Location last)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) return age_ms_api_17(last);
		return age_ms_api_pre_17(last);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private long age_ms_api_17(Location last)
	{
		return (SystemClock.elapsedRealtimeNanos() - last.getElapsedRealtimeNanos()) / 1000000;
	}

	private long age_ms_api_pre_17(Location last)
	{
		return System.currentTimeMillis() - last.getTime();
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public void onGpsStatusChanged(int event)
	{
		status = locationManager.getGpsStatus(status);

		switch (event) {
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			textGpsStatus.setText("GPS premier fix");
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			textGpsStatus.setText("GPS lancé");
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			textGpsStatus.setText("GPS arrêté");
			break;
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			textGpsStatus.setText("GPS en écoute");
			break;
		default:
			break;
		}

		int satellitesSize = 0;
		int satellitesUsedInFixSize = 0;

		for (GpsSatellite sat : status.getSatellites())
		{
			satellitesSize++;
			if (sat.usedInFix()) satellitesUsedInFixSize++;
		}
		textSatellitesFound.setText(Integer.toString(satellitesSize));
		textSatellitesUsed.setText(Integer.toString(satellitesUsedInFixSize));

	}

	private void sendLocation()
	{
		if (location != null)
		{
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
			String strLocation = "Je suis là, pouvez-vous venir me chercher ?\nhttp://maps.google.com/maps?geocode=&q="
			        + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
			intent.putExtra("sms_body", strLocation);
			startActivity(intent);
		}
	}

}
