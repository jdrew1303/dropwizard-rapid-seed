package org.drew.demo;

import javax.ws.rs.PUT;

/**
 * Created by jamesdrew on 26/04/2015.
 */
public interface UpdateResource<T> {

    @PUT
    public void update(T itemToBeUpdated);
}
