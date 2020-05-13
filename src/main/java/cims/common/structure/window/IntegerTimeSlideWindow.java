package cims.common.structure.window;

import java.time.temporal.ChronoUnit;

public class IntegerTimeSlideWindow extends TimeSlideWindow<Integer> {

	public IntegerTimeSlideWindow(ChronoUnit unit, int size) {
		super(unit, size, Integer.class);
	}

	@Override
	protected Integer add(Integer originValue, Integer newValue) {
		if (originValue == null) {
			return newValue;
		}
		if (newValue == null) {
			return originValue;
		}
		return originValue + newValue;
	}

}
