package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by kam07440 on 12/26/2016.
 */
@TeleOp(name = "Auton Tester Class")
public class AutonTester extends SynchronousOpMode {
    protected DcMotor motorLeft = null;
    protected DcMotor motorRight = null;
    double driveSpeedRatio = 0.5; //sets the top speed for drive train
    @Override
    public void main() throws InterruptedException {
        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight");
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);

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
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -motorLeft.getCurrentPosition();
        int rPos = -motorRight.getCurrentPosition();
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        if (revolutions < 0) {
            motorLeft.setDirection(DcMotor.Direction.FORWARD);
            motorRight.setDirection(DcMotor.Direction.REVERSE);
        }

        this.motorLeft.setPower(driveSpeedRatio/2);
        this.motorRight.setPower(driveSpeedRatio/2);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -motorLeft.getCurrentPosition();
            rPos = -motorRight.getCurrentPosition();

            motorRight.setPower(Math.min(Math.min(driveSpeedRatio*(560+rPos)/1120, driveSpeedRatio),driveSpeedRatio*(560+revolutions*1120-rPos)/1120));
            motorLeft.setPower(Math.min(Math.min(driveSpeedRatio*(560+lPos)/1120, driveSpeedRatio),driveSpeedRatio*(560+revolutions*1120-lPos)/1120));
            if(1120*Math.abs(revolutions)-lPos<0) motorLeft.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) motorRight.setPower(0);
            //if(1120*Math.abs(revolutions)-lPos<80) motorLeft.setPower(driveSpeedRatio/2);
            //if(1120*Math.abs(revolutions)-rPos<80) motorRight.setPower(driveSpeedRatio/2);
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
        if (revolutions < 0) {
            motorLeft.setDirection(DcMotor.Direction.REVERSE);
            motorRight.setDirection(DcMotor.Direction.FORWARD);
        }
    }


    void turnLeft(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -motorLeft.getCurrentPosition();
        int rPos = -motorRight.getCurrentPosition();
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeft.setDirection(DcMotor.Direction.FORWARD);

        this.motorLeft.setPower(driveSpeedRatio);
        this.motorRight.setPower(driveSpeedRatio);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -motorLeft.getCurrentPosition();
            rPos = -motorRight.getCurrentPosition();
            motorRight.setPower(Math.min(Math.min(driveSpeedRatio*(560+rPos)/1120, driveSpeedRatio),driveSpeedRatio*(560+revolutions*1120-rPos)/1120));
            motorLeft.setPower(Math.min(Math.min(driveSpeedRatio*(560+lPos)/1120, driveSpeedRatio),driveSpeedRatio*(560+revolutions*1120-lPos)/1120));
            if(1120*Math.abs(revolutions)-lPos<0) motorLeft.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) motorRight.setPower(0);
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    void turnRight(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        int lPos = -motorLeft.getCurrentPosition();
        int rPos = -motorRight.getCurrentPosition();
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setDirection(DcMotor.Direction.REVERSE);

        this.motorLeft.setPower(driveSpeedRatio);
        this.motorRight.setPower(driveSpeedRatio);
        while(lPos<1120*Math.abs(revolutions) && rPos < 1120*Math.abs(revolutions)){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -motorLeft.getCurrentPosition();
            rPos = -motorRight.getCurrentPosition();
            motorRight.setPower(Math.min(Math.min(driveSpeedRatio*(560+rPos)/1120, driveSpeedRatio),driveSpeedRatio*(560+revolutions*1120-rPos)/1120));
            motorLeft.setPower(Math.min(Math.min(driveSpeedRatio*(560+lPos)/1120, driveSpeedRatio),driveSpeedRatio*(560+revolutions*1120-lPos)/1120));
            if(1120*Math.abs(revolutions)-lPos<0) motorLeft.setPower(0);
            if(1120*Math.abs(revolutions)-rPos<0) motorRight.setPower(0);
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
        motorRight.setDirection(DcMotor.Direction.FORWARD);
    }
}
