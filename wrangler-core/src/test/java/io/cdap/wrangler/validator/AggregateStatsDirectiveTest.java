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
import java.util.List;

public class AggregateStatsDirectiveTest {
    private AggregateByteTimeDirective directive;
    private ExecutorContext context;

    String[] recipe = {
            "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
        };
    @Before
    public void setUp() {
        directive = new AggregateByteTimeDirective();
        context = new ExecutorContext();

        // Initialize directive with column names
        directive.initialize(Arrays.asList(
                new TokenGroup(new ColumnName("data_transfer_size"), TokenType.COLUMN_NAME),
                new TokenGroup(new ColumnName("response_time"), TokenType.COLUMN_NAME),
                new TokenGroup(new ColumnName("total_size_mb"), TokenType.COLUMN_NAME),
                new TokenGroup(new ColumnName("total_time_sec"), TokenType.COLUMN_NAME)
        ));
    }

    @Test
    public void testAggregateStatsDirective() {
        List<Row> inputData = Arrays.asList(
            new Row().add("data_transfer_size", new ByteSize("10MB"))
                     .add("response_time", new TimeDuration("5s")),
            new Row().add("data_transfer_size", new ByteSize("20MB"))
                     .add("response_time", new TimeDuration("10s")),
            new Row().add("data_transfer_size", new ByteSize("5MB"))
                     .add("response_time", new TimeDuration("3s"))
        );

        for (Row row : inputData) {
            directive.execute(row, context);
        }

        directive.finalizeDirective(context);
        Row result = context.getFinalRow();
        List<Row> outputData = TestingRig.execute(recipe, inputData);

        // Verify results
        Assert.assertEquals(1, outputData.size());
        Row resultRow = outputData.get(0);
        Assert.assertEquals(35, result.getValue("total_size_mb"));  // 35 MB total
        Assert.assertEquals(18, result.getValue("total_time_sec"));  // 18 seconds total
    }
}
