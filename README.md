# Intelligent Video Querying

The aim of this project is produce an efficient system for performing multimedia queries and finding videos within a database that are similar to a short query video clip. Videos in the database are pre-processed and video descriptors are extracted that contain information about the properties and content of each video. When a query video is provided, it is similarly processed and matched with videos that are most similar.

Setup
------

This project was written in Java 8, and thus we can only guarantee that it will work with this version.

It also requires valid Google Cloud credentials. Setup and trial information can be found here: https://cloud.google.com/video-intelligence/

Running
------
* Insert a new folder containing a query .mp4 file into the "query_videos" directory. 
* When executing the program, enter this video directory into the query video field.
* Select the descriptors you would like to use.
* Press the search button.


Descriptors
------
Note that each video descriptor that is selected when searching is given equal weight for the final results. The result scores that are reported are such that a higher score means a better match with the query video.

#### Dominant Color

  * **Goal**: Find videos that use similar color palettes throughout the video
  * **API:** OpenCV
  * **Usage:** K-means clustering is used to find 5 dominant colors within each frame of the database videos, as well as the percentage of the frame they each make up. This is also done for the query video. The query video’s dominant color palettes are then processed with K-means clustering to get a set of 5 dominant colors and their percentages for the entire query clip. These are then compared with database video on a frame-by-frame basis in order determine if the videos are have similar dominant colors.

#### Motion
* **Goal:** Find videos with similar amounts of frame by frame motion differences
* **API:** OpenCV
* **Usage:** Frames are converted to grayscale and the absolute difference between two successive frames is calculated. Thresholding is done on the difference frame to produce a binary image. The binary difference frames are dilated slightly and then contours are found around these areas. The total contour area found is the measure of motion between the two successive frames. Videos with closer average motion are considered more similar.


#### Object Similarity
* **Goal:** Find videos that contain similar objects and content
* **API:** Google Cloud Video Intelligence
* **Usage:** Given an .mp4 video, the Video Intelligence API returns a set of annotated video labels and shot labels. These labels contain brief descriptions of objects that it found in the video, along with a confidence score. For each frame in a given database video, we check if the video labels found in the query video are present in the current frame. If the label is found in the current frame, the frame’s score increased by the confidence of the query label multiplied by the confidence of the database video label. This value is then averaged by dividing this sum by the total number of matches found for the current frame. Similarly, the overall score is found as the sum of the frame scores (pre-averaged) divided by total number of labels considered across all frames.
