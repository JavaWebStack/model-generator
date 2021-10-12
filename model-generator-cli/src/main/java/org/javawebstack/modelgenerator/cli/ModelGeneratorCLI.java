package org.javawebstack.modelgenerator.cli;

import org.javawebstack.modelgenerator.cli.command.MainCommand;
import picocli.CommandLine;

public class ModelGeneratorCLI {

    public static void main(String[] args) {
        new CommandLine(new MainCommand()).execute(args);
    }

}
