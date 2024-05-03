package Player;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import equalizer.Equalizer;
import effects.Chorus;
import effects.Delay;
import effects.Overdrive;

public class AudioPlayer {

	private final SourceDataLine sourceDataLine;
	private final AudioInputStream audioInputStream;
	private final AudioFormat format;
	private Equalizer equalizer;
	private boolean stopStatus;
	private boolean pauseStatus;
	private boolean type_of_filters;
	
	private final Chorus chorus;
    private boolean isChor;
    private final Overdrive overdrive;
    private boolean isOverdrive;
	
	private double gain;
	private final byte[] buff;
	private final static int BUFF_SIZE = 32768;
	private final int kolOt = 2;
	private int i = 0;
	private short[] arrayOfShort = new short[BUFF_SIZE / 2];

	public AudioPlayer(File musicFile, boolean type) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		ReadMusicFile readFile = new ReadMusicFile(musicFile);
		this.sourceDataLine = readFile.getSourceDataLine();
		this.audioInputStream = readFile.getAudioInputStream();
		AudioFileFormat aff = new AudioFileFormat();
		format = new AudioFormat(aff.getSampleRate(), aff.getSampleSizeInBits(), aff.getChannels(), aff.isSigned(),
				aff.isBigEndian());
		this.buff = new byte[this.BUFF_SIZE];
		this.type_of_filters = type;
		this.equalizer = new Equalizer(this.type_of_filters);
		this.gain = 1.0;
		this.isOverdrive = false;
		this.overdrive = new Overdrive();
		this.isChor = false;
		this.chorus = new Chorus();

	}

	public void play() throws InterruptedException, ExecutionException {
		try {
			this.sourceDataLine.open(this.format);
			this.sourceDataLine.start();
			this.pauseStatus = false;
            this.stopStatus = false;

			this.audioInputStream.read(this.buff, 0, BUFF_SIZE);

			for (int l = 0, j = 0; j < BUFF_SIZE; j += 2, l++) {
				arrayOfShort[l] = (short) ((ByteBuffer.wrap(this.buff, j, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort() / 2) * this.gain);
			}
			
			if (this.pauseStatus) this.pause();
			
			if (this.isOverdrive) {
				this.overdrive(this.arrayOfShort);
			}

			if (this.isChor) {
				this.chor(this.arrayOfShort);
            }
			
			equalizer.setInputSignal(this.arrayOfShort, this.type_of_filters);
			this.equalizer.equalization();
			this.arrayOfShort = equalizer.getOutputSignal();
			
			for (int k = 0, j = 0; j < BUFF_SIZE / 2; j++, k += 2) {
				int vspom = arrayOfShort[j];
				this.buff[k] = (byte) vspom;
				this.buff[k + 1] = (byte) (vspom >>> 8);
			}

			sourceDataLine.write(this.buff, (i % kolOt) * BUFF_SIZE / kolOt, BUFF_SIZE / kolOt);

			while (this.audioInputStream.read(this.buff, (i % kolOt) * BUFF_SIZE / kolOt, BUFF_SIZE / kolOt) != -1) {
			
				for (int l = 0, j = 0; j < BUFF_SIZE / kolOt; j += 2, l++) { 
					arrayOfShort[l] = (short) ((ByteBuffer.wrap(this.buff, j + (i % kolOt) * BUFF_SIZE / kolOt, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort() / 2) * this.gain); 
				}
				
				if (this.pauseStatus) this.pause();

                if (this.stopStatus) break;
                
    			if (this.isOverdrive) {
				this.overdrive(this.arrayOfShort);
				}
                
                if (this.isChor) {
                	this.chor(this.arrayOfShort);
                }
				
				equalizer.setInputSignal(this.arrayOfShort, this.type_of_filters);
				this.equalizer.equalization();
				this.arrayOfShort = equalizer.getOutputSignal();
				
				for (int k = 0, j = 0; j < BUFF_SIZE / kolOt / 2; j++, k += 2) { 
					int vspom = arrayOfShort[j]; 
					this.buff[k + (i % kolOt) * BUFF_SIZE / kolOt] = (byte) vspom; 
					this.buff[k + 1 + (i % kolOt) * BUFF_SIZE / kolOt] = (byte) (vspom >>> 8); 
				} 
				i += 1; 
                sourceDataLine.write(this.buff, (i % kolOt) * BUFF_SIZE / kolOt, BUFF_SIZE / kolOt);
			}
			
			this.sourceDataLine.drain();
            this.sourceDataLine.close();

		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void pause() {
        if (this.pauseStatus) {
            while (true) {
                try {
                    if (!this.pauseStatus || this.stopStatus) break;
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

	public void setPauseStatus(boolean pauseStatus) {
		this.pauseStatus = pauseStatus;
	}
	
	public void setStopStatus(boolean stopStatus) {
        this.stopStatus = stopStatus;
    }
	
	public void close() {
        if (this.sourceDataLine != null) this.sourceDataLine.close();
    }
	
	public boolean getStopStatus() {
		return this.stopStatus;
	}
	
	public Equalizer getEqualizer() {
		return this.equalizer;
	}
	
	public void setGain(double gain) {
		this.gain = gain;
	}
	
	public static int getBuffSize() {
		return BUFF_SIZE;
	}
	
    private void overdrive(short[] inputSamples) {
        this.overdrive.setInputSampleStream(inputSamples);
        this.overdrive.createEffect();
    }

    public boolean distortionIsActive() {
        return this.isOverdrive;
    }

    public void setOverdrive(boolean b) {
        this.isOverdrive = b;
    }

    private void chor(short[] inputSamples) throws ExecutionException, InterruptedException {
        chorus.setInputSampleStream(inputSamples);
        chorus.createEffect();
    }

    public boolean chorIsActive() {
        return this.isChor;
    }

    public void setChor(boolean b) {
        this.isChor = b;
    }

}
