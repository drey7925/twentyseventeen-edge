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

    @Override public void main() throws InterruptedException
    {
        this.motorLeftBack = this.hardwareMap.dcMotor.get("motorLeftBack");
        this.motorLeftFront = this.hardwareMap.dcMotor.get("motorLeftFront");
        this.motorRightBack = this.hardwareMap.dcMotor.get("motorLeftBack");
        this.motorRightFront = this.hardwareMap.dcMotor.get("motorLeftFront");

    }

}
