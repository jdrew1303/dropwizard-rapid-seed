package org.drew.service.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.SchemaGeneratorBuilder;
import com.wordnik.swagger.annotations.ApiOperation;
import org.jooq.DAO;
import org.jooq.example.flyway.db.h2.flyway_test.tables.daos.BookDao;
import org.jooq.example.flyway.db.h2.flyway_test.tables.pojos.Book;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by jamesdrew on 24/07/2015.
 */
public class AbstractResource {
    protected final DAO dao;
    private final Class resourceClass;

    public AbstractResource(DAO dao) {
        this.dao = dao;
        resourceClass = dao.getType();
    }

    @GET
    @Path("/schema")
    @ApiOperation("get us the schema!!!")
    public JsonNode generateSchema() throws JsonMappingException {

        // https://github.com/reinert/JJSchema
        JsonSchemaGenerator v4generator = SchemaGeneratorBuilder.draftV4Schema().setAutoPutSchemaVersion(true).build();
        return v4generator.generateSchema(resourceClass);
    }

    @GET
    @Path("/schema2")
    @ApiOperation("get us the schema!!!")
    public JsonSchema getResourceSchema() throws JsonMappingException {

        // ================ JACKSON JSON SCHEMA ================
        // Create our base mapper. Use this for your configuration. Not sure if we could
        // handle this in a bundle and inject it in with guice?
        ObjectMapper m = new ObjectMapper();

        // Configure the mapper to emit Enums using #toValue() instead of #toString()
        m.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        // Here we let the magic begin. It will generate our schema for us. This can be
        // further serialised by jackson (ie we can return it directly from a resource
        // for consumption of an org.drew.service.health). This is what we really want it for.
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        m.acceptJsonFormatVisitor(m.constructType(resourceClass), visitor);

        // BOOM! Done.
        return visitor.finalSchema();
    }
}
