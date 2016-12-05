package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by Gabriel Kammer on 10/17/16
 */
@TeleOp(name = "Velocity Vortex Official Tele-Op Mode")
public class VVTeleOp extends SynchronousOpMode {
    /* Declare here any fields you might find useful. */
    DcMotor motorLeft = null;
    DcMotor motorRight = null; //declares motors
    DcMotor catapult = null;
    DcMotor ballPicker = null;
    Servo buttonPusher = null;
    @Override
    public void main() throws InterruptedException {
        /* Initialize our hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names you assigned during the robot configuration
         * step you did in the FTC Robot Controller app on the phone.
         */
        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight"); //instantiates
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.ballPicker = this.hardwareMap.dcMotor.get("ballPicker");
        this.buttonPusher = this.hardwareMap.servo.get("buttonPusher");
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);

        //this.ballPicker.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        double driveSpeedRatio = 0.5; //sets the top speed for drive train
        double correctedSpeedRatio = driveSpeedRatio; //sets a correction factor for accuracy mode
        double catapultSpeed = 0.25; //sets top catapult speed
        double ballPickerSpeed = 0.25; //sets top ball picker speed
        // Wait for the game to start
        waitForStart();
        while (opModeIsActive()) {
            this.updateGamepads();  //updates game pads
            if (this.gamepad1.right_trigger>0.5) {
                correctedSpeedRatio = 0.25;
            }
            else {
                correctedSpeedRatio = driveSpeedRatio;
            }
            this.motorLeft.setPower(-this.gamepad1.left_stick_y * correctedSpeedRatio); //sets power to motor left
            this.motorRight.setPower(-this.gamepad1.right_stick_y * correctedSpeedRatio); //sets power to motor right

          //  telemetry.addData("Left Power: ",this.motorLeft.getPower());
          //  telemetry.addData("Right Power: ", this.motorRight.getPower());

            if(this.catapult.getPower()==0 && this.gamepad2.right_bumper){
                this.catapult.setMode(DcMotorController.RunMode.RESET_ENCODERS);
                this.catapult.setTargetPosition((int)(1120*2.5));
                this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION);//starts the catapult cycle
                this.catapult.setPower(catapultSpeed);
            }
            if(Math.abs(this.catapult.getCurrentPosition()) > Math.abs(this.catapult.getTargetPosition())) {
                this.catapult.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
                this.catapult.setPower(0);
            }
            if (this.gamepad2.left_bumper) {                //
                this.ballPicker.setPower(ballPickerSpeed);  //
            }// sets ball picker speed based on left bumper
            else {                                          //
                this.ballPicker.setPower(0);                //
            }
            if(this.gamepad2.left_trigger>0.5){
                this.ballPicker.setPower(-ballPickerSpeed);
            }
            buttonPusher.setPosition(this.gamepad2.right_trigger>0.5 ? 0.091 : 0.365);
            telemetry.update(); //does important background stuff at the end of each loop
            this.idle(); //does more important background stuff at the end of each loop

        }
    }
}

