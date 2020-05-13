package cims.common.structure.window;

import java.time.temporal.ChronoUnit;

public class DoubleTimeSlideWindow extends TimeSlideWindow<Double> {

	public DoubleTimeSlideWindow(ChronoUnit unit, int size) {
		super(unit, size, Double.class);
	}

	@Override
	protected Double add(Double originValue, Double newValue) {
		if (originValue == null) {
			return newValue;
		}
		if (newValue == null) {
			return originValue;
		}
		return originValue + newValue;
	}

}
