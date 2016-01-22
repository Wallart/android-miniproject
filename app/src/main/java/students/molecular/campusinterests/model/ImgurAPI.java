package students.molecular.campusinterests.model;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by meradi on 21/01/16.
 */
public interface ImgurAPI {

    String server = "https://api.imgur.com";

    @POST("/3/image")
    void postImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Query("account_url") String username,
            @Body TypedFile file,
            Callback<ImageResponse> cb
    );

    @GET("{url}")
    void getImage(
            @Header("Authorization") String auth,
            @Path("url") String url,
            Callback<Response> cb
    );
}
