package hat.bemo.setting;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 2017/11/8.
 */

public class ChildThread {
    private Timer CountdownTimer;

    public interface OnHandlerListeners {
        void onHandlerParam();
    }

    public OnHandlerListeners mListener;


    public void setOnHandlerListeners(OnHandlerListeners ongpslisteners) {
        mListener = ongpslisteners;
    }

    public ChildThread(int millisInFuture, int countDownInterval) {
        CountdownTimer = new Timer(true);
        CountdownTimer.schedule(new CountdownTimerTask(), millisInFuture, millisInFuture);
    }

    public class CountdownTimerTask extends TimerTask {

        public void run() {
            mListener.onHandlerParam();
            CountdownTimer.cancel();
        }
    };

}
