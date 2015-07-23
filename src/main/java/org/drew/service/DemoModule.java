package org.drew.service;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.example.flyway.db.h2.flyway_test.tables.daos.BookDao;

/**
 * Created by jamesdrew on 04/05/2015.
 */
@Slf4j
public class DemoModule extends AbstractModule {

    private final JooqBundle jooqBundle;

    public DemoModule(JooqBundle jooqBundle) {
        this.jooqBundle = jooqBundle;
    }

    @Override
    protected void configure() {}

    @Provides
    BookDao providesBookDao(){
        return new BookDao(jooqBundle.getConfiguration());
    }
}
