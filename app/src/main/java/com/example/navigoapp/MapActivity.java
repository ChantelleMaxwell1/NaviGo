package com.example.navigoapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.navigoapp.directionhelpers.FetchURL;
import com.example.navigoapp.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
import com.example.navigoapp.directionhelpers.FetchURL;
import com.example.navigoapp.directionhelpers.TaskLoadedCallback;

 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback /*implements OnMapReadyCallback*//*, GoogleApiClient.OnConnectionFailedListener*/ {

    // variables new code
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<com.google.android.libraries.places.api.model.AutocompletePrediction> predictionList;
    ArrayList<LatLng> listPoints;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;
    private ImageView mGps;
    private ImageButton btnRefresh;
    private ImageButton btnSave;
    private ImageButton btnStarred;
    private ImageButton btnMenu;

    // MARKERS
    private Marker userLocation;
    private MarkerOptions searchMarker;
    //private MarkerOptions searchMarker = null;

    //polyline object
    private List<Polyline> polylines = null;
    private Polyline currentPolyline;

    // distance
    Double distance;
    Double miles;
    private String user_settings;

    // save to favourites dialog
    //private AlertDialog.Builder dialogBuilder;
    //private AlertDialog dialog;

    private static final String TAG = "MapActivity";
    private static final String FILE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    //private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

    //Variables
    private Boolean mLocationPermissionGranted = false;
    //private GoogleMap mMap;
    //private FusedLocationProviderClient mFusedLocationProviderClient;
    //private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    //private GoogleApiClient mGoogleApiClient;


    // DATABASE VARIABLES
    private DatabaseReference mDBRef;
    private DatabaseReference mDBRef2;
    private FirebaseDatabase mDB;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        materialSearchBar = findViewById(R.id.searchBar);
        // nav bar
        materialSearchBar.setNavButtonEnabled(false);
        btnFind = findViewById(R.id.btn_search);
        mGps = findViewById(R.id.ic_gps);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnSave = (ImageButton) findViewById(R.id.btn_save);
        btnStarred = (ImageButton) findViewById(R.id.btn_star);
        btnMenu = (ImageButton) findViewById(R.id.btn_nav);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        listPoints = new ArrayList<>();

        // Initialize the SDK
        //Places.initialize(getApplicationContext(), "@strings/maps_api_key");

        // find location and initialize places and search
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this, "AIzaSyAXkNCKULN-JmWtgbfBrijrnFvhuaBDPHQ");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        // DATABASE
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("Favourites").child(userID);
        mDBRef2 = mDB.getReference().child("User").child(userID);


        // ON SEARCH ACTION
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    materialSearchBar.setNavButtonEnabled(false);
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    //mMap.clear();
                    //searchMarker = null;
                    //currentPolyline.remove();
                    materialSearchBar.clearSuggestions();
                    materialSearchBar.hideSuggestionsList();
                    reset();
                }
            }
        });


        // ON SEARCH TEXT CHANGED
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // SEARCH RESULTS PRIORITISE PREDICTIONS LIST BASED ON USER LOCATION
                LatLng center = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                LatLng northSide = SphericalUtil.computeOffset(center, 50000, 0);
                LatLng southSide = SphericalUtil.computeOffset(center, 50000, 180);
                RectangularBounds bounds = RectangularBounds.newInstance(southSide, northSide);


                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder().setCountry("za")
                        .setTypeFilter(TypeFilter.ESTABLISHMENT).setLocationBias(bounds)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i(TAG, "prediction on fetching task unsuccessful");
                        }
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // CLICK SEARCH ITEM
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                // clear suggestions after click
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "Place found" + place.getName());
                        LatLng latLngOfPlace = place.getLatLng();

                        /* ---------------------------- ADD MARKER TO PLACE FOUND ------------------------ */

                        if (latLngOfPlace != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                            searchMarker = new MarkerOptions().position(latLngOfPlace).title(place.getAddress());
                            mMap.addMarker(searchMarker);

                            if(listPoints.size() == 1){
                                listPoints.add(searchMarker.getPosition());
                            } else if (listPoints.size() == 2){
                                listPoints.add(1, searchMarker.getPosition());
                                //listPoints.add(searchMarker.getPosition());
                            } else {
                                listPoints.add(1, searchMarker.getPosition());
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i(TAG, "Place not found" + e.getMessage());
                            Log.i(TAG, "Status code " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                if(searchMarker != null){
                    //searchMarker.visible(false);
                    mMap.clear();
                    searchMarker = null;
                    currentPolyline.remove();
                    listPoints.remove(searchMarker);
                }

            }
        });


        // RE-ALIGN MAP TO USERS CURRENT LOCATION
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation(); // recenter to location
                materialSearchBar.clearSuggestions();
            }
        });

        // MENU / NAV BUTTON
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MapActivity.this, btnMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.nav_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_settings:
                                startActivity(new Intent(MapActivity.this, SettingsActivity.class));
                                return true;
                            case R.id.menu_fav:
                                startActivity(new Intent(MapActivity.this, FavouritesListActivity.class));
                                return true;
                            case R.id.menu_logout:
                                mAuth.signOut();
                                startActivity(new Intent(MapActivity.this, MainActivity.class));
                                return true;
                            default:
                                return false;

                        }
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        // FIND ROUTE FROM USER LOCATION TO SEARCH LOCATION
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listPoints.size() == 2) {
                    String url = getUrl(listPoints.get(0), listPoints.get(1));
                    new FetchURL(MapActivity.this).execute(url, "driving");
                    Double distanceValue = SphericalUtil.computeDistanceBetween(userLocation.getPosition(), searchMarker.getPosition());
                    Double kms = distanceValue/1000;
                    distance = Math.round(kms * 100.00) / 100.00;
                    Double milesDistance = (kms * 0.62137);
                    miles = Math.round(milesDistance * 100.00) / 100.00;
                    //Toast.makeText(MapActivity.this, "Distance =" + distance.toString() + "kms", Toast.LENGTH_SHORT).show();
                    String duration;

                } else {
                    Toast.makeText(MapActivity.this, "No location entered", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // REFRESH MAP AND TAKE AWAY SEARCH MARKER
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });


        // SAVE SEARCH LOCATION AS FAVOURITE TO DATABASE
        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listPoints.size() == 2){
                            // confirm dialog box
                            final AlertDialog dialog = new AlertDialog.Builder(MapActivity.this)
                                    .setTitle("Save to Favourites")
                                    .setMessage("Are you sure you want to save this location to your favourites?")
                                    .setPositiveButton("Yes", null)
                                    .setNegativeButton("No", null)
                                    .show();

                            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DatabaseReference currentUserDB = mDBRef;

                                    //String title = searchMarker.getTitle();
                                    String title = materialSearchBar.getText().substring(0, materialSearchBar.getText().indexOf(","));
                                    String address = materialSearchBar.getText();
                                    Double lat = searchMarker.getPosition().latitude;
                                    Double lng = searchMarker.getPosition().longitude;


                                    if(snapshot.child(title).exists()){
                                        Toast.makeText(MapActivity.this, "Already exists in your favourites", Toast.LENGTH_LONG).show();
                                    } else {
                                        currentUserDB.child(title).child("placeName").setValue(title);
                                        currentUserDB.child(title).child("placeAddress").setValue(address);
                                        currentUserDB.child(title).child("placeLat").setValue(lat.toString());
                                        currentUserDB.child(title).child("placeLng").setValue(lng.toString());

                                        Toast.makeText(MapActivity.this, "Added to Favourites", Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                }
                            });

                        } else {
                            Toast.makeText(MapActivity.this, "No data to save", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // GO TO FAVOURITES LIST
        btnStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapActivity.this, FavouritesListActivity.class));
                //finish();
            }
        });


        // FIND IF USER SETTINGS IS METRIC OR IMPERIAL
        mDBRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_settings = snapshot.child("Settings").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private String getUrl(LatLng origin, LatLng dest) {
        // origin
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // mode
        //String mode = "mode=" + directionMode;
        //building parameters for web service
        String parameters = str_origin + "&" + str_dest /*+ "&" + mode*/;
        // output format
        String output = "json";
        //building url for web service
        //String url = "http://maps.googleapis.com/maps/api/directions" + output + "?" + parameters + "&key=" + getString(R.string.google_api_key);
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + getString(R.string.google_api_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        String duration;
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        currentPolyline.setClickable(true);

        if(user_settings.equals("Metric")){
            Marker travelMarker = mMap.addMarker(new MarkerOptions()
                    .position(searchMarker.getPosition())
                    .title("Best Route")
                    .snippet("Distance: " + distance + " km"));
            travelMarker.showInfoWindow();
        } else {
            Marker travelMarker = mMap.addMarker(new MarkerOptions()
                    .position(searchMarker.getPosition())
                    .title("Best Route")
                    .snippet("Distance: " + miles + " miles"));
            travelMarker.showInfoWindow();
        }

    }


    //@SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 60, 198);
        }

        // check if gps is enables or not and then request user enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled())
                    materialSearchBar.disableSearch();
                return false;
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }

    }

    private void reset(){
        if(mMap != null && listPoints.size() == 2){
            //listPoints.remove(searchMarker);
            listPoints.clear();
            mMap.clear();
            materialSearchBar.setText("");
            materialSearchBar.disableSearch();
            getDeviceLocation();
            //polylines.clear();
            //mapView.getDisplay();
            //currentPolyline.remove();
        } else if (mMap != null && listPoints.size() > 2){
            listPoints.clear();
            mMap.clear();
            materialSearchBar.setText("");
            materialSearchBar.disableSearch();
            getDeviceLocation();
        }
    }

    // GET DEVICE CURRENT LOCATION OF USER
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        /* ---------- ADD MARKER TO CURRENT POSITION ------------- */
                        if(listPoints.size() == 0){
                            userLocation = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("Your location"));
                            //userLocation = new MarkerOptions().position().title(place.getName());
                            //mMap.addMarker(userLocation);
                            listPoints.add(userLocation.getPosition());
                        } else if (listPoints.size() >= 1){
                            Toast.makeText(MapActivity.this, "Re-centered", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null) {
                                    return;
                                }
                                mLastKnownLocation = locationResult.getLastLocation();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                } else {
                    Toast.makeText(MapActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
