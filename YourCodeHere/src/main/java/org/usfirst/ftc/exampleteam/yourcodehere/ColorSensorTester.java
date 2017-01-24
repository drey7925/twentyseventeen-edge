package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.LightSensor;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by rirriput on 01/16/2017.
 */
@TeleOp(name = "Color Sensor Tester")
public class ColorSensorTester extends SynchronousOpMode {

  //  protected ColorSensor sensor = null;
  //  protected ColorSensor sensor = null;
    protected LightSensor sensor = null;

    @Override
    public void main() throws InterruptedException {
       // sensor = hardwareMap.colorSensor.get("cSensor");
        sensor = hardwareMap.lightSensor.get("lightSensor");

        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Raw value: ", sensor.getLightDetectedRaw());
            telemetry.update();
        }

    }
}
