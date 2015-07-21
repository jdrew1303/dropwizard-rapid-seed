package org.drew.service.health;

import com.hubspot.dropwizard.guice.InjectableHealthCheck;

import javax.inject.Inject;

/**
 * A org.drew.service.health check that displays the Maven version of the running code.
 * Is unhealthy if it is not available or a SNAPSHOT version.
 *
 * @author Bo Gotthardt
 */
public class VersionHealthCheck extends InjectableHealthCheck {

    private final BuildInfo buildInfo;

    @Inject
    public VersionHealthCheck(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    @Override
    protected Result check() throws Exception {
        if (buildInfo.isNotValid()) {
            return Result.unhealthy("Running non-Maven build, no version info available.");
        } else if (buildInfo.isSnapshot()) {
            return Result.healthy("Running snapshot build: %s", buildInfo.getVersion());
        } else {
            return Result.healthy(buildInfo.getVersion());
        }
    }

    @Override
    public String getName() {
        return "version-health-check";
    }
}
