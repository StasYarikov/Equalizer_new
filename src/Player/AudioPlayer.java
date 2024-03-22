package Player;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

	private final SourceDataLine sourceDataLine;
	private final AudioInputStream audioInputStream;
	private final AudioFormat format;

	private final byte[] buff;
	private final int BUFF_SIZE = 8;
	private final int kolOt = 2;
	private int i = 0;
	private short[] arrayOfShort = new short[BUFF_SIZE / 2];

	public AudioPlayer(File musicFile) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		ReadMusicFile readFile = new ReadMusicFile(musicFile);
		this.sourceDataLine = readFile.getSourceDataLine();
		this.audioInputStream = readFile.getAudioInputStream();
		AudioFileFormat aff = new AudioFileFormat();
		format = new AudioFormat(aff.getSampleRate(), aff.getSampleSizeInBits(), aff.getChannels(), aff.isSigned(),
				aff.isBigEndian());
		this.buff = new byte[this.BUFF_SIZE];
		/*
		 * for (int i = 0; i < ind.length; i++) ind[i] = (byte) 100;
		 */

	}

	public void play() {
		try {
			this.sourceDataLine.open(this.format);
			this.sourceDataLine.start();

			this.audioInputStream.read(this.buff, 0, BUFF_SIZE);

			for (int l = 0, j = 0; j < BUFF_SIZE; j += 4, l++) {
				arrayOfShort[l] = (short) ((ByteBuffer.wrap(this.buff, j, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort() / 2));
			}
			for (int k = 0, j = 0; j < BUFF_SIZE / 4; j++, k += 4) {
				int vspom = arrayOfShort[j];
				this.buff[k] = (byte) vspom;
				this.buff[k + 1] = (byte) (vspom >>> 8);
				this.buff[k + 2] = (byte) (vspom >>> 16);
				this.buff[k + 3] = (byte) (vspom >>> 24);
			}

			sourceDataLine.write(this.buff, (i % kolOt) * BUFF_SIZE / kolOt, BUFF_SIZE / kolOt);

			while (this.audioInputStream.read(this.buff, (i % kolOt) * BUFF_SIZE / kolOt, BUFF_SIZE / kolOt) != -1) {
			
				for (int l = 0, j = 0; j < BUFF_SIZE / kolOt; j += 4, l++) { 
					arrayOfShort[l] = (short) ((ByteBuffer.wrap(this.buff, j + (i % kolOt) * BUFF_SIZE / kolOt, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort() / 2)); 
				}
				System.out.println("New point");
				for (int k = 0, j = 0; j < BUFF_SIZE / kolOt / 4; j++, k += 4) { 
					int vspom = arrayOfShort[j]; 
					this.buff[k + (i % kolOt) * BUFF_SIZE / kolOt] = (byte) vspom; 
					this.buff[k + 1 + (i % kolOt) * BUFF_SIZE / kolOt] = (byte) (vspom >>> 8); 
					this.buff[k + 2 + (i % kolOt) * BUFF_SIZE / kolOt] = (byte) (vspom >>> 16); 
					this.buff[k + 3 + (i % kolOt) * BUFF_SIZE / kolOt] = (byte) (vspom >>> 24); 
				} 
				i += 1; 
				
				sourceDataLine.write(this.buff, (i % kolOt) * BUFF_SIZE / kolOt, BUFF_SIZE / kolOt);
			}

			/*
			 * sourceDataLine.write(this.buff, ((i + 1) % kolOt) * BUFF_SIZE / kolOt,
			 * BUFF_SIZE / kolOt * (kolOt - 1 - ((i + 1) % kolOt)));
			 */

		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

}
