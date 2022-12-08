package gr.compiler.vip.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_COMMON_TAG")
@Entity(name = "VIP_CommonTag")
public class CommonTag {
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

    @NotNull
    @Column(name = "TAG", nullable = false)
    private String tag;

    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "TOTALIZER")
    private String totalizer;

    @Column(name = "AVERAGED")
    private String averaged;

    @Column(name = "GID")
    private String gid;

    @Column(name = "NOTES")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CommonTag parent;

    @Column(name = "I0")
    private Integer i0;

    @Column(name = "I1")
    private Integer i1;

    @Column(name = "I2")
    private Integer i2;

    @Column(name = "I3")
    private Integer i3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_ID")
    private UnitOfMeasure unit;

    @Column(name = "CSV_ID", unique = true)
    private Integer csvID;

    @Column(name = "LOWER_VALUE")
    private Double lowerValue;

    @Column(name = "UPPER_VALUE")
    private Double upperValue;

    @Column(name = "DECIMALS")
    private Integer decimals;

    @Column(name = "IS_EXTERNAL")
    private Boolean isExternal;

    @Column(name = "IS_CALCULATED")
    private Boolean isCalculated;

    @Column(name = "CALCULATION_TAGS")
    private String calculationTags;

    @Column(name = "CALCULATION_EXPRESSION")
    @Lob
    private String calculationExpression;

    @Column(name = "COMMON_TAG_ID")
    private Integer commonTagId;

    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "requiredCommonTags")
    private List<RequiredCommonTags> requiredCommonTags;

    public List<RequiredCommonTags> getRequiredCommonTags() {
        return requiredCommonTags;
    }

    public void setRequiredCommonTags(List<RequiredCommonTags> requiredCommonTags) {
        this.requiredCommonTags = requiredCommonTags;
    }

    public Integer getCommonTagId() {
        return commonTagId;
    }

    public void setCommonTagId(Integer commonTagId) {
        this.commonTagId = commonTagId;
    }

    public String getCalculationExpression() {
        return calculationExpression;
    }

    public void setCalculationExpression(String calculationExpression) {
        this.calculationExpression = calculationExpression;
    }

    public String getCalculationTags() {
        return calculationTags;
    }

    public void setCalculationTags(String calculationTags) {
        this.calculationTags = calculationTags;
    }

    public Boolean getIsCalculated() {
        return isCalculated;
    }

    public void setIsCalculated(Boolean isCalculated) {
        this.isCalculated = isCalculated;
    }

    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    public Integer getCsvID() {
        return csvID;
    }

    public void setCsvID(Integer csvID) {
        this.csvID = csvID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAveraged() {
        return averaged;
    }

    public void setAveraged(String averaged) {
        this.averaged = averaged;
    }

    public String getTotalizer() {
        return totalizer;
    }

    public void setTotalizer(String totalizer) {
        this.totalizer = totalizer;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Double getUpperValue() {
        return upperValue;
    }

    public void setUpperValue(Double upperValue) {
        this.upperValue = upperValue;
    }

    public Double getLowerValue() {
        return lowerValue;
    }

    public void setLowerValue(Double lowerValue) {
        this.lowerValue = lowerValue;
    }

    public UnitOfMeasure getUnit() {
        return unit;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public CommonTag getParent() {
        return parent;
    }

    public void setParent(CommonTag parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getI3() {
        return i3;
    }

    public void setI3(Integer i3) {
        this.i3 = i3;
    }

    public Integer getI2() {
        return i2;
    }

    public void setI2(Integer i2) {
        this.i2 = i2;
    }

    public Integer getI1() {
        return i1;
    }

    public void setI1(Integer i1) {
        this.i1 = i1;
    }

    public Integer getI0() {
        return i0;
    }

    public void setI0(Integer i0) {
        this.i0 = i0;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @PostConstruct
    public void postConstruct() {
        setIsExternal(false);
        setDecimals(0);
        setI0(0);
        setI1(0);
        setI2(0);
        setI3(0);
        setLowerValue(0.0);
        setUpperValue(0.0);
        setTotalizer("f");
        setIsCalculated(false);
        setCommonTagId(0);
    }

    @DependsOnProperties({"tag", "name"})
    @InstanceName
    public String getInstanceName() {
        return String.format("%s %s", tag, name);
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