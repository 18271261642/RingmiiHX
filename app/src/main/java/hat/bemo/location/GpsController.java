package hat.bemo.location;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by apple on 2017/11/8.
 */

public class GpsController {

    // 獲取Gps開啟或關閉狀態
    @SuppressWarnings("deprecation")
    public static boolean getGpsStatus(Context context) {
        try{
            boolean status = Settings.Secure.isLocationProviderEnabled(
                    context.getContentResolver(), LocationManager.GPS_PROVIDER);
            return status;
        }catch(Exception e){

        }
        return false;
    }

    // 打開或關閉Gps
    @SuppressWarnings("deprecation")
    public static void setGpsStatus(Context context, boolean enabled) {
        try{
            Settings.Secure.setLocationProviderEnabled(context.getContentResolver(),
                    LocationManager.GPS_PROVIDER, enabled);
        }catch(Exception e){

        }
    }

}
