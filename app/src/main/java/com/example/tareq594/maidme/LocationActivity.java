package com.example.tareq594.maidme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener , LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , GoogleMap.OnCameraIdleListener , GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private MarkerOptions marker = new MarkerOptions();
    private double mylat;
    private double mylong;
    private EditText SearchText;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int ISMapLoaded = 4;
    Button MM ;
    LocationManager manager;




    private static final int GPS_ENABLE_REQUEST = 0x1001;



    Boolean mLocationPermissionGranted;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    String passDate,passTime,passPack,passCleaning = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getLocationPermission();

        MM = (Button) findViewById(R.id.MM);

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
            passDate = bundle.getString("passDate");
            passTime = bundle.getString("passTime");
            passPack = bundle.getString("passPack");
            passCleaning = bundle.getString("passCleaning");
//            Log.d("tag", passDate);

        }

        MM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GotoConformation = new Intent(LocationActivity.this, Conformation.class);

                GotoConformation.putExtra("passDate",passDate);
                GotoConformation.putExtra("passTime",passTime);
                GotoConformation.putExtra("passPack",passPack);
                GotoConformation.putExtra("passCleaning",passCleaning);
                GotoConformation.putExtra("passLat",mylat);
                GotoConformation.putExtra("passlong",mylong);
                startActivity(GotoConformation);
            }
        });

/*
        if (!( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )) {

            showGPSDiabledDialog();

        }
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")) {
            showGPSDiabledDialog();


        } */


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SearchText = (EditText) findViewById(R.id.SearchText);

        //marker = (MarkerOptions) new


        // we add function to the searchbar to open the autocomplete
        SearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                if (!( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )) {

                    showGPSDiabledDialog();

                }
            }
        });
    }


    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("permessionif", "getLocationPermission: true ");
            mLocationPermissionGranted = true;

        } else {
            Log.d("permessionelse", "getLocationPermission: else ");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.d("permession", "getLocationPermission: granted ");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Log.d("permession", "getLocationPermission: denied ");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

        Log.d("what is this", "what is this: ");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // this Override (delegate run when the map is ready, when the activity is first opened

        // first thing we get sure of the location permission , see the function above
        //  getLocationPermission();
        // then we initiate the map

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);


        LatLng defaultposition = new LatLng(32.0014643 , 35.7981345);


        // then we update the map ui accordingly
//        updateLocationUI();
//        configureCameraIdle();
//        configureCameraMoveListener();



    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // here we add listener for the map view after move is stabelized , our purpose is to set the marker to the original size when move stops
    // and we want to save the location variable and coordinates to pass it for conformation
    // we also want to use geocoder here so user can know the name of location


    // here we listen for the move of the map , so we want to change the icon to be like a dot
    private void configureCameraMoveListener() {
        GoogleMap.OnCameraMoveListener onCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                // first we set the marker position as the location of the map camera
                marker.position(mMap.getCameraPosition().target);
                // second we set the icon as the small dot one
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.smallpin);
                marker.icon(icon);


            }
        };
    }


    private void updateLocationUI() {
        // firstly we get sure that the map is already initiated , if not just return an do nothing
        if (mMap == null) {
            return;
        }
        // then we check the permession
        try {
            if (mLocationPermissionGranted) {

                // things to do if the permession is granted

                // 1- activate my location button , the one that when you press it , it moves the map to your location
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // 2- we want to set the map by default in the current location of the user

                // here we get the current location

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                Object mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

if (ISMapLoaded != 0){

    Log.d("log", "onLocationChanged: ");
    ISMapLoaded --;
    mLastLocation = location;
     double longitude = location.getLongitude();
     double latitude = location.getLatitude();
     LatLng mylocationLatLng = new LatLng( latitude ,longitude);

    Log.d("latlong", String.valueOf(mylocationLatLng));

    // we set the map camera (the position of the map) to the

      mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocationLatLng));
     mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

    // 3- we add marker to the location 32.0014643,35.7981345


    marker.position(mylocationLatLng);
    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.bigpin);
    marker.icon(icon);
    marker.draggable(true);
    mMap.addMarker(marker);

    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
            getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
    LatLng Neboundary = new LatLng(mylat + 0.0161795, mylong + 0.0331735);
    LatLng swboundary = new LatLng(mylat - 0.0161795, mylong - 0.0331735);
    LatLngBounds Bounds = new LatLngBounds(swboundary,Neboundary);
    autocompleteFragment.setBoundsBias(Bounds);

    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            Log.i("tag", "Place: " + place.getName());
        }


        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            Log.i("tag", "An error occurred: " + status);
        }
    });



}



        Log.d("location", String.valueOf(mLastLocation));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER))
        {
            showGPSDiabledDialog();
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }



        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    public void showGPSDiabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Disabled");
        builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device");
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST);
            }
        }).setNegativeButton("No, Just laa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    @Override
    public void onCameraIdle() {

        // first we set the marker position as the location of the map camera
        marker.position(mMap.getCameraPosition().target);
        // second we set the icon as the big one
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.bigpin);
        marker.icon(icon);
        // third we saved the location on variables to be passed later
        mMap.clear();
        mylat = mMap.getCameraPosition().target.latitude;
        mylong = mMap.getCameraPosition().target.longitude;
        mMap.addMarker(marker);
        //Todo: we want to create a geocoding depending on the cameraposition

        List<Address> Geolocations = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        try {
               Geolocations = geocoder.getFromLocation(mylat, mylong, 1);
           } catch (IOException e) {
              e.printStackTrace();
           }

        // hoon mesh 3aref bezzab6 shu bedde a3mal ,  bedde jehaz 7atta ajareb 3aleh

//        Address first = Geolocations.get(0);

        //String address = first.getAddressLine(0);

       // SearchText.setText(address);


    }


    @Override
    public void onCameraMove() {

        // first we set the marker position as the location of the map camera
        marker.position(mMap.getCameraPosition().target);
        // second we set the icon as the big one
        mMap.clear();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.smallpin);
        marker.icon(icon);
        mMap.addMarker(marker);
       // third we saved the location on variables to be passed later

        Log.d("moveee", "onCameraMove: ");


    }
}
