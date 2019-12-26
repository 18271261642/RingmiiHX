package hat.bemo.location;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by apple on 2017/11/8.
 */

public class GPSParameters {
    public LocationManager lms;
    public Location location;
    public String bestProvider = LocationManager.GPS_PROVIDER;	//最佳資訊提供者

}
