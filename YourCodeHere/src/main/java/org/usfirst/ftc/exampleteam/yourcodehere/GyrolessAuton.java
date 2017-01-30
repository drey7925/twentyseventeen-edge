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

    double DRIVE_SPEED_RATIO = 0.35; //sets the top speed for drive train
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
        startCamera();

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
        this.startCamera();
        this.waitForStart();

        lSweeper.setPosition(.40);
        rSweeper.setPosition(.30);
        buttonPusher.setPosition(0);

        if (teamColor.equals(ResqAuton.Colors.BLUE)) {
            int initialLightness = lightSensor.getLightDetectedRaw();
            goStraight(0.15);
            turnRight(0.8);
            shootCatapult();
            runBallPickerTime();
            shootCatapult();
            turnLeft(0.8);
            goStraight(0.5);
            turnRight(0.55);
            while (ultrasonic.getUltrasonicLevel() > 10 || ultrasonic.getUltrasonicLevel()==0) {
                lMotor.setPower(0.2);
                rMotor.setPower(0.2);
                telemetry.addData("Is Running: ", opModeIsActive());
                telemetry.update();
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            turnRight(0.7);
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(-0.2);
                rMotor.setPower(-0.2);
            }
            lMotor.setPower(0);
            rMotor.setPower(0);
            pressButtonSequence(Orientation.BACKWARD);
            while (Math.abs(lightSensor.getLightDetectedRaw()-initialLightness) < 10) {
                lMotor.setPower(-0.2);
                rMotor.setPower(-0.2);
            }
            pressButtonSequence(Orientation.BACKWARD);

        } else if (teamColor.equals(ResqAuton.Colors.RED)) {

        }

    }

    void goStraight(double revolutions) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (revolutions < 0) {
            lMotor.setDirection(DcMotor.Direction.FORWARD);
            rMotor.setDirection(DcMotor.Direction.REVERSE);
        }
        this.lMotor.setPower(DRIVE_SPEED_RATIO / 2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO / 2);
        int ticks = 0;
        while (lPos < COUNTS_PER_ENCODER * Math.abs(revolutions) && rPos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            telemetry.addData("Left Motor Position:", lPos);
            telemetry.addData("Right Motor Position:", rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            ticks++;
            rMotor.setPower(Math.min(Math.min(Math.max(DRIVE_SPEED_RATIO * (560 + rPos) / COUNTS_PER_ENCODER, ticks / START_SLEW_RATIO), DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - rPos) / COUNTS_PER_ENCODER));
            lMotor.setPower(Math.min(Math.min(Math.max(DRIVE_SPEED_RATIO * (560 + lPos) / COUNTS_PER_ENCODER, ticks / START_SLEW_RATIO), DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - lPos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - lPos < 0) lMotor.setPower(0);
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - rPos < 0) rMotor.setPower(0);
            //if(1120*Math.abs(revolutions)-lPos<80) lMotor.setPower(driveSpeedRatio/2);
            //if(1120*Math.abs(revolutions)-rPos<80) rMotor.setPower(driveSpeedRatio/2);
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
        if (revolutions < 0) {
            lMotor.setDirection(DcMotor.Direction.REVERSE);
            rMotor.setDirection(DcMotor.Direction.FORWARD);
        }
    }


    void turnLeft(double revolutions) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        lMotor.setDirection(DcMotor.Direction.FORWARD);
        this.lMotor.setPower(DRIVE_SPEED_RATIO / 2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO / 2);
        int ticks = 0;
        while (lPos < COUNTS_PER_ENCODER * Math.abs(revolutions) && rPos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            telemetry.addData("Left Motor Position:", lPos);
            telemetry.addData("Right Motor Position:", rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            ticks++;
            rMotor.setPower(Math.min(Math.min(Math.max(DRIVE_SPEED_RATIO * (560 + rPos) / COUNTS_PER_ENCODER, ticks / START_SLEW_RATIO), DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - rPos) / COUNTS_PER_ENCODER));
            lMotor.setPower(Math.min(Math.min(Math.max(DRIVE_SPEED_RATIO * (560 + lPos) / COUNTS_PER_ENCODER, ticks / START_SLEW_RATIO), DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - lPos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - lPos < 0) lMotor.setPower(0);
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - rPos < 0) rMotor.setPower(0);
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
        lMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    void turnRight(double revolutions) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rMotor.setDirection(DcMotor.Direction.REVERSE);
        this.lMotor.setPower(DRIVE_SPEED_RATIO / 2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO / 2);
        while (lPos < COUNTS_PER_ENCODER * Math.abs(revolutions) && rPos < COUNTS_PER_ENCODER * Math.abs(revolutions)) {
            telemetry.addData("Left Motor Position:", lPos);
            telemetry.addData("Right Motor Position:", rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + rPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - rPos) / COUNTS_PER_ENCODER));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO * (560 + lPos) / COUNTS_PER_ENCODER, DRIVE_SPEED_RATIO), DRIVE_SPEED_RATIO * (560 + revolutions * COUNTS_PER_ENCODER - lPos) / COUNTS_PER_ENCODER));
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - lPos < 0) lMotor.setPower(0);
            if (COUNTS_PER_ENCODER * Math.abs(revolutions) - rPos < 0) rMotor.setPower(0);
            telemetry.update();
        }
        lMotor.setPower(0);
        rMotor.setPower(0);
        rMotor.setDirection(DcMotor.Direction.FORWARD);
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

    void runBallPicker() {
        this.ballPicker.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.ballPicker.setTargetPosition(2240);
        this.ballPicker.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.ballPicker.setPower(0.5);
        while (Math.abs(ballPicker.getCurrentPosition()) < Math.abs(ballPicker.getTargetPosition())) {
            telemetry.addData("Ball Picker Position: ", ballPicker.getCurrentPosition());
            telemetry.update();
        }
        this.ballPicker.setPower(0);
    }

    void pressButton () {
        buttonPusher.setPosition(0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buttonPusher.setPosition(1);
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
        double motorPower = 0;
        //double initialWhiteness = colorSensorWhiteness();
        double rStartPos = rMotor.getCurrentPosition();
        double lStartPos = lMotor.getCurrentPosition();
        while (lStartPos == lMotor.getCurrentPosition() || rStartPos == rMotor.getCurrentPosition()) {
            motorPower += 0.001;
            rMotor.setPower(motorPower);
            lMotor.setPower(motorPower);
        }
        rMotor.setPower(motorPower-0.005);
        lMotor.setPower(motorPower-0.005);
        while (true) {
            if (cb.getState().equals("BR")||cb.getState().equals("RB")) {
                rMotor.setPower(0);
                lMotor.setPower(0);
                break;
            }
           /* if (colorSensorWhiteness() > initialWhiteness + 30) {
                rMotor.setPower(0);
                lMotor.setPower(0);
                break;
            }*/
        }
        if (cb.equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.1);}
        else if (cb.equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.1);}
        else if (cb.equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.1);}
        else if (cb.equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.1);}
        else {
            telemetry.addData("Robot Moved: ", "Keep going");
            telemetry.update();
            lMotor.setPower(motorPower);
            rMotor.setPower(motorPower);
            int iterations = 0;
            while (true) {
                iterations++;
                if (cb.equals("RB") && cb.equals("BR")) {
                    if (cb.equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.1);}
                    else if (cb.equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.1);}
                    else if (cb.equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.1);}
                    else if (cb.equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.1);}
                    break;
                }
                if (iterations > 100) {
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

    double colorSensorWhiteness () {return ((cSensor.red()+cSensor.blue()+cSensor.green())/3);}

    enum Orientation {FORWARD, BACKWARD};

}
