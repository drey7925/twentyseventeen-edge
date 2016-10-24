package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;  //imports important stuff
import org.swerverobotics.library.interfaces.*;

/**
 * Created by Gabriel Kammer on 10/17/16
 */
@TeleOp(name = "Velocity Vortex Official Tele-Op Mode")
public class MyFirstOpMode extends SynchronousOpMode {
    /* Declare here any fields you might find useful. */
    DcMotor motorLeft = null;
    DcMotor motorRight = null; //declares motors
    DcMotor catapult = null;
    /*

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
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight"); //instantiates motors
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
            /*

            this.linearSlideOne = this.hardwareMap.dcMotor.get("linearSlideOne");
            this.linearSlideTwo = this.hardwareMap.dcMotor.get("linearSlideTwo");
            this.buttonPusher = this.hardwareMap.servo.get("buttonPusher");
            this.ballPicker = this.hardwareMap.servo.get("ballPicker");
            */
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION); //sets the mode for each motor
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);
        this.catapult.setDirection(DcMotor.Direction.REVERSE);
        int initialCatapultPosition = catapult.getCurrentPosition();

        double topSpeedRatio = 0.5; //sets the top speed
        // Wait for the game to start
        waitForStart();
        while (opModeIsActive()) {
            this.updateGamepads();  //updates game pads
            this.motorLeft.setPower(this.gamepad1.left_stick_y); //sets power to motor left
            this.motorRight.setPower(this.gamepad1.right_stick_y); //sets power to motor right

            if(this.catapult.getPower()==0 && this.gamepad1.right_bumper){ //starts the catapult cycle
               // this.catapult.setTargetPosition(420);
                this.catapult.setPower(1);
            }
            if(this.catapult.getCurrentPosition()<=initialCatapultPosition-420){ //stops the catapult cycle
                this.catapult.setPower(0);
                initialCatapultPosition = catapult.getCurrentPosition();
            }
            telemetry.addData("position: ", catapult.getCurrentPosition());
            boolean update = telemetry.update(); //does important background stuff at the end of each loop
            this.idle(); //does more important background stuff at the end of each loop

        }
    }
}

