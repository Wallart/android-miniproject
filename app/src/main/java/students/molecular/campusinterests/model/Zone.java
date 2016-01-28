package students.molecular.campusinterests.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meradi on 21/01/16.
 */
public class Zone {

    private List<GeoPosition> positions = new ArrayList<>();
    private String name;
    private String description;
    private List<HashTag> tags = new ArrayList<>();

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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(List<HashTag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public List<HashTag> getTags() {
        return tags;
    }
}

