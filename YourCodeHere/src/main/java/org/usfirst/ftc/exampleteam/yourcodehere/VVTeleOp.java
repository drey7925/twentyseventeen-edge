package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
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
    @Override
    public void main() throws InterruptedException {
        /* Initialize our hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names you assigned during the robot configuration
         * step you did in the FTC Robot Controller app on the phone.
         */
        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight"); //instantiates
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.ballPicker = this.hardwareMap.dcMotor.get("ballpicker");
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION); //sets the mode for each motor
        this.motorRight.setDirection(DcMotor.Direction.REVERSE);
        this.catapult.setDirection(DcMotor.Direction.REVERSE);
        //this.ballPicker.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        int initialCatapultPosition = catapult.getCurrentPosition();

        double driveSpeedRatio = 0.5; //sets the top speed for drive train
        double catapultSpeed = 0.25; //sets top catapult speed
        double ballPickerSpeed = 0.25; //sets top ball picker speed
        // Wait for the game to start
        waitForStart();
        while (opModeIsActive()) {
            this.updateGamepads();  //updates game pads

            this.motorLeft.setPower(this.gamepad1.left_stick_y * driveSpeedRatio); //sets power to motor left
            this.motorRight.setPower(this.gamepad1.right_stick_y * driveSpeedRatio); //sets power to motor right

            if(this.catapult.getPower()==0 && this.gamepad1.right_bumper){      //
                this.catapult.setPower(catapultSpeed);                          //starts the catapult cycle
                telemetry.addData("Catapult Running: ", "yuppo");
                telemetry.update();
            }                                                                   //
            if(this.catapult.getCurrentPosition()>=initialCatapultPosition+1120){    //
                this.catapult.setPower(0);                                          //stops the catapult cycle
                telemetry.addData("Stop Catapult: ", "yuppo");
                telemetry.update();
                initialCatapultPosition = catapult.getCurrentPosition();            //
            }

            telemetry.addData("Catapult Speed: ", catapult.getPower());

            if (this.gamepad1.left_bumper) {                //
                this.ballPicker.setPower(ballPickerSpeed);  //
            }                                               // sets ball picker speed based on left bumper
            else {                                          //
                this.ballPicker.setPower(0);                //
            }

            telemetry.addData("position: ", catapult.getCurrentPosition());
            telemetry.addData("right stick: ", this.gamepad1.right_stick_y);
            telemetry.addData("left stick: ", this.gamepad1.left_stick_y);
            boolean update = telemetry.update(); //does important background stuff at the end of each loop
            this.idle(); //does more important background stuff at the end of each loop

        }
    }
}

