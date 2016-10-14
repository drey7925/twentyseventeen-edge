package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by kam07440 on 10/7/2016.
 */

@Autonomous(name="Mini Auton")
public class MiniAuton extends SynchronousOpMode{

    DcMotor motorLeftFront = null;
    DcMotor motorLeftBack = null;
    DcMotor motorRightFront = null;
    DcMotor motorRightBack = null;

    //double drivePower = 0.5;

    @Override public void main() throws InterruptedException
    {
        this.motorLeftBack = this.hardwareMap.dcMotor.get("motorLeftBack");
        this.motorLeftFront = this.hardwareMap.dcMotor.get("motorLeftFront");
        this.motorRightBack = this.hardwareMap.dcMotor.get("motorLeftBack");
        this.motorRightFront = this.hardwareMap.dcMotor.get("motorLeftFront");



    }

    void goStraight()
    {
        this.motorLeftBack.setPower(drivePower);
        this.motorLeftFront.setPower(drivePower);
        this.motorRightBack.setPower(drivePower);
        this.motorRightFront.setPower(drivePower);
    }
    void turnLeft()
    {
        this.motorLeftBack.setPower(-drivePower);
        this.motorLeftFront.setPower(-drivePower);
        this.motorRightBack.setPower(drivePower);
        this.motorRightFront.setPower(drivePower);

    }

    void turnRight() {

        this.motorLeftBack.setPower(drivePower);
        this.motorLeftFront.setPower(drivePower);
        this.motorRightBack.setPower(-drivePower);
        this.motorRightFront.setPower(-drivePower);
    }
    void stopRobot()
    {
        this.motorLeftBack.setPower(0);
        this.motorLeftFront.setPower(0);
        this.motorRightBack.setPower(0);
        this.motorRightFront.setPower(0);
    }
}

}
