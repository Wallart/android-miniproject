package students.molecular.campusinterests;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import students.molecular.campusinterests.model.ImageResponse;
import students.molecular.campusinterests.model.InterestPoint;
import students.molecular.campusinterests.model.Picture;
import students.molecular.campusinterests.services.ImgurService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private GoogleMap map;
    private float defaultZoom;
    private LatLng defaultLocation;
    private LatLng southwest;
    private LatLng northeast;

    private Uri fileURI;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public MainActivity() {
        this.defaultLocation = new LatLng(43.5598807, 1.46588);
        this.defaultZoom = 17.0f;
        this.southwest = new LatLng(43.555686, 1.461020);
        this.northeast = new LatLng(43.572560, 1.483850);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClick(view);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
        Toast.makeText(this, "Adding point...", Toast.LENGTH_SHORT).show();
    }

    public void addZone(MenuItem item) {
        Toast.makeText(this, "Adding zone...", Toast.LENGTH_SHORT).show();
    }

    public void addPointWithPicture(MenuItem item) {
        Toast.makeText(this, "Adding point with picture...", Toast.LENGTH_SHORT).show();
        this.dispatchTakePictureIntent();
    }

    ///////////
    // PHOTO //
    ///////////

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "Capture_" + timestamp + ".jpg";
            //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            photo = new File(Environment.getExternalStorageDirectory(), filename);
            if (photo != null) {
                this.fileURI = Uri.fromFile(photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.fileURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void onPhoto() {

        getContentResolver().notifyChange(this.fileURI, null);
        ContentResolver cr = getContentResolver();

        try {
            File photo = new File(this.fileURI.getPath());
            Picture picture = new Picture(null, null, photo, null);
            final InterestPoint fakePoint = new InterestPoint("Trololo", picture, null);

            ImgurService service = new ImgurService(getApplicationContext());
            service.upload(fakePoint, new Callback<ImageResponse>() {
                @Override
                public void success(ImageResponse imageResponse, Response response) {
                    fakePoint.getPicture().setUrl(imageResponse.data.link);
                    Toast.makeText(getApplicationContext(), imageResponse.data.link, Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println(error.toString());
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            });

            //Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, this.fileURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////
    // ACTIVITY //
    //////////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    this.onPhoto();
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (this.fileURI != null) {
            outState.putString("fileUri", this.fileURI.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("fileUri")) {
            this.fileURI = Uri.parse(savedInstanceState.getString("fileUri"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://students.molecular.campusinterests/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://students.molecular.campusinterests/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
