package ftc.team6460.javadeck.ftc.vision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FLANNCorrelator implements MatCallback {
    public static final int SCALEDOWN_FACTOR = 8;
    private final DescriptorMatcher dm;
    private FeatureDetector fd;
    private final MatOfKeyPoint targetKeypoints;
    private final MatOfKeyPoint camKeypoints;
    private final Mat target;
    private KeyPoint[] targetKpsArr;
    private KeyPoint[] camKpsArr;
    private final Mat targetDescriptors;
    private final Mat camDescriptors;
    private final DescriptorExtractor de;
    private final MatOfDMatch matches;
    private double minDist;
    private double maxDist;
    private List<Point> tgtGoodPoints;
    private List<Point> camGoodPoints;
    private Mat result;
    private int h;
    private int w;

    /*private static final int
            OPPONENTEXTRACTOR = 1000;


    public static final int
            SIFT = 1,
            SURF = 2,
            ORB = 3,
            BRIEF = 4,
            BRISK = 5,
            FREAK = 6,
            AKAZE = 7,
            OPPONENT_SIFT = OPPONENTEXTRACTOR + SIFT,
            OPPONENT_SURF = OPPONENTEXTRACTOR + SURF,
            OPPONENT_ORB = OPPONENTEXTRACTOR + ORB,
            OPPONENT_BRIEF = OPPONENTEXTRACTOR + BRIEF,
            OPPONENT_BRISK = OPPONENTEXTRACTOR + BRISK,
            OPPONENT_FREAK = OPPONENTEXTRACTOR + FREAK,
            OPPONENT_AKAZE = OPPONENTEXTRACTOR + AKAZE;*/

    public FLANNCorrelator(Mat target) {
        this.target = target;
        Imgproc.resize(this.target, this.target, new Size(target.width() / 2, target.height() / 2));
        // black magic; change if needed
        fd = FeatureDetector.create(FeatureDetector.SURF);
        de = DescriptorExtractor.create(DescriptorExtractor.SURF);

        dm = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        targetKeypoints = new MatOfKeyPoint();
        camKeypoints = new MatOfKeyPoint();
        targetDescriptors = new Mat();
        camDescriptors = new Mat();
        fd.detect(this.target, targetKeypoints);
        de.compute(this.target, targetKeypoints, targetDescriptors);
        tgtCardinality = targetDescriptors.rows();

        matches = new MatOfDMatch();
        targetKpsArr = new KeyPoint[0];
        camKpsArr = new KeyPoint[0];

    }

    int tgtCardinality;
    int camCardinality;
    int goodCardinality;

    @Override
    public synchronized void handleMat(Mat mat) {
        
        try {
            w = mat.width();
            h = mat.height();


            MatOfPoint2f tgtGood = new MatOfPoint2f();
            MatOfPoint2f camGood = new MatOfPoint2f();
            Imgproc.resize(mat, mat, new Size(mat.width() / SCALEDOWN_FACTOR, mat.height() / SCALEDOWN_FACTOR));
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
            fd.detect(mat, camKeypoints);
            de.compute(mat, camKeypoints, camDescriptors);
            camCardinality = camDescriptors.rows();
            targetDescriptors.convertTo(targetDescriptors, CvType.CV_32F);
            camDescriptors.convertTo(camDescriptors, CvType.CV_32F);
            if (camDescriptors.rows() == 0) {
                result = null;
                return;
            }
            dm.match(targetDescriptors, camDescriptors, matches);
            maxDist = 0;
            minDist = Double.MAX_VALUE;
            for (DMatch match : matches.toList()) {
                if (match.distance < minDist) minDist = match.distance;
                if (match.distance > maxDist) maxDist = match.distance;

            }
            targetKpsArr = targetKeypoints.toArray();
            camKpsArr = camKeypoints.toArray();
            double distCutoff = Math.max(2 * minDist, 0.02);
            tgtGoodPoints = new ArrayList<Point>();
            camGoodPoints = new ArrayList<Point>();
            for (DMatch match : matches.toList()) {
                if (match.distance < distCutoff) {
                    tgtGoodPoints.add(targetKpsArr[match.queryIdx].pt);
                    camGoodPoints.add(camKpsArr[match.trainIdx].pt);
                }

            }
            goodCardinality = tgtGoodPoints.size();
            tgtGood.fromList(tgtGoodPoints);
            camGood.fromList(camGoodPoints);
            //result = null;
            Log.d("FLANN:RPT", tgtGood.rows() + "," + camGood.rows());
            if (tgtGood.rows() > 9 && camGood.rows() > 9) {
                result = Calib3d.findHomography(tgtGood, camGood, Calib3d.RANSAC, 3);
                Log.d("FLANN:RSLT", result.dump());
            } else {
                result = null;
            }
        } catch(Exception e){
            Log.wtf("FLANN EXCEPTION", e);
            result = null;
        }
    }

    MatOfPoint2f rectMat = new MatOfPoint2f();

    @Override
    public synchronized void draw(Canvas canvas) {
        float scaleX = canvas.getWidth() / (float) w;
        float scaleY = canvas.getHeight() / (float) h;
        Paint p = new Paint();
        p.setColor(Color.RED);
        if (camGoodPoints == null) return;
        for (Point pt : camGoodPoints) {
            canvas.drawCircle((float) pt.x * scaleX * SCALEDOWN_FACTOR, (float) pt.y * scaleY * SCALEDOWN_FACTOR, 4, p);
        }
        p.setColor(Color.GREEN);
        if (result != null && result.rows() > 0) {
            rectMat.fromArray(new Point(0, 0),
                    new Point(target.width(), 0),
                    new Point(target.width(), target.height()),
                    new Point(0, target.height()));
            Core.perspectiveTransform(rectMat, rectMat, result);
            Point[] points = rectMat.toArray();
            assert (points.length == 4);
            canvas.drawLine((float) points[0].x * SCALEDOWN_FACTOR * scaleX, (float) points[0].y * SCALEDOWN_FACTOR * scaleY, (float) points[1].x * SCALEDOWN_FACTOR * scaleX, (float) points[1].y * SCALEDOWN_FACTOR * scaleY, p);
            canvas.drawLine((float) points[1].x * SCALEDOWN_FACTOR * scaleX, (float) points[1].y * SCALEDOWN_FACTOR * scaleY, (float) points[2].x * SCALEDOWN_FACTOR * scaleX, (float) points[2].y * SCALEDOWN_FACTOR * scaleY, p);
            canvas.drawLine((float) points[2].x * SCALEDOWN_FACTOR * scaleX, (float) points[2].y * SCALEDOWN_FACTOR * scaleY, (float) points[3].x * SCALEDOWN_FACTOR * scaleX, (float) points[3].y * SCALEDOWN_FACTOR * scaleY, p);
            canvas.drawLine((float) points[3].x * SCALEDOWN_FACTOR * scaleX, (float) points[3].y * SCALEDOWN_FACTOR * scaleY, (float) points[0].x * SCALEDOWN_FACTOR * scaleX, (float) points[0].y * SCALEDOWN_FACTOR * scaleY, p);
            canvas.drawLine((float) points[0].x * SCALEDOWN_FACTOR * scaleX, (float) points[0].y * SCALEDOWN_FACTOR * scaleY, (float) points[2].x * SCALEDOWN_FACTOR * scaleX, (float) points[2].y * SCALEDOWN_FACTOR * scaleY, p);
            canvas.drawLine((float) points[1].x * SCALEDOWN_FACTOR * scaleX, (float) points[1].y * SCALEDOWN_FACTOR * scaleY, (float) points[3].x * SCALEDOWN_FACTOR * scaleX, (float) points[3].y * SCALEDOWN_FACTOR * scaleY, p);
        }

        canvas.drawText(String.format("tgt: %d \ncam: %d\ngood: %d", tgtCardinality, camCardinality, goodCardinality), 0, canvas.getHeight()-256, p);
    }
}

