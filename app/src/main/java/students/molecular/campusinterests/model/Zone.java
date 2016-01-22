package students.molecular.campusinterests.model;

import java.util.List;

/**
 * Created by meradi on 21/01/16.
 */
public class Zone {

    private List<GeoPosition> positions;
    private String name;

    public Zone(String name, List<GeoPosition> positions) {
        this.name = name;
        this.positions = positions;
    }

    public List<GeoPosition> getPositions() {
        return positions;
    }

    public String getName() {
        return name;
    }
}
