package org.drew.demo;

/**
 * Created by jamesdrew on 26/04/2015.
 */
public interface CrudResource<T> extends CreateResource<T>, ReadResource<T>, UpdateResource<T>, DeleteResource {
}
