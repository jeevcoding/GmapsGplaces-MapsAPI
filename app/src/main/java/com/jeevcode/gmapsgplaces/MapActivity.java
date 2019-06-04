package com.jeevcode.gmapsgplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String TAG = "MAP_ACTIVITY";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15F;

    private Boolean mlocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedlocationproviderclient;


//widgets
    private EditText msearchText;
    private ImageView mGps;


   // Marker marker;
    //private SupportPlaceAutocompleteFragment placeAutocompleteFragment;









    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "The MAP IS READY...!!!!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady:Map is ready here....");
        mMap = googleMap;
        if (mlocationPermissionsGranted) {
            getDeviceLocation();


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            //call init if all permissions are given...
            init();
        }
    }









    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



        /*
        placeAutocompleteFragment=(SupportPlaceAutocompleteFragment)getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {


          /*  @Override
            public void onPlaceSelected(Place place) {

                final LatLng latlng=place.getLatLng();

                if (marker!=null){

                    marker.remove();
                }
                marker=mMap.addMarker(new MarkerOptions().position(latlng).title(place.getName().toString()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12),2000,null);

            }

            @Override
            public void onError(Status status) {

                Toast.makeText(MapActivity.this,""+status.toString(),Toast.LENGTH_SHORT).show();


            }


        });

        */






        msearchText=(EditText) findViewById(R.id.input_search);
        mGps=(ImageView)findViewById(R.id.ic_gps);



        getLocationPermission();

    }







    private void init(){
        Log.d(TAG,"init: initializing...");
        msearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId ==EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    //execute our method for searching....
                    geoLocate();

                }

                return false;
            }
        });



        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onclick: clocked on the gps icon");
                getDeviceLocation();//i.e the map should move to the location of the users device...i.e redirect to users location
            }
        });


        //this is the function tp hide the keyboard after searching for a place in the search bar....
        //HideSoftKeyboard();
        closeKeyboard();

    }








//the below function is to locate the place that is typed by the user on the search bar...
    private void geoLocate(){

        Log.d(TAG,"geolocate:geolocating");
        String searchString=msearchText.getText().toString();
        Geocoder geocoder=new Geocoder(MapActivity.this);
        List<Address> list=new ArrayList<>();

        try{

            list=geocoder.getFromLocationName(searchString,1);

        }catch (IOException e)
        {
            Log.d(TAG,"IOEXCEOTION:"+e.getMessage());
        }


        if (list.size()>0)//i.e if we have some results...
        {
            Address address=list.get(0);
            Log.d(TAG,"geolocate: found the address"+address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));//addressLine is an attribute showed in the logs...it gives the address of what you searched when you click on the red marker on the map...whatever is shown is the address line



        }
    }







//the below function is to get the users location on the map....
    private void getDeviceLocation(){

        Log.d(TAG,"getdevice location:getting the devices location");
        mfusedlocationproviderclient= LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mlocationPermissionsGranted)
            {
                Task location=mfusedlocationproviderclient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful())
                        {
                            Log.d(TAG,"onComplete:found the location!!");
                            Location currentlocation=(Location) task.getResult();
                            moveCamera(new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude()),DEFAULT_ZOOM,"My Location");



                        }else {

                            Log.d(TAG,"onComplete:Couldnt find the location sorray!current location is null");
                            Toast.makeText(MapActivity.this,"Unable to get current location",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }catch(SecurityException e){

            Log.d(TAG,"caught security exception"+e.getMessage());
        }

    }











    //the following is the copy pasted moveCamera















   //the follwing function is t move the camera to the location and add the marker on the location
    private void moveCamera(LatLng latlng,float zoom ,String title){

        Log.d(TAG,"moveCamera:moving the camera to lat:"+latlng.latitude+"and long:"+latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        //do not place a marker on the users location....but have a marker on whatever he types on the search bar...
        if(title !="My Location")
        {

            //CODE FOR DROPPING THE PIN...
            MarkerOptions options=new MarkerOptions()
                    .position(latlng)
                    .title(title);
            mMap.addMarker(options);

        }

        //this is the function tp hide the keyboard after searching for a place in the search bar....
        //HideSoftKeyboard();
        closeKeyboard();

    }





    private void initMap(){

        //this function will prepare our map....
        Log.d(TAG,"init:initializing the mapp...");
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

    }






    //the following function is to verify the permissions once again explicitly by in map activity itself.

    private void getLocationPermission() {
        Log.d(TAG,"getLocationPermission:getting the location permissions!!");
        //this will send the permission request
        //after android marshmallow,you always need to check specific permsiions like fine_location and coarse_location
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {

            mlocationPermissionsGranted=true;
            initMap();//we are calling the initmap() in two places....one is here,and one is in the fnction onRequestPermissionResult()
            }else {

                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }

        }else {

            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }


    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG,"onRewquestPermissionResult:this function was called!!");
        mlocationPermissionsGranted=false;
        //i.e we are assuming it is false to begin with

        switch (requestCode){

            case LOCATION_PERMISSION_REQUEST_CODE:

                {

                if (grantResults.length>0 )//i.e if any permissions have been granted....
                {
                    for (int i = 0;i<grantResults.length;i++)//iterate through each permission...and check if it is granted...
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mlocationPermissionsGranted=false;//i.e all the required m number of permission have not been granted by the user..
                            Log.d(TAG,"Permission failed!!!!....");
                            return;
                        }
                    }
                    mlocationPermissionsGranted=true;
                    Log.d(TAG,"Permission granted!!....!!");
                    //initialize the map if all permissions are granted
                    initMap();//we are calling the initmap() in two places....one is here,and one is in the fnction getLocationPermission()
                }
            }
        }
    }




    //this is the function tp hide the keyboard after searching for a place in the search bar....but this code is not workingg...try thr below one....
    private void HideSoftKeyboard(){

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }



    //given below is the function to hide the keyborad after the user has finished typing in the search bar..........
    private void closeKeyboard(){

        View view=this.getCurrentFocus();
        if (view !=null)
        {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        }
    }


}
