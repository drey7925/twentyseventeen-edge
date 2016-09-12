package ftc.team6460.javadeck.ftc.vision;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.qualcomm.ftcrobotcontroller.R;
import resq.MatColorSpreadCallback;

import java.io.IOException;

public class VisionTestActivity extends Activity {
    OpenCvActivityHelper ocvh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision_test);
        ocvh = new OpenCvActivityHelper(this, (FrameLayout) findViewById(R.id.testPrev));
       // ocvh.addCallback(new HoughCircleDetector(Integer.parseInt(((EditText) findViewById(R.id.param1)).getText().toString()),
         //       Integer.parseInt(((EditText) findViewById(R.id.param2)).getText().toString())));
        // see KPDetector for description of this numerical parameter
        //ocvh.addCallback(new KPDetector(2001));
        try {
            ocvh.addCallback(new FLANNCorrelator(AndroidOpenCvUtil.readGrayscale(getResources(), R.drawable.legos)));
        } catch (IOException e) {
            Log.wtf("EXCEPTION", e);
        }
        //ocvh.addCallback(new MatColorSpreadCallback(this, null));
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
        ((Button) findViewById(R.id.btnFlashOff)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocvh.flashOff();
            }
        });
        ((Button) findViewById(R.id.btnFlashOn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocvh.flashOn();
            }
        });
        ((Button) findViewById(R.id.btnFocus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocvh.focus();
            }
        });
    }

}

