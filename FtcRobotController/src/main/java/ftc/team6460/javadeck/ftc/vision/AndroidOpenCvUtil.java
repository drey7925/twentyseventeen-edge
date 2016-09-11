package ftc.team6460.javadeck.ftc.vision;

import android.content.res.Resources;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AndroidOpenCvUtil {
    public static Mat readGrayscale(Resources r, int resid) throws IOException {
        InputStream inputStream = r.openRawResource(resid);
        // Read into byte-array
        byte[] temporaryImageInMemory = readStream(inputStream);

        // Decode into mat. Use any IMREAD_ option that describes your image appropriately
        //return Imgcodecs.imread("/sdcard/tools.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        return Imgcodecs.imdecode(new MatOfByte(temporaryImageInMemory), Imgcodecs.IMREAD_GRAYSCALE);
    }

    private static byte[] readStream(InputStream stream) throws IOException {
        // Copy content of the image to byte-array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] temporaryImageInMemory = buffer.toByteArray();
        buffer.close();
        stream.close();
        return temporaryImageInMemory;
    }
}
