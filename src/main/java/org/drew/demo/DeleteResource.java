package org.drew.demo;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by jamesdrew on 26/04/2015.
 */
public interface DeleteResource {

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") int id);
}
