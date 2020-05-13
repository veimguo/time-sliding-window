package cims.common.structure.window;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SlideWindowTest {

	@Test
	public void testBigDecimal() {
		BigDecimalTimeSlideWindow window = new BigDecimalTimeSlideWindow(ChronoUnit.SECONDS, 10);
		try {
			// T
			window.put(new BigDecimal(1));
			window.put(new BigDecimal(2));
			window.put(new BigDecimal(3));
			window.put(new BigDecimal(4));
			window.put(new BigDecimal(5));
			window.put(new BigDecimal(6));
			assertEquals(new BigDecimal(21), window.get());

			// T+1
			Thread.sleep(1000);
			window.put(new BigDecimal(1));
			assertEquals(new BigDecimal(22), window.get());

			// T+2
			Thread.sleep(1000);
			window.put(new BigDecimal(2));
			assertEquals(new BigDecimal(24), window.get());

			// T+3
			Thread.sleep(1000);
			window.put(new BigDecimal(3));
			assertEquals(new BigDecimal(27), window.get()); // 21+ 1+ 2+ 3

			// T+4
			Thread.sleep(1000);
			window.put(new BigDecimal(4));
			assertEquals(new BigDecimal(31), window.get());

			// T+8
			Thread.sleep(4000);
			window.put(new BigDecimal(8));
			assertEquals(new BigDecimal(39), window.get());

			// T+11
			Thread.sleep(3000);
			window.put(new BigDecimal(10));
			assertEquals(new BigDecimal(27), window.get()); // 27+ 4+ 8+ 10- 21(T+0)-1(T+1)

			// T+12
			Thread.sleep(1000);
			window.put(new BigDecimal(11));
			assertEquals(new BigDecimal(36), window.get()); // 27 +11 -2(T+2)

			// T+21
			Thread.sleep(9000);
			assertEquals(new BigDecimal(11), window.get()); // T+12 ~ T+21
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
