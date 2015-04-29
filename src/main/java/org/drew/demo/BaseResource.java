package org.drew.demo;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.schema.JsonSchema;
import org.jooq.DAO;

/**
 * Created by jamesdrew on 26/04/2015.
 */
public abstract class BaseResource implements GeneratesSchema, CrudResource {

    private final Class klass;
    private final DAO dao;

    protected BaseResource(Class klass, DAO dao) {
        this.klass = klass;
        this.dao = dao;
    }

    @Override
    public JsonSchema generateSchema() throws JsonMappingException {
        // Create our base mapper. Use this for your configuration. Not sure if we could
        // handle this in a bundle and inject it in with guice?
        ObjectMapper mapper = new ObjectMapper();
        // Configure the mapper to emit Enums using #toValue() instead of #toString()
        mapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
        // Here we let the magic begin. It will generate our schema for us. This can be
        // further serialised by jackson (ie we can return it directly from a resource
        // for consumption of an application). This is what we really want it for.
        return mapper.generateJsonSchema(klass);
    }

}
