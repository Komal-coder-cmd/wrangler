package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTest {

    @Test
    public void testByteSizeParsing() {
        Assert.assertEquals(10240, new ByteSize("10KB").getBytes());
        Assert.assertEquals(1572864, new ByteSize("1.5MB").getBytes());
        Assert.assertEquals(5368709120L, new ByteSize("5GB").getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSizeParsing() {
        new ByteSize("10XYZ"); // Invalid unit
    }
}
