package org.usfirst.ftc.exampleteam.yourcodehere;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;
import resq.MatColorSpreadCallback;
import resq.ResqAuton;

import java.util.ArrayList;

/**
 * Created by kam07440 on 12/26/2016.
 */
@TeleOp(name = "Auton Tester Class")
public class AutonTester extends SynchronousOpMode {
    protected DcMotor lMotor = null;
    protected DcMotor rMotor = null;
    protected DcMotor centerOmni = null;
    protected Servo buttonPusher = null;
    protected Servo lSweeper = null;
    protected Servo rSweeper = null;
    double DRIVE_SPEED_RATIO = 0.5; //sets the top speed for drive train
    double OMNI_SPEED_RATIO = 1;
    int COUNTS_PER_ENCODER = 1120;
    double START_SLEW_RATIO = 40;
    SharedPreferences sharedPref;
    protected ResqAuton.Colors teamColor;
    protected ResqAuton.Side startSide;

    static MatColorSpreadCallback cb;
    private OpenCvActivityHelper ocvh;

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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        try {
            teamColor = getTeam();
            startSide = getSide();
        } catch (Exception e) {
            telemetry.addData("Error: ", "something done goofed.");
            telemetry.update();
        }
        this.lMotor = this.hardwareMap.dcMotor.get("lMotor");
        this.rMotor = this.hardwareMap.dcMotor.get("rMotor");
        this.centerOmni = this.hardwareMap.dcMotor.get("centerOmni");
        this.lSweeper = this.hardwareMap.servo.get("lSweeper");
        this.rSweeper = this.hardwareMap.servo.get("rSweeper");
        this.buttonPusher = this.hardwareMap.servo.get("buttonPusher");
         this.lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.lMotor.setDirection(DcMotor.Direction.REVERSE);

        double lSweeperPosition = 0;
        double rSweeperPosition = 0;
        double[] revolutions = new double[4];
        int method = 0;
        ArrayList<double[]> autonSequence = new ArrayList<double[]>();
        int pointer = 0;

        // Wait for the game to start
        waitForStart();
        startCamera();
        while (opModeIsActive()) {
            this.updateGamepads();

            //RUN SELECTED METHOD
            if (gamepad1.x) {
                if (method == 0) {goStraight(revolutions[0]);}
                else if (method == 1) {turnLeft(revolutions[1]);}
                else if (method == 2) {turnRight(revolutions[2]);}
                else if (method == 3) {slideLeft(revolutions[3]);}
            }
            if (gamepad1.b) pressButtonSequence(Orientation.FORWARD);
            if (gamepad1.a) pressButtonSequence(Orientation.BACKWARD);


            //SWEEPER TEST
            if (gamepad1.dpad_up) lSweeper.setPosition(lSweeperPosition+=0.01);
            else if (gamepad1.dpad_down) lSweeper.setPosition(lSweeperPosition-=0.01);
            if (gamepad1.y) rSweeper.setPosition(rSweeperPosition+=0.01);
            else if (gamepad1.a) rSweeper.setPosition(rSweeperPosition-=0.01);

            //CHANGE NUMBER OF REVOLUTIONS
            if (gamepad1.left_stick_y > 0.5) revolutions[method]+=0.01;
            else if (gamepad1.left_stick_y < -0.5) revolutions[method]-=0.01;

            //CHANGE METHOD
            if (gamepad1.right_stick_y > 0.5) method = (method+1)%4;
            else if (gamepad1.right_stick_y < -0.5) method = (method+3)%4;

            //ADD METHOD TO AUTON SEQUENCE
            if (gamepad2.a) {
                autonSequence.add(new double[] {method, revolutions[method]});
            }


            //TELEMETRY
            if (method == 0) {
                telemetry.addData("GO STRAIGHT: ", revolutions[0]);
                telemetry.addData("turn left: ", revolutions[1]);
                telemetry.addData("turn right: ", revolutions[2]);
                telemetry.addData("slide left: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.addData("","");
                telemetry.update();
            } else if (method == 1) {
                telemetry.addData("go straight: ", revolutions[0]);
                telemetry.addData("TURN LEFT: ", revolutions[1]);
                telemetry.addData("turn right: ", revolutions[2]);
                telemetry.addData("slide left: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.addData("","");
                telemetry.update();
            } else if (method == 2) {
                telemetry.addData("go straight: ", revolutions[0]);
                telemetry.addData("turn left: ", revolutions[1]);
                telemetry.addData("TURN RIGHT: ", revolutions[2]);
                telemetry.addData("slide left: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.addData("","");
                telemetry.update();
            } else if (method == 3) {
                telemetry.addData("go straight: ", revolutions[0]);
                telemetry.addData("turn left: ", revolutions[1]);
                telemetry.addData("turn right: ", revolutions[2]);
                telemetry.addData("SLIDE LEFT: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.addData("","");
                telemetry.update();
            }
            int location = 0;
            for (double[] arr: autonSequence) {
                if (arr[0] == 0 && location == pointer) telemetry.addData("GO STRAIGHT: ", arr[1]);
                else if (arr[0] == 0) telemetry.addData("Go Straight: ", arr[1]);
                else if (arr[0] == 1 && location == pointer) telemetry.addData("TURN LEFT: ", arr[1]);
                else if (arr[0] == 1) telemetry.addData("Turn Left: ", arr[1]);
                else if (arr[0] == 2 && location == pointer) telemetry.addData("TURN RIGHT: ", arr[1]);
                else if (arr[0] == 2) telemetry.addData("Turn Right: ", arr[1]);
                else if (arr[0] == 3 && location == pointer) telemetry.addData("SLIDE LEFT: ", arr[1]);
                else if (arr[0] == 3) telemetry.addData("Slide Left: ", arr[1]);
                location++;
            }

            Thread.sleep(100);
        }
    }
    void goStraight(double revolutions) {
        double rSpeed = DRIVE_SPEED_RATIO/2;
        double lSpeed = DRIVE_SPEED_RATIO/2;
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (revolutions < 0) {
            this.lMotor.setDirection(DcMotor.Direction.FORWARD);
            this.rMotor.setDirection(DcMotor.Direction.REVERSE);
        }

        this.lMotor.setPower(lSpeed);
        this.rMotor.setPower(rSpeed);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            lPos = -this.lMotor.getCurrentPosition();
            rPos = -this.rMotor.getCurrentPosition();
            lSpeed = Math.min(Math.min(DRIVE_SPEED_RATIO*(560+lPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-lPos)/1120);
            rSpeed = Math.min(Math.min(DRIVE_SPEED_RATIO*(560+rPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-rPos)/1120);
            rMotor.setPower(rSpeed);
            lMotor.setPower(lSpeed);
            if(1120*Math.abs(revolutions)-lPos<0) lMotor.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) rMotor.setPower(0);
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            telemetry.addData("Left Motor Speed:",lSpeed);
            telemetry.addData("Right Motor Speed:",rSpeed);
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

        this.lMotor.setPower(DRIVE_SPEED_RATIO);
        this.rMotor.setPower(DRIVE_SPEED_RATIO);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO*(560+rPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-rPos)/1120));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO*(560+lPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-lPos)/1120));
            if(1120*Math.abs(revolutions)-lPos<0) lMotor.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) rMotor.setPower(0);
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

        this.lMotor.setPower(DRIVE_SPEED_RATIO);
        this.rMotor.setPower(DRIVE_SPEED_RATIO);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO*(560+rPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-rPos)/1120));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO*(560+lPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-lPos)/1120));
            if(1120*Math.abs(revolutions)-lPos<0) lMotor.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) rMotor.setPower(0);
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

    void pressButton () {
        buttonPusher.setPosition(1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buttonPusher.setPosition(0);
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
        double rStartPos = rMotor.getCurrentPosition();
        double lStartPos = lMotor.getCurrentPosition();
        while (lStartPos == lMotor.getCurrentPosition() || rStartPos == rMotor.getCurrentPosition()) {
            motorPower += 0.0001;
            rMotor.setPower(motorPower);
            lMotor.setPower(motorPower);
        }

        while (true) {
            if (cb.getState().equals("BR")||cb.getState().equals("RB")) {
                rMotor.setPower(0);
                lMotor.setPower(0);
                break;
            }
        }
        if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.2);}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.2);}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.2);}
        else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.2);}
        else {
            telemetry.addData("Robot Moved: ", "Keep going");
            telemetry.update();
            lMotor.setPower(motorPower);
            rMotor.setPower(motorPower);
            int iterations = 0;
            while (true) {
                iterations++;
                if (cb.getState().equals("RB") && cb.getState().equals("BR")) {
                    if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.2);}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.2);}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.getState().equals("RB") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.2);}
                    else if (cb.getState().equals("RB") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.getState().equals("BR") && direction.equals(Orientation.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.2);}
                    break;
                }
                if (iterations > 100) {
                    break;
                }
            }
        }
        telemetry.addData("DETECTED BEACON: ", true);
        telemetry.update();
        pressButton();
        rMotor.setDirection(DcMotor.Direction.FORWARD);
        lMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));
    }

    public ResqAuton.Side getSide() {
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position", "MOUNTAIN"));
    }

    enum Orientation {FORWARD, BACKWARD};
}
