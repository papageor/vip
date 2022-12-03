package gr.compiler.vip.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_VESSEL_TYPE")
@Entity(name = "VIP_VesselType")
public class VesselType {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private Integer id;

    @JmixGeneratedValue
    @Column(name = "UUID")
    private UUID uuid;

    @InstanceName
    @Column(name = "NAME", nullable = false)
    @NotNull
    private String name;

    @Column(name = "MARKER_COLOR")
    private Integer markerColor;

    public ColorCodingEnum getMarkerColor() {
        return markerColor == null ? null : ColorCodingEnum.fromId(markerColor);
    }

    public void setMarkerColor(ColorCodingEnum markerColor) {
        this.markerColor = markerColor == null ? null : markerColor.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}