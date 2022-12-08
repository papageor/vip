package gr.compiler.vip.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_OPTIONS_LINES", indexes = {
        @Index(name = "IDX_VIPOPTIONSLI_USERSEVENTOP", columnList = "USERS_EVENT_OPTIONSES_ID")
})
@Entity(name = "VIP_OptionsLines")
public class OptionsLines {
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

    @Column(name = "CONDITION_")
    private String condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID")
    private CommonTag tag;

    @Column(name = "VALUE_")
    private String value;

    @Column(name = "SIDE")
    private Boolean side;

    @Column(name = "CONDITION_LEFT")
    private Boolean conditionLeft;

    @Column(name = "CONDITION_RIGHT")
    private Boolean conditionRight;

    @JoinColumn(name = "USERS_EVENT_OPTIONSES_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private UsersEventOptions usersEventOptionses;

    public UsersEventOptions getUsersEventOptionses() {
        return usersEventOptionses;
    }

    public void setUsersEventOptionses(UsersEventOptions usersEventOptionses) {
        this.usersEventOptionses = usersEventOptionses;
    }


    public CommonTag getTag() {
        return tag;
    }

    public void setTag(CommonTag tag) {
        this.tag = tag;
    }

    public void setConditionRight(Boolean conditionRight) {
        this.conditionRight = conditionRight;
    }

    public Boolean getConditionRight() {
        return conditionRight;
    }

    public Boolean getConditionLeft() {
        return conditionLeft;
    }

    public void setConditionLeft(Boolean conditionLeft) {
        this.conditionLeft = conditionLeft;
    }


    public Boolean getSide() {
        return side;
    }

    public void setSide(Boolean side) {
        this.side = side;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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