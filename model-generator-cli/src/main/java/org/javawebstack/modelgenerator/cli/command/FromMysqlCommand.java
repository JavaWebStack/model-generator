package org.javawebstack.modelgenerator.cli.command;

import org.javawebstack.modelgenerator.spec.TableSpec;
import org.javawebstack.orm.wrapper.MySQL;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "from-mysql")
public class FromMysqlCommand extends BaseGeneratorCommand implements Callable<Integer> {

    @Option(names = { "--mysql-host" }, defaultValue = "127.0.0.1")
    String mysqlHost;
    @Option(names = { "--mysql-port" }, defaultValue = "3306")
    int mysqlPort;
    @Option(names = { "--mysql-database" }, defaultValue = "app")
    String mysqlDatabase;
    @Option(names = { "--mysql-user" }, defaultValue = "root")
    String mysqlUser;
    @Option(names = { "--mysql-password" }, defaultValue = "")
    String mysqlPassword;

    public Integer call() {
        generate(TableSpec.fromDatabase(new MySQL(mysqlHost, mysqlPort, mysqlDatabase, mysqlUser, mysqlPassword)));
        return 0;
    }

}
