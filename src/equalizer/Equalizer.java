package equalizer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Equalizer {
	
	private Filter[] filters;
	private FilterIIR[] filtersIIR;
	private short[] outputSignal;
	private boolean type_of_filters;
	
	private final static char COUNT_OF_THREADS = 6;
	ExecutorService pool;
	
	public Equalizer(boolean type) {
		pool = Executors.newFixedThreadPool(COUNT_OF_THREADS);
		this.type_of_filters = type;
		this.createFilters(this.type_of_filters);
	}
	
	public void setInputSignal(short[] inputSignal, boolean type) {
		this.outputSignal = new short[inputSignal.length];
		if (type) {
	        this.filters[0].settings(inputSignal, FilterInfo.COFFS_FIR_BAND_0);
	        this.filters[1].settings(inputSignal, FilterInfo.COFFS_FIR_BAND_1);
	        this.filters[2].settings(inputSignal, FilterInfo.COFFS_FIR_BAND_2);
	        this.filters[3].settings(inputSignal, FilterInfo.COFFS_FIR_BAND_3);
	        this.filters[4].settings(inputSignal, FilterInfo.COFFS_FIR_BAND_4);
	        this.filters[5].settings(inputSignal, FilterInfo.COFFS_FIR_BAND_5);
		}
		else {
			this.filtersIIR[0].settings(inputSignal, FilterIIRInfo.COFFS_NUM_OF_BAND_0, FilterIIRInfo.COFFS_DEN_OF_BAND_0);
			this.filtersIIR[1].settings(inputSignal, FilterIIRInfo.COFFS_NUM_OF_BAND_1, FilterIIRInfo.COFFS_DEN_OF_BAND_1);
			this.filtersIIR[2].settings(inputSignal, FilterIIRInfo.COFFS_NUM_OF_BAND_2, FilterIIRInfo.COFFS_DEN_OF_BAND_2);
			this.filtersIIR[3].settings(inputSignal, FilterIIRInfo.COFFS_NUM_OF_BAND_3, FilterIIRInfo.COFFS_DEN_OF_BAND_3);
			this.filtersIIR[4].settings(inputSignal, FilterIIRInfo.COFFS_NUM_OF_BAND_4, FilterIIRInfo.COFFS_DEN_OF_BAND_4);
			this.filtersIIR[5].settings(inputSignal, FilterIIRInfo.COFFS_NUM_OF_BAND_5, FilterIIRInfo.COFFS_DEN_OF_BAND_5);
		}
    }
	
	private void createFilters(boolean type) {
		if (type) {
			this.filters = new  Filter [FilterInfo.COUNT_OF_BANDS] ;
			for (int i = 0; i < FilterInfo.COUNT_OF_BANDS; i++)
				this.filters[i] = new Filter();
		}
		else {
			this.filtersIIR = new  FilterIIR[FilterIIRInfo.COUNT_OF_BANDS] ;
			for (int i = 0; i < FilterIIRInfo.COUNT_OF_BANDS; i++)
				this.filtersIIR[i] = new FilterIIR();
		}
    }
	
	public void equalization() throws InterruptedException, ExecutionException {
		if (this.type_of_filters) {
			Future<short[]>[] fs = new Future[FilterInfo.COUNT_OF_BANDS];
			for (int i = 0; i < FilterInfo.COUNT_OF_BANDS; i++)
				fs[i] = pool.submit(this.filters[i]);
			
			for (int i = 0; i < this.outputSignal.length; i++) {
				this.outputSignal[i] += fs[0].get()[i] +
						fs[1].get()[i] +
						fs[2].get()[i] +
						fs[3].get()[i] +
						fs[4].get()[i] +
						fs[5].get()[i];
			}
		}
		else
		{
			Future<short[]>[] fs = new Future[FilterIIRInfo.COUNT_OF_BANDS];
			for (int i = 0; i < FilterIIRInfo.COUNT_OF_BANDS; i++)
				fs[i] = pool.submit(this.filtersIIR[i]);
			
			for (int i = 0; i < this.outputSignal.length; i++) {
				this.outputSignal[i] += fs[0].get()[i] +
						fs[1].get()[i] +
						fs[2].get()[i] +
						fs[3].get()[i] +
						fs[4].get()[i] +
						fs[5].get()[i];
			}
		}
		
	}
	
	public short[] getOutputSignal() {
		return this.outputSignal;
	}
	
	public Filter getFilter(int numberFilter) {
		return this.filters[numberFilter];
	}
	
	public FilterIIR getFilterIIR(int numberFilter) {
		return this.filtersIIR[numberFilter];
	}
	
	public void close() {
		if (this.pool != null)
			this.pool.shutdown();
	}

}
