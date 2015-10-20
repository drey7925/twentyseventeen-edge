package resq;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.opmodes.FtcOpModeRegister;
import ftc.team6460.javadeck.ftc.Utils;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.bytedeco.javacpp.opencv_core;

/**
 * Created by akh06977 on 9/18/2015.
 */

public class RockerBogieTeleop extends RockerBogieCommon {


    double scaledPower;

    @Override
    public void init() {
        super.init();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
        this.gamepad1.setJoystickDeadzone(0.1f);

    }

    @Override
    public void loop() {

        double scaleActual = (this.gamepad1.right_trigger>0.2)?scaledPower:1.00;
        //scaling
        l0.setPower(this.gamepad1.left_stick_y * scaleActual);
        r0.setPower(this.gamepad1.right_stick_y * scaleActual);

        l1.setPower(this.gamepad1.left_stick_y * scaleActual);
        r1.setPower(this.gamepad1.right_stick_y * scaleActual);

        l2.setPower(this.gamepad1.left_stick_y * scaleActual);
        r2.setPower(this.gamepad1.right_stick_y * scaleActual);

        //self explanatory winch
        if(this.gamepad1.left_bumper) {
            w.setPower(1.0);
            telemetry.addData("w", "1");
        }
        else if(this.gamepad1.right_bumper) {
            w.setPower(-1.0);
            telemetry.addData("w", "-1");
        }
        else {
            w.setPower(0);
            telemetry.addData("w", "0");
        }
    }
}
