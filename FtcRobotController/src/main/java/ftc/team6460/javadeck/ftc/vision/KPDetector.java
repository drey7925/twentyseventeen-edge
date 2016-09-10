package ftc.team6460.javadeck.ftc.vision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

public class KPDetector implements MatCallback {
    int param;
    public KPDetector(int param) {
        this.param = param;
    }
// positive for grayscale, negative for color
/*    private static final int
//            GRIDDETECTOR = 1000,
//            PYRAMIDDETECTOR = 2000,
//            DYNAMICDETECTOR = 3000;
//
//
//    public static final int
//            FAST = 1,
//            STAR = 2,
//            SIFT = 3,
//            SURF = 4,
//            ORB = 5,
//            MSER = 6,
//            GFTT = 7,
//            HARRIS = 8,
//            SIMPLEBLOB = 9,
//            DENSE = 10,
//            BRISK = 11,
//            AKAZE = 12,
//            GRID_FAST = GRIDDETECTOR + FAST,
//            GRID_STAR = GRIDDETECTOR + STAR,
//            GRID_SIFT = GRIDDETECTOR + SIFT,
//            GRID_SURF = GRIDDETECTOR + SURF,
//            GRID_ORB = GRIDDETECTOR + ORB,
//            GRID_MSER = GRIDDETECTOR + MSER,
//            GRID_GFTT = GRIDDETECTOR + GFTT,
//            GRID_HARRIS = GRIDDETECTOR + HARRIS,
//            GRID_SIMPLEBLOB = GRIDDETECTOR + SIMPLEBLOB,
//            GRID_DENSE = GRIDDETECTOR + DENSE,
//            GRID_BRISK = GRIDDETECTOR + BRISK,
//            GRID_AKAZE = GRIDDETECTOR + AKAZE,
//            PYRAMID_FAST = PYRAMIDDETECTOR + FAST,
//            PYRAMID_STAR = PYRAMIDDETECTOR + STAR,
//            PYRAMID_SIFT = PYRAMIDDETECTOR + SIFT,
//            PYRAMID_SURF = PYRAMIDDETECTOR + SURF,
//            PYRAMID_ORB = PYRAMIDDETECTOR + ORB,
//            PYRAMID_MSER = PYRAMIDDETECTOR + MSER,
//            PYRAMID_GFTT = PYRAMIDDETECTOR + GFTT,
//            PYRAMID_HARRIS = PYRAMIDDETECTOR + HARRIS,
//            PYRAMID_SIMPLEBLOB = PYRAMIDDETECTOR + SIMPLEBLOB,
//            PYRAMID_DENSE = PYRAMIDDETECTOR + DENSE,
//            PYRAMID_BRISK = PYRAMIDDETECTOR + BRISK,
//            PYRAMID_AKAZE = PYRAMIDDETECTOR + AKAZE,
//            DYNAMIC_FAST = DYNAMICDETECTOR + FAST,
//            DYNAMIC_STAR = DYNAMICDETECTOR + STAR,
//            DYNAMIC_SIFT = DYNAMICDETECTOR + SIFT,
//            DYNAMIC_SURF = DYNAMICDETECTOR + SURF,
//            DYNAMIC_ORB = DYNAMICDETECTOR + ORB,
//            DYNAMIC_MSER = DYNAMICDETECTOR + MSER,
//            DYNAMIC_GFTT = DYNAMICDETECTOR + GFTT,
//            DYNAMIC_HARRIS = DYNAMICDETECTOR + HARRIS,
//            DYNAMIC_SIMPLEBLOB = DYNAMICDETECTOR + SIMPLEBLOB,
//            DYNAMIC_DENSE = DYNAMICDETECTOR + DENSE,
//            DYNAMIC_BRISK = DYNAMICDETECTOR + BRISK,
//            DYNAMIC_AKAZE = DYNAMICDETECTOR + AKAZE; */
    FeatureDetector fd;
    MatOfKeyPoint features;
    Mat processedMat;
    int w, h;
    boolean pending = true;
    @Override
    public synchronized void handleMat(Mat mat) {
        if(param>0) {
            // grayscale
            if(processedMat ==null) processedMat = new Mat();
            Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_RGB2GRAY);
            Imgproc.blur(processedMat, processedMat, new Size(9,9));
            mat = processedMat;
        } else {param = -param;
            Imgproc.blur(mat, processedMat, new Size(9,9));
            mat = processedMat;
        }
        w = mat.width();
        h = mat.height();
        Log.i("KPPARAM", Integer.toString(param));
        if(fd==null) fd = FeatureDetector.create(param);
        if(features==null) features = new MatOfKeyPoint();
        fd.detect(mat, features);
        pending = false;
    }
Paint p = new Paint();


    @Override
    public synchronized void draw(Canvas canvas) {
        if(pending) return;
        float scaleX = canvas.getWidth()/(float)w;
        float scaleY = canvas.getHeight()/(float)h;
        p.setColor(Color.BLUE);
        for(KeyPoint kp : features.toList()){
            canvas.drawCircle((float) kp.pt.x*scaleX, (float) kp.pt.y*scaleY, kp.size, p);
        }
    }
}
