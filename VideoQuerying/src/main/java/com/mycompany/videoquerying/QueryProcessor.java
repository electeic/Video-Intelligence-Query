package com.mycompany.videoquerying;

import static com.mycompany.videoquerying.GcloudVideoIntel.analyzeLabels;
import static com.mycompany.videoquerying.OpenCVIntel.ClusterVideoCV;
import static com.mycompany.videoquerying.OpenCVIntel.MotionCV;
import static com.mycompany.videoquerying.VideoEncoder.encodeMp4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            double timePerFrame = 1.0 / 30.0;
            
            /**********************************************************************/        
            /*                  OBJECT RECOGNITION DESCRIPTOR
            /**********************************************************************/
            double overallObjectScore = 0;
            double[] objectFrameScore = new double[600];
            if (useObjectDescriptor && queryResults.objectResults != null)
            {
                // Get the GCloud object results for the current database video
                GCloudResults databaseObjectResults = databaseVideoMeta.get(i).objectResults;
                
                // Keep track of how many labels are considered when matching
                int numLabelsUsed = 0;
                
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
                            objectFrameScore[frame] = queryEntry.getValue().segmentData.confidence * 
                                                databaseObjectResults.videoLabels.get(queryEntry.getKey()).segmentData.confidence;
                            
                            numLabelsUsed += 1;
                        }
                    }
                    
                    overallObjectScore += objectFrameScore[frame];
                }
                
                // Calculate the overall object score as a percentage
                overallObjectScore = (overallObjectScore / (double) numLabelsUsed); // Optionally add an objectWeight parameter and mult here
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
            double overallMotionScore = 0;
            double[] motionFrameScore = new double[600];
            
            if (useMotionDescriptor && queryResults.motionResults != null)
            {
                // Get the GCloud object results for the current database video
                OpenCVMotionResults databaseMotionResults = databaseVideoMeta.get(i).motionResults;
                
                // Calculate the area of the frame
                double frameArea = 1056 * 864;
                        
                // for each frame in the current database, find a score based on the average motion in the query video clip
                for (int frame = 0; frame < databaseMotionResults.frameMotion.size(); frame++)
                {
                    double absDiff = Math.abs(queryResults.motionResults.averageMotion - databaseMotionResults.frameMotion.get(frame));
                    motionFrameScore[frame] = (1 - (absDiff / frameArea)); // Optionally add a motionWeight parameter and mult here
                }
                
                // Calculate the overall video motion match score
                double absDiff = Math.abs(queryResults.motionResults.averageMotion - databaseMotionResults.averageMotion);
                overallMotionScore = (1 - (absDiff / frameArea)); // Optionally add a motionWeight parameter and mult here
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
        ClusterVideoCV();
//        ClusterCV();
        return null;
    }



    // Performs analysis of the frames at the given filepath using OpenCV
    public static OpenCVMotionResults processOpenCVMotion(String filepath)
    {
        return MotionCV(filepath);
    }
    
    // Reads in the metadata in the provided file directory.
    public static VideoAnalysisResults readDatabaseMetadataFile(String videoDirectory)
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
    
    // Used to initialize metadata file for a video in the database.
    public static void writeDatabaseMetadataFile(VideoAnalysisResults results, String videoDirectory)
    {
        // Ensure that the directory to write to exists
        File videoFileDirectory = new File (videoDirectory);
        if (!videoFileDirectory.exists())
        {
            System.out.println("Video directory not found. Please enter a valid video location.");
            return;
        }
        
        String videoFilepath = videoFileDirectory.getAbsolutePath() + "/" + results.filename + ".meta";
        System.out.println(videoFilepath);
        
        // Serialize and write out the object
        try
        {   
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(videoFilepath);
            ObjectOutputStream out = new ObjectOutputStream(file);
             
            // Method for serialization of object
            out.writeObject(results);
             
            out.close();
            file.close();
             
            System.out.println("Object has been serialized.");
 
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            System.out.println("IOException is caught. Unable to write out results object.");
        }
    }
    
    // Creates a full metadata file for each of the videos in the database.
    // NOTE: Should not be used in production code as all videos should be pre-processed offline.
    public static void processAllDatabaseVideos(String databaseDirectory)
    {
        // for each video in the database
        File[] directories = new File(databaseDirectory).listFiles(File::isDirectory);
        
        for (int i = 0; i < directories.length; i++)
        {
            // Get video filepath
            String videoDirectory = directories[i].getAbsolutePath();
            
            // Encode the video (to get .png's and .mp4)
            encodeMp4(videoDirectory);
            
            // Setup VideoAnalysisResults for the current video
            VideoAnalysisResults dbVideoResults = new VideoAnalysisResults();
            
            // Get video name
            dbVideoResults.filename = directories[i].getName();
            
            // Process objects
            dbVideoResults.objectResults = processGoogleCloudObjects(videoDirectory);
            // Process color
            dbVideoResults.colorResults = processOpenCVColor(videoDirectory);
            // Process motion
            dbVideoResults.motionResults = processOpenCVMotion(videoDirectory);
            
            // Write out VideoAnalysisResults
            writeDatabaseMetadataFile(dbVideoResults, directories[i].getAbsolutePath());
            
            // Report success or failure
            System.out.println("Finished writing " + dbVideoResults.filename + ".meta");
        }
        System.out.println("Finished processing all videos in the database.");
    }
}
