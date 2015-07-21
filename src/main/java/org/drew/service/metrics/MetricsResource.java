package org.drew.service.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.status;

/**
 * @author Bo Gotthardt
 */
//@Path("/metrics")
public class MetricsResource {
    private final MetricRegistry metrics;

    // We use a 204 as this has the smallest header-size of any http request. There's no
    // point in sending needless bits across the wire. They cost money, ya know!! Kids
    // these days; wasting bits and bytes all over the place. And don't get me started
    // on those damn hipsters, with their hairy faces, their sawdust bedding and their
    // vegan diets! They disgust me! Oh wait that's hamsters... Never mind!...
    //
    // Damn hamsters!!!!
    @Inject
    public MetricsResource(MetricRegistry metrics) {
        this.metrics = metrics;
    }

    @POST
    @Path("/meter/{name}")
    public Response meter(@PathParam("name") String name, @QueryParam("value") Optional<Long> value) {
        metrics.meter(name).mark(value.or(1L));
        return status(NO_CONTENT).build();
    }

    @POST
    @Path("/timer/{name}")
    public Response timer(@PathParam("name") String name, @QueryParam("value") long value) {
        metrics.timer(name).update(value, TimeUnit.MILLISECONDS);
        return status(NO_CONTENT).build();
    }

    @GET
    @Path("/meter/{name}")
    public Response meterGif(@PathParam("name") String name, @QueryParam("value") Optional<Long> value) {
        metrics.meter(name).mark(value.or(1L));
        // Instead of returning a single pixel gif (as is standard practise) we return a 204 No Content
        // message instead. This saves us 34kb in the response and seeing as you pay for the bandwidth
        // it may save you a few pence too. Mind the pennies and the pounds mind themselves as my parents
        // say.
        return status(NO_CONTENT).build();
    }

    @GET
    @Path("/timer")
    public Response timerGif(@QueryParam("name") String name, @QueryParam("value") long value)  {
        metrics.timer(name).update(value, TimeUnit.MILLISECONDS);
        return status(NO_CONTENT).build();
    }
}
