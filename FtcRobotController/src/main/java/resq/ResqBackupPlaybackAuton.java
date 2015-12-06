package resq;

import android.preference.PreferenceManager;
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

            FileInputStream inStream;
            if (teamColor == Colors.BLUE) {
                if (startSide == Side.MOUNTAIN) {
                    inStream = hardwareMap.appContext.openFileInput("bluemtn.RUN");
                } else {
                    inStream = hardwareMap.appContext.openFileInput("bluemid.RUN");
                }

            } else {
                if (startSide == Side.MOUNTAIN) {
                    inStream = hardwareMap.appContext.openFileInput("redmtn.RUN");
                } else {
                    inStream = hardwareMap.appContext.openFileInput("redmid.RUN");
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
                int indexBefore = i;
                int indexAfter = i;
                double lStampWeight = 1;
                double rStampWeight = 0;
                if(i < 0){
                    indexBefore = -i-2;
                    indexAfter = -i-1;
                    rStampWeight = (stNow-nanoStamps[indexBefore])/(nanoStamps[indexAfter]-nanoStamps[indexBefore]);
                    lStampWeight = (nanoStamps[indexAfter]-stNow)/(nanoStamps[indexAfter]-nanoStamps[indexBefore]);
                }

                double lMtr = lStampWeight * leftDriveVals[indexBefore] + rStampWeight * leftDriveVals[indexAfter];
                double rMtr = lStampWeight * rightDriveVals[indexBefore] + rStampWeight * rightDriveVals[indexAfter];

                if(adjVoltage){
                    lMtr *= recordedVoltage/ourVoltage;
                    rMtr *= recordedVoltage/ourVoltage;
                }
                setLeftSpeed(lMtr);
                setRightSpeed(rMtr);
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
}
