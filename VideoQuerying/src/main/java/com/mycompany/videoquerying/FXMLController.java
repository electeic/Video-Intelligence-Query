package com.mycompany.videoquerying;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class FXMLController implements Initializable {
    
    /* Video Querying Variables */
    private VideoEncoder encoder;
    
    private Duration databaseVideoDuration;
    private Duration queryVideoDuration;
    
    private final int FRAME_RATE = 30;
    
    private final String DATABASE_DIR = "./database_videos/";
    
    /* FXML Variables */
    @FXML
    private MediaView mvDatabaseVideo;
    @FXML
    private MediaView mvQueryVideo;
    @FXML
    private TextField txtQueryVideo;
    @FXML
    private ListView lstviewResultsList;
    @FXML
    private Label lblQueryStatus;
    @FXML
    private ToggleGroup descriptorGroup;
    @FXML
    private Slider querySlider;
    @FXML
    private Slider databaseSlider;
    
    @FXML
    private void handleSearchAction(ActionEvent event) {
        // Provide query status feedback in the GUI
        lblQueryStatus.setText("Query Status: Loaded videos.");
        
        // Encode an mp4 video at the specified directory
//        encoder.encodeMp4(txtQueryVideo.getText());

        // Load database video
        loadDatabaseVideo("./database_videos/sports/sports.mp4");
        
        // Load query video
        loadQueryVideo(txtQueryVideo.getText());
    }
    
    private void loadQueryVideo(String filepath)
    {
        loadVideo(filepath, mvQueryVideo);
        
        // Dispose of this media player once it is done
        mvQueryVideo.getMediaPlayer().setOnEndOfMedia(new Runnable() 
        {
            @Override
            public void run() 
            {
                mvQueryVideo.getMediaPlayer().dispose();
            }
        });
        
        // Set up listeners so that the slider bar reflects the current values of the video being displayed
        mvQueryVideo.getMediaPlayer().currentTimeProperty().addListener(new InvalidationListener()
        {
            public void invalidated(javafx.beans.Observable ov)
            {
                updateSliderValue(mvQueryVideo.getMediaPlayer(), querySlider, queryVideoDuration);
            }
        });
        
        mvQueryVideo.getMediaPlayer().setOnReady(new Runnable() 
        {
            @Override
            public void run() 
            {
                queryVideoDuration = mvQueryVideo.getMediaPlayer().getMedia().getDuration();
                querySlider.setMax(100);
                updateSliderValue(mvQueryVideo.getMediaPlayer(), querySlider, queryVideoDuration);
            }
        });
        
        querySlider.valueProperty().addListener(new InvalidationListener()
        {
            public void invalidated(javafx.beans.Observable ov)
            {
                if (querySlider.isValueChanging())
                {
                    // multiply duration by percentage calculated by slider position
                    mvQueryVideo.getMediaPlayer().seek(queryVideoDuration.multiply(querySlider.getValue() / 100.0));
                }
            }
        });
    }
    
    // Loads in the given database video and updates GUI values accordingly
    private void loadDatabaseVideo(String filepath)
    {
        loadVideo(filepath, mvDatabaseVideo);

        // Dispose of this media player once it is done
        mvDatabaseVideo.getMediaPlayer().setOnEndOfMedia(new Runnable() 
        {
            @Override
            public void run() 
            {
                mvDatabaseVideo.getMediaPlayer().dispose();
            }
        });
        
        // Set up listeners so that the slider bar reflects the current values of the video being displayed
        mvDatabaseVideo.getMediaPlayer().currentTimeProperty().addListener(new InvalidationListener()
        {
            public void invalidated(javafx.beans.Observable ov)
            {
                updateSliderValue(mvDatabaseVideo.getMediaPlayer(), databaseSlider, databaseVideoDuration);
            }
        });
        
        mvDatabaseVideo.getMediaPlayer().setOnReady(new Runnable() 
        {
            @Override
            public void run() 
            {
                databaseVideoDuration = mvDatabaseVideo.getMediaPlayer().getMedia().getDuration();
                databaseSlider.setMax(100);
                updateSliderValue(mvDatabaseVideo.getMediaPlayer(), databaseSlider, databaseVideoDuration);
            }
        });
        
        databaseSlider.valueProperty().addListener(new InvalidationListener()
        {
            public void invalidated(javafx.beans.Observable ov)
            {
                if (databaseSlider.isValueChanging())
                {
                    // multiply duration by percentage calculated by slider position
                    mvDatabaseVideo.getMediaPlayer().seek(databaseVideoDuration.multiply(databaseSlider.getValue() / 100.0));
                }
            }
        });
    }

    // Updates the slider values so that the current video position is reflected in the slider's current value
    protected void updateSliderValue(final MediaPlayer player, final Slider slider, final Duration duration) 
    {
        if (slider != null) 
        {
            Platform.runLater(new Runnable() 
            {
                public void run() 
                {
                    Duration currentTime = player.getCurrentTime();
                    slider.setDisable(duration.isUnknown());
                    if (!slider.isDisabled() && duration.greaterThan(Duration.ZERO) && !slider.isValueChanging()) 
                    {
                        slider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                }
            });
        }
    }
    
    @FXML
    private void playDatabaseVideo()
    {
        if (mvDatabaseVideo.getMediaPlayer() != null)
        {
            mvDatabaseVideo.getMediaPlayer().play();
        }
    }
    
    @FXML
    private void pauseDatabaseVideo()
    {
        if (mvDatabaseVideo.getMediaPlayer() != null)
        {
            mvDatabaseVideo.getMediaPlayer().pause();
        }
    }
    
    @FXML
    private void stopDatabaseVideo()
    {
        if (mvDatabaseVideo.getMediaPlayer() != null)
        {
            mvDatabaseVideo.getMediaPlayer().stop();
        }
    }
    
    @FXML
    private void playQueryVideo()
    {
        if (mvQueryVideo.getMediaPlayer() != null)
        {
            mvQueryVideo.getMediaPlayer().play();
        }
    }
    
    @FXML
    private void pauseQueryVideo()
    {
        if (mvQueryVideo.getMediaPlayer() != null)
        {
            mvQueryVideo.getMediaPlayer().pause();
        }
    }
    
    @FXML
    private void stopQueryVideo()
    {
        if (mvQueryVideo.getMediaPlayer() != null)
        {
            mvQueryVideo.getMediaPlayer().stop();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up some default form values
        txtQueryVideo.setText("./query_videos/first/first.mp4");
        
        // Set default video descriptor toggle button
        descriptorGroup.selectToggle(descriptorGroup.getToggles().get(0));
        
        // Add the videos in the database directory to the list view
        initializeDatabaseListView();
        
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
        Media media = new Media(file.toURI().toString());
        
        MediaPlayer player = new MediaPlayer(media);
        viewer.setMediaPlayer(player);

        // Change width and height to fit video
        viewer.setPreserveRatio(true);
        
        return player;
    }
    
    // Adds a set of items to a ListView GUI item.
    private void initializeDatabaseListView()
    {
        // Add a listener to results list view to handle selection changes
        lstviewResultsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() 
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
            {
                loadDatabaseVideo(DATABASE_DIR + newValue + "/" + newValue + ".mp4");
            }
        });
        
        // Get the names of the videos in the database
        File[]directories = new File(DATABASE_DIR).listFiles(File::isDirectory);
        ArrayList<String> directoryNames = new ArrayList<String>();
        for (int i = 0; i < directories.length; i++)
        {
            directoryNames.add(directories[i].getName());
        }
        
        lstviewResultsList.getItems().addAll(directoryNames);
    }
}
