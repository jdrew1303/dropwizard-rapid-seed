package org.drew.demo;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.schema.JsonSchema;
import org.jooq.Configuration;
import org.jooq.DAO;
import org.jooq.example.flyway.db.h2.flyway_test.tables.daos.AuthorDao;
import org.jooq.example.flyway.db.h2.flyway_test.tables.daos.BookDao;
import org.jooq.example.flyway.db.h2.flyway_test.tables.interfaces.IAuthor;
import org.jooq.example.flyway.db.h2.flyway_test.tables.interfaces.IBook;
import org.jooq.example.flyway.db.h2.flyway_test.tables.pojos.Book;

import java.util.List;

/**
 * Created by jamesdrew on 22/04/2015.
 */
public class DemoApplication extends Application<DemoAppConfiguration>{
    public static void main(String[] args) throws Exception {
        new DemoApplication().run(args == null ? args : new String[] {"server", "example.yml"});
    }

    @Override
    public void initialize(Bootstrap<DemoAppConfiguration> bootstrap) {
    }

    @Override
    public void run(DemoAppConfiguration demoAppConfiguration, Environment environment) throws Exception {

        // Create our Connection pool for JOOQ.
        final DataSourceFactory dataSourceFactory = demoAppConfiguration.getDataSourceFactory();
        final JooqFactory jooqFactory = demoAppConfiguration.getJooqFactory();
        final Configuration jooqConfiguration = jooqFactory.build(environment, dataSourceFactory);


        // Try out messing with the database to see if it works properly. Really in this
        // stage you inject in the DAOs into the services and then the resources before
        // wiring the resources up to Jersey. This is more or less on par with what you
        // would do with dependency injection. Might be a lot easier to configure too in
        // the short term. Not too sure about the long run though.
        final DAO authorDao = new AuthorDao(jooqConfiguration);
        final DAO bookDao = new BookDao(jooqConfiguration);

        final List<IAuthor> results = authorDao.findAll();
        final List<IBook> books = bookDao.findAll();

        for (IAuthor author : results) {
            System.out.println("                      " + author.getFirstName());
        }

        for (IBook book : books) {
            System.out.println("                      " + book.getTitle());
        }

        environment.jersey().register(new BookResource(Book.class, bookDao));





        // Create our base mapper. Use this for your configuration. Not sure if we could
        // handle this in a bundle and inject it in with guice?
        ObjectMapper mapper = new ObjectMapper();
        // Configure the mapper to emit Enums using #toValue() instead of #toString()
        mapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
        // Here we let the magic begin. It will generate our schema for us. This can be
        // further serialised by jackson (ie we can return it directly from a resource
        // for consumption of an application). This is what we really want it for.
        JsonSchema schema = mapper.generateJsonSchema(IAuthor.class);
        // This part can be of use debugging and logging. (thinking of creating a helper
        // for this, maybe as part of a #toString() builder.
        String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
        // BOOM! Done.
        System.out.println(out);
    }
}
