package org.javawebstack.modelgenerator;

import org.javawebstack.modelgenerator.spec.TableSpec;
import org.javawebstack.orm.util.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelGenerator {

    private final File directory;
    private String packageName = "model";
    private String classNamePrefix = "";
    private String classNameSuffix = "";
    private String tablePrefix = "";
    private String modelClass = "org.javawebstack.orm.Model";
    private boolean lombok;

    public ModelGenerator(File directory) {
        this.directory = directory;
    }

    public void generate(List<TableSpec> specs) {
        specs.forEach(this::generate);
    }

    public void generate(TableSpec spec) {
        // Create directory
        File directory = getFullDirectory();
        if(!directory.exists())
            directory.mkdirs();

        // Build class name
        String className = Helper.toCamelCase(spec.getName().startsWith(tablePrefix) ? spec.getName().substring(tablePrefix.length()) : spec.getName());
        if(className.endsWith("sses"))
            className = className.substring(0, className.length()-2);
        else if(className.endsWith("s"))
            className = className.substring(0, className.length()-1);
        if(className.endsWith("ie"))
            className = className.substring(0, className.length()-2) + "y";
        className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        className = classNamePrefix + className + classNameSuffix;

        // Find imports and build type map
        List<String> imports = new ArrayList<>();
        Map<String, String> typeMap = new HashMap<String, String>() {{
            put("java.lang.String", "String");
            put("java.lang.Short", "Short");
            put("java.lang.Long", "Long");
            put("java.lang.Integer", "Integer");
            put("java.lang.Float", "Float");
            put("java.lang.Double", "Double");
            put("java.lang.Boolean", "Boolean");
            put("I", "int");
            put("Z", "boolean");
            put("B", "byte");
            put("[B", "byte[]");
            put("D", "double");
            put("F", "float");
            put("J", "long");
            put("S", "short");
            put("C", "char");
        }};
        spec.getColumns().forEach(colSpec -> {
            String type = colSpec.getType().getJavaType().getName();
            if(typeMap.containsKey(type))
                return;
            switch (type) {
                case "java.sql.Date":
                case "java.sql.Timestamp":
                    if(!imports.contains(type))
                        imports.add(type);
                    typeMap.put(type, type.substring(type.lastIndexOf(".")+1));
                    break;
                default:
                    typeMap.put(type, type);
                    break;
            }
        });

        boolean dates = spec.getColumns().stream().filter(s -> s.getName().equals("created_at") || s.getName().equals("updated_at")).count() == 2;
        boolean softDelete = spec.getColumns().stream().anyMatch(s -> s.getName().equals("deleted_at"));

        // Class Header
        StringBuilder sb = new StringBuilder("package ")
                .append(packageName)
                .append(";\n\nimport ")
                .append(modelClass)
                .append(";\nimport org.javawebstack.orm.annotation.Column;\n")
                .append("import org.javawebstack.orm.annotation.Table;\n");

        // Additional imports
        if(dates)
            sb.append("import org.javawebstack.orm.annotation.Dates;\n");
        if(softDelete)
            sb.append("import org.javawebstack.orm.annotation.SoftDelete;\n");
        if(lombok)
            sb.append("import lombok.Getter;\nimport lombok.Setter;\n");
        imports.forEach(s -> sb.append("import ").append(s).append(";\n"));
        sb.append("\n");

        // Additional annotations
        if(lombok)
            sb.append("@Getter\n@Setter\n");
        if(dates)
            sb.append("@Dates\n");
        if(softDelete)
            sb.append("@SoftDelete\n");

        // Class definition
        sb
                .append("@Table(\"")
                .append(spec.getName())
                .append("\")\n")
                .append("public class ")
                .append(className)
                .append(" extends ")
                .append(modelClass.substring(modelClass.lastIndexOf(".")+1))
                .append(" {\n\n");

        // Fields
        spec.getColumns().forEach(colSpec -> {
            sb
                    .append("    @Column(name = \"")
                    .append(colSpec.getName())
                    .append("\")\n    ")
                    .append(typeMap.get(colSpec.getType().getJavaType().getName()))
                    .append(" ")
                    .append(Helper.toCamelCase(colSpec.getName()))
                    .append(";\n");
        });

        // Generate Getter- and Setter methods if lombok is disabled
        if(!lombok) {
            String finalClassName = className;
            spec.getColumns().forEach(colSpec -> {
                String pascalCase = Helper.toCamelCase(colSpec.getName());
                pascalCase = Character.toUpperCase(pascalCase.charAt(0)) + pascalCase.substring(1);
                sb
                        .append("\n    public ")
                        .append(typeMap.get(colSpec.getType().getJavaType().getName()))
                        .append(" get")
                        .append(pascalCase)
                        .append("() {\n        return ")
                        .append(Helper.toCamelCase(colSpec.getName()))
                        .append(";\n    }\n\n    public ")
                        .append(finalClassName)
                        .append(" set")
                        .append(pascalCase)
                        .append("(")
                        .append(typeMap.get(colSpec.getType().getJavaType().getName()))
                        .append(" ")
                        .append(Helper.toCamelCase(colSpec.getName()))
                        .append(") {\n        this.")
                        .append(Helper.toCamelCase(colSpec.getName()))
                        .append(" = ")
                        .append(Helper.toCamelCase(colSpec.getName()))
                        .append(";\n        return this;\n    }\n");
            });
        }

        // Closing bracket
        sb.append("\n}\n");

        // Write class to file
        File file = new File(directory, className + ".java");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFullDirectory() {
        File d = directory;
        for(String p : packageName.split("\\."))
            d = new File(d, p);
        return d;
    }

    public String getPackageName() {
        return packageName;
    }

    public ModelGenerator setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getClassNamePrefix() {
        return classNamePrefix;
    }

    public ModelGenerator setClassNamePrefix(String classNamePrefix) {
        this.classNamePrefix = classNamePrefix;
        return this;
    }

    public String getClassNameSuffix() {
        return classNameSuffix;
    }

    public ModelGenerator setClassNameSuffix(String classNameSuffix) {
        this.classNameSuffix = classNameSuffix;
        return this;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public ModelGenerator setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    public String getModelClass() {
        return modelClass;
    }

    public ModelGenerator setModelClass(String modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    public boolean isLombok() {
        return lombok;
    }

    public ModelGenerator setLombok(boolean lombok) {
        this.lombok = lombok;
        return this;
    }

}
