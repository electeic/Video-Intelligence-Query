package com.mycompany.videoquerying;

import static com.mycompany.videoquerying.GcloudVideoIntel.analyzeLabels;
import static com.mycompany.videoquerying.OpenCVIntel.MotionCV;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * A wrapper class for handling all video query processing.
 * @author stermark
 */
public class QueryProcessor {
    
    // Returns a sorted ArrayList of the best database matches for the current query
    public static ArrayList<MatchResult> findDatabaseMatch(VideoAnalysisResults queryResults,
                                                            String databaseDirectory,
                                                            boolean useObjectDescriptor, 
                                                            boolean useColorDescriptor, 
                                                            boolean useMotionDescriptor)
    {
        System.out.println("Need to implement findDatabaseMatch()");
        
        /**********************************************************************/        
        /*   Load in the pre-processed database meta files for comparison
        /**********************************************************************/
        // Get the names of the videos in the database and read their meta files
        File[] directories = new File(databaseDirectory).listFiles(File::isDirectory);
        ArrayList<VideoAnalysisResults> databaseVideoMeta = new ArrayList();
        for (int i = 0; i < directories.length; i++)
        {
            VideoAnalysisResults nextResults = readDatabaseMetadataFile(directories[i].getAbsolutePath());
            if (nextResults != null)
            {
                databaseVideoMeta.add(nextResults);
                System.out.println("Read in meta file for database video: " + directories[i].getName());
            }
            else
            {
                System.out.println("No meta file found for database video: " + directories[i].getName());
            }
        }
        
        System.out.println("Finished reading database meta files.");
        
        // foreach video that has a meta file in the database
        for (int i = 0; i < databaseVideoMeta.size(); i++)
        {
            double[] frameScore = new double[600];
            double cumulativeScore = 0;
            double timePerFrame = 1.0 / 30.0;
            
            /**********************************************************************/        
            /*                  OBJECT RECOGNITION DESCRIPTOR
            /**********************************************************************/
            if (useObjectDescriptor && queryResults.objectResults != null)
            {
                // Get the GCloud object results for the current database video
                GCloudResults databaseObjectResults = databaseVideoMeta.get(i).objectResults;
                
                // for each frame in the database video
                for (int frame = 0; frame < 600; frame++)
                {
                    // for each video label in the query video
                    for (Entry<String, VideoLabelData> queryEntry : queryResults.objectResults.videoLabels.entrySet())
                    {
                        // Calculate the start and end time of the current frame
                        double frameStartTime = frame * timePerFrame;
                        double frameEndTime = frameStartTime + timePerFrame;
                        
                        // check if query video label is present in the current frame
                        if (databaseObjectResults.videoLabels.containsKey(queryEntry.getKey()) &&
                                (queryEntry.getValue().segmentData.startTime < databaseObjectResults.videoLabels.get(queryEntry.getKey()).segmentData.endTime  ||
                                 queryEntry.getValue().segmentData.endTime > databaseObjectResults.videoLabels.get(queryEntry.getKey()).segmentData.startTime))
                        {
                            frameScore[frame] = queryEntry.getValue().segmentData.confidence * 
                                                databaseObjectResults.videoLabels.get(queryEntry.getKey()).segmentData.confidence;
                        }
                    }
                    
                    cumulativeScore += frameScore[frame];
                }
            }
            
            /**********************************************************************/        
            /*                      DOMINANT COLOR DESCRIPTOR
            /**********************************************************************/
            if (useColorDescriptor && queryResults.colorResults != null)
            {

            }
            
            /**********************************************************************/        
            /*                          MOTION DESCRIPTOR
            /**********************************************************************/
            if (useMotionDescriptor && queryResults.motionResults != null)
            {

            }
        }

        
        // TODO: Sort the list of files from highest to lowest score before returning
        
        return null;
    }
    
    // Performs analysis of the query video using Google Cloud object recognition
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
        MotionCV();
        System.out.println("Need to implement processOpenCVMotion()");
        return null;
    }
    
    // Reads in the metadata in the provided file directory.
    private static VideoAnalysisResults readDatabaseMetadataFile(String videoDirectory)
    {
        // Ensure that the directory to read from exists
        File videoFileDirectory = new File (videoDirectory);
        if (!videoFileDirectory.exists())
        {
//            System.out.println("Video directory not found. Please enter a valid video location.");
            return null;
        }
        
        String metadataFilepath = videoFileDirectory.getAbsolutePath() + "/" + videoFileDirectory.getName() + ".meta";
//        System.out.println(metadataFilepath);
        
        VideoAnalysisResults metadata = null;
        
        // Deserialization
        try
        {   
            // Reading the object from a file
            FileInputStream file = new FileInputStream(metadataFilepath);
            ObjectInputStream in = new ObjectInputStream(file);
             
            // Method for deserialization of object
            metadata = (VideoAnalysisResults) in.readObject();
             
            in.close();
            file.close();
        }
         
        catch(IOException ex)
        {
//            System.out.println("IOException is caught. Unable to read metadata.");
        }
         
        catch(ClassNotFoundException ex)
        {
//            System.out.println("ClassNotFoundException is caught. Unable to read metadata.");
        }
        
        return metadata;
    }
}
