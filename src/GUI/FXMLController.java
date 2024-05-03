package GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Player.AudioPlayer;
import javafx.stage.FileChooser.ExtensionFilter;

public class FXMLController implements Initializable{

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
    private Slider volumeSlider;
    @FXML
    private Slider Slider0;
    @FXML
    private Slider Slider1;
    @FXML
    private Slider Slider2;
    @FXML
    private Slider Slider3;
    @FXML
    private Slider Slider4;
    @FXML
    private Slider Slider5;
    
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label label4;
    @FXML
    private Label label5;
    @FXML
    private Label label6;
    @FXML
    private Label label7;
    @FXML
    private Label label11;
    
    @FXML
    private CheckBox checkFir;

    @FXML
    private CheckBox checkIir;
    
    @FXML
    private CheckBox checkChor;

    @FXML
    private CheckBox checkOverdrive;
    
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
    	if (checkFir.isSelected() && checkIir.isSelected())
    		return;
    	this.audioPlayer = new AudioPlayer(selectedFile, checkFir.isSelected());
    	label7.setText(selectedFile.getPath());
    	System.out.println("PLAY");
		buttonPlay.setText("Pause");
    	playThread = new Thread(()->{
        	try {
                this.resetSliders();
                this.audioPlayer.play();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        playThread.start();
        
    }
    
    private void resetSliders() {
    	Slider0.setValue(1);
        Slider1.setValue(1);
        Slider2.setValue(1);
        Slider3.setValue(1);
        Slider4.setValue(1);
        Slider5.setValue(1);
        volumeSlider.setValue(1);
		
	}

	@FXML
    void play() {
        if (this.audioPlayer != null) {
            if (this.audioPlayer.getStopStatus()) {
                playThread = new Thread(() -> {
                    try {
						this.audioPlayer.play();
						System.out.println("PAUSE");
						buttonPlay.setText("Pause");
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                });
                playThread.start();
            } else if (buttonPlay.getText().equals("Play")) {
                this.audioPlayer.setPauseStatus(false);
                buttonPlay.setText("Pause");
                System.out.println("PLAY");
            } else {
            	this.audioPlayer.setPauseStatus(true);
            	buttonPlay.setText("Play");
            	System.out.println("PAUSE");
            }
        }
    }

    @FXML
    void stop() {
    	System.out.println("STOP");
        label7.setText("Path area");
        if (this.audioPlayer != null) {
            if (this.playThread != null)
                this.playThread.interrupt();
            this.audioPlayer.close();
        }
        resetSliders();
        buttonPlay.setText("Play");
        this.audioPlayer = null;
    }

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		this.listenSliders();
        this.gainFromSlider();
	}

	private void gainFromSlider() {
	    volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            audioPlayer.setGain(newValue.doubleValue() * newValue.doubleValue());
            label11.setText(String.format("x%.3f", newValue.doubleValue() * newValue.doubleValue()));
        });
	}

	private void listenSliders() {
		
		Slider0.valueProperty().addListener((observable, oldValue, newValue) -> {
			String str = String.format("%.3f", (Math.log10(newValue.doubleValue())) * 20.0);
            label1.setText(str);
            if (checkFir.isSelected())
            	audioPlayer.getEqualizer().getFilter(0).setGain(newValue.doubleValue());
            else
            	audioPlayer.getEqualizer().getFilterIIR(0).setGain(newValue.doubleValue());
        });

        Slider1.valueProperty().addListener((observable, oldValue, newValue) -> {
            String str = String.format("%.3f", (Math.log10(newValue.doubleValue())) * 20.0);
            label2.setText(str);
            if (checkFir.isSelected())
            	audioPlayer.getEqualizer().getFilter(1).setGain(newValue.doubleValue());
            else
            	audioPlayer.getEqualizer().getFilterIIR(1).setGain(newValue.doubleValue());
        });

        Slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
        	String str = String.format("%.3f", (Math.log10(newValue.doubleValue())) * 20.0);
            label3.setText(str);
            if (checkFir.isSelected())
            	audioPlayer.getEqualizer().getFilter(2).setGain(newValue.doubleValue());
            else
            	audioPlayer.getEqualizer().getFilterIIR(2).setGain(newValue.doubleValue());
        });

        Slider3.valueProperty().addListener((observable, oldValue, newValue) -> {
        	String str = String.format("%.3f", (Math.log10(newValue.doubleValue())) * 20.0);
            label4.setText(str);
            if (checkFir.isSelected())
            	audioPlayer.getEqualizer().getFilter(3).setGain(newValue.doubleValue());
            else
            	audioPlayer.getEqualizer().getFilterIIR(3).setGain(newValue.doubleValue());
        });

        Slider4.valueProperty().addListener((observable, oldValue, newValue) -> {
        	String str = String.format("%.3f", (Math.log10(newValue.doubleValue())) * 20.0);
            label5.setText(str);
            if (checkFir.isSelected())
            	audioPlayer.getEqualizer().getFilter(4).setGain(newValue.doubleValue());
            else
            	audioPlayer.getEqualizer().getFilterIIR(4).setGain(newValue.doubleValue());
        });

        Slider5.valueProperty().addListener((observable, oldValue, newValue) -> {
        	String str = String.format("%.3f", (Math.log10(newValue.doubleValue())) * 20.0);
            label6.setText(str);
            if (checkFir.isSelected())
            	audioPlayer.getEqualizer().getFilter(5).setGain(newValue.doubleValue());
            else
            	audioPlayer.getEqualizer().getFilterIIR(5).setGain(newValue.doubleValue());
        });
		
	}
	
    @FXML
    void chorCheckBox() {
    	System.out.println("CHORUS");
        if (this.checkChor.isSelected())
            this.audioPlayer.setChor(true);
        else this.audioPlayer.setChor(false);
    }

    @FXML
    void overdriveCheckBox(ActionEvent event) {
    	System.out.println("OVERDRIVE");
        if (this.checkOverdrive.isSelected())
            this.audioPlayer.setOverdrive(true);
        else this.audioPlayer.setOverdrive(false);
    }

}
