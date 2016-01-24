package students.molecular.campusinterests.model;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by meradi on 21/01/16.
 */
public class Picture {

    private Date date;
    private String url;
    private List<HashTag> tags;
    private User user;

    public Picture(){
    }

    public  Picture(User user, String url, List<HashTag> tags) {
        this.date = new Date();
        this.tags = tags;
        this.user = user;
        this.url = url;

    }

    public Date getDate() {
        return  date;
    }

    public List<HashTag> getTags() {
        return tags;
    }

    public User getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTags(List<HashTag> tags) {
        this.tags = tags;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
