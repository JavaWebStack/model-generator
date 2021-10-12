package org.javawebstack.modelgenerator.spec;

import org.javawebstack.orm.SQLType;

import java.util.Locale;

public class ColumnSpec {

    private final String name;
    private final SQLType type;
    private final String size;
    private boolean unsigned;
    private boolean nullable;
    private boolean primary;
    private boolean autoIncrement;

    public ColumnSpec(String name, SQLType type, String size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public ColumnSpec(String name, String type) {
        this.name = name;
        if(type.toUpperCase(Locale.ROOT).endsWith(" UNSIGNED")) {
            type = type.substring(0, type.length() - 9);
            this.unsigned = true;
        }
        int index = type.indexOf("(");
        if(index == -1) {
            this.type = SQLType.valueOf(type.toUpperCase(Locale.ROOT));
            this.size = "";
        } else {
            this.type = SQLType.valueOf(type.substring(0, index).toUpperCase(Locale.ROOT));
            this.size = type.substring(index + 1, type.length()-1);
        }
    }

    public String getName() {
        return name;
    }

    public SQLType getType() {
        return type;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public String getSize() {
        return size;
    }

    public boolean isNullable() {
        return nullable;
    }

    public ColumnSpec setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public ColumnSpec setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public ColumnSpec setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }
}
