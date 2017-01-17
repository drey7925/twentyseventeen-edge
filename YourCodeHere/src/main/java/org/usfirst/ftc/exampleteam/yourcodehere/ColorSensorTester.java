package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by rirriput on 01/16/2017.
 */
@TeleOp(name = "Color Sensor Tester")
public class ColorSensorTester extends SynchronousOpMode {

    protected ColorSensor sensor = null;

    @Override
    public void main() throws InterruptedException {
        sensor = hardwareMap.colorSensor.get("cSensor");
        double whiteness = 0;
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Red: ", sensor.red());
            telemetry.addData("Green: ", sensor.green());
            telemetry.addData("Blue: ", sensor.blue());
            whiteness = (sensor.red() + sensor.blue() + sensor.green()) / 3;
            telemetry.addData("Whiteness: ", whiteness);
            telemetry.update();
        }

    }
}
