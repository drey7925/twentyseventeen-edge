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
import com.qualcomm.robotcore.hardware.Servo;
import ftc.team6460.javadeck.ftc.Utils;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.bytedeco.javacpp.opencv_core;
import org.swerverobotics.library.interfaces.Acceleration;

/**
 * Created by akh06977 on 9/18/2015.
 */

public class RockerBogieFallbackTeleop extends RockerBogieCommon {


    double scaledPower;
    Servo btnPushSrvo; // Left servo, labeled 2
    boolean pushServoDeployed = false;

    @Override
    public void init() {
        super.init();
        try {
            btnPushSrvo = hardwareMap.servo.get("btnPush");
        } catch (Exception e) {
            telemetry.addData("INITFAULT", "BTNSERVO");
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
        this.gamepad1.setJoystickDeadzone(0.1f);

    }

    @Override
    public void loop() {

        double scaleActual = (this.gamepad1.right_trigger > 0.2) ? scaledPower : 1.00;

        double tipPreventionPower = 0;
        l0.setPower(this.gamepad1.left_stick_y * scaleActual + tipPreventionPower);
        r0.setPower(this.gamepad1.right_stick_y * scaleActual + tipPreventionPower);

        l1.setPower(this.gamepad1.left_stick_y * scaleActual + tipPreventionPower);
        r1.setPower(this.gamepad1.right_stick_y * scaleActual + tipPreventionPower);

        l2.setPower(this.gamepad1.left_stick_y * scaleActual + tipPreventionPower);
        r2.setPower(this.gamepad1.right_stick_y * scaleActual + tipPreventionPower);

        //self explanatory winch
        if (this.gamepad1.left_bumper) {
            w.setPower(1.0);
            telemetry.addData("w", "1");
        } else if (this.gamepad1.right_bumper) {
            w.setPower(-1.0);
            telemetry.addData("w", "-1");
        } else {
            w.setPower(0);
            telemetry.addData("w", "0");
        }
        pushServoDeployed = (this.gamepad1.left_trigger>0.2);

        btnPushSrvo.setPosition(pushServoDeployed ? 0.091 : 0.365);
    }


}
