package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by kam07440 on 2/2/2017.
 */
@TeleOp(name = "Encoder Tester")

public class EncoderTester extends SynchronousOpMode{

    protected DcMotor lMotor;
    protected DcMotor rMotor;

    @Override
    protected void main() throws InterruptedException {

        lMotor = hardwareMap.dcMotor.get("lMotor");
        rMotor = hardwareMap.dcMotor.get("rMotor");

        waitForStart();
        int time = (int) System.currentTimeMillis();
        while(opModeIsActive()){
            lMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            lMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            rMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

            lMotor.setPower(0.05);
            rMotor.setPower(0.05);
            telemetry.addData("Left Motor: ", lMotor.getCurrentPosition());
            telemetry.addData("Right Motor: ", rMotor.getCurrentPosition());

            if(System.currentTimeMillis() - time > 3000){
                lMotor.setPower(0);
                rMotor.setPower(0);
                break;
            }
        }

    }
}
