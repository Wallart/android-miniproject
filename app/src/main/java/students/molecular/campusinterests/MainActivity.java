package students.molecular.campusinterests;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private float defaultZoom;
    private LatLng defaultLocation;
    private LatLng southwest;
    private LatLng northeast;

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

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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
}
