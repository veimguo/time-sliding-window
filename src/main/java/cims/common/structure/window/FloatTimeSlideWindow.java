package cims.common.structure.window;

import java.time.temporal.ChronoUnit;

public class FloatTimeSlideWindow extends TimeSlideWindow<Float> {

	public FloatTimeSlideWindow(ChronoUnit unit, int size) {
		super(unit, size, Float.class);
	}

	@Override
	protected Float add(Float originValue, Float newValue) {
		if (originValue == null) {
			return newValue;
		}
		if (newValue == null) {
			return originValue;
		}
		return originValue + newValue;
	}

}
