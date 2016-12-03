package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
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
    double DRIVE_SPEED_RATIO = 0.35; //sets the top speed for drive train

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

        this.motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.catapult.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.motorLeft.setDirection(DcMotor.Direction.REVERSE);

        this.waitForStart();

        telemetry.addData("Now has it even started yet? ", true);
        telemetry.update();
        goStraightSmooth(2);

        if (teamColor.equals(ResqAuton.Colors.BLUE)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, blue
                goStraight(2);
                turnRight(0.5);
                shootCatapult();
                runBallPicker();
                shootCatapult();
                turnLeft(2);
                goStraight(3);
            }
            else{                                           //midline side, blue
                goStraight(2);
                turnRight(0.5);
                goStraight(3); // go to position
                shootCatapult();
                runBallPicker();
                shootCatapult();
                turnLeft(0.5);    //face the big ball
                goStraight(3);  //bump the big ball
            }
        }
        else if (teamColor.equals(ResqAuton.Colors.RED)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, red
                goStraight(2);
                turnRight(0.5);
                shootCatapult();
                runBallPicker();
                shootCatapult();
                turnLeft(2);
                goStraight(3);
            }
            else{                                   //midline side, red
                goStraight(2);
                turnRight(0.5);
                goStraight(-3); // go to position
                shootCatapult();
                runBallPicker();
                shootCatapult();
                turnLeft(0.5);    //face the big ball
                goStraight(3);
            }
        }

    }


    void goStraight(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition((int) (1120 * revolutions));
        this.motorRight.setTargetPosition((int) (1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(DRIVE_SPEED_RATIO);
        this.motorRight.setPower(DRIVE_SPEED_RATIO);
        telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
        telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
        telemetry.update();
        while(Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition())) {}
        while(Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {}
    }

    void goStraightSmooth(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition((int) (1120 * revolutions));
        this.motorRight.setTargetPosition((int) (1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(0.2);
        this.motorRight.setPower(0.2);
        while(Math.abs(motorLeft.getCurrentPosition())<Math.abs(0.5*motorLeft.getTargetPosition()) || Math.abs(motorRight.getCurrentPosition())<Math.abs(0.5*motorRight.getTargetPosition())) {
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setPower(0.2+Math.min(Math.abs(motorLeft.getCurrentPosition()/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            this.motorRight.setPower(0.2+Math.min(Math.abs(motorRight.getCurrentPosition()/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
        }
        while (Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition()) || Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setPower(0.2+Math.min(Math.abs((motorLeft.getTargetPosition()-motorLeft.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            this.motorRight.setPower(0.2+Math.min(Math.abs((motorRight.getTargetPosition()-motorRight.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
        }
        telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
        telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
        telemetry.update();
    }

    void turnLeft(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition(-(int)(1120 * revolutions));
        this.motorRight.setTargetPosition((int)(1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(DRIVE_SPEED_RATIO);
        this.motorRight.setPower(DRIVE_SPEED_RATIO);
        while(Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition())) {}
        while(Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {}
    }

    void turnRight(double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition((int)(1120 * revolutions));
        this.motorRight.setTargetPosition(-(int)(1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(DRIVE_SPEED_RATIO);
        this.motorRight.setPower(DRIVE_SPEED_RATIO);
        while(Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition())) {
            telemetry.addData("Left Position: ", motorLeft.getCurrentPosition());
            telemetry.addData("Left Target: ", motorLeft.getTargetPosition());
            telemetry.update();
        }
        while(Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {}
    }

    void shootCatapult() {
        this.catapult.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.catapult.setTargetPosition(1760);
        this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.catapult.setPower(0.25);
        while(Math.abs(catapult.getCurrentPosition())<Math.abs(catapult.getTargetPosition())) {}
    }

    void runBallPicker() {
        this.ballPicker.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.ballPicker.setTargetPosition(2240);
        this.ballPicker.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.ballPicker.setPower(0.5);
        while(Math.abs(ballPicker.getCurrentPosition())<Math.abs(ballPicker.getTargetPosition())) {}
    }

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));
    }

    public ResqAuton.Side getSide(){
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN"));
    }
}
