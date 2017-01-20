package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by kam07440 on 12/26/2016.
 */
@TeleOp(name = "Auton Tester Class")
public class AutonTester extends SynchronousOpMode {
    protected DcMotor lMotor = null;
    protected DcMotor rMotor = null;
    protected DcMotor centerOmni = null;
    double DRIVE_SPEED_RATIO = 0.5; //sets the top speed for drive train
    double OMNI_SPEED_RATIO = 1;
    int COUNTS_PER_ENCODER = 1120;
    double START_SLEW_RATIO = 40;

    @Override
    public void main() throws InterruptedException {
        this.lMotor = this.hardwareMap.dcMotor.get("lMotor");
        this.rMotor = this.hardwareMap.dcMotor.get("rMotor");
        this.centerOmni = this.hardwareMap.dcMotor.get("centerOmni");
        this.lMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.rMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.lMotor.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start
        waitForStart();
        while (opModeIsActive()) {
            this.updateGamepads();

            if (gamepad1.y) {
                goStraight(2);
            }
            if (gamepad1.b) {
                turnRight(1);
            }
            if (gamepad1.a) {
                goStraight(-2);
            }
            if (gamepad1.x) {
                turnLeft(1);
            }
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
}
