package org.drew.demo.jooq.converters;

import org.joda.time.DateTime;
import org.jooq.Converter;

import java.sql.Timestamp;

/**
 * Created by jamesdrew on 22/04/2015.
 */
public class TimestampToJodaDateTimeConverter implements Converter<Timestamp, DateTime> {
    @Override
    public DateTime from(Timestamp timestamp) {
        return new DateTime(timestamp.getTime());
    }

    @Override
    public Timestamp to(DateTime dateTime) {
        return new Timestamp(dateTime.getMillis());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<DateTime> toType() {
        return DateTime.class;
    }
}