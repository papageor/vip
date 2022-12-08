package qdata;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class QField implements Serializable {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QColumn.class.getSimpleName() + "[", "]")
                .add("label='" + label + "'")
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
        final QField that = (QField) o;
        return Objects.equals(label, that.label);

    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}