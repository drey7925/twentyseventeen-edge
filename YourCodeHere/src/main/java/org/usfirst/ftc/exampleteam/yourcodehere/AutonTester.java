package org.usfirst.ftc.exampleteam.yourcodehere;

import android.app.Activity;
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

        // Wait for the game to start
        waitForStart();
        while (opModeIsActive()) {
            this.updateGamepads();

            //RUN SELECTED METHOD
            if (gamepad1.x) {
                if (method == 0) {goStraight(revolutions[0]);}
                else if (method == 1) {turnLeft(revolutions[1]);}
                else if (method == 2) {turnRight(revolutions[2]);}
                else if (method == 3) {slideLeft(revolutions[3]);}
            }

            //SWEEPER TEST
            if (gamepad1.dpad_up) {lSweeper.setPosition(lSweeperPosition+=0.01);}
            else if (gamepad1.dpad_down) {lSweeper.setPosition(lSweeperPosition-=0.01);}
            if (gamepad1.y) {rSweeper.setPosition(rSweeperPosition+=0.01);}
            else if (gamepad1.a) {rSweeper.setPosition(rSweeperPosition-=0.01);}

            //CHANGE NUMBER OF REVOLUTIONS
            if (gamepad1.left_stick_y > 0.5) {revolutions[method]+=0.01;}
            else if (gamepad1.left_stick_y < -0.5) {revolutions[method]-=0.01;}

            //CHANGE METHOD
            if (gamepad1.right_stick_y > 0.5) {method = (method+1)%4;}
            else if (gamepad1.right_stick_y < -0.5) {method = (method+3)%4;}

            //TELEMETRY
            if (method == 0) {
                telemetry.addData("GO STRAIGHT: ", revolutions[0]);
                telemetry.addData("turn left: ", revolutions[1]);
                telemetry.addData("turn right: ", revolutions[2]);
                telemetry.addData("slide left: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.update();
            } else if (method == 1) {
                telemetry.addData("go straight: ", revolutions[0]);
                telemetry.addData("TURN LEFT: ", revolutions[1]);
                telemetry.addData("turn right: ", revolutions[2]);
                telemetry.addData("slide left: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.update();
            } else if (method == 2) {
                telemetry.addData("go straight: ", revolutions[0]);
                telemetry.addData("turn left: ", revolutions[1]);
                telemetry.addData("TURN RIGHT: ", revolutions[2]);
                telemetry.addData("slide left: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.update();
            } else if (method == 3) {
                telemetry.addData("go straight: ", revolutions[0]);
                telemetry.addData("turn left: ", revolutions[1]);
                telemetry.addData("turn right: ", revolutions[2]);
                telemetry.addData("SLIDE LEFT: ", revolutions[3]);
                telemetry.addData("Right Sweeper Pos: ", rSweeperPosition);
                telemetry.addData("Left Sweeper Pos: ", lSweeperPosition);
                telemetry.update();
            }
            Thread.sleep(100);
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

        this.lMotor.setPower(DRIVE_SPEED_RATIO/2);
        this.rMotor.setPower(DRIVE_SPEED_RATIO/2);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();

            rMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO*(560+rPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-rPos)/1120));
            lMotor.setPower(Math.min(Math.min(DRIVE_SPEED_RATIO*(560+lPos)/1120, DRIVE_SPEED_RATIO),DRIVE_SPEED_RATIO*(560+revolutions*1120-lPos)/1120));
            if(1120*Math.abs(revolutions)-lPos<0) lMotor.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) rMotor.setPower(0);
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
        buttonPusher.setPosition(0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buttonPusher.setPosition(1);
    }

    void pressButtonSequence (GyrolessAuton.Direction direction) {
        this.lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -lMotor.getCurrentPosition();
        int rPos = -rMotor.getCurrentPosition();
        this.lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (direction == GyrolessAuton.Direction.FORWARD) {
            rMotor.setDirection(DcMotor.Direction.FORWARD);
            lMotor.setDirection(DcMotor.Direction.REVERSE);
        } else {
            rMotor.setDirection(DcMotor.Direction.REVERSE);
            lMotor.setDirection(DcMotor.Direction.FORWARD);
        }
        double motorPower = 0;
        //double initialWhiteness = colorSensorWhiteness();
        while (lPos == lMotor.getCurrentPosition() || rPos == rMotor.getCurrentPosition()) {
            telemetry.addData("Left Motor Position:", lPos);
            telemetry.addData("Right Motor Position:", rPos);
            lPos = -lMotor.getCurrentPosition();
            rPos = -rMotor.getCurrentPosition();
            motorPower += 0.01;
            rMotor.setPower(motorPower);
            lMotor.setPower(motorPower);
            telemetry.update();
        }
        while (true) {
            if (cb.equals("BR")||cb.equals("RB")) {
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
        if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.1);}
        else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.1);}
        else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
        else if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.1);}
        else if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
        else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.1);}
        else {
            telemetry.addData("welp message: ", "oh shet boi");
            telemetry.update();
            lMotor.setPower(motorPower);
            rMotor.setPower(motorPower);
            int iterations = 0;
            while (true) {
                iterations++;
                if (cb.equals("RB") && cb.equals("BR")) {
                    if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(0.1);}
                    else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {goStraight(-0.1);}
                    else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.RED)) {}
                    else if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.BACKWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(0.1);}
                    else if (cb.equals("RB") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {}
                    else if (cb.equals("BR") && direction.equals(GyrolessAuton.Direction.FORWARD) && teamColor.equals(ResqAuton.Colors.BLUE)) {goStraight(-0.1);}
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
}
