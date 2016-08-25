package ftc.team6460.javadeck.ftc.vision;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.qualcomm.ftcrobotcontroller.R;
import org.opencv.engine.OpenCVEngineInterface;
import resq.MatColorSpreadCallback;

public class VisionTestActivity extends Activity {
    OpenCvActivityHelper ocvh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision_test);
        ocvh = new OpenCvActivityHelper(this, (FrameLayout) findViewById(R.id.testPrev));
        ocvh.addCallback(new MatColorSpreadCallback(this, null));
        ((Button) findViewById(R.id.btnStart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocvh.attach();
            }
        });
        ((Button) findViewById(R.id.btnStop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocvh.stop();
            }
        });
    }

}
