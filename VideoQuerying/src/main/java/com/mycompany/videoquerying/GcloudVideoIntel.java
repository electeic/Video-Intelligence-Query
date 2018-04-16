//package com.mycompany.videoquerying;
//
//import com.google.api.gax.longrunning.OperationFuture;
//import com.google.cloud.videointelligence.v1.AnnotateVideoProgress;
//import com.google.cloud.videointelligence.v1.AnnotateVideoRequest;
//import com.google.cloud.videointelligence.v1.AnnotateVideoResponse;
//import com.google.cloud.videointelligence.v1.Entity;
//import com.google.cloud.videointelligence.v1.ExplicitContentFrame;
//import com.google.cloud.videointelligence.v1.Feature;
//import com.google.cloud.videointelligence.v1.LabelAnnotation;
//import com.google.cloud.videointelligence.v1.LabelSegment;
//import com.google.cloud.videointelligence.v1.VideoAnnotationResults;
//import com.google.cloud.videointelligence.v1.VideoIntelligenceServiceClient;
//import com.google.cloud.videointelligence.v1.VideoSegment;
//import com.google.protobuf.ByteString;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import org.apache.commons.codec.binary.Base64;
//
//
///**
// * Created by ivanchen on 4/13/18.
// */
//public class GcloudVideoIntel {
//
//    public static void auth()
//    {
//        FileInputStream credentialsStream = new FileInputStream(fileName);
//        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
//        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
//    }
//
//    static void authImplicit() {
//        // If you don't specify credentials when constructing the client, the client library will
//        // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
//        Storage storage = StorageOptions.getDefaultInstance().getService();
//
//        System.out.println("Buckets:");
//        Page<Bucket> buckets = storage.list();
//        for (Bucket bucket : buckets.iterateAll()) {
//            System.out.println(bucket.toString());
//        }
//    }
//
//    public static void analyzeLabels(String filePath){
//
//        // Instantiate a com.google.cloud.videointelligence.v1.VideoIntelligenceServiceClient
//        try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
//            // Read file and encode into Base64
//            Path path = Paths.get(filePath);
//            byte[] data = Files.readAllBytes(path);
//            byte[] encodedBytes = Base64.encodeBase64(data);
//
//            AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
//                    .setInputContent(ByteString.copyFrom(encodedBytes))
//                    .addFeatures(Feature.LABEL_DETECTION)
//                    .build();
//
//            // Create an operation that will contain the response when the operation completes.
//            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> response =
//                    client.annotateVideoAsync(request);
//
//            System.out.println("Waiting for operation to complete...");
//            for (VideoAnnotationResults results : response.get().getAnnotationResultsList()) {
//                // process video / segment level label annotations
//                System.out.println("Locations: ");
//                for (LabelAnnotation labelAnnotation : results.getSegmentLabelAnnotationsList()) {
//                    System.out
//                            .println("Video label: " + labelAnnotation.getEntity().getDescription());
//                    // categories
//                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
//                        System.out.println("Video label category: " + categoryEntity.getDescription());
//                    }
//                    // segments
//                    for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
//                        double startTime = segment.getSegment().getStartTimeOffset().getSeconds()
//                                + segment.getSegment().getStartTimeOffset().getNanos() / 1e9;
//                        double endTime = segment.getSegment().getEndTimeOffset().getSeconds()
//                                + segment.getSegment().getEndTimeOffset().getNanos() / 1e9;
//                        System.out.printf("Segment location: %.3f:%.2f\n", startTime, endTime);
//                        System.out.println("Confidence: " + segment.getConfidence());
//                    }
//                }
//
//                // process shot label annotations
//                for (LabelAnnotation labelAnnotation : results.getShotLabelAnnotationsList()) {
//                    System.out
//                            .println("Shot label: " + labelAnnotation.getEntity().getDescription());
//                    // categories
//                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
//                        System.out.println("Shot label category: " + categoryEntity.getDescription());
//                    }
//                    // segments
//                    for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
//                        double startTime = segment.getSegment().getStartTimeOffset().getSeconds()
//                                + segment.getSegment().getStartTimeOffset().getNanos() / 1e9;
//                        double endTime = segment.getSegment().getEndTimeOffset().getSeconds()
//                                + segment.getSegment().getEndTimeOffset().getNanos() / 1e9;
//                        System.out.printf("Segment location: %.3f:%.2f\n", startTime, endTime);
//                        System.out.println("Confidence: " + segment.getConfidence());
//                    }
//                }
//
//                // process frame label annotations
//                for (LabelAnnotation labelAnnotation : results.getFrameLabelAnnotationsList()) {
//                    System.out
//                            .println("Frame label: " + labelAnnotation.getEntity().getDescription());
//                    // categories
//                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
//                        System.out.println("Frame label category: " + categoryEntity.getDescription());
//                    }
//                    // segments
//                    for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
//                        double startTime = segment.getSegment().getStartTimeOffset().getSeconds()
//                                + segment.getSegment().getStartTimeOffset().getNanos() / 1e9;
//                        double endTime = segment.getSegment().getEndTimeOffset().getSeconds()
//                                + segment.getSegment().getEndTimeOffset().getNanos() / 1e9;
//                        System.out.printf("Segment location: %.3f:%.2f\n", startTime, endTime);
//                        System.out.println("Confidence: " + segment.getConfidence());
//                    }
//                }
//            }
//        }
//    }
//
//}
