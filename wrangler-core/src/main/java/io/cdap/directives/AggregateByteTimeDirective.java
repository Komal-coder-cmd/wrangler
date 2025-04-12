package io.cdap.wrangler.core.directive;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.TokenDefinition;
import io.cdap.wrangler.api.parser.TokenGroup;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.executor.ExecutorContext;

import java.util.List;

/**
 * A directive that aggregates byte sizes and time durations for each row.
 */
public class AggregateByteTimeDirective implements Directive {
    private String byteSizeColumn;
    private String timeDurationColumn;
    private String totalSizeColumn;
    private String totalTimeColumn;
    private String outputSizeUnit = "MB";
    private String outputTimeUnit = "seconds";

    private long totalBytes = 0;
    private long totalTimeNano = 0;
    private int rowCount = 0;

    @Override
    public UsageDefinition define() {
        return UsageDefinition.builder("aggregate-bytes-time")
                .addToken("byteSizeColumn", TokenType.COLUMN_NAME)
                .addToken("timeDurationColumn", TokenType.COLUMN_NAME)
                .addToken("totalSizeColumn", TokenType.COLUMN_NAME)
                .addToken("totalTimeColumn", TokenType.COLUMN_NAME)
                .optionalToken("outputSizeUnit", TokenType.TEXT)
                .optionalToken("outputTimeUnit", TokenType.TEXT)
                .build();
    }

    @Override
    public void initialize(List<TokenGroup> args) {
        this.byteSizeColumn = ((ColumnName) args.get(0).getToken()).value();
        this.timeDurationColumn = ((ColumnName) args.get(1).getToken()).value();
        this.totalSizeColumn = ((ColumnName) args.get(2).getToken()).value();
        this.totalTimeColumn = ((ColumnName) args.get(3).getToken()).value();

        if (args.size() > 4) this.outputSizeUnit = args.get(4).getToken().value().toString();
        if (args.size() > 5) this.outputTimeUnit = args.get(5).getToken().value().toString();
    }

    @Override
    public void execute(Row row, ExecutorContext context) {
        Object byteSizeVal = row.getValue(byteSizeColumn);
        Object timeDurationVal = row.getValue(timeDurationColumn);

        if (byteSizeVal instanceof ByteSize && timeDurationVal instanceof TimeDuration) {
            totalBytes += ((ByteSize) byteSizeVal).getBytes();
            totalTimeNano += ((TimeDuration) timeDurationVal).getMilliseconds() * 1_000_000;
            rowCount++;
        }
    }

    @Override
    public void finalizeDirective(ExecutorContext context) {
        long finalSize = convertBytes(totalBytes);
        long finalTime = convertTime(totalTimeNano);

        context.write(new Row().add(totalSizeColumn, finalSize).add(totalTimeColumn, finalTime));
    }

    private long convertBytes(long bytes) {
        switch (outputSizeUnit) {
            case "GB": return bytes / (1024 * 1024 * 1024);
            case "KB": return bytes / 1024;
            case "B": return bytes;
            default: return bytes / (1024 * 1024); // Default to MB
        }
    }

    private long convertTime(long nanos) {
        switch (outputTimeUnit) {
            case "minutes": return nanos / (1_000_000_000 * 60);
            case "hours": return nanos / (1_000_000_000 * 3600);
            case "milliseconds": return nanos / 1_000_000;
            default: return nanos / 1_000_000_000; // Default to seconds
        }
    }
}
