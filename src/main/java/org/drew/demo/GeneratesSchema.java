package org.drew.demo;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.schema.JsonSchema;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by jamesdrew on 26/04/2015.
 */
public interface GeneratesSchema {

    @GET
    @Path("/schema")
    @Produces({ MediaType.APPLICATION_JSON })
    JsonSchema generateSchema() throws JsonMappingException;
}
