package hat.bemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import com.guider.healthring.R;

public class SOSCall extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soscall);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        System.out.println("Mark keykeyCode = " + keyCode);

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);

    }

}
