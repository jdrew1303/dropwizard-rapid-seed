package org.drew.service.health;

import com.google.inject.AbstractModule;

/**
 * Created by jamesdrew on 04/05/2015.
 */
public class BuildInfoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BuildInfo.class).toInstance(BuildInfo.create());
    }
}
