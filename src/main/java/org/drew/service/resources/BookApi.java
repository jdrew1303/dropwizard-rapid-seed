package org.drew.service.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.jooq.example.flyway.db.h2.flyway_test.tables.pojos.Book;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public interface BookApi {

    @GET
    @ApiOperation(value="Booooooks! but no cook books", response = Book.class)
    List<Book> getBook();
}
