package hat.bemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TurnOnReciever extends BroadcastReceiver  {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Mark 當開機完成後 執行Service
		/*
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

			Intent serviceIntent = new Intent(context, MyService.class);
			context.startService(serviceIntent);

			//Mark 當開機完成後 執行Activity
			Intent ActivityIntent = new Intent(context, Loading.class);
			ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			context.startActivity(ActivityIntent);
		}
		*/
		}

}
