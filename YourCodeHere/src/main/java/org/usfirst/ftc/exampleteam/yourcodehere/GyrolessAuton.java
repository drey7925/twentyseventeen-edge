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
import org.swerverobotics.library.interfaces.Autonomous;
import resq.MatColorSpreadCallback;
import resq.ResqAuton;

/**
 * Created by hon07726 on 10/28/2016.
 */
@Autonomous(name = "Gyroless Auton")
public class GyrolessAuton extends SynchronousOpMode {
    public static final int START_SLEW_RATIO = 40;
    public static final int COUNTS_PER_ENCODER = 1120;
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

        if (teamColor.equals(ResqAuton.Colors.BLUE)) {
            goStraight(0.15);
            turnRight(0.9);
            shootCatapult();
            runBallPickerTime();
            shootCatapult();
            turnLeft(0.9);
            goStraight(0.5);
            turnRight(0.47);
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            double latestUltrasonic = ultrasonic.getUltrasonicLevel();
            while (latestUltrasonic > 20 || latestUltrasonic==0) {
                lMotor.setPower(0.25);
                rMotor.setPower(0.25);
                telemetry.addData("Is Running: ", ultrasonic.getUltrasonicLevel());
                telemetry.update();
                latestUltrasonic = ultrasonic.getUltrasonicLevel();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRight(0.62);
            int initialLightness = lightSensor.getLightDetectedRaw();
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            while (lightSensor.getLightDetectedRaw()-initialLightness < 15) {
                lMotor.setPower(-0.25);
                rMotor.setPower(-0.25);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            pressButtonSequence(Orientation.BACKWARD);
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            long curtime = System.currentTimeMillis();
            while(System.currentTimeMillis() - curtime < 100){
                lMotor.setPower(-0.18);
                rMotor.setPower(-0.18);
            }
            while (lightSensor.getLightDetectedRaw()-initialLightness < 15) {
                lMotor.setPower(-0.25);
                rMotor.setPower(-0.25);
            }
            lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            pressButtonSequence(Orientation.BACKWARD);
        }
        else if(teamColor.equals(ResqAuton.Colors.BLUE) && ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN")).equals("MIDLINE")){
            goStraight(0.15);
            turnRight(0.5);
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            double latestUltrasonic = ultrasonic.getUltrasonicLevel();
            while (latestUltrasonic > 25 || latestUltrasonic==0) {
                lMotor.setPower(0.25);
                rMotor.setPower(0.25);
                telemetry.addData("Is Running: ", ultrasonic.getUltrasonicLevel());
                telemetry.update();
                latestUltrasonic = ultrasonic.getUltrasonicLevel();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRight(0.7);
            int initialLightness = lightSensor.getLightDetectedRaw();
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            while (lightSensor.getLightDetectedRaw()-initialLightness < 15) {
                lMotor.setPower(-0.25);
                rMotor.setPower(-0.25);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            pressButtonSequence(Orientation.BACKWARD);
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            while (lightSensor.getLightDetectedRaw()-initialLightness < 15) {
                lMotor.setPower(-0.25);
                rMotor.setPower(-0.25);
            }
            lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            pressButtonSequence(Orientation.BACKWARD);
        }
        else if (teamColor.equals(ResqAuton.Colors.RED)) {
            goStraight(0.15);
            turnRight(0.9);
            shootCatapult();
            runBallPickerTime();
            shootCatapult();
            turnLeft(0.9);
            goStraight(0.5);
            turnRight(0.5);
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            double latestUltrasonic = ultrasonic.getUltrasonicLevel();
            while (latestUltrasonic > 25 || latestUltrasonic==0) {
                lMotor.setPower(0.25);
                rMotor.setPower(0.25);
                telemetry.addData("Is Running: ", ultrasonic.getUltrasonicLevel());
                telemetry.update();
                latestUltrasonic = ultrasonic.getUltrasonicLevel();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRight(0.7);
            int initialLightness = lightSensor.getLightDetectedRaw();
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            while (lightSensor.getLightDetectedRaw()-initialLightness < 15) {
                lMotor.setPower(0.25);
                rMotor.setPower(0.25);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            pressButtonSequence(Orientation.FORWARD);
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            while (lightSensor.getLightDetectedRaw()-initialLightness < 15) {
                lMotor.setPower(0.25);
                rMotor.setPower(0.25);
            }
            pressButtonSequence(Orientation.FORWARD);
        }

    }

    void goStraight(double revolutions) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        if (revolutions < 0) {
            lMotor.setDirection(DcMotor.Direction.FORWARD);
            rMotor.setDirection(DcMotor.Direction.REVERSE);
        }
        lMotor.setTargetPosition((int) (revolutions * COUNTS_PER_ENCODER));
        rMotor.setTargetPosition((int) (revolutions * COUNTS_PER_ENCODER));
        this.lMotor.setPower(DRIVE_SPEED_RATIO / 2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO / 2);
        while (lPos < COUNTS_PER_ENCODER * Math.abs(revolutions) && rPos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + rPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - rPos) / COUNTS_PER_ENCODER));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + lPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - lPos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - lPos < 0) lMotor.setPower(0);
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - rPos < 0) rMotor.setPower(0);
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
        lPos = -lMotor.getCurrentPosition();
        rPos = -rMotor.getCurrentPosition();
        telemetry.addData("Left Overshoot", lPos - COUNTS_PER_ENCODER * Math.abs(revolutions));
        telemetry.addData("Right Overshoot", rPos - COUNTS_PER_ENCODER * Math.abs(revolutions));
        telemetry.update();
        this.lMotor.setDirection(DcMotor.Direction.REVERSE);
        this.rMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    void turnLeft(double revolutions) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        lMotor.setDirection(DcMotor.Direction.FORWARD);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        lMotor.setTargetPosition((int) (revolutions * COUNTS_PER_ENCODER));
        rMotor.setTargetPosition((int) (revolutions * COUNTS_PER_ENCODER));


        this.lMotor.setPower(DRIVE_SPEED_RATIO / 2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO / 2);
        while (lPos < COUNTS_PER_ENCODER * Math.abs(revolutions) && rPos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + rPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - rPos) / COUNTS_PER_ENCODER));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + lPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - lPos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - lPos < 0) lMotor.setPower(0);
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - rPos < 0) rMotor.setPower(0);
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
        this.lMotor.setDirection(DcMotor.Direction.REVERSE);
        this.rMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    void turnRight(double revolutions) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rMotor.setDirection(DcMotor.Direction.REVERSE);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        lMotor.setTargetPosition((int) (revolutions * COUNTS_PER_ENCODER));
        rMotor.setTargetPosition((int) (revolutions * COUNTS_PER_ENCODER));

        this.lMotor.setPower(DRIVE_SPEED_RATIO / 2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO / 2);
        while (lPos < COUNTS_PER_ENCODER * Math.abs(revolutions) && rPos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + rPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - rPos) / COUNTS_PER_ENCODER));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + lPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - lPos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - lPos < 0) lMotor.setPower(0);
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - rPos < 0) rMotor.setPower(0);
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
        this.lMotor.setDirection(DcMotor.Direction.REVERSE);
        this.rMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    void slideLeft(double revolutions) {
        this.centerOmni.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int pos = -centerOmni.getCurrentPosition();
        this.centerOmni.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (revolutions < 0) {centerOmni.setDirection(DcMotor.Direction.REVERSE);}
        this.centerOmni.setPower(0.5);
        int ticks = 0;
        while (pos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            telemetry.addData("Omni Position:", pos);
            pos = -lMotor.getCurrentPosition();
            ticks++;
            centerOmni.setPower(Math.min(Math.min(Math.max(OMNI_SPEED_RATIO * (560 + pos) / COUNTS_PER_ENCODER, ticks / START_SLEW_RATIO), OMNI_SPEED_RATIO), OMNI_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - pos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - pos < 0) centerOmni.setPower(0);
            telemetry.update();
        }
        centerOmni.setPower(0);
        centerOmni.setDirection(DcMotor.Direction.FORWARD);
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

    void pressButton () {
        buttonPusher.setPosition(0);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()-startTime > 500) {}
        buttonPusher.setPosition(1);
        while (System.currentTimeMillis()-startTime > 700) {}
    }

    void pressButtonSequence (Orientation direction) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (direction == Orientation.FORWARD) {
            rMotor.setDirection(DcMotor.Direction.FORWARD);
            lMotor.setDirection(DcMotor.Direction.REVERSE);
        } else {
            rMotor.setDirection(DcMotor.Direction.REVERSE);
            lMotor.setDirection(DcMotor.Direction.FORWARD);
        }
        //double initialWhiteness = colorSensorWhiteness();
        double rStartPos = rMotor.getCurrentPosition();
        double lStartPos = lMotor.getCurrentPosition();
        if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.05);}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.05);}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.05);}
        else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.05);}
        else {
            telemetry.addData("Two Beacon Colors Not Detected", "Keep going");
            telemetry.update();
            lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            this.lMotor.setDirection(DcMotor.Direction.REVERSE);
            this.rMotor.setDirection(DcMotor.Direction.FORWARD);
            lMotor.setPower(0.17);
            rMotor.setPower(0.17);
            long startTime = System.currentTimeMillis();
            while (true) {
                if (cb.getState().equals("RB") || cb.getState().equals("BR")) {
                    lMotor.setPower(0);
                    rMotor.setPower(0);
                    if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.05);}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.05);}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.05);}
                    else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.05);}
                    break;
                }
                if (System.currentTimeMillis()-startTime > 3000) {
                    break;
                }
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
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
