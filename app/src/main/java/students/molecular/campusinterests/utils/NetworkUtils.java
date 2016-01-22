package students.molecular.campusinterests.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkUtils {

        public static final String TAG = NetworkUtils.class.getSimpleName();

        public static boolean isConnected(Context mContext) {
            try {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
                }
            }catch (Exception ex){
            }
            return false;
        }
    }
