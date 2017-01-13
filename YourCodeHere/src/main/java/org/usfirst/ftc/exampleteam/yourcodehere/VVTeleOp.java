package org.usfirst.ftc.exampleteam.yourcodehere;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by Gabriel Kammer on 10/17/16
 */
@TeleOp(name = "Velocity Vortex Official Tele-Op Mode")
public class VVTeleOp extends SynchronousOpMode {
    /* Declare here any fields you might find useful. */
    protected DcMotor motorLeft = null;
    protected DcMotor motorRight = null; //declares motors
    protected DcMotor catapult = null;
    protected DcMotor ballPicker = null;
    protected Servo buttonPusher = null;
    protected UltrasonicSensor ultrasonic = null;
    protected ColorSensor cSensor = null;
    protected Servo lSweeper = null;
    protected Servo rSweeper = null;

    @Override
    public void main() throws InterruptedException {
        /* Initialize our hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names you assigned during the robot configuration
         * step you did in the FTC Robot Controller app on the phone.
         */
        this.motorLeft = this.hardwareMap.dcMotor.get("lMotor");
        this.motorRight = this.hardwareMap.dcMotor.get("rMotor"); //instantiates
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.ballPicker = this.hardwareMap.dcMotor.get("ballPicker");
        this.buttonPusher = this.hardwareMap.servo.get("buttonPusher");
        this.ultrasonic = this.hardwareMap.ultrasonicSensor.get("ultrasonic");
        this.cSensor = this.hardwareMap.colorSensor.get("cSensor");
        this.lSweeper = this.hardwareMap.servo.get("lSweeper");
        this.rSweeper = this.hardwareMap.servo.get("rSweeper");


        this.buttonPusher.setDirection(Servo.Direction.REVERSE);
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);


        double driveSpeedRatio = 0.5; //sets the top speed for drive train
        double correctedSpeedRatio = driveSpeedRatio; //sets a correction factor for accuracy mode
        double catapultSpeed = 0.25; //sets top catapult speed
        double ballPickerSpeed = 0.25; //sets top ball picker speed
        double buttonPusherPosition = 1;
        double lSweeperPosition = 0;
        double rSweeperPosition = 0;

        double sweeperSafePos = .5;

        // Wait for the game to start
        waitForStart();
        while (opModeIsActive()) {
            this.updateGamepads();  //updates game pads
            if (this.gamepad1.right_trigger > 0.5) {
                correctedSpeedRatio = 0.25;
            } else {
                correctedSpeedRatio = driveSpeedRatio;
            }
            this.motorLeft.setPower(-this.gamepad1.left_stick_y * correctedSpeedRatio); //sets power to motor left
            this.motorRight.setPower(-this.gamepad1.right_stick_y * correctedSpeedRatio); //sets power to motor right

            if (this.gamepad2.right_bumper) {
                this.catapult.setPower(catapultSpeed);
            }                     // sets catapult speed based on right controls
            else if (this.gamepad2.right_trigger > 0.5) {
                this.catapult.setPower(-catapultSpeed);
            } else {
                this.catapult.setPower(0);
            }

            if (this.gamepad2.left_bumper) {
                this.ballPicker.setPower(ballPickerSpeed);
                lSweeper.setPosition(.70);
                rSweeper.setPosition(.30);
            }                // sets ball picker speed based on left controls
            else if (this.gamepad2.left_trigger > 0.5) {
                this.ballPicker.setPower(-ballPickerSpeed);
                lSweeper.setPosition(.70);
                rSweeper.setPosition(.30);
            } else {
                this.ballPicker.setPower(0);
            }                //

            if (gamepad2.dpad_up) {
                buttonPusherPosition = Math.max(0, buttonPusherPosition - 0.01);
                buttonPusher.setPosition(buttonPusherPosition);
            } else if (gamepad2.dpad_down) {
                buttonPusherPosition = Math.min(1, buttonPusherPosition + 0.01);
                buttonPusher.setPosition(buttonPusherPosition);
            }

            if (gamepad2.left_stick_x > 0.5) {
                lSweeperPosition = Math.min(.84, lSweeperPosition + 0.01);
                lSweeper.setPosition(lSweeperPosition);
            } else if (gamepad2.left_stick_x < -0.5) {
                lSweeperPosition = Math.max(0.05, lSweeperPosition - 0.01);
                lSweeper.setPosition(lSweeperPosition);
            }
            if (gamepad2.right_stick_x > 0.5) {
                rSweeperPosition = Math.min(1, rSweeperPosition + 0.01);
                rSweeper.setPosition(rSweeperPosition);
            } else if (gamepad2.right_stick_x < -0.5) {
                rSweeperPosition = Math.max(0.17, rSweeperPosition - 0.01);
                rSweeper.setPosition(rSweeperPosition);
            }

            telemetry.addData("Left Sweeper: ", lSweeperPosition);
            telemetry.addData("Right Sweeper: ", rSweeperPosition);
            telemetry.addData("Red: ", cSensor.red());
            telemetry.addData("Green: ", cSensor.green());
            telemetry.addData("Blue: ", cSensor.blue());
            telemetry.addData("Ultrasonic: ", ultrasonic.getUltrasonicLevel());
            telemetry.update();


            //CODE FOR SMALL NUDGE MOVEMENTS:

            if (this.gamepad1.right_bumper) {
                this.motorLeft.setPower(driveSpeedRatio);
                this.motorRight.setPower(-driveSpeedRatio);
                Thread.sleep(100);
                this.motorLeft.setPower(0);
                this.motorRight.setPower(0);
            }
            if (this.gamepad1.left_bumper) {
                this.motorLeft.setPower(-driveSpeedRatio);
                this.motorRight.setPower(driveSpeedRatio);
                Thread.sleep(100);
                this.motorLeft.setPower(0);
                this.motorRight.setPower(0);
            }
        }
    }
}

