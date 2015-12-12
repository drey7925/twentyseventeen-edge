package resq;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Andrey Akhmetov on 12/6/2015.
 */
public class ResqBackupPlaybackAuton extends ResqAuton {
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
            if (teamColor == ResqAuton.Colors.BLUE) {
                if (startSide == ResqAuton.Side.MOUNTAIN) {
                    inStream = openFileInput("bluemtn.RUN");
                    telemetry.addData("FILENAME", "bluemtn.RUN");
                } else {
                    inStream = openFileInput("bluemid.RUN");
                    telemetry.addData("FILENAME", "bluemid.RUN");
                }

            } else {
                if (startSide == ResqAuton.Side.MOUNTAIN) {
                    inStream = openFileInput("redmtn.RUN");
                    telemetry.addData("FILENAME", "redmtn.RUN");
                } else {
                    inStream = openFileInput("redmid.RUN");
                    telemetry.addData("FILENAME", "redmid.RUN");
                }

            }
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
                setLeftSpeed(-leftDriveVals[i]);
                setRightSpeed(-rightDriveVals[i]);
                idle();
            }

            detectAndHitBeacon();


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

    private FileInputStream openFileInput(String s) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(), s);
        return new FileInputStream(file);
    }

}
