package students.molecular.campusinterests.model;


public class User {

    private String pseuodo;
    private String uuid;

    public User() {}

    public User(String pseuodo) {
        this.pseuodo = pseuodo;
    }

    public String getPseuodo(){
        return  pseuodo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setPseuodo(String pseuodo) {
        this.pseuodo = pseuodo;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
