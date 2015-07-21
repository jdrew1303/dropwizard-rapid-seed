package org.drew.service.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.drew.service.DemoAppConfiguration;
import org.drew.service.dao.AccessTokenDAO;
import org.drew.service.dao.UserDAO;

import javax.inject.Named;
import java.util.List;

/**
 * Created by jamesdrew on 04/05/2015.
 */
public class AuthModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccessTokenDAO.class).toInstance(new AccessTokenDAO());
        bind(UserDAO.class).toInstance(new UserDAO());
    }

    @Provides
    @Named("GrantTypes")
    public List<String> getRoles(DemoAppConfiguration configuration){
        return configuration.getAllowedGrantTypes();
    }
}
