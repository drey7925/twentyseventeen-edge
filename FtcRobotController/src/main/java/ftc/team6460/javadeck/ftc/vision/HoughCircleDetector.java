package ftc.team6460.javadeck.ftc.vision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class HoughCircleDetector implements MatCallback {
    Mat gray;
    Mat circles;
    private int param2;
    private int param1;

    public HoughCircleDetector(int param2, int param1) {
        this.param2 = param2;
        this.param1 = param1;
    }

    @Override
    public synchronized void handleMat(Mat mat) {
        if(param1==0 || param2==0){
            return;
        }
        if(gray==null) gray = new Mat();
        if(circles==null) circles = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(gray, gray, new Size(9,9));

        param2 = 60;
        param1 = 200;
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1, gray.height()/16, param1, param2, 0,0);

    }
float[] temp = new float[3];

    @Override
    public synchronized void draw(Canvas canvas) {
        if(param1==0 || param2==0){
            return;
        }
        if(circles==null) return;

        float scaleX = canvas.getWidth()/(float)gray.width();
        float scaleY = canvas.getHeight()/(float)gray.height();
        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);

        p.setColor(Color.RED);

        Paint q = new Paint();
        q.setStyle(Paint.Style.FILL_AND_STROKE);
        q.setColor(Color.GREEN);
        for(int i = 0; i < circles.height(); i++){
            for(int j = 0; j < circles.width(); j++){
                circles.get(i,j,temp);


                canvas.drawOval(new RectF((temp[0]-temp[2])*scaleX, (temp[1]-temp[2])*scaleY,
                        (temp[0]+temp[2])*scaleX, (temp[1]+temp[2])*scaleY), p);
                //p.setColor(Color.GREEN);
                canvas.drawCircle(temp[0]*scaleX, temp[1]*scaleY, 12, q);
            }
        }
    }
}
