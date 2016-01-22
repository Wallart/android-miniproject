package students.molecular.campusinterests.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by meradi on 21/01/16.
 */
public class GeoPosition {

    private LatLng coordinates;

    GeoPosition(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
}
