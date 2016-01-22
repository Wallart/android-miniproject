package students.molecular.campusinterests.model;

import java.util.Date;

/**
 * Created by meradi on 21/01/16.
 */
public class Picture {

    private String name;
    private Date date;
    private Byte[] content;

    public  Picture(String name, Byte[] content) {
        this.date = new Date();
        this.content = content;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public Date getDate() {
        return  date;
    }

    public Byte[] getContent() {
        return content;
    }

}
