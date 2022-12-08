package qdata;

import com.influxdb.utils.Arguments;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class QRecord implements Serializable {

    /**
     * The Index of the table that the record belongs.
     */
    private final Integer table;

    /**
     * The record's values.
     */
    private LinkedHashMap<String, Object> values = new LinkedHashMap<>();

    public QRecord(@Nonnull final Integer table) {

        Arguments.checkNotNull(table, "Table index");

        this.table = table;
    }

    @Nonnull
    public Map<String, Object> getValues() {
        return values;
    }

    @Nullable
    public Object getValueByIndex(final int index) {

        //noinspection unchecked
        return values.values().toArray()[index];
    }

    @Nullable
    public Object getValueByKey(@Nonnull final String key) {

        Arguments.checkNonEmpty(key, "key");

        return values.get(key);
    }

    public Boolean hasFieldWithValue(String fieldName, String value) {
        Boolean result = false;
        if (values.containsKey(fieldName)) {
            String field = values.get(fieldName).toString();
            if (!field.isEmpty()) {
                if (field.equals(value)) {
                    result = true;
                }

            }
        }
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QRecord.class.getSimpleName() + "[", "]")
                .add("table=" + table)
                .add("values=" + values.size())
                .toString();
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final QRecord that = (QRecord) o;
        return Objects.equals(table, that.table)
                && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, values);
    }
}

