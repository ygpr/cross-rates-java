package io.github.rates.configurations;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheUpdateProgram {

    private final ScheduledExecutorService executorService;
    private final Long initialDelay;
    private final Long delay;
    private final TimeUnit timeUnit;

    public CacheUpdateProgram(ScheduledExecutorService executorService, Long initialDelay, Long delay, TimeUnit timeUnit) {
        this.executorService = executorService;
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public Long getInitialDelay() {
        return initialDelay;
    }

    public Long getDelay() {
        return delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
