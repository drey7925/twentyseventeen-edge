package org.usfirst.ftc.exampleteam.yourcodehere;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.hardware.*;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;
import resq.MatColorSpreadCallback;
import resq.ResqAuton;

/**
 * Created by rirriput on 02/02/2017.
 */
@TeleOp(name = "Time Based Auton")
public class TimeGyrolessAuton extends SynchronousOpMode {
    protected DcMotor lMotor;
    protected DcMotor rMotor;
    protected DcMotor centerOmni;

    //  DcMotor linearSlideOne = null;
    //DcMotor linearSlideTwo = null;
    protected DcMotor catapult = null;
    Servo buttonPusher = null;
    protected DcMotor ballPicker = null;
    protected ColorSensor cSensor = null;
    protected UltrasonicSensor ultrasonic = null;
    protected Servo lSweeper = null;
    protected Servo rSweeper = null;
    protected LightSensor lightSensor = null;

    static MatColorSpreadCallback cb;
    private OpenCvActivityHelper ocvh;

    double DRIVE_SPEED_RATIO = 0.1; //sets the top speed for drive train
    double OMNI_SPEED_RATIO = 1;
    MediaPlayer r2Startup;
    SharedPreferences sharedPref;
    protected ResqAuton.Colors teamColor;
    protected ResqAuton.Side startSide;
    protected boolean waitFive;
    protected boolean hitCapBall;
    protected boolean doBeacon;

    protected void startCamera() throws InterruptedException {
        if (cb != null) return;
        cb = new MatColorSpreadCallback((Activity) hardwareMap.appContext, null);
        FtcRobotControllerActivity activity = (FtcRobotControllerActivity) hardwareMap.appContext;
        ocvh = new OpenCvActivityHelper(activity, (FrameLayout) activity.findViewById(com.qualcomm.ftcrobotcontroller.R.id.previewLayout));
        ((Activity) hardwareMap.appContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ocvh.addCallback(cb);
                ocvh.attach();
            }
        });
        ocvh.awaitStart();
    }



    @Override
    public void main() throws InterruptedException {
        r2Startup = MediaPlayer.create(hardwareMap.appContext, com.qualcomm.ftcrobotcontroller.R.raw.r2startup);
        r2Startup.setLooping(false);
        r2Startup.start();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        waitFive = sharedPref.getBoolean("auton_wait_5_seconds", false);
        hitCapBall = sharedPref.getBoolean("auton_hit_cap_ball", true);
        doBeacon = sharedPref.getBoolean("auton_do_beacon", true);

        try {
            teamColor = getTeam();
            startSide = getSide();
        } catch (Exception e) {
            telemetry.addData("Error: ", "something done goofed.");
            telemetry.update();
        }
        this.lSweeper = this.hardwareMap.servo.get("lSweeper");
        this.rSweeper = this.hardwareMap.servo.get("rSweeper");
        this.lightSensor = this.hardwareMap.lightSensor.get("lightSensor");
        this.lMotor = this.hardwareMap.dcMotor.get("lMotor");
        this.rMotor = this.hardwareMap.dcMotor.get("rMotor");
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.ballPicker = this.hardwareMap.dcMotor.get("ballPicker");
        this.cSensor = this.hardwareMap.colorSensor.get("cSensor");
        this.ultrasonic = this.hardwareMap.ultrasonicSensor.get("ultrasonic");
        this.buttonPusher = this.hardwareMap.servo.get("buttonPusher");
        this.centerOmni = this.hardwareMap.dcMotor.get("centerOmni");
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.catapult.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.lMotor.setDirection(DcMotor.Direction.REVERSE);
        this.rMotor.setDirection(DcMotor.Direction.FORWARD);
        lightSensor.enableLed(true);


        this.startCamera();
        this.waitForStart();

        lSweeper.setPosition(.40);
        rSweeper.setPosition(.30);
        buttonPusher.setPosition(0);

        if (teamColor.equals(ResqAuton.Colors.BLUE ) && !(ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN")).equals("MIDLINE"))) {
            goForwardTime(.3);
            turnRightTime(0.5);
            shootCatapult();
            runBallPickerTime();
            shootCatapult();
            turnLeftTime(0.5);
            goForwardTime(0.2);
            turnRightTime(0.3);
            while (ultrasonic.getUltrasonicLevel() > 15 || ultrasonic.getUltrasonicLevel()==0) {
                lMotor.setPower(0.1);
                rMotor.setPower(0.1);
                telemetry.addData("Is Running: ", ultrasonic.getUltrasonicLevel());
                telemetry.update();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRightTime(0.3);
            int initialLightness = lightSensor.getLightDetectedRaw();
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(-0.1);
                rMotor.setPower(-0.1);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            pressButtonSequence(GyrolessAuton.Orientation.BACKWARD);
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(-0.1);
                rMotor.setPower(-0.1);
            }
            pressButtonSequence(GyrolessAuton.Orientation.BACKWARD);
            lMotor.setPower(0);
            rMotor.setPower(0);

        }
        else if(teamColor.equals(ResqAuton.Colors.BLUE) && ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN")).equals("MIDLINE")){
            goForwardTime(0.5);
            turnRightTime(0.3);

            while (ultrasonic.getUltrasonicLevel() > 15  || ultrasonic.getUltrasonicLevel()==0) {
                lMotor.setPower(0.1);
                rMotor.setPower(0.1);
                telemetry.addData("Is Running: ", ultrasonic.getUltrasonicLevel());
                telemetry.update();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRightTime(0.3);
            int initialLightness = lightSensor.getLightDetectedRaw();
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(-0.1);
                rMotor.setPower(-0.1);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            pressButtonSequence(GyrolessAuton.Orientation.BACKWARD);
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(-0.1);
                rMotor.setPower(-0.1);
            }
            pressButtonSequence(GyrolessAuton.Orientation.BACKWARD);
            lMotor.setPower(0);
            rMotor.setPower(0);
        }
        else if (teamColor.equals(ResqAuton.Colors.RED)) {
            goForwardTime(0.3);
            turnRightTime(0.5);
            shootCatapult();
            runBallPickerTime();
            shootCatapult();
            turnLeftTime(0.5);
            goForwardTime(0.2);
            turnLeftTime(0.3);
            while (ultrasonic.getUltrasonicLevel() > 15) {
                lMotor.setPower(0.1);
                rMotor.setPower(0.1);
                telemetry.addData("Is Running: ", opModeIsActive());
                telemetry.update();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRightTime(0.3);
            int initialLightness = lightSensor.getLightDetectedRaw();
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(0.1);
                rMotor.setPower(0.1);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            pressButtonSequence(GyrolessAuton.Orientation.FORWARD);
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(0.1);
                rMotor.setPower(0.1);
            }
            pressButtonSequence(GyrolessAuton.Orientation.FORWARD);
            lMotor.setPower(0);
            rMotor.setPower(0);
        }

    }

    void turnRightTime(double seconds) {
        lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        lMotor.setPower(DRIVE_SPEED_RATIO);
        rMotor.setPower(-DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
    }
    void turnLeftTime(double seconds) {
        lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        lMotor.setPower(-DRIVE_SPEED_RATIO);
        rMotor.setPower(DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
    }
    void goForwardTime(double seconds) {
        lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        lMotor.setPower(DRIVE_SPEED_RATIO);
        rMotor.setPower(DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
    }
    void goBackwardTime(double seconds) {
        lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        lMotor.setPower(-DRIVE_SPEED_RATIO);
        rMotor.setPower(-DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
    }


    void shootCatapult() {
        this.catapult.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        //this.catapult.setTargetPosition(1760);
        this.catapult.setTargetPosition(6000);

        this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.catapult.setPower(0.25);
        while (Math.abs(catapult.getCurrentPosition()) < Math.abs(catapult.getTargetPosition())) {
            telemetry.addData("Catapult Position: ", catapult.getCurrentPosition());
            telemetry.update();
        }
        this.catapult.setPower(0);
    }

    void pressButton() {
        buttonPusher.setPosition(0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buttonPusher.setPosition(1);
    }

    void pressButtonSequence(GyrolessAuton.Orientation direction) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (direction == GyrolessAuton.Orientation.FORWARD) {
            rMotor.setDirection(DcMotor.Direction.FORWARD);
            lMotor.setDirection(DcMotor.Direction.REVERSE);
        } else {
            rMotor.setDirection(DcMotor.Direction.REVERSE);
            lMotor.setDirection(DcMotor.Direction.FORWARD);
        }
        double motorPower = 0.1;
        double rStartPos = rMotor.getCurrentPosition();
        double lStartPos = lMotor.getCurrentPosition();
        if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goForwardTime(0.1);}
        else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goBackwardTime(0.1);}
        else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goForwardTime(0.1);}
        else if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goBackwardTime(-0.1);}
        else {
            telemetry.addData("Two Beacon Colors Not Detected", "Keep going");
            telemetry.update();
            lMotor.setPower(motorPower);
            rMotor.setPower(motorPower);
            long startTime = System.currentTimeMillis();
            while (true) {
                if (cb.getState().equals("RB") || cb.getState().equals("BR")) {
                    if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goForwardTime(0.1);}
                    else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goBackwardTime(-0.1);}
                    else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goForwardTime(0.1);}
                    else if (cb.getState().equals("RB") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.getState().equals("BR") && direction.equals(GyrolessAuton.Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goBackwardTime(-0.1);}
                    break;
                }
                if (System.currentTimeMillis()-startTime > 3000) {
                    break;
                }
            }
        }
        pressButton();
        rMotor.setDirection(DcMotor.Direction.FORWARD);
        lMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    void runBallPickerTime() {
        ballPicker.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.ballPicker.setPower(0.3);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            telemetry.addData("Ballpicker Error", e.getMessage());
        }

        this.ballPicker.setPower(0);
    }

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));
    }

    public ResqAuton.Side getSide() {
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position", "MOUNTAIN"));
    }

    enum Orientation {FORWARD, BACKWARD};
}
