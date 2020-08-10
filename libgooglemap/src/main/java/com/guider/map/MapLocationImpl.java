package com.guider.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.guider.libbase.map.IMapLocation;
import com.guider.libbase.map.IOnLocation;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapLocationImpl implements IMapLocation, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "MapLocationImpl";
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Geocoder mGeocoder;
    private GeoApiContext mGeoApicontext;

    private int isConnected = 0;

    private double mLng;
    private double mLat;
    private String mAddr;

    private int mMaxTryCnt = 1;
    private int mTryCnt;

    private IOnLocation mIOnLocation;

    private ExecutorService mThreadPoolExecutor;
    @Override
    public boolean start(Context context, int maxTryCnt, IOnLocation iOnLocation) {
        mContext = context;
        mIOnLocation = iOnLocation;
        mMaxTryCnt = maxTryCnt;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        if (mGeocoder == null) {
            mGeocoder = new Geocoder(mContext, Locale.getDefault());
        }

        if (mThreadPoolExecutor == null) {
            mThreadPoolExecutor = Executors.newSingleThreadExecutor();
        }
        if (mGeoApicontext == null) {
            mGeoApicontext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyBn2pd95dFYdnPTR3ks5poGlnsC-t27JTc")
                    .build();
        }

        if (mLastLocation == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
        }
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (isConnected == 0) {// 未连接
            Log.i(TAG, "Google Api Client connect ..");
            mGoogleApiClient.connect();
            isConnected = 1;
        }
        return true;
    }

    @Override
    public void stop() {
        if (locationListener != null) {
            Log.i(TAG, "Google Api Client stop ..");
            mTryCnt = 0;
            isConnected = 0;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location == null) return;
            // mLastLocation = location;

            mThreadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    setAddress(location);
                }
            });
        }
    };

    void setAddress(Location location) {
        try {
            // -105.037613,40.813819
            // 116.413384,39.910925
            List<Address> addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                String locality = hadnle(addresses.get(0).getCountryName())
                        + hadnle(addresses.get(0).getAdminArea())
                        + hadnle(addresses.get(0).getLocality())
                        + hadnle(addresses.get(0).getSubLocality())
                        + hadnle(addresses.get(0).getFeatureName());

                Log.i(TAG, locality);

                if (mTryCnt++ >= mMaxTryCnt - 1) {
                    mTryCnt = 0;
                    stop();
                }
                mAddr = locality;
                mLng = location.getLongitude();
                mLat = location.getLatitude();
                if (mIOnLocation != null) mIOnLocation.onLocation(mLng, mLat, mAddr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setAddressV2(Location location) {
        try {
            // -105.037613,40.813819
            // 116.413384,39.910925
            GeocodingApiRequest request = GeocodingApi
                    .reverseGeocode(mGeoApicontext, new LatLng(location.getLatitude(), location.getLongitude()))
                    .language(Locale.getDefault().getLanguage());
            GeocodingResult [] results = request.await();
            if (results != null && results.length > 0) {
                String locality = hadnle(results[0].toString());
                Log.i(TAG, locality);
                mAddr = locality;
                mLng = location.getLongitude();
                mLat = location.getLatitude();
                if (mIOnLocation != null) mIOnLocation.onLocation(mLng, mLat, mAddr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hadnle(String addr) {
        return addr == null ? "" : addr;
    }
    @Override
    public double getCurrLng() {
        return mLng;
    }

    @Override
    public double getCurrLat() {
        return mLat;
    }

    @Override
    public String getAddr()  {
        return mAddr;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mGoogleApiClient != null) {
            mTryCnt = 0;
            isConnected = 2;
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isConnected = 0;
        mLastLocation = null;
    }
}
