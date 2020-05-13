package cims.common.structure.window;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public abstract class TimeSlideWindow<T> {

    private static final int MAX_SIZE = Integer.MAX_VALUE >> 1;
    private final T[] dataArray;
    private final ChronoUnit unit;
    private final int size;
    private final int doubleSize;
    private final int endIdx;
    private final long expireIdx; // 当要查询的数据的下标大于等于此值时，dataArray中的数据已全部过期

    private int lastIdx;
    private LocalDateTime startTime;

    @SuppressWarnings("unchecked")
    public TimeSlideWindow(ChronoUnit unit, int size, Class<T> clazz) {
        super();
        if (size > MAX_SIZE) {
            throw new RuntimeException("窗口大小不能超过：" + MAX_SIZE);
        }
        this.unit = unit;
        this.size = size;
        doubleSize = size * 2;
        expireIdx = size * 3 - 1;
        endIdx = doubleSize - 1;
        lastIdx = -1;
        dataArray = (T[]) Array.newInstance(clazz, doubleSize);
    }

    public void put(T data) {
        LocalDateTime time = LocalDateTime.now();
        if (startTime == null) {
            dataArray[0] = data;
            startTime = time;
            lastIdx = 0;
            return;
        }
        int unitGap = (int) startTime.until(time, unit);
        if (unitGap >= doubleSize) {
            startTime = time;
            lastIdx = -1;
        }
        int idx = unitGap % doubleSize;
        if (lastIdx == idx) {
            dataArray[idx] = add(dataArray[idx], data);
        } else {
            dataArray[idx] = data;
        }
        lastIdx = idx;
    }

    public T get() {
        LocalDateTime time = LocalDateTime.now();
        T result = null;
        long idxGap = startTime.until(time, unit);
        if (idxGap >= expireIdx) {
            return result;
        }
        int idx = (int) idxGap;
        int count = size;
        if (idxGap > endIdx) {
            count = (int) (expireIdx - idxGap);
            idx = endIdx;
        }
        for (int i = 0; i < count; i++, idx = (idx - 1 + doubleSize) % doubleSize) {
            result = add(result, dataArray[idx]);
        }
        return result;
    }

    protected abstract T add(T originValue, T newValue);

}
