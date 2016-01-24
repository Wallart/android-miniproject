package students.molecular.campusinterests.services;

import android.content.Context;

import java.io.File;
import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import students.molecular.campusinterests.Constants;
import students.molecular.campusinterests.model.ImageResponse;
import students.molecular.campusinterests.model.ImgurAPI;
import students.molecular.campusinterests.model.InterestPoint;
import students.molecular.campusinterests.utils.NetworkUtils;

public class ImgurService {
    public final static String TAG = ImgurService.class.getSimpleName();

    private WeakReference<Context> mContext;

    public ImgurService(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void upload(String file, Callback<ImageResponse> cb) {
        if(new File(file).length() == 0)
            return;
        if (!NetworkUtils.isConnected(mContext.get())) {
            return;
        }
        RestAdapter restAdapter = buildRestAdapter();
        restAdapter.create(ImgurAPI.class).postImage(
                Constants.getClientAuth(),
                file,
                null,
                null,
                new TypedFile("image/*", new File(file)),
                cb);
    }

    private RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPI.server)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return imgurAdapter;
    }

    public void download(InterestPoint point, Callback<Response> cb) {
        if (!NetworkUtils.isConnected(mContext.get())) {
            return;
        }
        RestAdapter restAdapter = buildRestAdapter();
        restAdapter.create(ImgurAPI.class).getImage(
                Constants.getClientAuth(),
                point.getPicture().getUrl(),
                cb
        );
    }
}
