package students.molecular.campusinterests;

/**
 * Created by meradi on 21/01/16.
 */
public class Constants {
    public static final String MY_IMGUR_CLIENT_ID = "8163994ea913f77";
    public static final String MY_IMGUR_CLIENT_SECRET = "2cf5f7e8a1410d5ff5b324670ec88adcfa3f00b6";

    /*
      Redirect URL for android.
     */
    public static final String MY_IMGUR_REDIRECT_URL = "http://android";

    /*
      Client Auth
     */
    public static String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

}
