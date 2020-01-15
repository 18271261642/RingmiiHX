package hat.bemo.BlueTooth.blegatt;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;

public class AlarmManagerTool {
	private AlarmManager mAlarmManager = null;
	private PendingIntent mPendingIntent = null;
	private Context mContext;
	
	public AlarmManagerTool(Context mContext) {
		this.mContext = mContext;
	}
	
	@SuppressWarnings("static-access")
	public void start() {
		Intent intent = new Intent(mContext, BLEScanService.class);
		mAlarmManager = (AlarmManager)mContext.getSystemService(mContext.ALARM_SERVICE);
		mPendingIntent = PendingIntent.getService(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		long now = System.currentTimeMillis();
		mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now, 20000, mPendingIntent);
	}
}