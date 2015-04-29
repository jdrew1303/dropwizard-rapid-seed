package org.drew.demo;

import com.google.common.base.Optional;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import lombok.Getter;
import lombok.Setter;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.tools.jdbc.JDBCUtils;

import javax.validation.constraints.NotNull;


/**
 * Created by jamesdrew on 22/04/2015.
 */
@Getter
@Setter
public class JooqFactory {
    @NotNull
    private Optional<SQLDialect> dialect = Optional.absent();

    private boolean logExecutedSql = false;

    private boolean renderSchema = true;

    @NotNull
    private RenderNameStyle renderNameStyle = RenderNameStyle.QUOTED;

    @NotNull
    private RenderKeywordStyle renderKeywordStyle = RenderKeywordStyle.UPPER;

    private boolean renderFormatted = false;

    @NotNull
    private ParamType paramType = ParamType.INDEXED;

    @NotNull
    private StatementType statementType = StatementType.PREPARED_STATEMENT;

    private boolean executeLogging = false;

    private boolean executeWithOptimisticLocking = false;

    private boolean attachRecords = true;

    private boolean updatablePrimaryKeys = false;

    public Configuration build(Environment environment, DataSourceFactory factory) throws ClassNotFoundException {
        final Settings settings = buildSettings();
        final SQLDialect dialect = determineDialect(factory);
        final ManagedDataSource dataSource = factory.build(environment.metrics(), "jooq");
        final ConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);
        final Configuration config = new DefaultConfiguration();
        config.set(settings);
        config.set(dialect);
        config.set(connectionProvider);

        if (logExecutedSql && !executeLogging) {
            final Settings loggerSettings = (Settings) settings.clone();
            loggerSettings.setRenderFormatted(true);

            final DSLContext loggerContext = DSL.using(dialect, loggerSettings);
            final LoggingExecutionListener listener = new LoggingExecutionListener(loggerContext);

            config.set(new DefaultExecuteListenerProvider(listener));
        }

        environment.lifecycle().manage(dataSource);

        return config;
    }

    private SQLDialect determineDialect(DataSourceFactory dataSourceFactory) {
        if (getDialect().isPresent()) {
            return dialect.get();
        }

        return JDBCUtils.dialect(dataSourceFactory.getUrl());
    }

    private Settings buildSettings() {
        final Settings settings = new Settings();
        settings.setRenderSchema(renderSchema);
        settings.setRenderNameStyle(renderNameStyle);
        settings.setRenderKeywordStyle(renderKeywordStyle);
        settings.setRenderFormatted(renderFormatted);
        settings.setParamType(paramType);
        settings.setStatementType(statementType);
        settings.setExecuteLogging(executeLogging);
        settings.setExecuteWithOptimisticLocking(executeWithOptimisticLocking);
        settings.setAttachRecords(attachRecords);
        settings.setUpdatablePrimaryKeys(updatablePrimaryKeys);

        return settings;
    }
}
