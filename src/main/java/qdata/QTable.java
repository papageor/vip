package qdata;

import com.influxdb.utils.Arguments;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class QTable implements Serializable {
    /**
     * Table column's labels and types.
     */
    private List<QColumn> columns = new ArrayList<>();

    /**
     * Table records.
     */
    private List<QRecord> records = new ArrayList<>();

    private HashSet<QField> fields = new HashSet<>();

    @Nonnull
    public List<QColumn> getColumns() {
        return columns;
    }

    @Nonnull
    public List<QRecord> getRecords() {
        return records;
    }

    @Nonnull
    public HashSet<QField> getFields() {
        return fields;
    }

    public void addRecord(Map<String, Object> values) {
        int tableIndex = 0;
        QRecord record = new QRecord(tableIndex);

        for (Map.Entry<String, Object> fieldValue : values.entrySet()) {
            Optional<QColumn> column = getColumns().stream().filter(p -> p.getLabel().equals(fieldValue.getKey())).findFirst();
            if (column.isPresent()) {
                record.getValues().put(fieldValue.getKey(), toValue(fieldValue.getValue().toString(), column.get()));
            }
        }
        getRecords().add(record);

        return;
    }

    @Nullable
    private Object toValue(@Nullable final String strValue, final @Nonnull QColumn column) {

        Arguments.checkNotNull(column, "column");

        // Default value
        if (strValue == null || strValue.isEmpty()) {
            String defaultValue = column.getDefaultValue();
            if (defaultValue == null || defaultValue.isEmpty()) {
                return null;
            }

            return toValue(defaultValue, column);
        }

        String dataType = column.getDataType();
        switch (dataType) {
            case "boolean":
                return Boolean.valueOf(strValue);
            case "unsignedLong":
                return Long.parseUnsignedLong(strValue);
            case "long":
                return Long.parseLong(strValue);
            case "double":
                return Double.parseDouble(strValue);
            case "base64Binary":
                return Base64.getDecoder().decode(strValue);
            case "dateTime:RFC3339":
            case "dateTime:RFC3339Nano":
                LocalDateTime datetime = LocalDateTime.ofInstant(Instant.parse(strValue), ZoneOffset.UTC);
                String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(datetime);
                return formatted;
            case "duration":
                return Duration.ofNanos(Long.parseUnsignedLong(strValue));
            default:
            case "string":
                return strValue;
        }
    }

    public TreeMap<Object, Object> getTimeSeriesInflux(String primaryKey) {
        TreeMap<Object, Object> timeSeries = new TreeMap<>();

        List<QRecord> tx = records.parallelStream()
                .collect(Collectors.toList());

        tx.forEach(p -> timeSeries.put(p.getValueByKey(primaryKey), p.getValueByKey("_value")));

        return timeSeries;
    }
}

