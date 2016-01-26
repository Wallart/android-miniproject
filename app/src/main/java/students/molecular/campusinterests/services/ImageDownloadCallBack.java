package students.molecular.campusinterests.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by meradi on 27/01/16.
 */
public class ImageDownloadCallBack implements Callback {
    private BufferedInputStream isr;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        isr = new BufferedInputStream(response.body().byteStream());
        bitmap = BitmapFactory.decodeStream(isr);
    }
}
