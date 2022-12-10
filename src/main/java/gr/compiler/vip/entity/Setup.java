package gr.compiler.vip.entity;

import io.jmix.core.annotation.TenantId;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.multitenancy.core.AcceptsTenant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_SETUP")
@Entity(name = "VIP_Setup")
public class Setup implements AcceptsTenant {
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

    @Column(name = "FORM_BUILDER_URL")
    protected String formBuilderURL;

    @Column(name = "FORM_RUNNER_URL")
    protected String formRunnerURL;

    @Column(name = "FORM_SERVICES_URL")
    protected String formServicesURL;

    @Column(name = "FORM_DEMO_DOCUMENT_ID")
    protected String formDemoDocumentID;

    @Column(name = "FFMPEG_DIRECTORY")
    protected String ffmpegDirectory;

    @Column(name = "FFMPEG_PARAMETERS")
    protected String ffmpegParameters;

    @Column(name = "IMAGES_DIRECTORY")
    protected String imagesDirectory;

    @Column(name = "VIDEOS_REPOSITORY")
    protected String videosRepository;

    @Column(name = "DEMO_MODE")
    private Boolean demoMode;

    @Column(name = "DEMO_WORKING_DATE")
    private LocalDateTime demoWorkingDate;

    @Column(name = "VESSEL_VALUE_LIST")
    private String vesselValueList;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "ORGANIZATION")
    private String organization;

    @Column(name = "ORGANIZATION_ID")
    private String organizationID;

    @Column(name = "OPERATION_DATE_RANGE")
    private Integer operationDateRange;

    @Column(name = "REPORTS_DATE_RANGE")
    private Integer reportsDateRange;

    @Column(name = "CISCO_KEY")
    private String ciscoKey;

    @Column(name = "CISCO_CLIENT_ID")
    private String ciscoClientID;

    @Column(name = "CISCO_SERVICE_URI")
    private String ciscoServiceURI;

    @TenantId
    @Column(name = "SYS_TENANT_ID")
    private String tenant;
    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getTenantId() {
        return tenant;
    }


    public String getCiscoServiceURI() {
        return ciscoServiceURI;
    }

    public void setCiscoServiceURI(String ciscoServiceURI) {
        this.ciscoServiceURI = ciscoServiceURI;
    }

    public String getCiscoClientID() {
        return ciscoClientID;
    }

    public void setCiscoClientID(String ciscoClientID) {
        this.ciscoClientID = ciscoClientID;
    }

    public String getCiscoKey() {
        return ciscoKey;
    }

    public void setCiscoKey(String ciscoKey) {
        this.ciscoKey = ciscoKey;
    }

    public void setOperationDateRange(Integer operationDateRange) {
        this.operationDateRange = operationDateRange;
    }

    public Integer getOperationDateRange() {
        return operationDateRange;
    }

    public void setReportsDateRange(Integer reportsDateRange) {
        this.reportsDateRange = reportsDateRange;
    }

    public Integer getReportsDateRange() {
        return reportsDateRange;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVesselValueList() {
        return vesselValueList;
    }

    public void setVesselValueList(String vesselValueList) {
        this.vesselValueList = vesselValueList;
    }

    public LocalDateTime getDemoWorkingDate() {
        return demoWorkingDate;
    }

    public void setDemoWorkingDate(LocalDateTime demoWorkingDate) {
        this.demoWorkingDate = demoWorkingDate;
    }

    public Boolean getDemoMode() {
        return demoMode;
    }

    public void setDemoMode(Boolean demoMode) {
        this.demoMode = demoMode;
    }


    public String getVideosRepository() {
        return videosRepository;
    }

    public void setVideosRepository(String videosRepository) {
        this.videosRepository = videosRepository;
    }

    public String getImagesDirectory() {
        return imagesDirectory;
    }

    public void setImagesDirectory(String imagesDirectory) {
        this.imagesDirectory = imagesDirectory;
    }

    public String getFfmpegParameters() {
        return ffmpegParameters;
    }

    public void setFfmpegParameters(String ffmpegParameters) {
        this.ffmpegParameters = ffmpegParameters;
    }

    public String getFfmpegDirectory() {
        return ffmpegDirectory;
    }

    public void setFfmpegDirectory(String ffmpegDirectory) {
        this.ffmpegDirectory = ffmpegDirectory;
    }

    public String getFormDemoDocumentID() {
        return formDemoDocumentID;
    }

    public void setFormDemoDocumentID(String formDemoDocumentID) {
        this.formDemoDocumentID = formDemoDocumentID;
    }

    public String getFormServicesURL() {
        return formServicesURL;
    }

    public void setFormServicesURL(String formServicesURL) {
        this.formServicesURL = formServicesURL;
    }

    public String getFormRunnerURL() {
        return formRunnerURL;
    }

    public void setFormRunnerURL(String formRunnerURL) {
        this.formRunnerURL = formRunnerURL;
    }

    public String getFormBuilderURL() {
        return formBuilderURL;
    }

    public void setFormBuilderURL(String formBuilderURL) {
        this.formBuilderURL = formBuilderURL;
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
        setDemoMode(false);
        setOperationDateRange(7);
        setReportsDateRange(30);
    }
}