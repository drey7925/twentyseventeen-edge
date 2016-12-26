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

        this.motorLeft.setPower(driveSpeedRatio);
        this.motorRight.setPower(driveSpeedRatio);
        while(lPos<1120*revolutions && rPos < 1120*revolutions){
            telemetry.addData("Left Motor Position:",lPos);
            telemetry.addData("Right Motor Position:",rPos);
            lPos = -motorLeft.getCurrentPosition();
            rPos = -motorRight.getCurrentPosition();
            if(1120*revolutions-lPos<0) motorLeft.setPower(0);
            if(1120*revolutions-rPos<0) motorRight.setPower(0);
            if(1120*revolutions-lPos<80) motorLeft.setPower(driveSpeedRatio/2);
            if(1120*revolutions-rPos<80) motorRight.setPower(driveSpeedRatio/2);
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }

    void turnLeft(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition(-(int)(1120 * revolutions));
        this.motorRight.setTargetPosition((int)(1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(driveSpeedRatio);
        this.motorRight.setPower(driveSpeedRatio);
        telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
        telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
        telemetry.update();
    }

    void turnRight(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition((int)(1120 * revolutions));
        this.motorRight.setTargetPosition(-(int)(1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(driveSpeedRatio);
        this.motorRight.setPower(driveSpeedRatio);
        telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
        telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
        telemetry.update();
    }
}
