package Player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ReadMusicFile {
	
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	
	public ReadMusicFile(File filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		if (filePath != null) {
			this.audioInputStream = AudioSystem.getAudioInputStream(filePath);
			AudioFormat format = audioInputStream.getFormat();
			this.sourceDataLine = AudioSystem.getSourceDataLine(format);
			this.sourceDataLine.flush();
			
			
		}
	}
	
	public SourceDataLine getSourceDataLine() {
		return this.sourceDataLine;
	}
	
	public AudioInputStream getAudioInputStream() {
		return this.audioInputStream;
	}

}
