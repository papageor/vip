package gr.compiler.vip.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

import java.util.UUID;

@JmixEntity(name = "VIP_Point")
public class Point {
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private Double x;

    private Double y;

   public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}