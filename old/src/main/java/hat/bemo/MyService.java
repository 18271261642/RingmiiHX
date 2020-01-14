package hat.bemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import hat.bemo.measure.service.BluetoothShowDataActivity;

public class MyService extends Service {   
    public final static String SEND_OK_MESSAGE = "send.ok.message";  
    public static final String PARAMETERS_KEY = "KEY";
    private static final String TAG = "MyService";

    private BroadcastReceiver myBroadCast = new BroadcastReceiver() {  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();
            if(action == null)
                return;
            System.out.println("Mark 有嗎4?");
            Log.d(TAG,"啊啊啊啊啊啊啊");
            if (action.equals(SEND_OK_MESSAGE)) {
                System.out.println("Mark 有嗎?");
                Log.d(TAG,"SEND_OK_MESSAGE");
                String dataStr = intent.getStringExtra(PARAMETERS_KEY)+"";
//                ComponentName com = new ComponentName("hat.bemo", "hat.bemo.measure.service.BluetoothShowDataActivity");
//            	Intent i =new Intent();
//                i.setComponent(new ComponentName("hat.bemo", "hat.bemo.measure.service.BluetoothShowDataActivity"));
//            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//         		Bundle b = new Bundle();
//         		b.putInt("type", 0);
//         		b.putInt("TYPES", intent.getIntExtra("TYPES", 0));
//         		b.putString(PARAMETERS_KEY, intent.getStringExtra(PARAMETERS_KEY));
//         		i.putExtras(b);
//                startActivity(i);

                Intent i = new Intent(context, BluetoothShowDataActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle b = new Bundle();
                b.putInt("type", 0);
                int types = intent.getIntExtra("TYPES", 0);
                b.putInt("TYPES", types);
                b.putString(PARAMETERS_KEY, dataStr+"");
                i.putExtras(b);
                startActivity(i);


//                final Dialog dialog = new Dialog(context, R.style.AppTheme);
//                dialog.setContentView(R.layout.fora_device_list_pairing_dialog);
//                TextView pairingtext = (TextView) dialog.findViewById(R.id.pairingtext);
//                View pairingYes = (View) dialog.findViewById(R.id.pairingYes);
//                View pairingNo = (View) dialog.findViewById(R.id.pairingNo);
//                pairingtext.setText("yo");
//
//
//                dialog.show();

            }  
        }  
    };  
  
    @Override  
    public void onCreate() {  
        super.onCreate();     
        
    }  
  
    @SuppressWarnings("deprecation")
	@Override  
    public void onStart(Intent intent, int startId) {  
    	super.onStart(intent, startId);  
//        IntentFilter myFilter = new IntentFilter();
//        myFilter.addAction(SEND_OK_MESSAGE);
//        this.registerReceiver(myBroadCast, myFilter);

        registerReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private boolean mReceiverTag = false;   //广播接受者标识

    //代码中动态注册广播
    private void registerReceiver() {
        if (!mReceiverTag) {     //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
            IntentFilter myFilter = new IntentFilter();
            myFilter.addAction(SEND_OK_MESSAGE);
            Log.d("=============", "注册了 SEND_OK_MESSAGE");
            mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
            this.registerReceiver(myBroadCast, myFilter);
        }
    }


    //注销广播
    private void unregisterReceiver() {
        if (mReceiverTag) {   //判断广播是否注册
            mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            this.unregisterReceiver(myBroadCast);   //注销广播
        }

    }


    @Override  
    public IBinder onBind(Intent arg0) {  
        return null;  
    }  
}  