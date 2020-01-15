package hat.bemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TurnOnService extends Service{
	private boolean running = false;
	private final String TAG = "TurnOnService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
