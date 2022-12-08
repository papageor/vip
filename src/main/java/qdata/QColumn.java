package qdata;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class QColumn implements Serializable {
    /**
     * Column index in record.
     */
    private int index;

    /**
     * The label of column (e.g., "_start", "_stop", "_time").
     */
    private String label;

    /**
     * The data type of column (e.g., "string", "long", "dateTime:RFC3339").
     */
    private String dataType;

    /**
     * Default value to be used for rows whose string value is the empty string.
     */
    private String defaultValue;

    @Nonnull
    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }


    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QColumn.class.getSimpleName() + "[", "]")
                .add("index=" + index)
                .add("label='" + label + "'")
                .add("dataType='" + dataType + "'")
                .add("defaultValue='" + defaultValue + "'")
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
        final QColumn that = (QColumn) o;
        return index == that.index
                && Objects.equals(label, that.label)
                && Objects.equals(dataType, that.dataType)
                && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, label, dataType, defaultValue);
    }
}