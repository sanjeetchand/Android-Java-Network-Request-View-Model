package lib.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * ******************************************************************************************
 * Created by:          Sanjeet Chand
 * Created When:        30/04/2019
 * URL:                 https://www.linkedin.com/in/sanjeetchand/
 * <p>
 * Package Name:        lib.network
 * Job Number:          v1.0
 * Description:         Get network information; WiFi Connection Status, Mobile Data Connection Status
 * Dependencies:        None
 * Change History:
 * Date       Name        Job Number      Description/Reason      Reviewed By      Review Date
 * ******************************************************************************************
 */
public class NetworkUtils {

    /**
     * Gets the Network Status of the device, whether any connection is available. Either mobile or WiFi
     *
     * @param context
     * @return
     */
    public static NetworkStatus getNetworkStatus(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : connMgr.getAllNetworks()) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            }
        } else {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }

        return new NetworkStatus(isWifiConn, isMobileConn);
    }

    /**
     * Checks to see if any network connection, either the Mobile Data or the WiFi connection is available
     *
     * @param context
     * @return true if the connection is available
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkStatus networkStatus = getNetworkStatus(context);
        return networkStatus.isMobileConn || networkStatus.isWifiConn;
    }

    /**
     * Class to store the connection status of WiFi or Mobile data
     */
    public static class NetworkStatus {

        public boolean isWifiConn;
        public boolean isMobileConn;

        /**
         * Constructor of NetworkStatus
         *
         * @param isWifiConn
         * @param isMobileConn
         */
        public NetworkStatus(boolean isWifiConn, boolean isMobileConn) {
            this.isWifiConn = isWifiConn;
            this.isMobileConn = isMobileConn;
        }
    }
}
