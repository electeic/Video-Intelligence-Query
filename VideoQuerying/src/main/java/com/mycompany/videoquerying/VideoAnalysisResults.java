package com.mycompany.videoquerying;

/**
 * A struct containing the results of all descriptors for a single video.
 * @author stermark
 */
public class VideoAnalysisResults {
    public String filename;
    public GCloudResults objectResults;
    public OpenCVColorResults colorResults;
    public OpenCVMotionResults motionResults;
}
