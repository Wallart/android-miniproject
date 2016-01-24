package students.molecular.campusinterests;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private GoogleMap map;
    private float defaultZoom;
    private LatLng defaultLocation;
    private LatLng southwest;
    private LatLng northeast;;

    private Uri fileURI;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);




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
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.clearFocus(); // close the keyboard on load
        ComponentName componentName = new ComponentName(this, SearchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
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
        /*if(id == R.id.search) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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
        Toast.makeText(this, "Adding point with picture...", Toast.LENGTH_SHORT).show();
        this.takePhoto();
    }

      ///////////
     // PHOTO //
    ///////////

    private void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        String date = new Date().toString();
        String filename = "Photo_"+".jpg";

        //File photo = new File(getCacheDir(), filename);
        File photo;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), filename);
            System.out.println("Accessed to external storage");
        }
        else {
            System.out.println("Cannot access external storage");
            photo = new File(getCacheDir(), filename);
        }


        this.fileURI = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.fileURI);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void onPhoto() {
        Uri selectedImage = this.fileURI;
        getContentResolver().notifyChange(selectedImage, null);

        ContentResolver cr = getContentResolver();

        try {
            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);

            //ImageView view = new ImageView(this);
            //view.setImageBitmap(bitmap);
            //setContentView(view);

            setContentView(R.layout.activity_main);
            Toast.makeText(this, "Picture taken", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e) {
            Toast.makeText(this, "Failed to load.", Toast.LENGTH_SHORT).show();
            Log.e("Camera", e.toString());
        }
    }
}
