package resq;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import ftc.team6460.javadeck.ftc.Utils;
import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.*;

import java.util.Arrays;

/**
 * Created by hon07726 on 10/2/2015.
 */
public class ResqAuton extends SynchronousOpMode{

    private static Side startSide;
    private static Colors teamColor;
    private static double curX, curY, curYAW;
    private double delay;

    public void main() throws InterruptedException {
        fillInSettings();
        startUpHardware();
        waitTime(1000);
        if (teamColor == Colors.BLUE) {
            if (startSide == Side.MOUNTAIN) {
                offsetPosition(err(), err(), err());
            } else if (startSide == Side.MIDLINE) {
                offsetPosition(err(), err(), err());
            }

        } else if (teamColor == Colors.RED) {
            if (startSide == Side.MOUNTAIN) {
                offsetPosition(err(), err(), err());
            } else if (startSide == Side.MIDLINE) {
                offsetPosition(err(), err(), err());
            }

        }
        waitForStart();
        waitAllianceTeamDelay();
        goForward(0.5); //Get away from wall
        if (teamColor == Colors.BLUE) {
            if (startSide == Side.MOUNTAIN) {
                navigateTo(err(), err(), err()); //REPLACE
            } else if (startSide == Side.MIDLINE) {
                navigateTo(err(), err(), err()); //REPLACE
            }

        } else if (teamColor == Colors.RED) {
            if (startSide == Side.MOUNTAIN) {
                navigateTo(err(), err(), err()); //REPLACE
            } else if (startSide == Side.MIDLINE) {
                navigateTo(err(), err(), err()); //REPLACE
            }

        }



    }

    private double err() {
        RuntimeException e = new RuntimeException("Fill in the caller!");
        e.printStackTrace();
        for(StackTraceElement ste : e.getStackTrace()){
            Log.e("FILLIN", ste.toString());
        }
        throw e;
    }

    private void waitTime(int i) {
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() < t + (i)) {
            doPeriodicTasks();
        }
    }


    public void fillInSettings() {
        startSide = getStartSide();
        teamColor = getTeam();
        delay = getAllyDelay();//such code
        throw new RuntimeException("You didn't finish the code, skrub!");

    }

    public void goForward(double secs, double speed) {
        setLeftSpeed(speed);
        setRightSpeed(speed);
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() < t + (secs * 1000)) {
            doPeriodicTasks();
        }
        setLeftSpeed(0);
        setRightSpeed(0);

    }
    public void goForward(double secs) {
        goForward(secs,100);

    }


    public void turnTo(double YAW) {
        curYAW = getGyroYAW();
        double angle = curYAW-YAW;
        if((curYAW-YAW)>180){
            angle = Math.abs(360-angle);
        }
        double incA = Math.min(15,angle); //the increment angle
        double oriYAW = getGyroYAW();
        double incr = 0;
        //begin turning cases
        if (((oriYAW>YAW) && (oriYAW-YAW)<180)) { //TURN RIGHT

            while((oriYAW-curYAW)<incA){
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr=100*((oriYAW-curYAW)/15);
                if(oriYAW-curYAW==incA){
                    break;
                }
                curYAW = getGyroYAW();

            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while ((oriYAW - curYAW) < (angle - incA)){
                setLeftSpeed(100);
                setRightSpeed(-100);
                curYAW = getGyroYAW();
            }
       	/* START DECLINING INCREMENT */
            while((oriYAW-curYAW)<angle){
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr=(100*(-(YAW-curYAW)/15));
                if(oriYAW-curYAW==incA){
                    break;
                }
                curYAW = getGyroYAW();
            }
            curYAW = getGyroYAW();
            if(curYAW<=YAW){
                setLeftSpeed(0);
                setRightSpeed(0);
            }
        }

        else if((oriYAW<YAW) && (oriYAW-YAW)>180){ //turn right past 0 line fuck the zero line btw
            curYAW = getGyroYAW();
            if(oriYAW>incA){
                while((oriYAW-curYAW)<incA){
                    setLeftSpeed(incr);
                    setRightSpeed(-incr);
                    incr=100*((oriYAW-curYAW)/15);
                    if(oriYAW-curYAW==incA){
                        break;
                    }
                    curYAW = getGyroYAW();
                }
            }
            else{
                curYAW = getGyroYAW();
                while(oriYAW+(360-curYAW)<incA){
                    setLeftSpeed(incr);
                    setRightSpeed(-incr);
                    incr=100*((oriYAW-curYAW)/15);
                    if(oriYAW+(360-curYAW)==incA){
                        break;
                    }
                    curYAW = getGyroYAW();
                }
            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while(curYAW<oriYAW){ //these two while loops are the same thing
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                curYAW = getGyroYAW();
            }
            while((360-curYAW)<(angle-incA)){//they're just accounting for before the 0 line and after it
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                curYAW = getGyroYAW();
            } //fuck the zero line so much @zeroline I hate u
       	/*START DECLINING INCREMENT SPEED */
            while((oriYAW+(360-curYAW))<angle){
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr = 100*(-(YAW-curYAW)/15);
                curYAW = getGyroYAW();
            }
            curYAW = getGyroYAW();
            if((oriYAW+(360-curYAW))>=angle){ //stops motors
                setLeftSpeed(0);
                setRightSpeed(0);
            }

        }
        else if((oriYAW>YAW)&&((oriYAW-YAW)>180)){ // LEFT TURN (DO THIS!!)
            while((curYAW-oriYAW)<incA){
                setLeftSpeed(-incr);
                setRightSpeed(incr);
                incr=100*((curYAW-oriYAW)/15);
                if(curYAW-oriYAW>=incA){
                    break;
                }
                curYAW = getGyroYAW();

            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while ((curYAW-oriYAW) < (angle - incA)){
                setLeftSpeed(-100);
                setRightSpeed(100);
                curYAW = getGyroYAW();
            }
       	/* START DECLINING INCREMENT */
            while((curYAW-oriYAW)<angle){
                setLeftSpeed(-incr);
                setRightSpeed(incr);
                incr=(100*(-(YAW-curYAW)/15));
                if(curYAW-oriYAW==incA){
                    break;
                }
                curYAW = getGyroYAW();
            }
            curYAW = getGyroYAW();
            if(curYAW>=YAW){
                setLeftSpeed(0);
                setRightSpeed(0);
            }
        }

    }
    DcMotor l0;
    DcMotor l1;
    DcMotor l2;
    DcMotor r0;
    DcMotor r1;
    DcMotor r2;
    DcMotor w;

    public void setLeftSpeed(double spd) {
        l0.setPower(spd);
        l1.setPower(spd);
        l2.setPower(spd);
    }

    public void setRightSpeed(double spd) {
        r0.setPower(spd);
        r1.setPower(spd);
        r2.setPower(spd);
    }

    public Side getStartSide() {
        return Side.valueOf(sharedPref.getString("auton_start_position", "INVALID"));
    } //get starting side from settings dialog (

    public Colors getTeam() {
        return Colors.valueOf(sharedPref.getString("auton_team_color", "INVALID"));

    } //get team from settings dialog

    public void offsetPosition(double X, double Y, double YAW) {
        x = X;
        y = Y;
        initYaw = YAW;
    }






    /**
     * wait <i>delay</i> seconds, then go on
     */
    public void waitAllianceTeamDelay() {
        waitTime(getAllyDelay());
    }
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
    public int getAllyDelay() {
        return (int)Utils.getSafeDoublePref("auton_beacon_area_clear_time", sharedPref, 5);
    }
//what does YAW give you -->

    /**
     * @param X   destination X coordinate
     * @param Y   destination Y coordinate
     * @param YAW destination YAW, set -1 for no destination YAW
     */
    public void navigateTo(double X, double Y, double YAW) {
        curX = getGyroX();
        curY = getGyroY();
        curYAW = getGyroYAW();
        long sTime = System.nanoTime();
        double bearing2Dest = Math.atan2(X-curX,Y-curY);
        while (!(Math.abs(curX-X)<=0.05) || !(Math.abs(curY-Y)<=0.05)) {
            while(Math.abs(bearing2Dest - curYAW)<= 2.5) {
                bearing2Dest = Math.atan2(X-curX,Y-curY);
                if(bearing2Dest - curYAW < 0)
                    turnTo(curYAW+0.1);
                else
                    turnTo(curYAW-0.1);
            }
            // divide by half a second
            goForward(0.01, Math.min(Math.min((System.nanoTime()-sTime) / 500_000_000.0, 1.0), 2*Math.hypot(curX-X, curY-Y)));
            curX = getGyroX();
            curY = getGyroY();
            curYAW = getGyroYAW();

        }
        if(YAW >=0) {
            if (((curYAW>YAW) && (curYAW-YAW)<180) || ((curYAW<YAW) && (curYAW-YAW)>180)) {
                while ((curYAW-YAW)>2.5 || (curYAW-YAW)<357.5) {
                    turnTo(YAW);
                }
            }
            else {
                while ((YAW-curYAW)>2.5 || (YAW-curYAW)<357.5) {
                    turnTo(YAW);
                }
            }
        }
    }

    public void detectAndHitBeacon() {
        throw new RuntimeException("Andrei will make something awesome here. Hit the beacon!");
    }

    public enum Side {
        MOUNTAIN, MIDLINE // returns whether you are starting close to the
    }

    public enum Colors {
        RED, BLUE
    }

    public enum Direction {
        RIGHT, LEFT, FORWARDS, BACKWARDS
    }

    IBNO055IMU              imu;
    ElapsedTime elapsed    = new ElapsedTime();
    IBNO055IMU.Parameters   parameters = new IBNO055IMU.Parameters();

    // Here we have state we use for updating the dashboard. The first of these is important
    // to read only once per update, as its acquisition is expensive. The remainder, though,
    // could probably be read once per item, at only a small loss in display accuracy.
    EulerAngles angles;
    Position position;
    Acceleration accel;
    private void startUpHardware() {
        l0 = hardwareMap.dcMotor.get("l0");
        r0 = hardwareMap.dcMotor.get("r0");

        l1 = hardwareMap.dcMotor.get("l1");
        r1 = hardwareMap.dcMotor.get("r1");

        l2 = hardwareMap.dcMotor.get("l2");
        r2 = hardwareMap.dcMotor.get("r2");

        w = hardwareMap.dcMotor.get("w");

        r0.setDirection(DcMotor.Direction.REVERSE);
        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);

        l0.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        l1.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        l2.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r0.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r1.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r2.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        w.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        lastEncoderL = new int[]{l0.getCurrentPosition(), l1.getCurrentPosition(), l2.getCurrentPosition()};
        lastEncoderR = new int[]{r0.getCurrentPosition(), r1.getCurrentPosition(), r2.getCurrentPosition()};
        parameters.angleunit      = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelunit      = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        parameters.mode           = IBNO055IMU.SENSOR_MODE.NDOF;
        parameters.loggingTag     = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("bno055"), parameters);
        // Enable reporting of position using the naive integrator

        // Set up our dashboard computations
        composeDashboard();
    }

    public double getGyroX() {
        return x;
    }

    public double getGyroY() {
        return y;
    }

    public double getGyroYAW() {
        return normalizeDegrees(angles.heading - initYaw);
    }

    double initYaw = 0;
    double x = 0;
    double y = 0;
    int[] lastEncoderL = new int[3];
    int[] lastEncoderR = new int[3];
    public void doPeriodicTasks() {
        angles = imu.getAngularOrientation();
        position = imu.getPosition();
        accel = imu.getLinearAcceleration();
        int[] uEL = new int[]{l0.getCurrentPosition()- lastEncoderL[0], l1.getCurrentPosition()- lastEncoderL[1], l2.getCurrentPosition()- lastEncoderL[2]};
        int[] uER = new int[]{r0.getCurrentPosition()- lastEncoderR[0], r1.getCurrentPosition()- lastEncoderR[1], r2.getCurrentPosition()- lastEncoderR[2]};
        Arrays.sort(uEL);
        Arrays.sort(uER);
        int delta = weightPositionEffects(uEL[1], uER[1]);
        double dist = remapWheelDiameter(delta);
        x += (dist * Math.cos(getGyroYAW()));
        y += (dist * Math.sin(getGyroYAW()));
        // The rest of this is pretty cheap to acquire, but we may as well do it
        // all while we're gathering the above.
        loopCycles = getLoopCount();
        i2cCycles = ((II2cDeviceClientUser) imu).getI2cDeviceClient().getI2cCycleCount();
        ms = elapsed.time() * 1000.0;
    }

    private double remapWheelDiameter(int delta) {
        throw new RuntimeException("BOO BUILDERS!");
    }

    private int weightPositionEffects(int l, int r) {
        return (l+r)/2;
    }

    int                     loopCycles;
    int                     i2cCycles;
    double                  ms;
    void composeDashboard()    {
        // The default dashboard update rate is a little to slow for us, so we update faster
        telemetry.setUpdateIntervalMs(200);

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() {
            @Override
            public void run() {
                // Acquiring the angles is relatively expensive; we don't want
                // to do that in each of the three items that need that info, as that's
                // three times the necessary expense.
                angles = imu.getAngularOrientation();
                position = imu.getPosition();
                accel = imu.getLinearAcceleration();
                // The rest of this is pretty cheap to acquire, but we may as well do it
                // all while we're gathering the above.
                loopCycles = getLoopCount();
                i2cCycles = ((II2cDeviceClientUser) imu).getI2cDeviceClient().getI2cCycleCount();
                ms = elapsed.time() * 1000.0;
            }
        });
        telemetry.addLine(
                telemetry.item("loop count: ", new IFunc<Object>() {
                    public Object value() {
                        return loopCycles;
                    }
                }),
                telemetry.item("i2c cycle count: ", new IFunc<Object>() {
                    public Object value() {
                        return i2cCycles;
                    }
                }));

        telemetry.addLine(
                telemetry.item("loop rate: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatRate(ms / loopCycles);
                    }
                }),
                telemetry.item("i2c cycle rate: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatRate(ms / i2cCycles);
                    }
                }));

        telemetry.addLine(
                telemetry.item("status: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return decodeStatus(imu.getSystemStatus());
                    }
                }),
                telemetry.item("calib: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return decodeCalibration(imu.read8(IBNO055IMU.REGISTER.CALIB_STAT));
                    }
                }));

        telemetry.addLine(
                telemetry.item("heading: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatAngle(angles.heading);
                    }
                }),
                telemetry.item("roll: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatAngle(angles.roll);
                    }
                }),
                telemetry.item("pitch: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatAngle(angles.pitch);
                    }
                }));

        telemetry.addLine(
                telemetry.item("x: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatPosition(position.x);
                    }
                }),
                telemetry.item("y: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatPosition(position.y);
                    }
                }),
                telemetry.item("z: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatPosition(position.z);
                    }
                }));

        telemetry.addLine(
                telemetry.item("cal: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return imu.isSystemCalibrated();
                    }
                })
        );
        telemetry.addLine(
                telemetry.item("xa: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatPosition(accel.accelX);
                    }
                }),
                telemetry.item("ya: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatPosition(accel.accelY);
                    }
                }),
                telemetry.item("za: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatPosition(accel.accelZ);
                    }
                }));

    }

    String formatAngle(double angle)    {
        return parameters.angleunit==IBNO055IMU.ANGLEUNIT.DEGREES ? formatDegrees(angle) : formatRadians(angle);
    }
    String formatRadians(double radians)
    {
        return formatDegrees(degreesFromRadians(radians));
    }
    String formatDegrees(double degrees)
    {
        return String.format("%.1f", normalizeDegrees(degrees));
    }
    String formatRate(double cyclesPerSecond)
    {
        return String.format("%.2f", cyclesPerSecond);
    }
    String formatPosition(double coordinate)
    {
        String unit = parameters.accelunit== IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC
                ? "m" : "??";
        return String.format("%.2f%s", coordinate, unit);
    }

    /** Normalize the angle into the range [-180,180) */
    double normalizeDegrees(double degrees)
    {
        while (degrees >= 360) degrees -= 360.0;
        while (degrees < 0.0) degrees += 360.0;
        return degrees;
    }
    double degreesFromRadians(double radians)
    {
        return radians * 180.0 / Math.PI;
    }

    /** Turn a system status into something that's reasonable to show in telemetry */
    String decodeStatus(int status)
    {
        switch (status)
        {
            case 0: return "idle";
            case 1: return "syserr";
            case 2: return "periph";
            case 3: return "sysinit";
            case 4: return "selftest";
            case 5: return "fusion";
            case 6: return "running";
        }
        return "unk";
    }

    /** Turn a calibration code into something that is reasonable to show in telemetry */
    String decodeCalibration(int status)
    {
        StringBuilder result = new StringBuilder();

        result.append(String.format("s%d", (status >> 6) & 0x03));  // SYS calibration status
        result.append(" ");
        result.append(String.format("g%d", (status >> 4) & 0x03));  // GYR calibration status
        result.append(" ");
        result.append(String.format("a%d", (status >> 2) & 0x03));  // ACC calibration status
        result.append(" ");
        result.append(String.format("m%d", (status >> 0) & 0x03));  // MAG calibration status

        return result.toString();
    }
}


