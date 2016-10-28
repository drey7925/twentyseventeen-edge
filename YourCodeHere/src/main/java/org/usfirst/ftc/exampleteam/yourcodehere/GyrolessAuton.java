package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import resq.GyroHelper;
import resq.ResqAuton;

/**
 * Created by hon07726 on 10/28/2016.
 */
@Autonomous(name="Gyroless Auton")
public class GyrolessAuton extends SynchronousOpMode{
    DcMotor motorLeft = null;
    DcMotor motorRight = null;

    DcMotor catapult = null;
    DcMotor linearSlideOne = null;
    DcMotor linearSlideTwo = null;
    Servo buttonPusher = null;
    Servo ballPicker = null;


    SharedPreferences sharedPref;
    protected static ResqAuton.Colors teamColor;
    protected static ResqAuton.Side startSide;

    protected double curYAW;
    protected double initYaw = 0;
    double x = 0;
    double y = 0;
    final GyroHelper gyroHelper = new GyroHelper(this);



    double drivePower = 0.5;

    @Override public void main() throws InterruptedException
    {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        teamColor = getTeam();
        startSide = getSide();


        startUpHardware();

        if (teamColor.equals(ResqAuton.Colors.BLUE)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, blue

            }
            else{                                   //midline side, blue

            }
        }
        else if (teamColor.equals(ResqAuton.Colors.RED)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, red
                turnLeft();

            }
            else{                                   //midline side, red

            }
        }

    }

    protected void startUpHardware() throws InterruptedException {


        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorFront");
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);

        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.linearSlideOne = this.hardwareMap.dcMotor.get("catapult");
        this.linearSlideTwo= this.hardwareMap.dcMotor.get("linearSlideOne");
        this.buttonPusher = this.hardwareMap.servo.get("linearSlideTwo");
        this.ballPicker = this.hardwareMap.servo.get("ballPicker");



    }



    double lSpd, rSpd;

    public void setLeftSpeed(double speed) {

        speed = Range.clip(speed, -1, 1);
        lSpd = speed;
        motorLeft.setPower(speed);
    }

    public void setRightSpeed(double speed) {
        speed = Range.clip(speed, -1, 1);
        rSpd = speed;
        motorRight.setPower(speed);
    }

    public void blendLeftSpeed(double spd) {
        setLeftSpeed((lSpd + spd) / 2);
    }

    public void blendRightSpeed(double spd) {
        setRightSpeed((rSpd + spd) / 2);
    }

    void goStraight()
    {
        this.motorLeft.setPower(drivePower);
        this.motorRight.setPower(drivePower);
    }
    void turnLeft()
    {
        this.motorLeft.setPower(-drivePower);
        this.motorRight.setPower(drivePower);

    }

    void turnRight() {

        this.motorLeft.setPower(drivePower);
        this.motorRight.setPower(-drivePower);
    }
    void stopRobot()
    {
        this.motorLeft.setPower(0);
        this.motorRight.setPower(0);
    }
    void turnBackwardLeft(long time) throws InterruptedException {
        //when facing in the same direction as forward robot, the robot's left
        this.motorRight.setDirection(DcMotor.Direction.REVERSE);
        this.motorRight.setPower(0.5);
        Thread.sleep(time);
        this.motorRight.setDirection(DcMotor.Direction.FORWARD);
        stopRobot();
    }
    void turnBackwardRight(long time) throws InterruptedException {
        //when facing in the same direction as forward robot, the robot's left
        this.motorLeft.setDirection(DcMotor.Direction.FORWARD);
        this.motorLeft.setPower(0.5);
        Thread.sleep(time);
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);
        stopRobot();
    }

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));

    }

    public ResqAuton.Side getSide(){
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN"));

    }
}
