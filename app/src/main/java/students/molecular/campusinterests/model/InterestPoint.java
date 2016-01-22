package students.molecular.campusinterests.model;

/**
 * Created by meradi on 21/01/16.
 */
public class InterestPoint {

    private String name;
    private String description;
    private long views;
    private Picture picture;
    private GeoPosition position;


    public InterestPoint(String name, Picture picture, GeoPosition position) {
        this.name = name;
        this.picture = picture;
        this.position = position;
    }


    public String getName() {
        return  name;
    }

    public  String getDescription() {
        return description;
    }

    public Picture getPicture() {
        return  picture;
    }

    public GeoPosition getPosition() {
        return position;
    }

    public long getViews() {
        return views;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setViews(long views) {
        this.views = views;
    }
}
