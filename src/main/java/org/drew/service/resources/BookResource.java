package org.drew.service.resources;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.SchemaGeneratorBuilder;
import com.wordnik.swagger.annotations.Api;
import org.jooq.DAO;
import org.jooq.example.flyway.db.h2.flyway_test.tables.daos.BookDao;
import org.jooq.example.flyway.db.h2.flyway_test.tables.pojos.Book;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jamesdrew on 29/04/2015.
 */

@Api
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

    private final DAO dao;

    @Inject
    public BookResource(BookDao dao) {
        this.dao = dao;
    }

    @GET
    @Path("/schema")
    public JsonNode generateSchema() throws JsonMappingException {
//        // ================ JACKSON JSON SCHEMA ================
//        // Create our base mapper. Use this for your configuration. Not sure if we could
//        // handle this in a bundle and inject it in with guice?
//        ObjectMapper mapper = new ObjectMapper();
//        // Configure the mapper to emit Enums using #toValue() instead of #toString()
//        mapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
//        // Here we let the magic begin. It will generate our schema for us. This can be
//        // further serialised by jackson (ie we can return it directly from a resource
//        // for consumption of an org.drew.service.health). This is what we really want it for.
//        JsonSchema schema = mapper.generateJsonSchema(Author.class);
//        // This part can be of use debugging and logging. (thinking of creating a helper
//        // for this, maybe as part of a #toString() builder.
//        String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
//        // BOOM! Done.
//        System.out.println(out);

        // TODO replace this with a more functional module
        // https://github.com/reinert/JJSchema
        JsonSchemaGenerator v4generator = SchemaGeneratorBuilder.draftV4Schema().setAutoPutSchemaVersion(true).build();
        return v4generator.generateSchema(Book.class);
    }

    @GET
    public List<Book> getBook(){
        return dao.findAll();
    }

}
