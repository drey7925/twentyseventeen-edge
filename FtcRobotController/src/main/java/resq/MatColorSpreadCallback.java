package resq;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import org.opencv.core.Mat;

/**
 * Created by hexafraction on 9/30/15.
 */
public class MatColorSpreadCallback implements MatCallback {
    private Activity cx;
    private TextView tv;
    private volatile String overText = "";
    long lastTs;
    long lastTime;
    public void setOverText(String overText) {
        this.overText = overText;
    }

    public String getState() {
        return state;
    }

    private volatile String state;

    public MatColorSpreadCallback(final Activity cx, final TextView tv) {
        this.cx = cx;
        this.tv = tv;
    }  // rdepend callback


    @Override
    public void handleMat(Mat mat) { //called on every frame

        int row = mat.rows() / 2; // find middle row
        int cols = mat.cols();
        double xT = 0, yT = 0;
        float[] hsv = new float[3];
        byte[] rgb = new byte[3];
        int mTotal = 0;

        // center oriented weight
        // statistics to find mean only, not s-err value for this data sample
        // stdev requires additional trig and a second pass

        /* Weighting

                   *   C   *
                *****  T  ******
            ***********R***********
         */

        for (int i = 0; i < cols / 2; i += 8) { // for each pixel in left: Add unitized vector to vecsum
            int mul = Math.min(i, (cols / 2 - i) * 3);
            //convert RGB to HSV
            mat.get(row, i, rgb);
            Color.RGBToHSV(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF, hsv);
            if (hsv[2] > 0.1 && hsv[1] > 0.3) {
                xT += Math.cos(Math.toRadians(hsv[0])) * mul;
                yT += Math.sin(Math.toRadians(hsv[0])) * mul;
                mTotal += mul;
            }
        }

        String lS;
        double theta = Math.toDegrees(Math.atan2(yT, xT));

        if (theta < (-60)) lS = "B";
        else if (theta > (60)) lS = "G";
        else lS = "R";
        Log.v("CLRES", String.format("theta: %f rad: %f samples: %d", theta, Math.hypot(yT / mTotal, xT / mTotal), mTotal));

        // repeat for other side.
        xT = 0;
        yT = 0;
        mTotal = 0;
        for (int i = cols / 2; i < cols; i += 8) { // for each pixel in right: Add unitized vector to vecsum
            int mul = Math.min((i - cols / 2) * 3, cols - i);
            mat.get(row, i, rgb);
            Color.RGBToHSV(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF, hsv);
            if (hsv[2] > 0.1 && hsv[1] > 0.3) {
                xT += Math.cos(Math.toRadians(hsv[0])) * mul;
                yT += Math.sin(Math.toRadians(hsv[0])) * mul;
                mTotal += mul;
            }

        }

        String rS;
        double thetaR = Math.toDegrees(Math.atan2(yT, xT));
        if (thetaR < (-60)) rS = "B";
        else if (thetaR > (60)) rS = "G";
        else rS = "R";
        Log.v("CRRES", String.format("theta: %f rad: %f samples: %d", thetaR, Math.hypot(yT / mTotal, xT / mTotal), mTotal));
        state = lS + rS;
        cx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tv != null) tv.setText(state);
            }
        });
        Log.i("STATE", state);
        lastTime = System.currentTimeMillis() - lastTs;
        Log.d("PERF", "Last frame millis: " + lastTime);
        lastTs = System.currentTimeMillis();
    }

    @Override
    public void draw(Canvas canvas) {
        //self explanatory
        if(state==null) return;
        Paint p = new Paint();
        switch (state.charAt(0)) {
            case 'R':
                p.setColor(Color.RED);
                break;
            case 'G':
                p.setColor(Color.GREEN);
                break;
            case 'B':
                p.setColor(Color.BLUE);
                break;
            default:
                p.setColor(Color.YELLOW);
        }
        canvas.drawRect(0, 0, canvas.getWidth()/2, canvas.getHeight() / 16, p);
        switch (state.charAt(1)) {
            case 'R':
                p.setColor(Color.RED);
                break;
            case 'G':
                p.setColor(Color.GREEN);
                break;
            case 'B':
                p.setColor(Color.BLUE);
                break;
            default:
                p.setColor(Color.YELLOW);
        }
        canvas.drawRect(canvas.getWidth()/2, 0, canvas.getWidth(), canvas.getHeight() / 16, p);
        p.setColor(Color.RED);

        canvas.drawLine(0, canvas.getHeight()/2, canvas.getWidth(), canvas.getHeight()/2, p);
        canvas.drawText(overText, 0, canvas.getHeight()/3, p);
        //canvas.drawText(Long.toString(lastTime), canvas.getWidth()/2, canvas.getHeight()/3, p);
    }
}
