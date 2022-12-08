package gr.compiler.vip.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import org.apache.commons.math3.util.Precision;

import javax.annotation.PostConstruct;
import java.util.UUID;

@JmixEntity(name = "VIP_Coordinates")
public class Coordinates {
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private Double latitude;

    private Double longitude;

    private Double heading;

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getX() {
        return longitude;
    }

    public Double getY() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Coordinates() {
    }

    public Coordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates(Double latitude, Double longitude, Double heading) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
    }

    public Point getLocation() {
        if (getLatitude() == null || getLongitude() == null) {
            return null;
        }
        Point point = new Point(getLongitude(), getLatitude());
        return point;
    }

    public void setLocation(Point point) {
        Point prevValue = getLocation();
        if (point == null) {
            setLatitude(null);
            setLongitude(null);
        } else {
            setLatitude(point.getY());
            setLongitude(point.getX());
        }
//        propertyChanged("location", prevValue, point);
    }

    @PostConstruct
    public void postConstruct() {
        setLatitude(0.0);
        setLongitude(0.0);
        setHeading(0.0);
    }

    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        Coordinates c = (Coordinates) o;


        return (Precision.equals(this.getLatitude(), c.getLatitude()) && Precision.equals(this.getLongitude(), c.getLongitude()));
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + getLatitude().intValue() + getLongitude().intValue();
        return result;
    }
}