package students.molecular.campusinterests.model;

/**
 * Created by meradi on 21/01/16.
 */
public class InterestPoint {

    private String name;
    private String description;
    private Picture picture;
    private GeoPosition position;

    public InterestPoint() {}

    public InterestPoint(String name, Picture picture, GeoPosition position, String description) {
        this.name = name;
        this.picture = picture;
        this.position = position;
        this.description = description;
    }


    public String getName() {
        return  name;
    }

    public Picture getPicture() {
        return  picture;
    }

    public GeoPosition getPosition() {
        return position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public void setPosition(GeoPosition position) {
        this.position = position;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
