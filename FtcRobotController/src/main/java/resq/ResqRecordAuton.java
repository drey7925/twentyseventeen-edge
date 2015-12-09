package resq;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.Servo;
import ftc.team6460.javadeck.ftc.Utils;

import java.io.*;

/**
 * Created by akh06977 on 9/18/2015.
 */

public class ResqRecordAuton extends RectResqCommon {
    public void fillInSettings() {
        startSide = getStartSide();
        teamColor = getTeam();

    }

    protected static ResqAuton.Side startSide;
    protected static ResqAuton.Colors teamColor;

    public ResqAuton.Side getStartSide() {
        return ResqAuton.Side.valueOf(sharedPref.getString("auton_start_position", "MOUNTAIN"));
    } //get starting side from settings dialog (

    public ResqAuton.Colors getTeam() {
        return ResqAuton.Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));

    }

    double scaledPower;
    Servo btnPushSrvo; // Left servo, labeled 2
    boolean pushServoDeployed = false;
    double aimPos = 0.32;
    Servo aimServo; // Lift servo
    SharedPreferences sharedPref;
    FileOutputStream fileOutputStream;
    DataOutputStream dos;
    ByteArrayOutputStream baos;
    DataOutputStream dbaos;
    double ourVoltage;

    @Override
    public void init() {
        try {
            super.init();
            try {
                btnPushSrvo = hardwareMap.servo.get("btnPush");
            } catch (Exception e) {
                telemetry.addData("INITFAULT", "BTNSERVO");
            }
            try {

                aimServo = hardwareMap.servo.get("aimServo");
            } catch (Exception e) {
                telemetry.addData("INITFAULT", "BTNSERVO");
            }

            sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
            scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
            this.gamepad1.setJoystickDeadzone(0.1f);

            if (teamColor == ResqAuton.Colors.BLUE) {
                if (startSide == ResqAuton.Side.MOUNTAIN) {
                    fileOutputStream = hardwareMap.appContext.openFileOutput("bluemtn.RUN", Context.MODE_PRIVATE);
                    telemetry.addData("FILENAME", "bluemtn.RUN");
                } else {
                    fileOutputStream = hardwareMap.appContext.openFileOutput("bluemid.RUN", Context.MODE_PRIVATE);
                    telemetry.addData("FILENAME", "bluemid.RUN");
                }

            } else {
                if (startSide == ResqAuton.Side.MOUNTAIN) {
                    fileOutputStream = hardwareMap.appContext.openFileOutput("redmtn.RUN", Context.MODE_PRIVATE);
                    telemetry.addData("FILENAME", "redmtn.RUN");
                } else {
                    fileOutputStream = hardwareMap.appContext.openFileOutput("redmid.RUN", Context.MODE_PRIVATE);
                    telemetry.addData("FILENAME", "redmid.RUN");
                }

            }

            try {
                ourVoltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
            } catch (Exception e) {
                ourVoltage = 12.0;
            }
            dos = new DataOutputStream(fileOutputStream);
            baos = new ByteArrayOutputStream();
            dbaos = new DataOutputStream(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    boolean runDrive = true;
    boolean hasWritten = false;
    int loops = 0;
    long ns = 0;

    @Override
    public void loop() {
        if (ns == 0) ns = System.nanoTime();
        if (runDrive) {
            double scaleActual = (this.gamepad1.right_trigger > 0.2) ? scaledPower : 1.00;
            boolean fullOverrideNeg = (this.gamepad1.right_trigger > 0.2);
            boolean fullOverridePos = (this.gamepad1.left_trigger > 0.2);
            double tipPreventionPower = 0;
            double lCalculated = this.gamepad1.left_stick_y * scaleActual + tipPreventionPower;

            double rCalculated = this.gamepad1.right_stick_y * scaleActual + tipPreventionPower;


            if (fullOverrideNeg) {
                lCalculated = -1;
                rCalculated = -1;
            } else if (fullOverridePos) {
                lCalculated = 1;
                rCalculated = 1;
            }

            l0.setPower(lCalculated);
            r0.setPower(rCalculated);

            l1.setPower(lCalculated);
            r1.setPower(rCalculated);

            l2.setPower(lCalculated);
            r2.setPower(rCalculated);
            try {
                dbaos.writeLong(System.nanoTime() - ns);
                dbaos.writeDouble(lCalculated);
                dbaos.writeDouble(rCalculated);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            loops++;

            if(gamepad1.a){
                runDrive = false;
            }
        } else if (!hasWritten) {
            try {
                hasWritten = true;
                dos.writeInt(loops);
                dos.writeDouble(ourVoltage);
                dbaos.flush();
                baos.flush();
                baos.close();
                dos.write(baos.toByteArray());
                dos.close();
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
        }


    }


}

