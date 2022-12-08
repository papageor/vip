package gr.compiler.vip.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_MAIN_MODULES")
@Entity(name = "VIP_MainModules")
public class MainModules {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    @Column(name = "OPERATION")
    private Boolean operation;

    @Column(name = "PERFORMANCE")
    private Boolean performance;

    @Column(name = "COMMUNICATION")
    private Boolean communication;

    @Column(name = "SURVEILLANCE")
    private Boolean surveillance;

    @Column(name = "CYBERSECURITY")
    private Boolean cybersecurity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VESSEL_ID")
    private Vessel vessel;

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public Boolean getCybersecurity() {
        return cybersecurity;
    }

    public void setCybersecurity(Boolean cybersecurity) {
        this.cybersecurity = cybersecurity;
    }

    public Boolean getSurveillance() {
        return surveillance;
    }

    public void setSurveillance(Boolean surveillance) {
        this.surveillance = surveillance;
    }

    public Boolean getCommunication() {
        return communication;
    }

    public void setCommunication(Boolean communication) {
        this.communication = communication;
    }

    public Boolean getPerformance() {
        return performance;
    }

    public void setPerformance(Boolean performance) {
        this.performance = performance;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @PostConstruct
    public void postConstruct() {
        this.communication = true;
        this.cybersecurity = true;
        this.operation = true;
        this.performance = true;
        this.surveillance = true;
    }
}