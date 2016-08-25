package resq;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import ftc.team6460.javadeck.ftc.Utils;

import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by akh06977 on 9/18/2015.
 */
@Autonomous
public class AutonCameraTestOpMode extends OpMode {


    double scaledPower;
    volatile String state;
    private TextView tv;
    private OpenCvActivityHelper ocvh;

    @Override
    public void init() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext); // obtain pref softref
        scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
        this.gamepad1.setJoystickDeadzone(0.1f);
        FtcRobotControllerActivity activity = (FtcRobotControllerActivity) hardwareMap.appContext;
        ocvh = new OpenCvActivityHelper(activity, (FrameLayout) activity.findViewById(R.id.previewLayout));
        ocvh.addCallback(new MatColorSpreadCallback(activity, tv));
        tv = new TextView(hardwareMap.appContext);

        // start color scan
        ((Activity) this.hardwareMap.appContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ocvh.attach();
                ((FrameLayout) ((Activity) AutonCameraTestOpMode.this.hardwareMap.appContext).findViewById(com.qualcomm.ftcrobotcontroller.R.id.previewLayout)).addView(tv);
            }
        });
    }

    @Override
    public void loop() {
        //nullop

        //self explanatory
    }

    @Override
    public void stop() {

        //self explanatory
        ocvh.stop();
        super.stop();
    }
}
