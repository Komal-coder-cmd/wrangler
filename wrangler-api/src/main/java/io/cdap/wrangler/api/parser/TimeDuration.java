package io.cdap.wrangler.api.parser;

public class TimeDuration extends Token {
    private final long milliseconds;

    public TimeDuration(String value) {
        super(value);
        this.milliseconds = parseTimeDuration(value);
    }

    private long parseTimeDuration(String value) {
        if (value.endsWith("ms")) return Long.parseLong(value.replace("ms", ""));
        if (value.endsWith("s")) return Long.parseLong(value.replace("s", "")) * 1000;
        if (value.endsWith("m")) return Long.parseLong(value.replace("m", "")) * 1000 * 60;
        if (value.endsWith("h")) return Long.parseLong(value.replace("h", "")) * 1000 * 60 * 60;
        if (value.endsWith("d")) return Long.parseLong(value.replace("d", "")) * 1000L * 60L * 60L * 24L;
        throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    @Override
    public Object value() {
        return milliseconds;
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }
}
