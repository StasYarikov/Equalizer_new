package equalizer;

import java.util.concurrent.Callable;

public class FilterIIR implements Callable<short[]> {
	
	private short[] inputSignal;
	
	private short[] outputSignal;
	private double gain;
	private double[][] coffsNUMFilter;
	private double[][] coffsDENFilter;
	private int count_coffs;
	
	public FilterIIR() {
		this.gain = 1.0;
	}
	
	
	public void settings(final short[] inputSignal, final double[][] coffsNUMFilter, final double[][] coffsDENFilter) {
		this.inputSignal = inputSignal;
		this.coffsNUMFilter = coffsNUMFilter;
		this.coffsDENFilter = coffsDENFilter;
		this.outputSignal = new short[inputSignal.length];
        this.count_coffs = coffsNUMFilter.length;
	}
	
	private void convolution() {
		int numSections = this.coffsNUMFilter.length;
        int inputLength = this.inputSignal.length;

        // Проходим по всем элементам входного массива
        for (int i = 0; i < inputLength; i++) {
            // Инициализируем выходное значение текущего элемента
            double y = 0;

            // Проходим по всем секциям фильтра
            for (int j = 0; j < numSections; j++) {
                // Получаем коэффициенты для текущей секции
                double b0 = this.coffsNUMFilter[j][0];
                double b1 = this.coffsNUMFilter[j][1];
                double b2 = this.coffsNUMFilter[j][2];
                double a1 = this.coffsDENFilter[j][1];
                double a2 = this.coffsDENFilter[j][2];

                // Вычисляем выходное значение текущей секции
                if (i >= 2) {
                    y = b0 * this.inputSignal[i] + b1 * this.inputSignal[i - 1] + b2 * this.inputSignal[i - 2]
                            - a1 * this.outputSignal[i - 1] - a2 * this.outputSignal[i - 2];
                } else if (i == 1) {
                    y = b0 * this.inputSignal[i] + b1 * this.inputSignal[i - 1]
                            - a1 * this.outputSignal[i - 1];
                } else {
                    y = b0 * this.inputSignal[i];
                }

                // Обновляем выходное значение для текущего элемента
                this.outputSignal[i] += this.gain * (short)(y / numSections / 6);
            }
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