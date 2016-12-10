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
        if (teamColor.equals(ResqAuton.Colors.BLUE)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, blue
                goForwardTime(0.3);
                turnRightTime(0.5);
                shootCatapult();
                runBallPickerTime();
                shootCatapult();
                turnLeftTime(.5);
                goForwardTime(1.5);
            }
            else{                                           //midline side, blue
                goForwardTime(0.3);
                turnRightTime(0.5);
                goForwardTime(0.75); // go to position
                shootCatapult();
                runBallPickerTime();
                shootCatapult();
                turnLeftTime(.5);    //face the big ball
                goForwardTime(1.5);  //bump the big ball
            }
        }
        else if (teamColor.equals(ResqAuton.Colors.RED)){
            if(startSide.equals(ResqAuton.Side.MOUNTAIN)){  //mountain side, red
                goForwardTime(0.3);
                turnRightTime(0.5);
                shootCatapult();
                runBallPickerTime();
                shootCatapult();
                turnLeftTime(0.5);
                goForwardTime(1.5);
            }
            else{                                   //midline side, red
                goForwardTime(0.3);
                turnRightTime(0.5);
                goBackwardTime(0.75); // go to position
                shootCatapult();
                runBallPickerTime();
                shootCatapult();
                turnLeftTime(0.5);    //face the big ball
                goForwardTime(1.5);  //bump the big ball
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
            telemetry.addData("Left Power: ", motorLeft.getPower());
            telemetry.addData("Right Power: ", motorRight.getPower());
            telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
            telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
            telemetry.update();
        }
        while (Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition()) || Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setPower(0.2+Math.min(Math.abs((motorLeft.getTargetPosition()-motorLeft.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            this.motorRight.setPower(0.2+Math.min(Math.abs((motorRight.getTargetPosition()-motorRight.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            telemetry.addData("Left Power: ", motorLeft.getPower());
            telemetry.addData("Right Power: ", motorRight.getPower());
            telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
            telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
            telemetry.update();
        }
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

    void turnLeftSmooth (double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition(-(int) (1120 * revolutions));
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
            telemetry.addData("Left Power: ", motorLeft.getPower());
            telemetry.addData("Right Power: ", motorRight.getPower());
            telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
            telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
            telemetry.update();
        }
        while (Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition()) || Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setPower(0.2+Math.min(Math.abs((motorLeft.getTargetPosition()-motorLeft.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            this.motorRight.setPower(0.2+Math.min(Math.abs((motorRight.getTargetPosition()-motorRight.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            telemetry.addData("Left Power: ", motorLeft.getPower());
            telemetry.addData("Right Power: ", motorRight.getPower());
            telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
            telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
            telemetry.update();
        }
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

    void turnRightSmooth (double revolutions) {
        this.motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorLeft.setTargetPosition((int) (1120 * revolutions));
        this.motorRight.setTargetPosition(-(int) (1120 * revolutions));
        this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorLeft.setPower(0.2);
        this.motorRight.setPower(0.2);
        while(Math.abs(motorLeft.getCurrentPosition())<Math.abs(0.5*motorLeft.getTargetPosition()) || Math.abs(motorRight.getCurrentPosition())<Math.abs(0.5*motorRight.getTargetPosition())) {
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setPower(0.2+Math.min(Math.abs(motorLeft.getCurrentPosition()/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            this.motorRight.setPower(0.2+Math.min(Math.abs(motorRight.getCurrentPosition()/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            telemetry.addData("Left Power: ", motorLeft.getPower());
            telemetry.addData("Right Power: ", motorRight.getPower());
            telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
            telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
            telemetry.update();
        }
        while (Math.abs(motorLeft.getCurrentPosition())<Math.abs(motorLeft.getTargetPosition()) || Math.abs(motorRight.getCurrentPosition())<Math.abs(motorRight.getTargetPosition())) {
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setPower(0.2+Math.min(Math.abs((motorLeft.getTargetPosition()-motorLeft.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            this.motorRight.setPower(0.2+Math.min(Math.abs((motorRight.getTargetPosition()-motorRight.getCurrentPosition())/1120)*(DRIVE_SPEED_RATIO-0.2),DRIVE_SPEED_RATIO));
            telemetry.addData("Left Power: ", motorLeft.getPower());
            telemetry.addData("Right Power: ", motorRight.getPower());
            telemetry.addData("Left Motor Position:",motorLeft.getCurrentPosition());
            telemetry.addData("Right Motor Position:",motorRight.getCurrentPosition());
            telemetry.update();
        }
    }

    void turnRightTime(double seconds) {
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeft.setPower(DRIVE_SPEED_RATIO);
        motorRight.setPower(-DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }
    void turnLeftTime(double seconds) {
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeft.setPower(-DRIVE_SPEED_RATIO);
        motorRight.setPower(DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }
    void goForwardTime(double seconds) {
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeft.setPower(DRIVE_SPEED_RATIO);
        motorRight.setPower(DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }
    void goBackwardTime(double seconds) {
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorLeft.setPower(-DRIVE_SPEED_RATIO);
        motorRight.setPower(-DRIVE_SPEED_RATIO);
        try {
            Thread.sleep((long)(seconds*1000.0));
        }
        catch (Exception e) {
            telemetry.addData("Ohnoes", "welplew");
            telemetry.update();
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }


    void shootCatapult() {
        this.catapult.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        //this.catapult.setTargetPosition(1760);
        this.catapult.setTargetPosition(5000);

        this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.catapult.setPower(0.25);
        while(Math.abs(catapult.getCurrentPosition())<Math.abs(catapult.getTargetPosition())) {
            telemetry.addData("Catapult Position: ", catapult.getCurrentPosition());
            telemetry.update();
        }
        this.catapult.setPower(0);
    }

    void runBallPicker() {
        this.ballPicker.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.ballPicker.setTargetPosition(2240);
        this.ballPicker.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.ballPicker.setPower(0.5);
        while(Math.abs(ballPicker.getCurrentPosition())<Math.abs(ballPicker.getTargetPosition())) {
            telemetry.addData("Ball Picker Position: ", ballPicker.getCurrentPosition());
            telemetry.update();
        }
        this.ballPicker.setPower(0);
    }

    void runBallPickerTime() {
        ballPicker.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.ballPicker.setPower(0.3);

        try{
        Thread.sleep(1000);}
        catch(Exception e){
            telemetry.addData("Ballpicker Error", e.getMessage());
        }

        this.ballPicker.setPower(0);
    }

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));
    }

    public ResqAuton.Side getSide(){
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position","MOUNTAIN"));
    }
}
