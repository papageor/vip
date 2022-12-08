package gr.compiler.vip.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.util.Date;
import java.util.UUID;

@JmixEntity(name = "VIP_VesselPoint")
public class VesselPoint {
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private Double me;

    private Double ae;

    private Date time;

    private Double speedOverGround; //00020101

    private Double logSpeed; //00020102

    private Double totalConsumption; //01010305

    private Double windSpeed; //00030103

    private Date entryDate;

    private Double latitude; //00010101

    private Double longitude; //00010102

    private Double heading;  //00020103

    private Double shaftPower; //01010203

    private Double rpm; //01010202

    private Double seaState;

    public void setSeaState(Double seaState) {
        this.seaState = seaState;
    }

    public Double getSeaState() {
        return seaState;
    }

    public Double getAe() {
        return ae;
    }

    public void setAe(Double ae) {
        this.ae = ae;
    }

    public Double getMe() {
        return me;
    }

    public void setMe(Double me) {
        this.me = me;
    }

    public Double getRpm() {
        if (rpm == null) return 0.0;
        return Math.round(rpm *10.0) / 10.0;
    }

    public void setRpm(Double rpm) {
        this.rpm = rpm;
    }

    public Double getShaftPower() {
        if (shaftPower == null) return 0.0;
        return Math.round(shaftPower *10.0) / 10.0;
    }

    public void setShaftPower(Double shaftPower) {
        this.shaftPower = shaftPower;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Date getEntryDate() {
        return time;
    }

    public Double getWindSpeed() {

        return  windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getTotalConsumption() {
        return  totalConsumption;
    }

    public void setTotalConsumption(Double totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public Double getSpeedOverGround() {
        if (speedOverGround == null) return 0.0;
        return Math.round(speedOverGround *10.0) / 10.0;
    }

    public void setSpeedOverGround(Double speedOverGround) {
        this.speedOverGround = speedOverGround;
    }

    public void setLogSpeed(Double logSpeed) {
        this.logSpeed = logSpeed;
    }

    public Double getLogSpeed() {
        if (logSpeed == null) return 0.0;
        return Math.round(logSpeed *10.0) / 10.0;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}