package com.mycompany.videoquerying;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;


/**
 * Created by ivanchen on 4/22/18.
 */
public class OpenCVIntel {

    public static void CVtest(){
        System.out.println("hello world.");
        nu.pattern.OpenCV.loadShared();

        // create and print on screen a 3x3 identity matrix
        System.out.println("Create a 3x3 identity matrix...");
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());

        // prepare to convert a RGB image in gray scale
//        String location = "resources/Poli.jpg";
        String location = "/Users/ivanchen/Desktop/kirisu.jpg";
        System.out.print("Convert the image at " + location + " in gray scale... ");
        // get the jpeg image from the internal resource folder
        Mat image = Imgcodecs.imread(location);
        // convert the image in gray scale
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        // write the new image on disk
        Imgcodecs.imwrite("/Users/ivanchen/Desktop/kirisu-grey.jpg", image);
        System.out.println("Done!");

        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());
    }

    public static void stuff (String[] args) {
//        CVLoader.load();
//        Mat img = Mat.zeros(200, 200, CvType.CV_8UC3);
//        Core.rectangle(img, new Point(0, 0), new Point(100, 200), new Scalar(0, 255, 0), -1);
//        Core.rectangle(img, new Point(100, 0), new Point(200, 200), new Scalar(0, 0, 255), -1);
//
//        Mat clusters = cluster(img, 2).get(0);
//
//        ImgWindow.newWindow(img).setTitle("img");;
//        ImgWindow.newWindow(clusters).setTitle("clusters");
    }

    public static List<Mat> cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);
        return showClusters(cutout, labels, centers);
    }

    private static List<Mat> showClusters (Mat cutout, Mat labels, Mat centers) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);

        List<Mat> clusters = new ArrayList<Mat>();
        for(int i = 0; i < centers.rows(); i++) {
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
        }

        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);

        int rows = 0;
        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                int r = (int)centers.get(label, 2)[0];
                int g = (int)centers.get(label, 1)[0];
                int b = (int)centers.get(label, 0)[0];
                counts.put(label, counts.get(label) + 1);
                clusters.get(label).put(y, x, b, g, r);
                rows++;
            }
        }
        System.out.println(counts);
        return clusters;
    }

    public static void MotionCV() {
        //load library
        System.load("/usr/local/Cellar/opencv/3.4.1_2/share/OpenCV/java/libopencv_java341.dylib");
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat frame = new Mat();
        Mat firstFrame = new Mat();
        Mat gray = new Mat();
        Mat frameDelta = new Mat();
        Mat thresh = new Mat();
        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();

        VideoCapture camera = new VideoCapture("./database_videos/sports/sports.mp4");
//        camera.open(0); //open camera

        //set the video size to 512x288
        camera.set(3, 1056);
        camera.set(4, 864);

        camera.read(frame);
        //convert to grayscale and set the first frame
        Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

        int j = 0;
        float totalscore = 0;
        while(camera.read(frame)) {
            //convert to grayscale
            System.out.println(j++);

            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

            //compute difference between first frame and current frame
            Core.absdiff(firstFrame, gray, frameDelta);
            Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);

            /*
             * This is to write the frames out to a folder
             */
//            Imgcodecs.imwrite("/Users/ivanchen/Desktop/delta/delta" + j + ".jpg", frameDelta);

            Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
            Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

        }

        /*
         * This is to calculate the entropy within the video. It gives a score at the very end
         */

        for(int i=0; i < cnts.size(); i++) {
            totalscore += Imgproc.contourArea(cnts.get(i));
        }

        System.out.println(totalscore);
    }
}
