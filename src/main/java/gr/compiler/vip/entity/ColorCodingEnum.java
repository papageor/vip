package gr.compiler.vip.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum ColorCodingEnum implements EnumClass<Integer> {

    BLUE(10),
    CYAN(20),
    GREEN(30),
    GREENDARK(40),
    RED(50),
    YELLOW(60);

    private Integer id;

    ColorCodingEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ColorCodingEnum fromId(Integer id) {
        for (ColorCodingEnum at : ColorCodingEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}