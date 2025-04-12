package io.cdap.wrangler.core.directive;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.executor.ExecutorContext;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.TokenGroup;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class AggregateStatsDirectiveTest {
    private AggregateByteTimeDirective directive;
    private ExecutorContext context;

    @Before
    public void setUp() {
        directive = new AggregateByteTimeDirective();
        context = new ExecutorContext();

        // Initialize directive with column names
        directive.initialize(Arrays.asList(
                new TokenGroup(new ColumnName("sizeColumn"), TokenType.COLUMN_NAME),
                new TokenGroup(new ColumnName("durationColumn"), TokenType.COLUMN_NAME),
                new TokenGroup(new ColumnName("totalSize"), TokenType.COLUMN_NAME),
                new TokenGroup(new ColumnName("totalTime"), TokenType.COLUMN_NAME)
        ));
    }

    @Test
    public void testDirectiveExecution() {
        Row row1 = new Row().add("sizeColumn", new ByteSize("10KB")).add("durationColumn", new TimeDuration("5s"));
        Row row2 = new Row().add("sizeColumn", new ByteSize("20KB")).add("durationColumn", new TimeDuration("10s"));

        directive.execute(row1, context);
        directive.execute(row2, context);
        directive.finalizeDirective(context);

        Row result = context.getFinalRow();
        Assert.assertEquals(30720, result.getValue("totalSize")); // 30 KB
        Assert.assertEquals(15000, result.getValue("totalTime")); // 15 seconds
    }
}
