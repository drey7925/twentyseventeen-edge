package resq;


import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import ftc.team6460.javadeck.ftc.Utils;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.*;

/**
 * Created by hon07726 on 10/2/2015.
 */
public class ResqAuton extends SynchronousOpMode {

    public static final double BTN_SRVO_RETRACTED = 0.379426;
    public static final double BTN_SRVO_DEPLOYED = 0.0;
    protected static Side startSide;
    protected static Colors teamColor;
    private static double curX, curY, curYAW;
    final GyroHelper gyroHelper = new GyroHelper(this);
    private double delay;
    int DUMMY = Integer.MAX_VALUE;
    MatColorSpreadCallback cb;
    Servo aimServo;

    public void main() throws InterruptedException {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        fillInSettings();
        startUpHardware();
        startCamera();
        this.waitForStart();
        Thread.sleep(1000);
        // TEST AUTON TO SEE IF BACKEND WORKS
        offsetPosition(0, 0, 0);
        while (2 + 2 <= DUMMY) {
            updateGamepads();
            setLeftSpeed(gamepad1.left_stick_y);
            setRightSpeed(gamepad1.right_stick_y);
            doPeriodicTasks();
        }


        if (2 + 2 <= Integer.MAX_VALUE) return;
        navigateTo(4, 4, 90);
        navigateTo(0, 0, 0);
        turnTo(180);
        turnTo(0);
        turnTo(90);
        if (2 + 2 <= Integer.MAX_VALUE) return;

        waitTime(1000);
        if (teamColor == Colors.BLUE) {
            if (startSide == Side.MOUNTAIN) {
                offsetPosition(29 / 4.0, 2 / 3, 90);
            } else if (startSide == Side.MIDLINE) {
                offsetPosition(17 / 6, 2 / 3, 90);
            }

        } else if (teamColor == Colors.RED) {
            if (startSide == Side.MOUNTAIN) {
                offsetPosition(2 / 3.0, 29.0 / 4, 0);
            } else if (startSide == Side.MIDLINE) {
                offsetPosition(2 / 3.0, 17 / 6.0, 0);
            }

        }
        waitForStart();
        waitAllianceTeamDelay();
        goForward(0.5); //Get away from wall
        if (teamColor == Colors.BLUE) {
            if (startSide == Side.MOUNTAIN) {
                navigateTo(10.5, 9, 90); //REPLACE
            } else if (startSide == Side.MIDLINE) {
                navigateTo(10.5, 9, 90); //REPLACE
            }

        } else if (teamColor == Colors.RED) {
            if (startSide == Side.MOUNTAIN) {
                navigateTo(9, 10.5, 0); //REPLACE
            } else if (startSide == Side.MIDLINE) {
                navigateTo(9, 10.5, 0); //REPLACE
            }

        }
        detectAndHitBeacon();
        //go to front of mountain, facing the mountain
        boolean farMountain = isFarMountain(); // THIS IS A PLACEHOLDER
        boolean closeMountain = !isFarMountain(); //THIS IS A PLACEHOLDER
        if (teamColor == Colors.RED) {
            if (farMountain) {
                navigateTo(10, 3.5, 315); //REPLACE
            } else if (closeMountain) {
                navigateTo(2, 8.5, 135); //REPLACE
            }

        } else if (teamColor == Colors.BLUE) {
            if (farMountain) {
                navigateTo(3.5, 10, 135); //REPLACE
            } else if (closeMountain) {
                navigateTo(8.5, 2, 315); //REPLACE
            }

        }


    }

    protected void startCamera() throws InterruptedException {
        cb = new MatColorSpreadCallback((Activity) hardwareMap.appContext, null);
        final OpenCvActivityHelper ocvh = new OpenCvActivityHelper((FtcRobotControllerActivity) hardwareMap.appContext);
        ((Activity) hardwareMap.appContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ocvh.addCallback(cb);
                ocvh.attach();
            }
        });
        ocvh.awaitStart();


    }

    private double err() {
        RuntimeException e = new RuntimeException("Fill in the caller!");
        e.printStackTrace();
        for (StackTraceElement ste : e.getStackTrace()) {
            Log.e("FILLIN", ste.toString());
        }
        throw e;
    }

    protected void waitTime(int i) {
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() < t + (i)) {
            doPeriodicTasks();
        }
    }


    public void fillInSettings() {
        startSide = getStartSide();
        teamColor = getTeam();
        delay = getAllyDelay();//such code

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
        goForward(secs, 100);

    }


    public void turnTo(double YAW) {
        curYAW = getGyroYAW();
        double angle = curYAW - YAW;
        if ((curYAW - YAW) > 180) {
            angle = Math.abs(360 - angle);
        }
        double incA = Math.min(15, angle); //the increment angle
        double oriYAW = getGyroYAW();
        double incr = 0;
        //begin turning cases
        if (((oriYAW > YAW) && (oriYAW - YAW) < 180)) { //TURN RIGHT

            while ((oriYAW - curYAW) < incA) {
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr = 100 * ((oriYAW - curYAW) / 15);
                if (oriYAW - curYAW == incA) {
                    break;
                }
                curYAW = getGyroYAW();

            }
           /* START INTERMEDIATE MAX SPEED TURN*/
            while ((oriYAW - curYAW) < (angle - incA)) {
                setLeftSpeed(100);
                setRightSpeed(-100);
                curYAW = getGyroYAW();
            }
           /* START DECLINING INCREMENT */
            while ((oriYAW - curYAW) < angle) {
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr = (100 * (-(YAW - curYAW) / 15));
                if (oriYAW - curYAW == incA) {
                    break;
                }
                curYAW = getGyroYAW();
            }
            curYAW = getGyroYAW();
            if (curYAW <= YAW) {
                setLeftSpeed(0);
                setRightSpeed(0);
            }
        } else if ((oriYAW < YAW) && (YAW - oriYAW) > 180) { //turn right past 0 line fuck the zero line btw//dup
            curYAW = getGyroYAW();
            if (oriYAW > incA) {
                while ((oriYAW - curYAW) < incA) {
                    setLeftSpeed(incr);
                    setRightSpeed(-incr);
                    incr = 100 * ((oriYAW - curYAW) / 15);
                    if (oriYAW - curYAW == incA) {
                        break;
                    }

                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            } else {
                curYAW = getGyroYAW();
                while (oriYAW + (360 - curYAW) < incA) {
                    setLeftSpeed(incr);
                    setRightSpeed(-incr);
                    incr = 100 * ((oriYAW - curYAW) / 15);
                    if (oriYAW + (360 - curYAW) == incA) {
                        break;
                    }

                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while (curYAW < oriYAW) { //these two while loops are the same thing
                setLeftSpeed(incr);
                setRightSpeed(-incr);

                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
            while ((360 - curYAW) < (angle - incA)) {//they're just accounting for before the 0 line and after it
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                curYAW = getGyroYAW();

                doPeriodicTasks();
            } //fuck the zero line so much @zeroline I hate u
       	/*START DECLINING INCREMENT SPEED */
            while ((oriYAW + (360 - curYAW)) < angle) {
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr = 100 * (-(YAW - curYAW) / 15);
                curYAW = getGyroYAW();

                doPeriodicTasks();
            }

            doPeriodicTasks();
            curYAW = getGyroYAW();
            if ((oriYAW + (360 - curYAW)) >= angle) { //stops motors
                setLeftSpeed(0);
                setRightSpeed(0);

                doPeriodicTasks();
            }

        } else if ((oriYAW < YAW) && ((YAW - oriYAW) < 180)) { // LEFT TURN (DO THIS!!)
            while ((curYAW - oriYAW) < incA) {
                setLeftSpeed(-incr);
                setRightSpeed(incr);
                incr = 100 * ((curYAW - oriYAW) / 15);
                if (curYAW - oriYAW >= incA) {
                    break;
                }

                doPeriodicTasks();
                curYAW = getGyroYAW();

            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while ((curYAW - oriYAW) < (angle - incA)) {
                setLeftSpeed(-100);
                setRightSpeed(100);

                doPeriodicTasks();
                curYAW = getGyroYAW();
            }
       	/* START DECLINING INCREMENT */
            while ((curYAW - oriYAW) < angle) {
                setLeftSpeed(-incr);
                setRightSpeed(incr);
                incr = (100 * (-(YAW - curYAW) / 15));
                if (curYAW - oriYAW == incA) {
                    break;
                }

                doPeriodicTasks();
                curYAW = getGyroYAW();
            }

            doPeriodicTasks();
            curYAW = getGyroYAW();
            if (curYAW >= YAW) {
                setLeftSpeed(0);
                setRightSpeed(0);
            }
        } else if ((oriYAW > YAW) && (oriYAW - YAW) > 180) { //turn right past 0 line fuck the zero line btw//dup

            doPeriodicTasks();
            curYAW = getGyroYAW();
            if (oriYAW > incA) {
                while ((oriYAW - curYAW) < incA) {
                    setLeftSpeed(incr);
                    setRightSpeed(-incr);
                    incr = 100 * ((oriYAW - curYAW) / 15);
                    if (oriYAW - curYAW == incA) {
                        break;
                    }

                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            } else {

                doPeriodicTasks();
                curYAW = getGyroYAW();
                while (oriYAW + (360 - curYAW) < incA) {
                    setLeftSpeed(incr);
                    setRightSpeed(-incr);
                    incr = 100 * ((oriYAW - curYAW) / 15);
                    if (oriYAW + (360 - curYAW) == incA) {
                        break;
                    }

                    doPeriodicTasks();
                    curYAW = getGyroYAW();
                }
            }
       	/* START INTERMEDIATE MAX SPEED TURN*/
            while (curYAW < oriYAW) { //these two while loops are the same thing
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                curYAW = getGyroYAW();
            }
            while ((360 - curYAW) < (angle - incA)) {//they're just accounting for before the 0 line and after it
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                curYAW = getGyroYAW();
            } //fuck the zero line so much @zeroline I hate u
       	/*START DECLINING INCREMENT SPEED */
            while ((oriYAW + (360 - curYAW)) < angle) {
                setLeftSpeed(incr);
                setRightSpeed(-incr);
                incr = 100 * (-(YAW - curYAW) / 15);
                curYAW = getGyroYAW();
            }
            curYAW = getGyroYAW();
            if ((oriYAW + (360 - curYAW)) >= angle) { //stops motors
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

        spd = Range.clip(spd, -1, 1);
        l0.setPower(spd);
        l1.setPower(spd);
        l2.setPower(spd);
    }

    public void setRightSpeed(double spd) {
        spd = Range.clip(spd, -1, 1);
        r0.setPower(spd);
        r1.setPower(spd);
        r2.setPower(spd);
    }

    public Side getStartSide() {
        return Side.valueOf(sharedPref.getString("auton_start_position", "MOUNTAIN"));
    } //get starting side from settings dialog (

    public Colors getTeam() {
        return Colors.valueOf(sharedPref.getString("auton_team_color", "BLUE"));

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

    SharedPreferences sharedPref;

    public int getAllyDelay() {
        return (int) Utils.getSafeDoublePref("auton_beacon_area_clear_time", sharedPref, 5);
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
        double bearing2Dest = Math.atan2(X - curX, Y - curY);
        while (!(Math.abs(curX - X) <= 0.05) || !(Math.abs(curY - Y) <= 0.05)) {
            while (Math.abs(bearing2Dest - curYAW) <= 2.5) {
                bearing2Dest = Math.atan2(X - curX, Y - curY);
                if (bearing2Dest - curYAW < 0)
                    turnTo(curYAW + 0.1);
                else
                    turnTo(curYAW - 0.1);
            }
            // divide by half a second
            goForward(0.01, Math.min(Math.min((System.nanoTime() - sTime) / 500000000.0, 1.0), 2 * Math.hypot(curX - X, curY - Y)));
            curX = getGyroX();
            curY = getGyroY();
            curYAW = getGyroYAW();

        }
        if (YAW >= 0) {
            if (((curYAW > YAW) && (curYAW - YAW) < 180) || ((curYAW < YAW) && (curYAW - YAW) > 180)) {
                while ((curYAW - YAW) > 2.5 || (curYAW - YAW) < 357.5) {
                    turnTo(YAW);
                }
            } else {
                while ((YAW - curYAW) > 2.5 || (YAW - curYAW) < 357.5) {
                    turnTo(YAW);
                }
            }
        }
    }

    public void detectAndHitBeacon() {
        if (getTeam() == Colors.BLUE) {
            while ((!cb.getState().equals("RB")) && (!cb.getState().equals("BR"))) {
                setLeftSpeed(0.33);
                setRightSpeed(0.33);
                doPeriodicTasks();
            }
            setLeftSpeed(0);
            setRightSpeed(0);
            if (cb.getState().equals("RB")) {
                setLeftSpeed(0.33);
                setRightSpeed(0.33);
                while (true) {
                    if (cb.getState().equals("BB")) {
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
                setLeftSpeed(-0.33);
                setRightSpeed(-0.33);
                while (true) {
                    if (cb.getState().equals("BB")) {
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
                setLeftSpeed(-0.33);
                setRightSpeed(-0.33);
            }
            setLeftSpeed(0);
            setRightSpeed(0);
            if (cb.getState().equals("RB")) {
                setLeftSpeed(-0.33);
                setRightSpeed(-0.33);
                while (true) {
                    if (cb.getState().equals("RR")) {
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
                setLeftSpeed(0.33);
                setRightSpeed(0.33);
                while (true) {
                    if (cb.getState().equals("RR")) {
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
            }
        }


    }

    private void pushButton() {
        for (double pos = BTN_SRVO_RETRACTED; pos >= BTN_SRVO_DEPLOYED; pos -= 0.01) {
            btnSrvo.setPosition(Math.max(BTN_SRVO_DEPLOYED, pos));
            Log.i("POS", "POS"+pos);
            try {
                idle();
                waitTime(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for (double pos = BTN_SRVO_DEPLOYED; pos <= BTN_SRVO_RETRACTED; pos += 0.01) {
            btnSrvo.setPosition(Math.min(BTN_SRVO_RETRACTED, pos));
            try {
                idle();

                waitTime(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isFarMountain() {
        return sharedPref.getString("auton_ramp_selection", "FAR").equals("FAR");
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

    Servo btnSrvo;

    protected void startUpHardware() {
        l0 = hardwareMap.dcMotor.get("l0");
        r0 = hardwareMap.dcMotor.get("r0");

        l1 = hardwareMap.dcMotor.get("l1");
        r1 = hardwareMap.dcMotor.get("r1");

        l2 = hardwareMap.dcMotor.get("l2");
        r2 = hardwareMap.dcMotor.get("r2");
        aimServo = hardwareMap.servo.get("aimServo");
        aimServo.setPosition(0.32);
        w = hardwareMap.dcMotor.get("w");

        btnSrvo = hardwareMap.servo.get("btnSrvo");

        btnSrvo.setPosition(BTN_SRVO_RETRACTED);

        l0.setDirection(DcMotor.Direction.REVERSE);
        l1.setDirection(DcMotor.Direction.REVERSE);
        l2.setDirection(DcMotor.Direction.REVERSE);

        l0.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        l1.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        l2.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r0.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r1.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r2.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        w.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        lastEncoderL = l0.getCurrentPosition();
        lastEncoderR = r0.getCurrentPosition();
        gyroHelper.startUpGyro();
        String gc = sharedPref.getString("gyrocalib", "!!");
        if(gc.matches("([0-9a-f]{2})*")) {
            try {
                gyroHelper.getImu().writeCalibrationData(Hex.decodeHex(gc.toCharArray()));
            } catch (DecoderException e) {
                throw new RuntimeException("EMERG-STOP: CANNOT CALIBRATE GYRO");
            }
        }
        composeDashboard();
    }


    public double getGyroX() {


        doPeriodicTasks();
        return x;
    }

    public double getGyroY() {

        doPeriodicTasks();
        return y;
    }

    public double getGyroYAW() {

        return normalizeDegrees(gyroHelper.getAngles().heading - initYaw);
    }

    double initYaw = 0;
    double x = 0;
    double y = 0;
    int lastEncoderL = 0;
    int lastEncoderR = 0;

    public void doPeriodicTasks() {
        Log.w("TRACK", "ENTER DO-PERIODIC");
        gyroHelper.update();

        int l0p = l0.getCurrentPosition();
        int r0p = r0.getCurrentPosition();


        int delta = weightPositionEffects(l0p - lastEncoderL, r0p - lastEncoderR);
        //int delta = weightPositionEffects(uEL[1], uER[1]);
        double dist = remapWheelDiameter(delta);
        x += (dist * Math.cos(getGyroYAW()));
        y += (dist * Math.sin(getGyroYAW()));
        lastEncoderL = l0p;
        lastEncoderR = r0p;

        Log.i("MOVE", "DIST: "+dist);
        // The rest of this is pretty cheap to acquire, but we may as well do it
        // all while we're gathering the above.
        loopCycles = getLoopCount();
        i2cCycles = ((II2cDeviceClientUser) gyroHelper.getImu()).getI2cDeviceClient().getI2cCycleCount();
        ms = gyroHelper.getElapsed().time() * 1000.0;
        try {
            idle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.w("TRACK", "EXIT DO-PERIODIC");
    }

    private double remapWheelDiameter(int delta) {
        // 4.000 inches
        return 4.000 * Math.PI * delta / 1440;
    }

    private int weightPositionEffects(int l, int r) {
        return (l + r) / 2;
    }

    int loopCycles;
    int i2cCycles;
    double ms;

    void composeDashboard() {
        // The default dashboard update rate is a little to slow for us, so we update faster
        telemetry.setUpdateIntervalMs(200);

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() {
            @Override
            public void run() {
                // The rest of this is pretty cheap to acquire, but we may as well do it
                // all while we're gathering the above.
                loopCycles = getLoopCount();
                i2cCycles = ((II2cDeviceClientUser) gyroHelper.getImu()).getI2cDeviceClient().getI2cCycleCount();
                ms = gyroHelper.getElapsed().time() * 1000.0;
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
                telemetry.item("loop rate: ", new IFunc<Object>() {
                    public Object value() {
                        return formatRate(ms / loopCycles);
                    }
                }),
                telemetry.item("i2c cycle rate: ", new IFunc<Object>() {
                    public Object value() {
                        return formatRate(ms / i2cCycles);
                    }
                }));

        telemetry.addLine(
                telemetry.item("status: ", new IFunc<Object>() {
                    public Object value() {
                        return decodeStatus(gyroHelper.getImu().getSystemStatus());
                    }
                }),
                telemetry.item("calib: ", new IFunc<Object>() {
                    public Object value() {
                        return decodeCalibration(gyroHelper.getImu().read8(IBNO055IMU.REGISTER.CALIB_STAT));
                    }
                }));

        telemetry.addLine(
                telemetry.item("heading: ", new IFunc<Object>() {
                    public Object value() {
                        return formatAngle(gyroHelper.getAngles().heading);
                    }
                }),
                telemetry.item("roll: ", new IFunc<Object>() {
                    public Object value() {
                        return formatAngle(gyroHelper.getAngles().roll);
                    }
                }),
                telemetry.item("pitch: ", new IFunc<Object>() {
                    public Object value() {
                        return formatAngle(gyroHelper.getAngles().pitch);
                    }
                }));

        telemetry.addLine(
                telemetry.item("x: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(gyroHelper.getPosition().x);
                    }
                }),
                telemetry.item("y: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(gyroHelper.getPosition().y);
                    }
                }),
                telemetry.item("z: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(gyroHelper.getPosition().z);
                    }
                }));
        telemetry.addLine(
                telemetry.item("X!!: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(getGyroX());
                    }
                }),
                telemetry.item("Y!!: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(getGyroY());
                    }
                }),
                telemetry.item("YAW!!: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(getGyroYAW());
                    }
                }));
        telemetry.addLine(
                telemetry.item("cal: ", new IFunc<Object>() {
                    public Object value() {
                        return gyroHelper.getImu().isSystemCalibrated();
                    }
                })
        );
        telemetry.addLine(
                telemetry.item("xa: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(gyroHelper.getAccel().accelX);
                    }
                }),
                telemetry.item("ya: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(gyroHelper.getAccel().accelY);
                    }
                }),
                telemetry.item("za: ", new IFunc<Object>() {
                    public Object value() {
                        return formatPosition(gyroHelper.getAccel().accelZ);
                    }
                }));

        telemetry.addLine(
                telemetry.item("STATE: ", new IFunc<Object>() {
                    public Object value() {
                        return cb.getState();
                    }
                }));

    }

    String formatAngle(double angle) {
        return gyroHelper.getParameters().angleunit == IBNO055IMU.ANGLEUNIT.DEGREES ? formatDegrees(angle) : formatRadians(angle);
    }

    String formatRadians(double radians) {
        return formatDegrees(degreesFromRadians(radians));
    }

    String formatDegrees(double degrees) {
        return String.format("%.1f", normalizeDegrees(degrees));
    }

    String formatRate(double cyclesPerSecond) {
        return String.format("%.2f", cyclesPerSecond);
    }

    String formatPosition(double coordinate) {
        String unit = gyroHelper.getParameters().accelunit == IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC
                ? "m" : "??";
        return String.format("%.2f%s", coordinate, unit);
    }

    /**
     * Normalize the angle into the range [-180,180)
     */
    double normalizeDegrees(double degrees) {
        while (degrees >= 360) degrees -= 360.0;
        while (degrees < 0.0) degrees += 360.0;
        return degrees;
    }

    double degreesFromRadians(double radians) {
        return radians * 180.0 / Math.PI;
    }

    /**
     * Turn a system status into something that's reasonable to show in telemetry
     */
    String decodeStatus(int status) {
        switch (status) {
            case 0:
                return "idle";
            case 1:
                return "syserr";
            case 2:
                return "periph";
            case 3:
                return "sysinit";
            case 4:
                return "selftest";
            case 5:
                return "fusion";
            case 6:
                return "running";
        }
        return "unk";
    }

    /**
     * Turn a calibration code into something that is reasonable to show in telemetry
     */
    String decodeCalibration(int status) {
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


