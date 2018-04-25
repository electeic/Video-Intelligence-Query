package com.mycompany.videoquerying;

import static com.mycompany.videoquerying.GcloudVideoIntel.analyzeLabels;
import java.io.File;
import java.util.ArrayList;

/**
 * A wrapper class for handling all video query processing.
 * @author stermark
 */
public class QueryProcessor {
    
    public static ArrayList<MatchResult> findDatabaseMatch(VideoAnalysisResults queryResults, 
                                                            boolean useObjectDescriptor, 
                                                            boolean useColorDescriptor, 
                                                            boolean useMotionDescriptor)
    {
        System.out.println("Need to implement findDatabaseMatch()");
        if (useObjectDescriptor && queryResults.objectResults != null)
        {
            
        }
        if (useColorDescriptor && queryResults.colorResults != null)
        {
            
        }
        if (useMotionDescriptor && queryResults.motionResults != null)
        {
            
        }
        return null;
    }
    
    // Performs analysis of the video using Google Cloud object recognition
    public static GCloudResults processGoogleCloudObjects(String filePath)
    {
        try
        {
            System.out.println("Calling the cloud analysis.");
            GCloudResults results = analyzeLabels(filePath);
            return results;
        }
        catch (Exception e)
        {
            System.out.println("Error when calling Google Cloud's analyzeLabels()");
            e.printStackTrace();
        }
        return null;
    }
    
    // Performs analysis of the frames at the given filepath using OpenCV
    public static OpenCVColorResults processOpenCVColor(String filepath)
    {
        System.out.println("Need to implement processOpenCVColor()");
        return null;
    }
    
    // Performs analysis of the frames at the given filepath using OpenCV
    public static OpenCVMotionResults processOpenCVMotion(String filepath)
    {
        System.out.println("Need to implement processOpenCVMotion()");
        return null;
    }
}
