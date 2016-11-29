package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.preference.PreferenceManager;
import android.util.Log;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
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
    DcMotor motorLeft;
    DcMotor motorRight;

  //  DcMotor linearSlideOne = null;
    //DcMotor linearSlideTwo = null;
    DcMotor catapult = null;
    //Servo buttonPusher = null;
    DcMotor ballPicker = null;
    double driveSpeedRatio = 0.5; //sets the top speed for drive train

    SharedPreferences sharedPref;
    protected static ResqAuton.Colors teamColor;
    protected static ResqAuton.Side startSide;

    protected double curYAW;
    protected double initYaw = 0;
    double x = 0;
    double y = 0;
    final GyroHelper gyroHelper = new GyroHelper(this);


    @Override public void main() throws InterruptedException
    {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        try {
            teamColor = getTeam();
            startSide = getSide();
        }
        catch (Exception e) {
            telemetry.addData("Error: ", "something done goofed.");
            telemetry.update();
        }

        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight");
        this.catapult = this.hardwareMap.dcMotor.get("catapult");
        this.ballPicker = this.hardwareMap.dcMotor.get("ballPicker");
        /*this.linearSlideOne = this.hardwareMap.dcMotor.get("catapult");
        this.linearSlideTwo= this.hardwareMap.dcMotor.get("linearSlideOne");
        this.buttonPusher = this.hardwareMap.servo.get("linearSlideTwo");*/
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);
       /* this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.catapult.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.ballPicker.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        this.ballPicker.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION); */

       this.motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.catapult.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        this.waitForStart();

        telemetry.addData("Now has it even started yet? ", true);
        telemetry.update();

        if (teamColor.equals(ResqAuton.Colors.BLUE)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, blue
                telemetry.addData("Running1? ", true);
                telemetry.update();
                goStraight(8);
                while (Math.abs(motorLeft.getPower())>0) {}//
                turnRight(1);
                while (Math.abs(motorLeft.getPower())>0) {}//
                shootCatapult();
                while (Math.abs(catapult.getPower())>0) {}//
                runBallPicker();
                while (ballPicker.isBusy()) {}
                shootCatapult();
                while (catapult.isBusy()) {}
                turnLeft(1);    //face the big ball
                goStraight(1);  //bump the big ball
                turnRight(1);   //turn parallel to midline
                goStraight(1);  //run along the midline
                turnRight(2); //turn to be parallel along the beacon walls

            }
            else{
                telemetry.addData("Running2? ", true);
                telemetry.update();
                goStraight(1);  //
                turnRight(1);
                goStraight(2); // go to position
                shootCatapult();
                runBallPicker();
                shootCatapult();
                turnLeft(1);    //face the big ball
                goStraight(1);  //bump the big ball
                turnRight(2); //turn to be parallel along the beacon walls//midline side, blue

            }
        }
        else if (teamColor.equals(ResqAuton.Colors.RED)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, red

            }
            else{                                   //midline side, red


            }
        }

    }


    void goStraight(double revolutions) {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorLeft.setTargetPosition((int) (1120 * revolutions));
        motorRight.setTargetPosition((int) (1120 * revolutions));
        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorLeft.setPower(driveSpeedRatio);
        motorRight.setPower(driveSpeedRatio);

        telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
        telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());

    }

    void turnLeft(double revolutions) {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorLeft.setTargetPosition(-(int)(1120 * revolutions));
        motorRight.setTargetPosition((int)(1120 * revolutions));
        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorLeft.setPower(-driveSpeedRatio);
        motorRight.setPower(driveSpeedRatio);
    }

    void turnRight(double revolutions) {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorLeft.setTargetPosition((int)(1120 * revolutions));
        motorRight.setTargetPosition(-(int)(1120 * revolutions));
        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorLeft.setPower(driveSpeedRatio);
        motorRight.setPower(-driveSpeedRatio);
    }

    void shootCatapult() {
        catapult.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        catapult.setTargetPosition(1760);
        catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        catapult.setPower(0.25);
    }

    void runBallPicker() {
        ballPicker.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        ballPicker.setTargetPosition(2240);
        ballPicker.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        ballPicker.setPower(0.5);
    }

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));

    }

    public ResqAuton.Side getSide(){
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN"));

    }
}
