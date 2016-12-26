package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.Servo;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by hon07726 on 12/19/2016.
 */
@TeleOp(name = "Tester for RGB Servo")
public class TestRGBServo extends SynchronousOpMode {


    protected Servo serv = null;

    protected void main() throws InterruptedException {

        waitForStart();
        while(opModeIsActive()){
            serv = hardwareMap.servo.get("rbgServo");
            serv.setPosition(this.gamepad1.right_trigger>0.5 ? 0 : 1);
        }

    }
}
