package resq;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import ftc.team6460.javadeck.ftc.Utils;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Andrey Akhmetov on 12/6/2015.
 */
public class ResqBackupPlaybackAuton extends ResqAuton {
    public String getGoal() {
        return (sharedPref.getString("auton_goal_position", "INVALID"));

    }
    @Override
    public void main() throws InterruptedException {
        try {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
            boolean adjVoltage = sharedPref.getBoolean("doVoltageAdj", false);
            fillInSettings();
            startUpHardware();
            startCamera();

            l0.setDirection(DcMotor.Direction.REVERSE);
            l1.setDirection(DcMotor.Direction.REVERSE);
            r0.setDirection(DcMotor.Direction.FORWARD);
            r1.setDirection(DcMotor.Direction.FORWARD);
            FileInputStream inStream;
            String name = "FTCREC-";
            if (teamColor == ResqAuton.Colors.BLUE) {
                name = name + "BLUE-";
            } else {
                name = name + "RED-";
            }

            if (startSide == ResqAuton.Side.MIDLINE) {
                name = name + "MID-";
            } else {
                name = name + "MTN-";
            }
            name  = name + getGoal() + ".run";
            inStream = openFileInput(name);
            DataInputStream dis = new DataInputStream(inStream);
            int length = dis.readInt();
            double recordedVoltage = dis.readDouble(); // battery voltage at recording
            double ourVoltage;
            try {
                ourVoltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
            } catch (Exception e) {
                ourVoltage = 12.0;
            }
            if(recordedVoltage <= 0 || ourVoltage <= 0){
                adjVoltage = false;
            }
            long[] nanoStamps = new long[length];
            double[] leftDriveVals = new double[length];
            double[] rightDriveVals = new double[length];
            for (int i = 0; i < length; i++) {
                nanoStamps[i] = dis.readLong();
                leftDriveVals[i] = dis.readDouble();
                rightDriveVals[i] = dis.readDouble();
            }
            long maxStamp = nanoStamps[length-1];

            dis.close();

            this.waitForStart();
            long ns = System.nanoTime();
            long stNow;
            while((stNow=System.nanoTime() - ns) < maxStamp){
                int i = Arrays.binarySearch(nanoStamps, stNow);
                // binary search
                if(i < 0) i = -i-2;
                //edge case for binary search before first element. Jump to first element. No issue with that, since most recordings don't have critical movement there.
                if(i < 0) i = 0;
                telemetry.addData("IDX", i);
                /*int indexBefore = i;
                int indexAfter = i;
                double lStampWeight = 1;
                double rStampWeight = 0;
                if(i < 0){
                    indexBefore = -i-2;
                    indexAfter = -i-1;
                    if(indexBefore<0 ||indexAfter<0){ indexBefore = 0;
                    indexAfter = 0;
                    telemetry.addData("WARN", "WARN");}
                    else {
                        rStampWeight = (stNow - nanoStamps[indexBefore]) / (nanoStamps[indexAfter] - nanoStamps[indexBefore]);
                        lStampWeight = (nanoStamps[indexAfter] - stNow) / (nanoStamps[indexAfter] - nanoStamps[indexBefore]);
                        telemetry.addData("WARN", "NO");
                    }
                }

                double lMtr = lStampWeight * leftDriveVals[indexBefore] + rStampWeight * leftDriveVals[indexAfter];
                double rMtr = lStampWeight * rightDriveVals[indexBefore] + rStampWeight * rightDriveVals[indexAfter];
                telemetry.addData("LM", lMtr);
                telemetry.addData("RM", rMtr);
                if(adjVoltage){
                    lMtr *= recordedVoltage/ourVoltage;
                    rMtr *= recordedVoltage/ourVoltage;
                }
                setLeftSpeed(lMtr);
                setRightSpeed(rMtr);
                idle();
                */
                double lS = -leftDriveVals[i];
                double rS = -rightDriveVals[i];
                if(adjVoltage){
                    lS *= recordedVoltage/ourVoltage;
                    rS *= recordedVoltage/ourVoltage;
                }

                setLeftSpeed(lS);
                setRightSpeed(rS);
                idle();
            }
            try {
                switch (GoalPos.valueOf(getGoal())) {
                    case BEACON_BEHIND:
                        detectAndHitBeaconFwdForce();
                        break;
                    case BEACON_INFRONT:
                        detectAndHitBeaconBackForce();
                        break;
                }
            } catch(Exception e){
                // pass
            }

            /*double[] v = new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6};
            int i = Arrays.binarySearch(v, 0.31);
            if(i<0)
                System.out.println(v[-i-2]+","+v[-i-1]);
            else System.out.println(v[i]);*/


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (EOFException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void detectAndHitBeaconFwdForce() throws InterruptedException {
        ledCtrl.setPower(0);
        waitTime(1000);
        if (getTeam() == Colors.BLUE) {
            while ((!cb.getState().equals("RB")) && (!cb.getState().equals("BR"))) {
                setLeftSpeed(0.07);
                setRightSpeed(0.07);
                doPeriodicTasks();
            }
            setLeftSpeed(0);
            setRightSpeed(0);
            dumpClimbers();
            if (cb.getState().equals("RB")) {
                setLeftSpeed(-0.07);
                setRightSpeed(-0.07);
                while (true) {
                    if (cb.getState().equals("BB")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("RR")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }

                }

                setLeftSpeed(0.0);
                setRightSpeed(0.0);
                Log.e("MADEIT", "MADEIT");
                pushButton();
            } else {
                setLeftSpeed(0.07);
                setRightSpeed(0.07);
                while (true) {
                    if (cb.getState().equals("BB")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("RR")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }
                }
                setLeftSpeed(0.0);
                setRightSpeed(0.0);

                Log.e("MADEIT", "MADEIT");
                pushButton();
            }
        } else {
            while ((!cb.getState().equals("RB")) && (!cb.getState().equals("BR"))) {
                setLeftSpeed(0.07);
                setRightSpeed(0.07);
            }
            setLeftSpeed(0);
            setRightSpeed(0);

            dumpClimbers();
            if (cb.getState().equals("RB")) {
                setLeftSpeed(0.07);
                setRightSpeed(0.07);
                while (true) {
                    if (cb.getState().equals("RR")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("BB")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }
                }
                setLeftSpeed(0.0);
                setRightSpeed(0.0);
                pushButton();
            } else {
                setLeftSpeed(-0.07);
                setRightSpeed(-0.07);
                while (true) {
                    if (cb.getState().equals("RR")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("BB")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }
                }
                setLeftSpeed(0.0);
                setRightSpeed(0.0);
                pushButton();
                ledCtrl.setPower(1.0);
            }
        }
    }

    private void detectAndHitBeaconBackForce() throws InterruptedException {
        ledCtrl.setPower(0);
        waitTime(1000);
        if (getTeam() == Colors.BLUE) {
            while ((!cb.getState().equals("RB")) && (!cb.getState().equals("BR"))) {
                setLeftSpeed(-0.07);
                setRightSpeed(-0.07);
                doPeriodicTasks();
            }
            setLeftSpeed(0);
            setRightSpeed(0);
            dumpClimbers();
            if (cb.getState().equals("RB")) {
                setLeftSpeed(-0.07);
                setRightSpeed(-0.07);
                while (true) {
                    if (cb.getState().equals("BB")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("RR")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }

                }

                setLeftSpeed(0.0);
                setRightSpeed(0.0);
                Log.e("MADEIT", "MADEIT");
                pushButton();
            } else {
                setLeftSpeed(0.07);
                setRightSpeed(0.07);
                while (true) {
                    if (cb.getState().equals("BB")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("RR")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }
                }
                setLeftSpeed(0.0);
                setRightSpeed(0.0);

                Log.e("MADEIT", "MADEIT");
                pushButton();
            }
        } else {
            while ((!cb.getState().equals("RB")) && (!cb.getState().equals("BR"))) {
                setLeftSpeed(-0.07);
                setRightSpeed(-0.07);
            }
            setLeftSpeed(0);
            setRightSpeed(0);

            dumpClimbers();
            if (cb.getState().equals("RB")) {
                setLeftSpeed(0.07);
                setRightSpeed(0.07);
                while (true) {
                    if (cb.getState().equals("RR")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("BB")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }
                }
                setLeftSpeed(0.0);
                setRightSpeed(0.0);
                pushButton();
            } else {
                setLeftSpeed(-0.07);
                setRightSpeed(-0.07);
                while (true) {
                    if (cb.getState().equals("RR")) {
                        waitTime(Utils.safeInt(sharedPref.getString("camera_undershoot_correction", "0"), 0));
                        break;
                    }
                    if (cb.getState().equals("BB")) {
                        return;
                    }
                    if (cb.getState().contains("G")) {
                        return;
                    }
                }
                setLeftSpeed(0.0);
                setRightSpeed(0.0);
                pushButton();
                ledCtrl.setPower(1.0);
            }
        }
    }

    private FileInputStream openFileInput(String s) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(), s);
        return new FileInputStream(file);
    }

}
