package equalizer;

import java.util.concurrent.Callable;

public class Filter implements Callable<short[]> {
	
	private short[] inputSignal;
	
	private short[] outputSignal;
	private double gain;
	private double[] coffsFIRFilter;
	private int count_coffs;
	
	public Filter() {
		this.gain = 1.0;
	}
	
	
	public void settings(final short[] inputSignal, final double[] coffsFIRFilter) {
		this.inputSignal = inputSignal;
		this.coffsFIRFilter = coffsFIRFilter;
		this.outputSignal = new short[inputSignal.length];
        this.count_coffs = coffsFIRFilter.length;
	}
	
	private void convolution() {
		double tmp;
        for(int i = 0; i <  this.inputSignal.length; i++) {
            tmp = 0;
            for(int j = 0; j < this.count_coffs; j++) {
                if(i - j >= 0)
                    tmp += coffsFIRFilter[j] * this.inputSignal[i - j];
            }
            this.outputSignal[i] += this.gain * (short)(tmp / 6); //делим на 6, чтобы не было перегруза на пересечении фильтров
        }
    }
	
	public void setGain(double gain) {
		this.gain = gain;
	}

	@Override
	public short[] call() throws Exception {
		this.convolution();
		return this.outputSignal;
	}
	
	

}
