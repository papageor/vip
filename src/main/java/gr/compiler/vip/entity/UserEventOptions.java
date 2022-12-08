package gr.compiler.vip.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "VIP_USER_EVENT_OPTIONS")
@Entity(name = "VIP_UserEventOptions")
public class UserEventOptions {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    private Integer condition;

    private List<String> line1;

    private List<String> line2;

    private List<String> line3;

    private List<String> line4;

    private List<String> line5;

    private List<String> line6;

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public List<String> getLine1() {
        return line1;
    }

    public void setLine1(List<String> line1) {
        this.line1 = line1;
    }

    public List<String> getLine2() {
        return line2;
    }

    public void setLine2(List<String> line2) {
        this.line2 = line2;
    }

    public List<String> getLine3() {
        return line3;
    }

    public void setLine3(List<String> line3) {
        this.line3 = line3;
    }

    public List<String> getLine4() {
        return line4;
    }

    public void setLine4(List<String> line4) {
        this.line4 = line4;
    }

    public List<String> getLine5() {
        return line5;
    }

    public void setLine5(List<String> line5) {
        this.line5 = line5;
    }

    public List<String> getLine6() {
        return line6;
    }

    public void setLine6(List<String> line6) {
        this.line6 = line6;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}