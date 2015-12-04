package resq;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.Servo;
import ftc.team6460.javadeck.ftc.Utils;
import org.swerverobotics.library.interfaces.Acceleration;

/**
 * Created by akh06977 on 9/18/2015.
 */

public class RockerBogieTeleop extends RockerBogieCommon {


    double scaledPower;
    private static final double TIP_PREVENTION_WARNING_ANGLE = 50;
    private static final double TIP_PREVENTION_CRIT_ANGLE = 65;
    private static final double TIP_PREVENTION_PWR = 0.1; // per m*s^-2
    GyroHelper gh;


    Servo btnPushSrvo; // Left servo, labeled 2
    boolean pushServoDeployed = false;
    @Override
    public void init() {
        super.init();
        btnPushSrvo = hardwareMap.servo.get("btnPush");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
        this.gamepad1.setJoystickDeadzone(0.1f);
        gh = new GyroHelper(this);
        gh.startUpGyro();
    }

    @Override
    public void loop() {

        double scaleActual = (this.gamepad1.right_trigger > 0.2) ? scaledPower : 1.00;

        gh.update();
        Acceleration grav = gh.getRawAccel();
        double tipAngle = Math.toDegrees(Math.acos(grav.accelZ/Math.hypot(Math.hypot(grav.accelX, grav.accelY), grav.accelZ)));
        telemetry.addData("TIPANGLE", "tipAngle");
        double tipPreventionPower = 0;
        if(tipAngle>TIP_PREVENTION_CRIT_ANGLE) {
            tipPreventionPower = grav.accelX * TIP_PREVENTION_PWR;
            telemetry.addData("TIP", "DANGER");

        }
        else if (tipAngle > TIP_PREVENTION_WARNING_ANGLE){
            telemetry.addData("TIP", "WARNING");
        } else {
            telemetry.addData("TIP", "OK");
        }

        boolean fullOverrideNeg = (this.gamepad1.right_trigger > 0.2);
        boolean fullOverridePos = (this.gamepad1.left_trigger > 0.2);

        double lCalculated = this.gamepad1.left_stick_y * scaleActual + tipPreventionPower;

        double rCalculated = this.gamepad1.right_stick_y * scaleActual + tipPreventionPower;

        l0.setPower(lCalculated);
        r0.setPower(rCalculated);

        l1.setPower(lCalculated);
        r1.setPower(rCalculated);

        l2.setPower(lCalculated);
        r2.setPower(rCalculated);

        //self explanatory winch

        w.setPower(this.gamepad2.right_stick_y);
        telemetry.addData("w", "1");

        pushServoDeployed = (this.gamepad1.left_trigger > 0.2);

        btnPushSrvo.setPosition(pushServoDeployed ? 0.091 : 0.365);
        pushServoDeployed = (this.gamepad1.left_trigger>0.2);

        btnPushSrvo.setPosition(pushServoDeployed ? 0.091 : 0.365);

    }


}

