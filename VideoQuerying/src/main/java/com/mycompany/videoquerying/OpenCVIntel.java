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

    public static void CVInit()
    {
        nu.pattern.OpenCV.loadShared();
    }

    /*
    This should be called when trying to find the primary colors of the images
    Input: string directory
    output: Mat
     */
    public static OpenCVColorResults ClusterVideoCV(String directory)
    {
        OpenCVColorResults ocvcr = new OpenCVColorResults();
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture("./query_videos/Q5/Q5.mp4");

        //set the video size to 1056x864
        camera.set(3, 1056);
        camera.set(4, 864);

        int j = 0;
        while(camera.read(frame) && j < 10)
        {
            FrameData framedata = new FrameData();
            System.out.println(j++);
            PopulateFrameDataCluster(frame, 5, framedata);
            ocvcr.frames.add(framedata);
        }
        return ocvcr;
    }

    public static OpenCVColorResults ClusterQueryVideos()
    {
        OpenCVColorResults ocvcr = new OpenCVColorResults();
        Mat totalColors = new Mat();
        Mat returnedColors = new Mat();
        Mat frame = new Mat();
        //"query_videos/first/first.mp4"
        //"./query_videos/second/second.mp4"
        //"./query_videos/Q5/Q5.mp4"
        VideoCapture camera = new VideoCapture("./query_videos/Q5/Q5.mp4");

        //set the video size to 1056x864
        camera.set(3, 1056);
        camera.set(4, 864);

        int j = 0;
        while(camera.read(frame) && j < 10)
        {
            System.out.println(j++);
            totalColors.push_back(cluster(frame, 5));
        }

        System.out.println("Finished total colors pushback");
        Mat combinedTotalColors = new Mat();


        ArrayList<Mat> singleColorMat = new ArrayList();
        singleColorMat.add(totalColors.col(0));
        singleColorMat.add(totalColors.col(1));
        singleColorMat.add(totalColors.col(2));
        Core.merge(singleColorMat, combinedTotalColors);

        FrameData frameData = new FrameData();
        System.out.println(combinedTotalColors.dump());
        PopulateFrameDataCluster(combinedTotalColors, 5, frameData);
        return ocvcr;

//        System.out.println(returnedColors.dump());
//        return returnedColors;
    }


    public static Mat cluster(Mat cutout, int k) {

        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);
//        System.out.println(centers.dump());
        return centers;
    }

    public static void PopulateFrameDataCluster(Mat cutout, int k, FrameData framedata) {

        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        countClusters(cutout, labels, centers, framedata);
    }

    private static void countClusters (Mat cutout, Mat labels, Mat centers, FrameData frameData) {

        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for (int i = 0; i < centers.rows(); i++) counts.put(i, 0);

        int rows = 0;
        for (int y = 0; y < cutout.rows(); y++) {
            for (int x = 0; x < cutout.cols(); x++) {
                int label = (int) labels.get(rows, 0)[0];
                counts.put(label, counts.get(label) + 1);
                rows++;
            }
        }

        for(int it = 0; it < counts.size(); it++)
        {
            int label = (int) labels.get(rows, 0)[0];
            frameData.frameColors.add(new ColorData((int)centers.get(label, 2)[0], (int)centers.get(label, 1)[0],
                    (int)centers.get(label, 0)[0], counts.get(it)/(cutout.rows() * cutout.cols())));
        }
    }

    public static void MotionCV() {
        //load library
        nu.pattern.OpenCV.loadShared();

        Mat frame = new Mat();
        Mat firstFrame = new Mat();
        Mat gray = new Mat();
        Mat frameDelta = new Mat();
        Mat thresh = new Mat();
        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();

        /*
        Sample directory is "./database_videos/sports/sports.mp4"
         */
        VideoCapture camera = new VideoCapture("./database_videos/sports/sports.mp4");

        //set the video size to 1056x864
        camera.set(3, 1056);
        camera.set(4, 864);

        camera.read(frame);
        //convert to grayscale and set the first frame
        Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

        /*
         * This is to write the frames out to a folder
         */
        int j = 0;

        float totalScore = 0;
        while(camera.read(frame)) {
            //convert to grayscale

            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

            //compute difference between first frame and current frame
            Core.absdiff(firstFrame, gray, frameDelta);
            Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);

            /*
             * This is to write the frames out to a folder
             */
//            Imgcodecs.imwrite("/Users/ivanchen/Desktop/delta/delta" + j++ + ".jpg", frameDelta);

            Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
            Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            /*
             * This resets the frame each time
             */
            Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

        }

        /*
         * This is to calculate the entropy within the video. It gives a score at the very end
         */

        for(int i=0; i < cnts.size(); i++) {
            totalScore += Imgproc.contourArea(cnts.get(i));
        }

        System.out.println(totalScore);
//        return totalScore;
    }

}
