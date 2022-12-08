package gr.compiler.vip.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum DatabaseType implements EnumClass<String> {

    POSTGRESQL("POSTGRESQL"),
    MSSQL2008("MSSQL2008"),
    MSSQL2012PLUS("MSSQL2012PLUS"),
    MYSQL("MYSQL"),
    MARIADB("MARIADB"),
    INFLUXDB("INFLUXDB");

    private String id;

    DatabaseType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static DatabaseType fromId(String id) {
        for (DatabaseType at : DatabaseType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}