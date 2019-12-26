package hat.bemo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import com.guider.healthring.R;

public class ToBT extends BaseActivity {

    Context context;

    private BluetoothAdapter mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gd800_activity_frame);

        context = this;

        if (!mBluetoothAdapter.isEnabled()){

            mBluetoothAdapter.enable();
            System.out.println("Mark 開藍芽");

        }



        initFragment(new FORASetupFragment());

    }
}
