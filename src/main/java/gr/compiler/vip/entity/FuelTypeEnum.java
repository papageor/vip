package gr.compiler.vip.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum FuelTypeEnum implements EnumClass<String> {

    HFO("HFO"),
    MDO("MDO"),
    LNG("LNG"),
    METHANOL("METHANOL"),
    LSHFO_1("LSHFO_1");

    private String id;

    FuelTypeEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static FuelTypeEnum fromId(String id) {
        for (FuelTypeEnum at : FuelTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}