package site.zido.coffee.extra.limiter;

import java.util.concurrent.TimeUnit;

public class LimiterOperation {
    private final String key;
    private final long timeout;
    private final TimeUnit unit;
    private final String name;

    public LimiterOperation(Builder builder) {
        this.key = builder.getKey();
        this.timeout = builder.getTimeout();
        this.unit = builder.getUnit();
        name = builder.getName();
    }

    public String getKey() {
        return key;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private String key = "";
        private long timeout = 55;
        private TimeUnit unit = TimeUnit.SECONDS;
        private String name = "";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }


        public LimiterOperation build() {
            return new LimiterOperation(this);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
