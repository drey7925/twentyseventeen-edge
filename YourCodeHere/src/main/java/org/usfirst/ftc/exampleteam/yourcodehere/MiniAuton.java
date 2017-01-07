package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.Range;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;
import org.swerverobotics.library.interfaces.Autonomous;
import resq.DeviceNaming;
import resq.GyroHelper;
import resq.ResqAuton;
import resq.ResqAuton.*;

/**
 * Created by kam07440 on 10/7/2016.
 * Contributors:
 * Lillian Hong and Gabriel Kammer
 */

@Autonomous(name="Mini Auton")
public class MiniAuton extends SynchronousOpMode{

    DcMotor motorLeft = null;
    DcMotor motorRight = null;

    DcMotor catapult = null;
    DcMotor linearSlideOne = null;
    DcMotor linearSlideTwo = null;
    Servo buttonPusher = null;
    Servo ballPicker = null;


    SharedPreferences sharedPref;
    protected static Colors teamColor;
    protected static Side startSide;

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

        turnTo(150);

       startUpHardware();

        if (teamColor.equals(Colors.BLUE)){
            if(startSide.equals(Side.MOUNTAIN)){  //mountain side, blue

            }
            else{                                   //midline side, blue

            }
        }
        else if (teamColor.equals(Colors.RED)){
            if(startSide.equals(Side.MOUNTAIN)){  //mountain side, red

            }
            else{                                   //midline side, red

            }
        }

    }

    protected void startUpHardware() throws InterruptedException {


        this.motorLeft = this.hardwareMap.dcMotor.get("lMotor");
        this.motorRight = this.hardwareMap.dcMotor.get("motorFront");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        this.catapult = this.hardwareMap.dcMotor.get("catapult");

        this.linearSlideOne = this.hardwareMap.dcMotor.get("catapult");
        this.linearSlideTwo= this.hardwareMap.dcMotor.get("linearSlideOne");
        this.buttonPusher = this.hardwareMap.servo.get("linearSlideTwo");
        this.ballPicker = this.hardwareMap.servo.get("ballPicker");

        gyroHelper.startUpGyro();
        String gc = sharedPref.getString("gyrocalib", "!!");
        if (gc.matches("([0-9a-f]{2})*")) {
            try {
                gyroHelper.getImu().writeCalibrationData(Hex.decodeHex(gc.toCharArray()));
            } catch (DecoderException e) {
                throw new RuntimeException("EMERG-STOP: CANNOT CALIBRATE GYRO");
            }
        }
        idle();

    }


    public void turnTo(double YAW) {
        doPeriodicTasks();
        curYAW = getGyroYAW();
        double angle = Math.abs(curYAW - YAW);
        if (angle > 180) {
            angle = 360 - angle;
        }

        double incA = Math.min(15, angle); //the increment angle
        double oriYAW = getGyroYAW();
        double incr = 0;
        //begin turning cases
        if (((oriYAW > YAW) && (oriYAW - YAW) < 180)) { //TURN RIGHT

            while ((oriYAW - curYAW) < incA) {
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                incr = 1 * ((oriYAW - curYAW) / 15);

                doPeriodicTasks();
                curYAW = getGyroYAW();

            }
           /* START INTERMEDIATE MAX SPEED TURN*/
            while ((oriYAW - curYAW) < (angle - incA)) {
                setLeftSpeed(1 * .25);
                setRightSpeed(-1 * .25);
                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
           /* START DECLINING INCREMENT */
            while ((oriYAW - curYAW) < angle) {
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                incr = (1 * (-(YAW - curYAW) / 15));

                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
            doPeriodicTasks();
            curYAW = getGyroYAW();

        } else if ((oriYAW < YAW) && (YAW - oriYAW) > 180) { //turn right past 0 line
            doPeriodicTasks();
            curYAW = getGyroYAW();
            if (oriYAW > incA) {
                while ((oriYAW - curYAW) < incA) {
                    setLeftSpeed(incr * .25);
                    setRightSpeed(-incr * .25);
                    incr = 1 * ((oriYAW - curYAW) / 15);
                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            } else {
                doPeriodicTasks();
                curYAW = getGyroYAW();
                while (curYAW < 180) {
                    setLeftSpeed(incr * .25);
                    setRightSpeed(-incr * .25);
                    incr = 1 * ((oriYAW - curYAW) / 15);
                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
                while (oriYAW + (360 - curYAW) < incA) {
                    setLeftSpeed(incr * .25);
                    setRightSpeed(-incr * .25);
                    incr = 1 * ((oriYAW - curYAW) / 15);
                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }

            }
           /* START INTERMEDIATE MAX SPEED TURN*/
            while (curYAW < oriYAW) { //these two while loops are the same thing
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
            while ((360 - curYAW) < (angle - incA)) {//they're just accounting for before the 0 line and after it
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                doPeriodicTasks();
                curYAW = getGyroYAW();

                doPeriodicTasks();
            }
           /*START DECLINING INCREMENT SPEED */
            while ((oriYAW + (360 - curYAW)) < angle) {
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                incr = 1 * (-(YAW - curYAW) / 15);

                doPeriodicTasks();
                curYAW = getGyroYAW();

            }

            doPeriodicTasks();
            curYAW = getGyroYAW();
            if ((oriYAW + (360 - curYAW)) >= angle) { //stops motors
                setLeftSpeed(0);
                setRightSpeed(0);

                doPeriodicTasks();
            }

        } else if ((oriYAW < YAW) && ((YAW - oriYAW) < 180)) { // LEFT TURN (DO THIS!!)
            while ((curYAW - oriYAW) < incA) {
                setLeftSpeed(-incr * .25);
                setRightSpeed(incr * .25);
                incr = 1 * ((curYAW - oriYAW) / 15);


                doPeriodicTasks();
                curYAW = getGyroYAW();

            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while ((curYAW - oriYAW) < (angle - incA)) {
                setLeftSpeed(-1);
                setRightSpeed(1);

                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
       	/* START DECLINING INCREMENT */
            while ((curYAW - oriYAW) < angle) {
                setLeftSpeed(-incr * .25);
                setRightSpeed(incr * .25);
                incr = (1 * (-(YAW - curYAW) / 15));


                doPeriodicTasks();
                curYAW = getGyroYAW();
            }

            doPeriodicTasks();
            curYAW = getGyroYAW();
            if (curYAW >= YAW) {
                setLeftSpeed(0);
                setRightSpeed(0);
            }
        } else if ((oriYAW > YAW) && (oriYAW - YAW) > 180) { //turn left past 0 line

            doPeriodicTasks();
            curYAW = getGyroYAW();
            if (oriYAW > incA) {
                while ((oriYAW - curYAW) < incA) {
                    setLeftSpeed(incr * .25);
                    setRightSpeed(-incr * .25);
                    incr = 1 * ((oriYAW - curYAW) / 15);

                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            } else {

                doPeriodicTasks();
                curYAW = getGyroYAW();
                while (oriYAW + (360 - curYAW) < incA) {
                    setLeftSpeed(incr * .25);
                    setRightSpeed(-incr * .25);
                    incr = 1 * ((oriYAW - curYAW) / 15);


                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while (curYAW < oriYAW) { //these two while loops are the same thing
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
            while ((360 - curYAW) < (angle - incA)) {//they're just accounting for before the 0 line and after it
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
       	/*START DECLINING INCREMENT SPEED */
            while ((oriYAW + (360 - curYAW)) < angle) {
                setLeftSpeed(incr * .25);
                setRightSpeed(-incr * .25);
                incr = (-(YAW - curYAW) / 15);
                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
            doPeriodicTasks();

            curYAW = getGyroYAW();
            if ((oriYAW + (360 - curYAW)) >= angle) { //stops motors
                setLeftSpeed(0);
                setRightSpeed(0);
            }

        }
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

    public void doPeriodicTasks() {
        Log.w("TRACK", "ENTER DO-PERIODIC");
        gyroHelper.update();

        int l0p = motorLeft.getCurrentPosition();
        int r0p = motorRight.getCurrentPosition();

        try {
            idle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.w("TRACK", "EXIT DO-PERIODIC");
    }


/*
    private double origYAW;

    void turnTo(double turnYAW){

        curYAW = getGyroYAW();
        origYAW = curYAW;

        if(turnYAW>origYAW){
            if(turnYAW-origYAW>180){ //turn right past zero line

                while(curYAW<origYAW){
                    turnRight();
                }

            }
            else if(turnYAW-origYAW<180){ //turn left

            }
        }
        else{
            if(origYAW-turnYAW > 180) { //turn left past zero line

            }
            else if(origYAW-turnYAW < 180){ //turn right

            }

        }


    }
    */

    public double getGyroYAW() {

        return normalizeDegrees(gyroHelper.getAngles().heading - initYaw);
    }

    public void offsetPosition(double X, double Y, double YAW) {
        x = X;
        y = Y;
        initYaw = YAW;
    }
    double normalizeDegrees(double degrees) {
        while (degrees >= 360) degrees -= 360.0;
        while (degrees < 0.0) degrees += 360.0;
        return degrees;
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

    public Colors getTeam() {
        return Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));

    }

    public Side getSide(){
        return Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN"));

    }
}


