package hat.bemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by apple on 2017/10/13.
 */

public class Receiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceintent = new Intent(context, TopFloatService.class);
            context.startService(serviceintent);
        }

    }

}
