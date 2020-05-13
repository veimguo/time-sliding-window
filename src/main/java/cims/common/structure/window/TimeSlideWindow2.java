package cims.common.structure.window;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * 每个规则中维护一个窗口，因此不考虑并发的情况
 */
public abstract class TimeSlideWindow2<T> {

	private final T[] dataArray;
	private ChronoUnit unit;

	private boolean clean;
	private int preStartIdx;
	private int startIdx;
	private int lastIdx;

	private int size;
	private long doubleSize;

	private LocalDateTime startTime;

	@SuppressWarnings("unchecked")
	public TimeSlideWindow2(ChronoUnit unit, int size, Class<T> clazz) {
		super();
		this.unit = unit;
		this.size = size;
		clean = true;
		doubleSize = size * 2;
		preStartIdx = 0;
		startIdx = 0;
		lastIdx = 0;
		dataArray = (T[]) Array.newInstance(clazz, size);
	}

	public void put(T data) {
		LocalDateTime time = LocalDateTime.now();
		if (startTime == null) {
			dataArray[0] = data;
			startTime = time;
			return;
		}
		long unitGap = startTime.until(time, unit);
		int idx = (int) ((startIdx + unitGap) % size);
		if (unitGap >= size) {
			startTime = time;
			if (unitGap < doubleSize) {
				clean = false;
				preStartIdx = startIdx;
				startIdx = idx;
			} else {
				clean = true;
				lastIdx = preStartIdx = startIdx = 0;
				Arrays.fill(dataArray, null);
			}
			dataArray[startIdx] = data;
		} else {
			lastIdx = idx;
			if (clean) {
				dataArray[idx] = add(dataArray[idx], data);
			} else {
				dataArray[idx] = data;
			}
		}
	}

	public void put(LocalDateTime time, T data) {
		if (startTime == null) {
			dataArray[0] = data;
			startTime = time;
			return;
		}
		long unitGap = startTime.until(time, unit);
		int idx = (int) ((startIdx + unitGap) % size);
		if (unitGap >= size) {
			startTime = time;
			if (unitGap < doubleSize) {
				clean = false;
				preStartIdx = startIdx;
				startIdx = idx;
				lastIdx = idx;
			} else {
				clean = true;
				preStartIdx = startIdx = 0;
				Arrays.fill(dataArray, null);
			}
			dataArray[startIdx] = data;
		} else {
			if (clean || lastIdx == idx) {
				dataArray[idx] = add(dataArray[idx], data);
				lastIdx = idx;
				return;
			}
			dataArray[idx] = data;
			if (lastIdx < idx) {
				Arrays.fill(dataArray, lastIdx + 1, idx, null);
			} else {
				Arrays.fill(dataArray, 0, idx, null);
				Arrays.fill(dataArray, lastIdx + 1, size, null);
			}
			lastIdx = idx;
		}
	}

	public T get() {
		LocalDateTime time = LocalDateTime.now();
		long unitGap = startTime.until(time, unit);
		if (unitGap >= size) {
			return null;
		} else {
			if (clean) {
				T result = dataArray[0];
				for (int i = 1; i < size; i++) {
					result = add(result, dataArray[i]);
				}
				return result;
			} else {
				int idx = (int) ((startIdx + unitGap) % size);
				T result = dataArray[startIdx];
				if (idx < startIdx) {
					return result;
				}
				if (idx == startIdx) {
					++idx;
				}
				for (; idx < size; idx++) {
					result = add(result, dataArray[idx]);
				}
				return result;
			}
		}
	}

	public T get(LocalDateTime time) {
		long unitGap = startTime.until(time, unit);
		long gap = unitGap - lastIdx;
		if (unitGap >= doubleSize - 1 || gap >= size) {
			return null;
		} else {
			if (clean) {
				T result = dataArray[0];
				for (int i = 1; i < size; i++) {
					result = add(result, dataArray[i]);
				}
				return result;
			} else {
				int idx = (int) ((startIdx + unitGap) % size);
				T result = dataArray[startIdx];
				if (idx < startIdx) {
					for (; idx < preStartIdx; idx++) {
						result = add(result, dataArray[idx]);
					}
					return result;
				}
				if (idx == startIdx) {
					++idx;
				}
				for (int i = 0; i < preStartIdx; i++) {
					result = add(result, dataArray[i]);
				}
				for (; idx < size; idx++) {
					result = add(result, dataArray[idx]);
				}
				return result;
			}
		}
	}

	protected abstract T add(T originValue, T newValue);

}
