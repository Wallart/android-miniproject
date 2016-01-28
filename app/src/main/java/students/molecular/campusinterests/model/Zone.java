package students.molecular.campusinterests.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meradi on 21/01/16.
 */
public class Zone {

    private List<GeoPosition> positions = new ArrayList<>();
    private String name;

    public Zone() {}

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPositions(List<GeoPosition> positions) {
        this.positions = positions;
    }
}
