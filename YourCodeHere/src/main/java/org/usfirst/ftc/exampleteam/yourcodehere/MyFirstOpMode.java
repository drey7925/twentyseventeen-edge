package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;

/**
 * A skeletal example of a do-nothing first OpMode. Go ahead and change this code
 * to suit your needs, or create sibling OpModes adjacent to this one in the same
 * Java package.
 */
@TeleOp(name = "My Very First OpMode")
public class MyFirstOpMode extends SynchronousOpMode {
    /* Declare here any fields you might find useful. */
    DcMotor motorLeft = null;
    DcMotor motorRight = null;

    /*
    DcMotor catapult = null;
        DcMotor linearSlideOne = null;
        DcMotor linearSlideTwo = null;
        Servo buttonPusher = null;
        Servo ballPicker = null;
        */

    @Override
    public void main() throws InterruptedException {
        /* Initialize our hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names you assigned during the robot configuration
         * step you did in the FTC Robot Controller app on the phone.
         */
        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight");
            /*
            this.catapult = this.hardwareMap.dcMotor.get("catapult");
            this.linearSlideOne = this.hardwareMap.dcMotor.get("linearSlideOne");
            this.linearSlideTwo = this.hardwareMap.dcMotor.get("linearSlideTwo");
            this.buttonPusher = this.hardwareMap.servo.get("buttonPusher");
            this.ballPicker = this.hardwareMap.servo.get("ballPicker");
            */
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setDirection(DcMotor.Direction.REVERSE);
        float adjustedRightPower = this.gamepad1.right_stick_y;
        float adjustedLeftPower = this.gamepad1.left_stick_y;
        long cycleStartTime = System.currentTimeMillis();
        double decelerationTime = 0.25; //in seconds
        double topSpeedRatio = 0.5;

        // Wait for the game to start
        waitForStart();
        // Go go gadget robot!
        while (opModeIsActive()) {
            this.idle();
            boolean update = telemetry.update();
            this.updateGamepads();

            if (adjustedLeftPower > this.gamepad1.left_stick_y + 0.05) {
                adjustedLeftPower = (adjustedLeftPower - (System.currentTimeMillis()-cycleStartTime)/1000*(float)decelerationTime)*(float)topSpeedRatio;
            }
            else if (adjustedLeftPower < this.gamepad1.left_stick_y - 0.05) {
                adjustedLeftPower = (adjustedLeftPower + (System.currentTimeMillis()-cycleStartTime)/1000*(float)decelerationTime)*(float)topSpeedRatio;
            }

            if (adjustedRightPower > this.gamepad1.right_stick_y + 0.05) {
                adjustedRightPower = (adjustedRightPower - (System.currentTimeMillis()-cycleStartTime)/1000*(float)decelerationTime)*(float)topSpeedRatio;
            }
            else if (adjustedRightPower < this.gamepad1.right_stick_y - 0.05) {
                adjustedRightPower = (adjustedRightPower + (System.currentTimeMillis()-cycleStartTime)/1000*(float)decelerationTime)*(float)topSpeedRatio;
            }

            this.motorLeft.setPower(adjustedLeftPower);
            this.motorRight.setPower(adjustedRightPower);
            cycleStartTime = System.currentTimeMillis();
        }
    }
}

