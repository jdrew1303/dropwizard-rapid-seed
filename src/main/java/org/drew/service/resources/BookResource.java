package org.drew.service.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.SchemaGeneratorBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.jooq.DAO;
import org.jooq.example.flyway.db.h2.flyway_test.tables.daos.BookDao;
import org.jooq.example.flyway.db.h2.flyway_test.tables.pojos.Book;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class BookResource extends AbstractResource implements BookApi {

    @Inject
    public BookResource(BookDao dao) {
        super(dao);
    }

    public List<Book> getBook(){
        return dao.findAll();
    }

}
