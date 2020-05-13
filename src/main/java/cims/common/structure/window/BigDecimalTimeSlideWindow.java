package cims.common.structure.window;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public class BigDecimalTimeSlideWindow extends TimeSlideWindow<BigDecimal> {

    public BigDecimalTimeSlideWindow(ChronoUnit unit, int size) {
        super(unit, size, BigDecimal.class);
    }

    @Override
    protected BigDecimal add(BigDecimal originValue, BigDecimal newValue) {
        if (originValue == null) {
            return newValue;
        }
        if (newValue == null) {
            return originValue;
        }
        return originValue.add(newValue);
    }

}
