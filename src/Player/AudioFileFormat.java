package Player;

public class AudioFileFormat {
	
	private final boolean bigEndian;
	private final boolean signed;
	
	private int channels;
	private int sampleSizeInBits;
	private float sampleRate;
	
	public AudioFileFormat() {
		this.bigEndian = false;
		this.signed = false;
		this.sampleSizeInBits = 16;
		this.channels = 2;
		this.sampleRate = 44100.0f;
	}
	
	public float getSampleRate() {
		return this.sampleRate;
	}
	
	public int getSampleSizeInBits() {
		return this.sampleSizeInBits;
	}
	
	public int getChannels() {
		return this.channels;
	}
	
	public boolean isSigned() {
		return this.signed;
	}
	
	public boolean isBigEndian() {
		return this.bigEndian;
	}

}
