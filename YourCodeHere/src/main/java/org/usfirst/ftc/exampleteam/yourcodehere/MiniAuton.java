package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;
import org.swerverobotics.library.interfaces.Autonomous;
import resq.ResqAuton.*;

/**
 * Created by kam07440 on 10/7/2016.
 */

@Autonomous(name="Mini Auton")
public class MiniAuton extends SynchronousOpMode{

    DcMotor motorLeftFront = null;
    DcMotor motorLeftBack = null;
    DcMotor motorRightFront = null;
    DcMotor motorRightBack = null;
    DcMotor catapult = null;
    DcMotor linearSlideOne = null;
    DcMotor linearSlideTwo = null;
    Servo buttonPusher = null;
    Servo ballPicker = null;

    SharedPreferences sharedPref;
    protected static Colors teamColor;
    protected static Side startSide;


    double drivePower = 0.5;

    @Override public void main() throws InterruptedException
    {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        teamColor = getTeam();
        startSide = getSide();

        this.motorLeftBack = this.hardwareMap.dcMotor.get("motorLeftBack");
        this.motorLeftFront = this.hardwareMap.dcMotor.get("motorLeftFront");
        this.motorRightBack = this.hardwareMap.dcMotor.get("motorRightBack");
        this.motorRightFront = this.hardwareMap.dcMotor.get("motorRightFront");
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.linearSlideOne = this.hardwareMap.dcMotor.get("catapult");
        this.linearSlideTwo= this.hardwareMap.dcMotor.get("linearSlideOne");
        this.buttonPusher = this.hardwareMap.servo.get("linearSlideTwo");
        this.ballPicker = this.hardwareMap.servo.get("ballPicker");

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

    public Colors getTeam() {
        return Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));

    }

    public Side getSide(){
        return Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN"));

    }
}


