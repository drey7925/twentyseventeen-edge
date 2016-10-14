package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * A skeletal example of a do-nothing first OpMode. Go ahead and change this code
 * to suit your needs, or create sibling OpModes adjacent to this one in the same
 * Java package.
 */
@TeleOp(name="My First OpMode")
public class SingleMotorTester extends SynchronousOpMode
    {
    /* Declare here any fields you might find useful. */
        DcMotor motor = null;

    @Override public void main() throws InterruptedException
        {
        /* Initialize our hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names you assigned during the robot configuration
         * step you did in the FTC Robot Controller app on the phone.
         */
            this.motor = this.hardwareMap.dcMotor.get("motor");


        // Wait for the game to start
        waitForStart();

        // Go go gadget robot!
        while (opModeIsActive())
            {
            if (updateGamepads())
                {
                    motor.setPower(this.gamepad1.left_stick_y);

                }

            telemetry.update();
            idle();
            }
        }
    }