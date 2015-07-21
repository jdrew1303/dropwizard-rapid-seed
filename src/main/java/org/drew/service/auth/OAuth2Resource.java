package org.drew.service.auth;


import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.drew.service.dao.AccessTokenDAO;
import org.drew.service.dao.UserDAO;
import org.drew.service.models.AccessToken;
import org.drew.service.models.User;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Slf4j
@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class OAuth2Resource {
	private List<String> allowedGrantTypes;
	private AccessTokenDAO accessTokenDAO;
	private UserDAO userDAO;

    @Inject
	public OAuth2Resource(@Named("GrantTypes") List<String> allowedGrantTypes, AccessTokenDAO accessTokenDAO, UserDAO userDAO) {
		this.allowedGrantTypes = allowedGrantTypes;
		this.accessTokenDAO = accessTokenDAO;
		this.userDAO = userDAO;

		log.info("Constructed OAuth2Resource with grant types {}", allowedGrantTypes);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String postForToken(
			@FormParam("grant_type") String grantType,
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("client_id") String clientId
	) {
		// Check if the grant type is allowed
		if (!allowedGrantTypes.contains(grantType)) {
			Response response = Response.status(METHOD_NOT_ALLOWED).build();
			throw new WebApplicationException(response);
		}

		// Try to find a user with the supplied credentials.
		Optional<User> user = userDAO.findUserByUsernameAndPassword(username, password);
		if (user == null || !user.isPresent()) {
			throw new WebApplicationException(Response.status(UNAUTHORIZED).build());
		}

		// User was found, generate a token and return it.
		AccessToken accessToken = accessTokenDAO.generateNewAccessToken(user.get(), new DateTime());
		return accessToken.getAccessTokenId().toString();
	}

    @GET
    public List<String> getGrantTypes(){
        return allowedGrantTypes;
    }

}
