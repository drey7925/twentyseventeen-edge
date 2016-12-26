package resq;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.hardware.DcMotor;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by akh06977 on 12/12/2015.
 */
@Autonomous
public class CameraCodeTest extends SynchronousOpMode {
    public void main() throws InterruptedException {
        final MatColorSpreadCallback cb = new MatColorSpreadCallback((Activity) hardwareMap.appContext, null);
        FtcRobotControllerActivity activity = (FtcRobotControllerActivity) hardwareMap.appContext;
        final OpenCvActivityHelper ocvh = new OpenCvActivityHelper(activity, (FrameLayout) activity.findViewById(R.id.previewLayout));
        ((Activity) hardwareMap.appContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ocvh.addCallback(cb);
                ocvh.attach();
            }
        });
        ocvh.awaitStart();
        this.waitForStart();
        Thread.sleep(1000);
        // TEST AUTON TO SEE IF BACKEND WORKS

        while (opModeIsActive()) {
            telemetry.addData("STATE", cb.getState());
            telemetry.update();
        }
        //go to front of mountain, facing the mountain


    }
}
