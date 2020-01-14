package hat.bemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.guider.healthring.R;

public class ToCall extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_call);

        Intent intent = new Intent(Intent.ACTION_DIAL);
        startActivity(intent);

        finish();

    }
}
