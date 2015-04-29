package org.drew.demo;

import org.jooq.DAO;

import java.util.List;

/**
 * Created by jamesdrew on 29/04/2015.
 */
public class BookResource extends BaseResource implements IBookResource{

    protected BookResource(Class klass, DAO dao) {
        super(klass, dao);
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public Object getBy(int id) {
        return null;
    }

    @Override
    public void update(Object itemToBeUpdated) {

    }
}
