package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by kam07440 on 10/31/2016.
 */
public class GamepadTester  extends SynchronousOpMode{
    @Override
    public void main() throws InterruptedException {
        telemetry.addData("Right bumper: ", this.gamepad1.right_bumper);
        telemetry.addData("Left bumper: ", this.gamepad1.left_bumper);
        telemetry.addData("Right trigger: ", this.gamepad1.right_trigger);
        telemetry.addData("Left trigger: ", this.gamepad1.left_trigger);
        telemetry.addData("Right stick y: ", this.gamepad1.right_stick_y);
        telemetry.addData("Right stick x: ", this.gamepad1.right_stick_x);
        telemetry.addData("Left stick y: ", this.gamepad1.left_stick_y);
        telemetry.addData("Left stick x: ", this.gamepad1.left_stick_x);
        telemetry.addData("a: ", this.gamepad1.a);
        telemetry.addData("b: ", this.gamepad1.b);
        telemetry.addData("x: ", this.gamepad1.x);
        telemetry.addData("y: ", this.gamepad1.y);
        telemetry.addData("dpad up: ", this.gamepad1.dpad_up);
        telemetry.addData("dpad down: ", this.gamepad1.dpad_down);
        telemetry.addData("dpad left: ", this.gamepad1.dpad_left);
        telemetry.addData("dpad right: ", this.gamepad1.dpad_right);
        telemetry.addData("start: ", this.gamepad1.start);
        telemetry.addData("back: ", this.gamepad1.back);
    }
}
