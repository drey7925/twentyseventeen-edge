package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import resq.GyroHelper;
import resq.ResqAuton;

/**
 * Created by hon07726 on 10/31/2016.
 */
@Autonomous(name="Workshop Gyroless Auton")
public class WorkshopGyrolessAuton extends SynchronousOpMode{



        DcMotor motorLeft = null;
        DcMotor motorRight = null;

        DcMotor linearSlideOne = null;
        DcMotor linearSlideTwo = null;
        DcMotor catapult = null;
        Servo buttonPusher = null;
        Servo ballPicker = null;
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
                    goStraight(0.5);
                    Thread.sleep(sharedPref.getLong("1_mountainside", 1000));
                    turnRight(0.5);
                    Thread.sleep(sharedPref.getLong("2_mountainside",1000));
                    goBackStraight(0.5);
                    Thread.sleep(sharedPref.getLong("3_mountainside",1000));
                    stopRobot();
                    this.motorLeft.setDirection(DcMotor.Direction.REVERSE);
                    this.motorRight.setDirection(DcMotor.Direction.FORWARD);
                    this.catapult.setPower(1);
                    Thread.sleep(sharedPref.getLong("catapult_time",1000));
                    this.catapult.setPower(0);
                    //shoot second ball
                    goBackStraight(0.5);
                    Thread.sleep(sharedPref.getLong("4_mountainside",1000));
                    turnRight(0.5);
                    Thread.sleep(sharedPref.getLong("5_mountainside",1000));
                    goBackStraight(0.5);
                    Thread.sleep(sharedPref.getLong("6_mountainside",1000));
                    //detect and hit beacons



                }
                else{                                   //midline side, red

                    goStraight(0.5);
                    Thread.sleep(sharedPref.getLong("1_corner", 1));  //ADD IN ALL OF THESE XML VALUES
                    turnLeft(0.5);
                    Thread.sleep(sharedPref.getLong("2_corner", 1));
                    goStraight(0.5);
                    Thread.sleep(sharedPref.getLong("3_corner", 1));
                    turnRight(0.5);
                    Thread.sleep(sharedPref.getLong("4_corner", 1));
                    stopRobot();
                    catapult.setPower(1.0);
                    Thread.sleep(sharedPref.getLong("catapult_time", 1));
                    catapult.setPower(0);
                    //COCK THE CATAPULT
                    catapult.setPower(1.0);
                    Thread.sleep(sharedPref.getLong("catapult_time", 1));
                    catapult.setPower(0);
                    turnLeft(0.5);
                    Thread.sleep(sharedPref.getLong("5_corner", 1));
                    goStraight(0.5);
                    Thread.sleep(sharedPref.getLong("6_corner", 1));
                    turnLeft(0.5);
                    Thread.sleep(sharedPref.getLong("7_corner", 1));
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
            this.motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            this.catapult.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
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

        void goBackStraight(double drivePower){
            this.motorLeft.setDirection(DcMotor.Direction.FORWARD);
            this.motorRight.setDirection(DcMotor.Direction.REVERSE);
            this.motorLeft.setPower(drivePower);
            this.motorRight.setPower(drivePower);
        }
        void goStraight(double revolutions)
        {
            double leftSpeed = driveSpeedRatio;
            double rightSpeed = driveSpeedRatio;
            int leftStartPosition = this.motorLeft.getCurrentPosition();
            int rightStartPosition = this.motorRight.getCurrentPosition();
            this.motorLeft.setPower(leftSpeed);
            this.motorRight.setPower(rightSpeed);
            while (this.motorLeft.getCurrentPosition()-leftStartPosition < 1120*revolutions) {

            }
            this.motorLeft.setPower(0);
            this.motorRight.setPower(0);
        }
        void turnLeft(double drivePower)
        {
            this.motorLeft.setPower(-drivePower);
            this.motorRight.setPower(drivePower);

        }

        void turnRight(double drivePower) {

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
