package org.drew.service;

import com.bendb.dropwizard.jooq.JooqFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by jamesdrew on 22/04/2015.
 */
@Getter
@Setter
public class DemoAppConfiguration extends Configuration{

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("jooq")
    private JooqFactory jooqFactory = new JooqFactory();

    @Valid
    @JsonProperty
    private ImmutableList<String> allowedGrantTypes;

    @Valid
    @JsonProperty
    @NotEmpty
    private String bearerRealm;
}
