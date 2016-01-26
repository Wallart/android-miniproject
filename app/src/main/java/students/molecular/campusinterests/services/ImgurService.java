package students.molecular.campusinterests.services;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    public void download(String url, okhttp3.Callback cb) {
        if (!NetworkUtils.isConnected(mContext.get())) {
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(cb);
    }
}
