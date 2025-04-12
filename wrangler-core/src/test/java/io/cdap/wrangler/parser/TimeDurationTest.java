package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

    @Test
    public void testTimeDurationParsing() {
        Assert.assertEquals(5, new TimeDuration("5ms").getMilliseconds());
        Assert.assertEquals(2100, new TimeDuration("2.1s").getMilliseconds());
        Assert.assertEquals(3600000, new TimeDuration("1h").getMilliseconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeDurationParsing() {
        new TimeDuration("10XYZ"); // Invalid unit
    }
}
