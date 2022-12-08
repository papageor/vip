package gr.compiler.vip.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum QueryParameterType implements EnumClass<String> {

    TEXT("TEXT"),
    NUMBER("NUMBER"),
    DATE_TIME("DATE_TIME"),
    LOOKUP_FIELD("LOOKUP_FIELD");

    private String id;

    QueryParameterType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static QueryParameterType fromId(String id) {
        for (QueryParameterType at : QueryParameterType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}