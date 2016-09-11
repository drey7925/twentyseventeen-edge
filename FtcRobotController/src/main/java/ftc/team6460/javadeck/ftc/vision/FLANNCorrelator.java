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
    private boolean lastGood;

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
        rectMat = new MatOfPoint2f();

        renderMat = new MatOfPoint2f();
        this.target = target;
        Imgproc.resize(this.target, this.target, new Size(target.width() / 2, target.height() / 2));
        rectMat.fromArray(new Point(0, 0),
                new Point(target.width(), 0),
                new Point(target.width(), target.height()),
                new Point(0, target.height()));
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

            MatOfPoint2f tgtGood = new MatOfPoint2f();
            MatOfPoint2f camGood = new MatOfPoint2f();
            Imgproc.resize(mat, mat, new Size(mat.width() / SCALEDOWN_FACTOR, mat.height() / SCALEDOWN_FACTOR));
            w = mat.width();
            h = mat.height();
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
            double l = mat.width(), r = 0, t = mat.height(), b = 0;
            if(roiLostLock>4 || (roiR-roiL<40) || (roiB-roiT<40)){
                roiL = 0;
                roiR = w;
                roiT = 0;
                roiB = h;
            }
            mat = mat.submat(roiT, roiB, roiL, roiR);
            fd.detect(mat, camKeypoints);
            de.compute(mat, camKeypoints, camDescriptors);
            camCardinality = camDescriptors.rows();
            //targetDescriptors.convertTo(targetDescriptors, CvType.CV_32F);
            //camDescriptors.convertTo(camDescriptors, CvType.CV_32F);
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

                if (result.rows() > 0) {
                    Core.perspectiveTransform(rectMat, renderMat, result);

                    lastGood = true;
                    Log.d("FLANN:RSLT", result.dump());
                    roiLostLock = 0;
                    Point[] points = renderMat.toArray();

                    for (int i = 0; i < points.length; i++) {
                        points[i].x += roiL;
                        points[i].y += roiT;
                        if (points[i].x < l) l = points[i].x;
                        if (points[i].x > r) r = points[i].x;
                        if (points[i].y < t) t = points[i].y;
                        if (points[i].y > b) b = points[i].y;
                    }
                    renderMat.fromArray(points);
                    l-=10;
                    r+=10;
                    t-=10;
                    b+=10;
                    if(l<0) l = 0;
                    if(r>w) r = w;
                    if(t<0) t = 0;
                    if(b>h) b = h;
                    oroiL = roiL;
                    oroiR = roiR;
                    oroiT = roiT;
                    oroiB = roiB;
                    roiL = (int) l;
                    roiR = (int) r;
                    roiT = (int) t;
                    roiB = (int) b;
                    if((roiR-roiL<40) || (roiB-roiT<40)){
                        roiL = 0;
                        roiR = w;
                        roiT = 0;
                        roiB = h;
                    }

                } else {
                    result = null;
                    lastGood = false;
                    roiLostLock++;
                }
            } else {
                result = null;
                lastGood = false;
                roiLostLock++;
            }
        } catch (Exception e) {
            Log.wtf("FLANN EXCEPTION", e);
            result = null;
        }
    }

    MatOfPoint2f rectMat;
    MatOfPoint2f renderMat;
    int roiL, roiR, roiT, roiB;
    int oroiL, oroiR, oroiT, oroiB;
    int roiLostLock;

    @Override
    public synchronized void draw(Canvas canvas) {
        float scaleX = canvas.getWidth() / (float) w;
        float scaleY = canvas.getHeight() / (float) h;
        Paint p = new Paint();
        p.setColor(Color.RED);
        if (camGoodPoints == null) return;
        for (Point pt : camGoodPoints) {
            canvas.drawCircle((float) (pt.x+oroiL) * scaleX, (float) (pt.y+oroiT) * scaleY, 4, p);
        }
        p.setColor(Color.GREEN);
        if (lastGood) {

            Point[] points = renderMat.toArray();
            assert (points.length == 4);
            canvas.drawLine((float) points[0].x * scaleX, (float) points[0].y * scaleY, (float) points[1].x * scaleX, (float) points[1].y * scaleY, p);
            canvas.drawLine((float) points[1].x * scaleX, (float) points[1].y * scaleY, (float) points[2].x * scaleX, (float) points[2].y * scaleY, p);
            canvas.drawLine((float) points[2].x * scaleX, (float) points[2].y * scaleY, (float) points[3].x * scaleX, (float) points[3].y * scaleY, p);
            canvas.drawLine((float) points[3].x * scaleX, (float) points[3].y * scaleY, (float) points[0].x * scaleX, (float) points[0].y * scaleY, p);
            canvas.drawLine((float) points[0].x * scaleX, (float) points[0].y * scaleY, (float) points[2].x * scaleX, (float) points[2].y * scaleY, p);
            canvas.drawLine((float) points[1].x * scaleX, (float) points[1].y * scaleY, (float) points[3].x * scaleX, (float) points[3].y * scaleY, p);
        }
        p.setColor(Color.BLUE);
        canvas.drawLine(oroiL * scaleX, oroiT * scaleY, oroiR * scaleX, oroiT * scaleY, p);
        canvas.drawLine(oroiR * scaleX, oroiT * scaleY, oroiR * scaleX, oroiB * scaleY, p);
        canvas.drawLine(oroiL * scaleX, oroiB * scaleY, oroiR * scaleX, oroiB * scaleY, p);
        canvas.drawLine(oroiL * scaleX, oroiT * scaleY, oroiL * scaleX, oroiB * scaleY, p);
        p.setColor(Color.YELLOW);
        canvas.drawLine(roiL * scaleX, roiT * scaleY, roiR * scaleX, roiT * scaleY, p);
        canvas.drawLine(roiR * scaleX, roiT * scaleY, roiR * scaleX, roiB * scaleY, p);
        canvas.drawLine(roiL * scaleX, roiB * scaleY, roiR * scaleX, roiB * scaleY, p);
        canvas.drawLine(roiL * scaleX, roiT * scaleY, roiL * scaleX, roiB * scaleY, p);
        canvas.drawText(String.format("ROI: %d %d %d %d", roiL, roiR, roiT, roiB), 0, canvas.getHeight() - 192, p);
        p.setColor(Color.RED);
        canvas.drawText(String.format("tgt: %d \ncam: %d\ngood: %d \nmatches: %d \nminDist: %f \nmaxDist: %f", tgtCardinality, camCardinality, goodCardinality, matches.rows(), minDist, maxDist), 0, canvas.getHeight() - 256, p);
    }
}


