package org.javawebstack.modelgenerator.cli.command;

import picocli.CommandLine;

@CommandLine.Command(name = "main", subcommands = { FromMysqlCommand.class })
public class MainCommand {

}
