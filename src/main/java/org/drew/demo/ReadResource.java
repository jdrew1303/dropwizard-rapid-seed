package org.drew.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

/**
 * Created by jamesdrew on 26/04/2015.
 */
public interface ReadResource<T> {

    @GET
    public List<T> getAll();

    @GET
    @Path("/{id}")
    public T getBy(@PathParam("id") int id);
}
