package org.drew.demo;

import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * Created by jamesdrew on 23/04/2015.
 */
public class CreateTimestampedMigrationFile<T extends Configuration> extends ConfiguredCommand<T> {
    private static final String GENERATE_MIGRATION = "generateMigration";

    protected CreateTimestampedMigrationFile(String name, String description) {
        super(name, description);
    }

    @Override
    public void configure(Subparser subparser) {

        subparser.addArgument("--" + GENERATE_MIGRATION)
                .dest("fileName")
                .help("This will generate a new file for you in the migrations folder. " +
                        "It prepends your chosen filename with a timestamp. This helps to " +
                        "solve the problem of multiple people working in the same database " +
                        "simultaneously. If you just do a version increment then another " +
                        "developer could have taken the same version number.");

    }


    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception {

    }
}
