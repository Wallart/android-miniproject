package students.molecular.campusinterests;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.BufferedInputStream;
import java.io.File;
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
import students.molecular.campusinterests.model.ImageResponse;
import students.molecular.campusinterests.model.InterestPoint;
import students.molecular.campusinterests.model.Picture;
import students.molecular.campusinterests.services.IInterestPoint;
import students.molecular.campusinterests.services.ImgurService;
import students.molecular.campusinterests.services.InterestPointImpl;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, IInterestPoint.DataChangedListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private GoogleMap map;
    private float defaultZoom;
    private LatLng defaultLocation;
    private LatLng southwest;
    private boolean isMapReady = false, addPointMode = false, addZoneMode = false;
    private ArrayList<InterestPoint> pointsOfInterest;
    private LatLng northeast;
    private Bitmap bitmap;
    private Context context;
    private InterestPoint point;
    ImgurService service;
    IInterestPoint interestPointService;
    GoogleApiClient mGoogleApiClient;
    private Dialog addPointDialog, addZoneDialog;
    private Button btnAjouter, btnAnnuler;
    private TextView pointName, pointDescription;
    private CheckBox checkBoxAddCurrentLoc;
    private List<GeoPosition> zoneBoundary;
    private boolean wantsAnotherPoint = true;
    private EditText zoneName;
    private Polygon aZoneToAdd;


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
            Toast.makeText(this, "Mes Photos", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Configuration", Toast.LENGTH_LONG).show();
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
        isMapReady = true;
        this.map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        this.map.addMarker(new MarkerOptions().position(this.defaultLocation).title(getString(R.string.college_name)));
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.defaultLocation, this.defaultZoom));
        this.map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                checkMapBounds();
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                if (point != null) {
                    point.setPosition(new GeoPosition(arg0.latitude, arg0.longitude));
                    addPoint();
                } else {
                    Toast.makeText(context, "Utiliser le button pour rajouter un point", Toast.LENGTH_SHORT).show();
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showImage(marker);
                return true;
            }
        });
        addMarkers(pointsOfInterest);
    }

    private void showImage(Marker marker) {
        point = interestPointService.getPointsOfInterestByPosition(marker.getPosition());
        System.out.println(point.getName());
        System.out.println(point.getPicture().getUrl());
        if (point != null && point.getPicture() != null && point.getPicture().getUrl() != null) {
            Dialog builder = new Dialog(this);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //nothing;
                }
            });

            ImageView imageView = new ImageView(this);
            imageView.setImageURI(Uri.parse(point.getPicture().getUrl()));
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            builder.show();
        } else {
            Toast.makeText(this, "Aucune image n'est associée à ce point", Toast.LENGTH_LONG).show();
        }
    }

    private void showAddPointDialog() {
        addPointDialog = new Dialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        addPointDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addPointDialog.setCancelable(true);
        addPointDialog.setContentView(R.layout.add_point);
        pointName = (EditText) addPointDialog.findViewById(R.id.pointName);
        pointDescription = (EditText) addPointDialog.findViewById(R.id.pointDescription);
        checkBoxAddCurrentLoc = (CheckBox) addPointDialog.findViewById(R.id.checkBoxAddCurrentLoc);
        checkBoxAddCurrentLoc.setEnabled(true);
        btnAjouter = (Button) addPointDialog.findViewById(R.id.btnAjout);
        btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = pointName.getText().toString().trim();
                String description = pointDescription.getText().toString().trim();
                if (name.isEmpty()) {
                    pointName.setError("Champs requis");
                    pointDescription.setError("Nom de point requis");
                    return;
                }
                point.setName(name);
                if (checkBoxAddCurrentLoc.isChecked()) {
                    point.setPosition(getCurrentPosition());
                }
                if (description != null && !description.isEmpty())
                    point.setDescription(description);
                if (point.getPosition() == null) {
                    Toast.makeText(context, "Sélectionner la position du point sur la map", Toast.LENGTH_SHORT).show();
                } else {
                    interestPointService.save(point);
                }
                addPointDialog.dismiss();
            }
        });
        btnAnnuler = (Button) addPointDialog.findViewById(R.id.btnAnnule);
        btnAnnuler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addPointMode = false;
                addPointDialog.dismiss();
            }
        });
        addPointDialog.show();
    }

    private void drawZone(List<GeoPosition> boundary) {
        PolygonOptions rectOptions = new PolygonOptions();
        for (GeoPosition geo : boundary)
            rectOptions.add(new LatLng(geo.getLatitude(), geo.getLongtitude()));

        if (aZoneToAdd != null) {
            aZoneToAdd.remove();
        }
        aZoneToAdd = map.addPolygon(rectOptions.strokeColor(Color.RED).strokeWidth(2));
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
        MenuItem pointAndPicture = menu.findItem(R.id.itemPointWithPicture);
        MenuItem point = menu.findItem(R.id.itemPoint);
        MenuItem zone = menu.findItem(R.id.itemZone);
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

    public void onAddClick(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
    }

    public void addPointWithoutPicture(MenuItem item) {
        point = new InterestPoint();
        showAddPointDialog();
    }

    public void addZone(MenuItem item) {
        addZoneMode = true;
        Toast.makeText(this, "Adding zone...", Toast.LENGTH_SHORT).show();
    }

    public void addPointWithPicture(MenuItem item) {
        takePhoto();
    }

    public GeoPosition getCurrentPosition() {
        GeoPosition position = null;
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            position = new GeoPosition(latitude, longitude);
        } else {
            gps.showSettingsAlert();
        }
        return position;
    }

    ///////////
    // PHOTO //
    ///////////

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String date = new Date().toString();
        String filename = "Photo_" + ".jpg";

        //File photo = new File(getCacheDir(), filename);
        File photo;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), filename);
            Log.d("takePhoto", "Accessed to external storage");
        } else {
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
                Toast.makeText(context, "Uploading image...", Toast.LENGTH_LONG).show();
                service.upload(fileURI.getPath().toString(), new Callback<ImageResponse>() {
                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                        point = new InterestPoint();
                        GeoPosition position = getCurrentPosition();
                        point.setPosition(position);
                        Picture pic = new Picture(null, imageResponse.data.link, null);
                        point.setPicture(pic);
                        showAddPointDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(context, "Uploading failed, check your connection", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


    private void addPoint() {
        interestPointService.save(point);
        point = null;
    }

    /**
     * Adds Point markers on the map.
     */
    public void addMarkers(Collection<InterestPoint> pointsOfInterest) {
        /** Make sure that the map has been initialised **/
        BufferedInputStream isr = null;
        if (pointsOfInterest != null && isMapReady && (map != null)) {
            for (InterestPoint point : pointsOfInterest) {
                LatLng position = new LatLng(point.getPosition().getLatitude(), point.getPosition().getLongtitude());
                Bitmap btm = null;
                    /*if(point.getPicture().getUrl() != null) {
                        ImageDownloadCallBack callback = new ImageDownloadCallBack();
                        service.download(point.getPicture().getUrl(), callback);
                        btm = callback.getBitmap();
                    }*/
                MarkerOptions marker = new MarkerOptions()
                        .position(position)
                        .title(point.getName())
                        .snippet(point.getDescription());
                if (btm != null) {
                    marker.icon(BitmapDescriptorFactory.fromBitmap(btm));
                    btm.recycle();
                }
                map.addMarker(marker);
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
        if (searchResultsFromDB.size() > 0 && isMapReady) {
            //Clear all markers
            map.clear();
            addMarkers(searchResultsFromDB);
        } else {
            Toast.makeText(this, "No results for your search " + isMapReady, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataChange() {
        addMarkers(interestPointService.getPointsOfInterest());
    }
}
