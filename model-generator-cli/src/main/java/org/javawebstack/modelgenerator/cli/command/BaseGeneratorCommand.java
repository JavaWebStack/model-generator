package org.javawebstack.modelgenerator.cli.command;

import org.javawebstack.modelgenerator.ModelGenerator;
import org.javawebstack.modelgenerator.spec.TableSpec;
import picocli.CommandLine.*;

import java.io.File;
import java.util.List;

public abstract class BaseGeneratorCommand {

    @Parameters(index = "0", description = "Destination directory")
    File directory;
    @Option(names = "--package")
    String packageName;
    @Option(names = "--model-class")
    String modelClass;
    @Option(names = "--lombok")
    boolean lombok;
    @Option(names = "--table-prefix")
    String tablePrefix;
    @Option(names = "--class-prefix")
    String classPrefix;
    @Option(names = "--class-suffix")
    String classSuffix;

    protected void generate(List<TableSpec> specs) {
        ModelGenerator generator = new ModelGenerator(directory).setLombok(lombok);
        if(packageName != null)
            generator.setPackageName(packageName);
        if(modelClass != null)
            generator.setModelClass(modelClass);
        if(tablePrefix != null)
            generator.setTablePrefix(tablePrefix);
        if(classPrefix != null)
            generator.setClassNamePrefix(classPrefix);
        if(classSuffix != null)
            generator.setClassNameSuffix(classSuffix);
        generator.generate(specs);
    }

}
