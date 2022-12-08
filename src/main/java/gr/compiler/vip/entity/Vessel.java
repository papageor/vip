package gr.compiler.vip.entity;

import io.jmix.core.FileRef;
import io.jmix.core.annotation.TenantId;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.NumberFormat;
import io.jmix.multitenancy.core.AcceptsTenant;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_VESSEL", indexes = {
        @Index(name = "IDX_VIP_VESSEL_TYPE", columnList = "TYPE_ID")
})
@Entity(name = "VIP_Vessel")
public class Vessel implements AcceptsTenant {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private Integer id;

    @JmixGeneratedValue
    @Column(name = "UUID")
    private UUID uuid;

    @Column(name = "MMSI")
    private String mmsi;

    @Column(name = "GT")
    private String gt;

    @Column(name = "PROPELLER_PITCH", precision = 19, scale = 2)
    private BigDecimal propellerPitch;

    @InstanceName
    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @Column(name = "LONGITUDE", precision = 19, scale = 3)
    @NumberFormat(pattern = "###.000")
    protected BigDecimal longitude;

    @Column(name = "LATITUDE", precision = 19, scale = 3)
    @NumberFormat(pattern = "###.000")
    protected BigDecimal latitude;

    @NumberFormat(pattern = "###.000")
    @Column(name = "HEADING", precision = 19, scale = 3)
    private BigDecimal heading;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected VesselType type;

    @Column(name = "OWNER")
    protected String owner;

    @Column(name = "CATEGORY")
    protected String category;

    @Column(name = "BUILT")
    protected String built;

    @Column(name = "YEAR_BUILT")
    protected String yearBuilt;

    @Column(name = "DWT")
    protected Double dwt;

    @Column(name = "LBD")
    protected String lbd;

    @Column(name = "FULL_LOAD_DRAFT")
    protected Double fullLoadDraft;

    @Column(name = "FLAG")
    protected String flag;

    @Column(name = "CLASSIFICATION")
    protected String classification;

    @Column(name = "IMO")
    protected String imo;

    @Column(name = "IMAGE_FILE", length = 1024)
    private FileRef imageFile;

    @Column(name = "FOC_LIMIT")
    protected Double focLimit;

    @Column(name = "SPEED_LIMIT")
    protected Double speedLimit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ETA")
    protected Date eta;

    @Column(name = "DESTINATION_PORT")
    protected String destinationPort;

    @Column(name = "DEMO_FOC_DEVIATION")
    protected Double demoFOCDeviation;

    @Column(name = "DEMO_SPEED_DEVIATION")
    protected Double demoSpeedDeviation;


    @Column(name = "MESSAGE_ON_SELECTION")
    private String messageOnSelection;

    @TenantId
    @Column(name = "SYS_TENANT_ID")
    private String tenant;

    @Column(name = "DISABLED")
    private Boolean disabled;

    @Column(name = "REFERENCE_ID")
    private Integer referenceId;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "ORGANIZATION")
    private String organization;

    @Column(name = "MAIN_ENGINE_MODEL_NO")
    private String mainEngineModelNo;

    @Column(name = "MAIN_ENGINE_RATED_POWER")
    private Integer mainEngineRatedPower;

    @Column(name = "MAIN_ENGINE_RATED_SPEED", precision = 19, scale = 2)
    private BigDecimal mainEngineRatedSpeed;

    @Column(name = "MAIN_ENGINE_CYLINDER_NUM")
    private Integer mainEngineCylinderNum;

    @Column(name = "WEBEX_ROOM")
    private String webexRoom;

    @Column(name = "TURBO_CHARGER_NUM")
    private Integer turboChargerNum;

    @Column(name = "CSGROUP_GUID")
    private String csgroupGuid;

    @Column(name = "TCSPEED_MARGIN")
    private Double tcspeedMargin;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vessel")
    private MainModules mainModules;

    @Column(name = "FUEL_TYPE1")
    private String fuelType1;

    @Column(name = "FUEL_TYPE2")
    private String fuelType2;

    @Column(name = "FUEL_TYPE3")
    private String fuelType3;

    @Column(name = "FUEL_TYPE4")
    private String fuelType4;

    @Column(name = "MAIN_ENGINE_COUNT")
    private Integer mainEngineCount;

    @Column(name = "AUX_ENGINE_COUNT")
    private Integer auxEngineCount;

    @Column(name = "AUX_ENGINE_CYLINDER_COUNT")
    private Integer auxEngineCylinderCount;

    @Column(name = "AUX_ENGINE_TURBO_CHARGER_COUNT")
    private Integer auxEngineTurboChargerCount;

    public Integer getAuxEngineTurboChargerCount() {
        return auxEngineTurboChargerCount;
    }

    public void setAuxEngineTurboChargerCount(Integer auxEngineTurboChargerCount) {
        this.auxEngineTurboChargerCount = auxEngineTurboChargerCount;
    }

    public Integer getAuxEngineCylinderCount() {
        return auxEngineCylinderCount;
    }

    public void setAuxEngineCylinderCount(Integer auxEngineCylinderCount) {
        this.auxEngineCylinderCount = auxEngineCylinderCount;
    }

    public Integer getAuxEngineCount() {
        return auxEngineCount;
    }

    public void setAuxEngineCount(Integer auxEngineCount) {
        this.auxEngineCount = auxEngineCount;
    }

    public Integer getMainEngineCount() {
        return mainEngineCount;
    }

    public void setMainEngineCount(Integer mainEngineCount) {
        this.mainEngineCount = mainEngineCount;
    }

    public FuelTypeEnum getFuelType4() {
        return fuelType4 == null ? null : FuelTypeEnum.fromId(fuelType4);
    }

    public void setFuelType4(FuelTypeEnum fuelType4) {
        this.fuelType4 = fuelType4 == null ? null : fuelType4.getId();
    }

    public FuelTypeEnum getFuelType3() {
        return fuelType3 == null ? null : FuelTypeEnum.fromId(fuelType3);
    }

    public void setFuelType3(FuelTypeEnum fuelType3) {
        this.fuelType3 = fuelType3 == null ? null : fuelType3.getId();
    }

    public FuelTypeEnum getFuelType2() {
        return fuelType2 == null ? null : FuelTypeEnum.fromId(fuelType2);
    }

    public void setFuelType2(FuelTypeEnum fuelType2) {
        this.fuelType2 = fuelType2 == null ? null : fuelType2.getId();
    }

    public FuelTypeEnum getFuelType1() {
        return fuelType1 == null ? null : FuelTypeEnum.fromId(fuelType1);
    }

    public void setFuelType1(FuelTypeEnum fuelType1) {
        this.fuelType1 = fuelType1 == null ? null : fuelType1.getId();
    }

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

    public FileRef getImageFile() {
        return imageFile;
    }

    public void setImageFile(FileRef imageFile) {
        this.imageFile = imageFile;
    }

    public MainModules getMainModules() {
        return mainModules;
    }

    public void setMainModules(MainModules mainModules) {
        this.mainModules = mainModules;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public BigDecimal getHeading() {
        return heading;
    }

    public void setHeading(BigDecimal heading) {
        this.heading = heading;
    }

    public BigDecimal getPropellerPitch() {
        return propellerPitch;
    }

    public void setPropellerPitch(BigDecimal propellerPitch) {
        this.propellerPitch = propellerPitch;
    }

    public void setTcspeedMargin(Double tcspeedMargin) {
        this.tcspeedMargin = tcspeedMargin;
    }

    public Double getTcspeedMargin() {
        return tcspeedMargin;
    }

    public String getCsgroupGuid() {
        return csgroupGuid;
    }

    public void setCsgroupGuid(String csgroupGuid) {
        this.csgroupGuid = csgroupGuid;
    }

    public Integer getTurboChargerNum() {
        return turboChargerNum;
    }

    public void setTurboChargerNum(Integer turboChargerNum) {
        this.turboChargerNum = turboChargerNum;
    }

    public String getWebexRoom() {
        return webexRoom;
    }

    public void setWebexRoom(String webexRoom) {
        this.webexRoom = webexRoom;
    }

    public Integer getMainEngineCylinderNum() {
        return mainEngineCylinderNum;
    }

    public void setMainEngineCylinderNum(Integer mainEngineCylinderNum) {
        this.mainEngineCylinderNum = mainEngineCylinderNum;
    }

    public BigDecimal getMainEngineRatedSpeed() {
        return mainEngineRatedSpeed;
    }

    public void setMainEngineRatedSpeed(BigDecimal mainEngineRatedSpeed) {
        this.mainEngineRatedSpeed = mainEngineRatedSpeed;
    }

    public Integer getMainEngineRatedPower() {
        return mainEngineRatedPower;
    }

    public void setMainEngineRatedPower(Integer mainEngineRatedPower) {
        this.mainEngineRatedPower = mainEngineRatedPower;
    }

    public String getMainEngineModelNo() {
        return mainEngineModelNo;
    }

    public void setMainEngineModelNo(String mainEngineModelNo) {
        this.mainEngineModelNo = mainEngineModelNo;
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

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }


    public String getMessageOnSelection() {
        return messageOnSelection;
    }

    public void setMessageOnSelection(String messageOnSelection) {
        this.messageOnSelection = messageOnSelection;
    }

    public Double getDemoSpeedDeviation() {
        return demoSpeedDeviation;
    }

    public void setDemoSpeedDeviation(Double demoSpeedDeviation) {
        this.demoSpeedDeviation = demoSpeedDeviation;
    }

    public Double getDemoFOCDeviation() {
        return demoFOCDeviation;
    }

    public void setDemoFOCDeviation(Double demoFOCDeviation) {
        this.demoFOCDeviation = demoFOCDeviation;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public Double getFocLimit() {
        return focLimit;
    }

    public void setFocLimit(Double focLimit) {
        this.focLimit = focLimit;
    }

    public String getImo() {
        return imo;
    }

    public void setImo(String imo) {
        this.imo = imo;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Double getFullLoadDraft() {
        return fullLoadDraft;
    }

    public void setFullLoadDraft(Double fullLoadDraft) {
        this.fullLoadDraft = fullLoadDraft;
    }

    public String getLbd() {
        return lbd;
    }

    public void setLbd(String lbd) {
        this.lbd = lbd;
    }

    public Double getDwt() {
        return dwt;
    }

    public void setDwt(Double dwt) {
        this.dwt = dwt;
    }

    public String getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(String yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public VesselType getType() {
        return type;
    }

    public void setType(VesselType type) {
        this.type = type;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

     public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PostConstruct
    public void postConstruct() {
        setDisabled(false);
        setMainEngineCylinderNum(0);
        setMainEngineRatedPower(0);
        setMainEngineRatedSpeed(BigDecimal.ZERO);
        setTurboChargerNum(1);
        setTcspeedMargin(0.5);
        setPropellerPitch(BigDecimal.ZERO);
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