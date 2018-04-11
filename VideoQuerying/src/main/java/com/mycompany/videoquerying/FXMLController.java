package com.mycompany.videoquerying;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class FXMLController implements Initializable {
    
    /* Video Querying Variables */
    VideoEncoder encoder;
    
    /* FXML Variables */
    @FXML
    private MediaView mvDatabaseVideo;
    @FXML
    private MediaView mvQueryVideo;
    @FXML
    private TextField txtVideoDatabase;
    @FXML
    private TextField txtQueryVideo;
    @FXML
    private ListView lstviewResultsList;
    @FXML
    private Label lblQueryStatus;
    @FXML
    private ToggleGroup descriptorGroup;
    
    @FXML
    private void handleSearchAction(ActionEvent event) {
        // Provide query status feedback in the GUI
        lblQueryStatus.setText("Query Status: Loaded videos.");
        
        // Encode an mp4 video at the specified directory
//        encoder.encodeMp4(txtQueryVideo.getText());

        // Load in the video
        MediaPlayer player = loadVideo("/Users/stermark/Desktop/sports.mp4", mvDatabaseVideo);
        
        // Play the video
        player.play();
        
        // Load in the video
        MediaPlayer player2 = loadVideo("/Users/stermark/Desktop/sports.mp4", mvQueryVideo);
        
        // Play the video
        player2.play();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up some default form values
        txtVideoDatabase.setText("/Users/stermark/Desktop/MultimediaFinalProject/databse_videos/");
        txtQueryVideo.setText("/Users/stermark/Desktop/MultimediaFinalProject/query/second/");
        
        // Set default video descriptor toggle button
        descriptorGroup.selectToggle(descriptorGroup.getToggles().get(0));
        
        // Testing initializing the ListView
        addItemsToListView();
        
        // Initialize other variables
        encoder = new VideoEncoder();
    }   
    
    /**
     * Load in the h.264 encoded .mp4 at the given filepath into the given MediaView.
     * @param filepath - File path to the h.264 encoded .mp4 video.
     * @param viewer - The MediaViwer in the GUI that this generated MediaPlayer will be attached to.
     * @return 
     */
    private MediaPlayer loadVideo(String filepath, MediaView viewer)
    {
        // Load in an mp4 video file to the MediaPlayer
        File file = new File(filepath);
        String MEDIA_URL = file.toURI().toString(); 
        Media media = new Media(MEDIA_URL);
        MediaPlayer player = new MediaPlayer(media);
        viewer.setMediaPlayer(player);

        // Change width and height to fit video
        viewer.setPreserveRatio(true);
        
        return player;
    }
    
    /**
     * Adds a set of items to a ListView GUI item.
     */
    private void addItemsToListView()
    {
        ArrayList<String> months = new ArrayList<String>();

        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        
        lstviewResultsList.getItems().addAll(months);
    }
}
