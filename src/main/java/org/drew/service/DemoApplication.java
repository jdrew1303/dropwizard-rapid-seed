package org.drew.service;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.tradier.raven.logging.RavenBootstrap;
import com.tradier.raven.logging.UncaughtExceptionHandlers;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.drew.service.auth.AuthModule;
import org.drew.service.health.BuildInfoModule;
import org.drew.service.metrics.MetricsResource;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * The main entry point for our applciation.
 */
@Slf4j
public class DemoApplication extends Application<DemoAppConfiguration>{

    // USED TO INSTRUMENT THE MAIN METHOD
    private static Timer startupTimeMetric;
    private static final String DSN = "https://90d5b01f067b4b9b94f65bd0ecafd8eb:04362be665824f75abe5b2fc39eddfc1@app.getsentry.com/42917";
    private static final Optional<String> TAGS = Optional.of("startup:woooooo");

    // BUNDLES
    private JooqBundle<DemoAppConfiguration> jooqBundle;
    private GuiceBundle<DemoAppConfiguration> guiceBundle;

    /**
     * Handle the startup of the dropwizard org.drew.service.application. The only issue
     * with this is that it blocks the main thread. It is however possible
     * to start this as an embedded org.drew.service.application.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Stopwatch startupTimer = Stopwatch.createStarted();

        // We need to kick start logging before the org.drew.service.health starts,
        // otherwise we cant catch org.drew.service.health startup errors.
        RavenBootstrap.bootstrap(DSN, TAGS, false);
        Thread.currentThread().setUncaughtExceptionHandler(UncaughtExceptionHandlers.systemExit());


        new DemoApplication().run(args == null ? args : new String[]{"server", "example.yml"});

        if (startupTimeMetric != null) {
            long elapsed = startupTimer.stop().elapsed(TimeUnit.MILLISECONDS);
            startupTimeMetric.update(elapsed, TimeUnit.MILLISECONDS);
            log.info("Startup took {} ms.", elapsed);
        }
    }

    /**
     * Used to configure bundles before starting the main org.drew.service.application.
     *
     * @param bootstrap
     */
    @Override
    public void initialize(Bootstrap<DemoAppConfiguration> bootstrap) {

        // CONFIGURE BUNDLES
        jooqBundle = new JooqBundle<DemoAppConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DemoAppConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            public JooqFactory getJooqFactory(DemoAppConfiguration configuration) {
                return configuration.getJooqFactory();
            }
        };

        guiceBundle = GuiceBundle.<DemoAppConfiguration>newBuilder()
                        .addModule(new DemoModule())
                        .addModule(new BuildInfoModule())
                        .addModule(new AuthModule())
                        .setConfigClass(DemoAppConfiguration.class)
                        .enableAutoConfig(getClass().getPackage().getName())
                        .build();

        // LOAD BUNDLES
        bootstrap.addBundle(jooqBundle);
        bootstrap.addBundle(guiceBundle);
    }

    /**
     * Use this section to wire up dependencies and to register resources. Also
     * used for configuring security.
     *
     * @param demoAppConfiguration
     * @param environment
     * @throws Exception
     */
    @Override
    public void run(DemoAppConfiguration demoAppConfiguration, Environment environment) throws Exception {

        // Register Resources
        environment.jersey().register(new MetricsResource(environment.metrics()));

        // Register security component
//        environment.jersey().register(new OAuthProvider<Long>(new SimpleAuthenticator(accessTokenDAO), demoAppConfiguration.getBearerRealm()));
//        environment.jersey().register(new OAuth2Resource(demoAppConfiguration.getAllowedGrantTypes(), accessTokenDAO, userDAO));

        // Remove all of Dropwizard's custom ExceptionMappers
        removeDefaultExceptionMappers(environment);

        // STARTUP METRIC
        // Note: We handle this last to try to get as accurate a picture as possible of
        // the startup time. There may be other items in their own threads but this is
        // only for the main thread.
        startupTimeMetric = environment.metrics().timer(MetricRegistry.name(DemoApplication.class, "startup"));
    }

    /**
     * This will remove all exception mappers from Dropwizard. Because Dropwizard stores
     * the mappers in a set, if you try to register your own mapper then the run order is
     * not explicitly guaranteed. By forcing you to explicitly register all exception
     * mappers you know the exact output. Sometimes framework magic can get in the way of
     * building applications even in a small framework like Dropwizard. This should be
     * called in run method of the application class.
     *
     * @param environment the actual application environment.
     */
    private void removeDefaultExceptionMappers(Environment environment) {
        ResourceConfig resourceConfig = environment.jersey().getResourceConfig();
        Set<Object> singletons = resourceConfig.getSingletons();

        // Checks to see if a Mapper was registered by Dropwizard.
        Predicate<Object> isDropwizardExceptionMapper = singleton ->
                singleton instanceof ExceptionMapper && singleton.getClass().getName().startsWith("io.dropwizard.jersey.");

        singletons.stream()
                .filter(isDropwizardExceptionMapper)
                .map(singletons::remove);
    }

    // Need to have a better place to store this :/ @__refactor
    private Server mServer;
    private void addKillPathToApplication(Environment environment){

        LifecycleEnvironment lce = environment.lifecycle();

        // Pick up Server instance, so I can shut him down
        lce.addServerLifecycleListener(
                new ServerLifecycleListener() {
                    @Override
                    public void serverStarted(Server server) {
                        mServer = server;
                    }
                }
        );
        ServletEnvironment se = environment.servlets();
        se.addServlet("Terminator", new ServerTerminator(mServer)).addMapping("path/to/ByeBye");
    }


    class ServerTerminator extends HttpServlet {
        private Server server;

        private ServerTerminator(Server server){
            this.server = server;
        }
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            log.info("Terminator");
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            resp.getWriter().println();
            // Must initiate shut-down in separate thread to not deadlock here
            new Thread() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception ex) {
                        log.warn("Terminator failed", ex);
                    }
                }
            }.start();
        }
    }

}
