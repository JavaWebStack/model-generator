package org.javawebstack.modelgenerator.spec;

import org.javawebstack.orm.wrapper.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableSpec {

    private final String name;
    private final List<ColumnSpec> columns = new ArrayList<>();

    public TableSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<ColumnSpec> getColumns() {
        return columns;
    }

    public static List<TableSpec> fromDatabase(SQL sql) {
        List<TableSpec> tables = new ArrayList<>();
        try {
            ResultSet rs = sql.read("SHOW TABLES;");
            while (rs.next())
                tables.add(new TableSpec(rs.getString(1)));
            sql.close(rs);
            for(TableSpec table : tables) {
                rs = sql.read("SHOW COLUMNS FROM `" + table.getName() + "`;");
                while (rs.next()) {
                    table.getColumns().add(new ColumnSpec(rs.getString(1), rs.getString(2))
                            .setNullable(rs.getString(3).toUpperCase(Locale.ROOT).equals("YES"))
                            .setPrimary(rs.getString(4).toUpperCase(Locale.ROOT).contains("PRI"))
                            .setAutoIncrement(rs.getString(6).toUpperCase(Locale.ROOT).contains("AUTO_INCREMENT"))
                    );
                }
                sql.close(rs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return tables;
    }

}
