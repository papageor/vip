package gr.compiler.vip.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_USER_EVENT_CONF")
@Entity(name = "VIP_UserEventConf")
public class UserEventConf {
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

    @Column(name = "TASK_ID")
    private String taskId;

    @NotNull
    @Column(name = "VESSEL_NAME", nullable = false)
    private String vesselName;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    private Boolean active = false;

    @Column(name = "TASK_QUERY", nullable = false)
    @NotNull
    private String description;

    @Column(name = "IF_EVENT_PERSISTS_AT_LEAST")
    private Integer ifEventPersistsAtLeast;

    @Column(name = "IDLE_TIME_AFTER_ALERTING")
    private Integer idleTimeAfterAlerting;

    @Column(name = "OFFSET_", length = 10)
    private String offset;

    @Column(name = "NOTIFICATION_MESSAGE")
    private String notificationMessage;

    @Column(name = "TARGET_GROUP")
    private Integer targetGroup;

    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "userEventConfs")
    private List<RowsEvents> rows;

    public List<RowsEvents> getRows() {
        return rows;
    }

    public void setRows(List<RowsEvents> rows) {
        this.rows = rows;
    }

    public void setTargetGroup(Integer targetGroup) {
        this.targetGroup = targetGroup;
    }

    public Integer getTargetGroup() {
        return targetGroup;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setIfEventPersistsAtLeast(Integer ifEventPersistsAtLeast) {
        this.ifEventPersistsAtLeast = ifEventPersistsAtLeast;
    }

    public Integer getIfEventPersistsAtLeast() {
        return ifEventPersistsAtLeast;
    }

    public void setIdleTimeAfterAlerting(Integer idleTimeAfterAlerting) {
        this.idleTimeAfterAlerting = idleTimeAfterAlerting;
    }

    public Integer getIdleTimeAfterAlerting() {
        return idleTimeAfterAlerting;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
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
}