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
    private File file;
    private List<HashTag> tags;
    private User user;

    public  Picture(User user, String url, File file, List<HashTag> tags) {
        this.date = new Date();
        this.file = file;
        this.tags = tags;
        this.user = user;
        this.url = url;

    }

    public Date getDate() {
        return  date;
    }

    public File getFile() {
        return file;
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
}
