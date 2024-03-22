package GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Player.AudioPlayer;
import javafx.stage.FileChooser.ExtensionFilter;

public class FXMLController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button buttonOpen;

    @FXML
    private Button buttonPlay;

    @FXML
    private Button buttonStop;

    @FXML
    private TextArea textField;

    @FXML
    private Slider volumeSlider;
    
    private AudioPlayer audioPlayer;
    
    private Thread playThread;
    
    @FXML
    private void open() throws FileNotFoundException, IOException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException {
    	
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource file");
    	fileChooser.getExtensionFilters().addAll(
    			new ExtensionFilter("Audio Files", "*.wav"));
    	File selectedFile = fileChooser.showOpenDialog(new Stage());
    	
    	if (selectedFile == null)
    		return;
    	this.audioPlayer = new AudioPlayer(selectedFile);
    	
    	playThread = new Thread(()->{
        	this.audioPlayer.play();
        });
        playThread.start();
    	
    	
        
    }

    @FXML
    void initialize() {

    }

}
