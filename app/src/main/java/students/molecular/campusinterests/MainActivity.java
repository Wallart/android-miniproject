package students.molecular.campusinterests;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import students.molecular.campusinterests.model.GeoPosition;
import students.molecular.campusinterests.model.HashTag;
import students.molecular.campusinterests.model.ImageResponse;
import students.molecular.campusinterests.model.InterestPoint;
import students.molecular.campusinterests.model.Picture;
import students.molecular.campusinterests.services.IInterestPoint;
import students.molecular.campusinterests.services.ImgurService;
import students.molecular.campusinterests.services.InterestPointImpl;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, IInterestPoint.DataChangedListener
         {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private GoogleMap map;
    private float defaultZoom;
    private LatLng defaultLocation;
    private LatLng southwest;
    private boolean isMapReady = false;
    private ArrayList<InterestPoint> pointsOfInterest;
    private LatLng northeast;
    private Bitmap bitmap;
    private Context context;
    ImgurService service;
             IInterestPoint interestPointService;
    GoogleApiClient mGoogleApiClient;


    private Uri fileURI;
    private Location mLastLocation;
    private GeoPosition position;
             private LocationRequest locationRequest;
             private GPSTracker gps;

             public MainActivity() {
        this.defaultLocation = new LatLng(43.5598807, 1.46588);
        this.defaultZoom = 17.0f;
        this.southwest = new LatLng(43.555686, 1.461020);
        this.northeast = new LatLng(43.572560, 1.483850);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        gps = new GPSTracker(MainActivity.this);
        service = new ImgurService(context);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        interestPointService = new InterestPointImpl(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClick(view);
            }
        });
        handleIntent(getIntent());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pointsOfInterest = new ArrayList<InterestPoint>(interestPointService.getPointsOfInterest()); //fetch all points
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setIconified(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.clearFocus();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == R.id.nav_gallery) {
            Toast.makeText(this, "Mes Photos",Toast.LENGTH_LONG).show();
        }  else if (id == R.id.nav_manage) {
             Toast.makeText(this, "Configuration",Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

      /////////
     // MAP //
    /////////

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        this.map.addMarker(new MarkerOptions().position(this.defaultLocation).title(getString(R.string.college_name)));
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.defaultLocation, this.defaultZoom));
        this.map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                checkMapBounds();
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                addPoint(null, new GeoPosition(arg0.latitude, arg0.longitude));
            }
        });
        isMapReady = true;
        addMarkers(pointsOfInterest); //add markers
    }

    private void checkMapBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(northeast);
        builder.include(southwest);
        final LatLngBounds allowedBounds = builder.build();

        LatLngBounds centro = map.getProjection().getVisibleRegion().latLngBounds;

        if (allowedBounds.contains(centro.getCenter()))
            return;
        else {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(this.defaultLocation, this.defaultZoom));
        }
    }

      //////////////////
     // CONTEXT MENU //
    //////////////////

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderTitle(getString(R.string.ctx_title));
        inflater.inflate(R.menu.main_context_menu, menu);
        MenuItem pointAndPicture =  menu.findItem(R.id.itemPointWithPicture);
        MenuItem point =  menu.findItem(R.id.itemPoint);
        MenuItem zone =  menu.findItem(R.id.itemZone);
        pointAndPicture.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addPointWithPicture(item);
                return true;
            }
        });
        point.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addPointWithoutPicture(item);
                return true;
            }
        });
        zone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addZone(item);
                return true;
            }
        });
    }

    public void onAddClick(View v){
        registerForContextMenu(v);
        openContextMenu(v);
    }

    public void addPointWithoutPicture(MenuItem item) {
        Toast.makeText(this, "Adding point...", Toast.LENGTH_SHORT).show();
    }

    public void addZone(MenuItem item) {
        Toast.makeText(this, "Adding zone...", Toast.LENGTH_SHORT).show();
    }

    public void addPointWithPicture(MenuItem item) {
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            position = new GeoPosition(latitude, longitude);
        }else{
            gps.showSettingsAlert();
        }
        takePhoto();
    }

      ///////////
     // PHOTO //
    ///////////

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String date = new Date().toString();
        String filename = "Photo_"+".jpg";

        //File photo = new File(getCacheDir(), filename);
        File photo;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), filename);
            Log.d("takePhoto", "Accessed to external storage");
        }
        else {
            Log.d("takePhoto", "Cannot access external storage");
            photo = new File(getCacheDir(), filename);
        }
        try {
            photo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fileURI = Uri.fromFile(photo);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;
        String path = this.fileURI.getPath();
        OutputStream fOut = null;
        File file = new File(path);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                fOut = new FileOutputStream(file);
                stream = this.getApplicationContext().getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
                service.upload(fileURI.getPath().toString(), new Callback<ImageResponse>() {
                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                        addPoint(imageResponse.data.link, position);
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("error: " + error);
                    }
                });
            }
        }
    }



    private void addPoint(String imageUrl, GeoPosition position){
        List<HashTag> tags = new ArrayList<>();
        tags.add(new HashTag("paul sabatier"));
        Picture pic = new Picture(null, imageUrl, tags);
        InterestPoint point = new InterestPoint("", pic, position, "");
        interestPointService.save(point);
    }

    /**
     *  Adds Point markers on the map.
     */
    public void addMarkers(Collection<InterestPoint> pointsOfInterest){
        /** Make sure that the map has been initialised **/
        if( pointsOfInterest != null && isMapReady && (map != null)){
                for (InterestPoint point : interestPointService.getPointsOfInterest()) {
                    LatLng position = new LatLng(point.getPosition().getLatitude(), point.getPosition().getLongtitude());
                    map.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title(point.getName())
                                    .snippet(point.getDescription()));

                }
            }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        ArrayList<InterestPoint> searchResultsFromDB = new ArrayList<>(interestPointService.getPointsOfInterest(query));
        pointsOfInterest = searchResultsFromDB;
        if(searchResultsFromDB.size() > 0 && isMapReady ) {
            //Clear all markers
            map.clear();
            addMarkers(interestPointService.getPointsOfInterest(query));

        } else {
            Toast.makeText(this, "No results for your search " + isMapReady, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataChange() {
        addMarkers(interestPointService.getPointsOfInterest());
    }
}
