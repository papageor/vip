package gr.compiler.vip.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.Store;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Store(name = "secondary")
@Table(name = "IFORMS_SETUP")
@Entity(name = "VIP_IformsSetup")
public class IformsSetup {
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

    @Column(name = "APPLICATION_TYPE", length = 50)
    private String applicationType;

    @Column(name = "EMAIL_RECIPIENT")
    private String emailRecipient;

    @Column(name = "ENABLE_UTC")
    private Boolean enableUtc;

    @Column(name = "FORM_BUILDER_URL")
    private String formBuilderUrl;

    @Column(name = "FORM_RESOURCES_PATH")
    private String formResourcesPath;

    @Column(name = "FORM_RUNNER_URL")
    private String formRunnerUrl;

    @Column(name = "FORM_SERVICES_URL")
    private String formServicesUrl;

    @Column(name = "LABEL_FO")
    private String labelFo;

    @Column(name = "LABEL_GO")
    private String labelGo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean isDeleted() {
        return deletedDate != null;
    }

    public String getLabelGo() {
        return labelGo;
    }

    public void setLabelGo(String labelGo) {
        this.labelGo = labelGo;
    }

    public String getLabelFo() {
        return labelFo;
    }

    public void setLabelFo(String labelFo) {
        this.labelFo = labelFo;
    }

    public String getFormServicesUrl() {
        return formServicesUrl;
    }

    public void setFormServicesUrl(String formServicesUrl) {
        this.formServicesUrl = formServicesUrl;
    }

    public String getFormRunnerUrl() {
        return formRunnerUrl;
    }

    public void setFormRunnerUrl(String formRunnerUrl) {
        this.formRunnerUrl = formRunnerUrl;
    }

    public String getFormResourcesPath() {
        return formResourcesPath;
    }

    public void setFormResourcesPath(String formResourcesPath) {
        this.formResourcesPath = formResourcesPath;
    }

    public String getFormBuilderUrl() {
        return formBuilderUrl;
    }

    public void setFormBuilderUrl(String formBuilderUrl) {
        this.formBuilderUrl = formBuilderUrl;
    }

    public Boolean getEnableUtc() {
        return enableUtc;
    }

    public void setEnableUtc(Boolean enableUtc) {
        this.enableUtc = enableUtc;
    }

    public String getEmailRecipient() {
        return emailRecipient;
    }

    public void setEmailRecipient(String emailRecipient) {
        this.emailRecipient = emailRecipient;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
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


}