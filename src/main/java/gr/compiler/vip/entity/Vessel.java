package gr.compiler.vip.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_VESSEL", indexes = {
        @Index(name = "IDX_VIP_VESSEL_TYPE", columnList = "TYPE_ID")
})
@Entity(name = "VIP_Vessel")
public class Vessel {
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

    @JoinColumn(name = "TYPE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private VesselType type;

    public VesselType getType() {
        return type;
    }

    public void setType(VesselType type) {
        this.type = type;
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