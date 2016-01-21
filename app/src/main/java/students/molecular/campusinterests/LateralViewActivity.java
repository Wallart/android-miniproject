package students.molecular.campusinterests;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LateralViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnMapReadyCallback {
    private ListView  listView;
    private DrawerLayout lateralView;
    private String[] lateralViewItems = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lateral_view);
        listView = (ListView)findViewById(R.id.list_view_drawer);
        lateralViewItems[0] = "Mes Photos";
        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lateralViewItems));
        listView.setOnItemClickListener(this);
        lateralView = (DrawerLayout)findViewById(R.id.drawer_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Add  toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Display home button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            lateralView.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Called when an item in the lateral view (Navigational drawer) is clicked
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lateralView.closeDrawer(Gravity.LEFT);
                if(position == 0) {
                    //Mes Photos selected => first and the only item of the list
                    startActivity(new Intent(this, MainActivity.class));
                }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(43.5598807, 1.46588);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.addMarker(new MarkerOptions().position(location).title(getString(R.string.college_name)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
    }

}
