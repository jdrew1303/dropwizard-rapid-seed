package org.drew.demo.jooq.converters;

import org.joda.time.DateTime;
import org.jooq.Converter;

import java.sql.Date;

/**
 * Created by jamesdrew on 22/04/2015.
 */
public class SqlDateToJodaDateTime implements Converter<Date, DateTime> {
    @Override
    public DateTime from(Date databaseObject) {
        return new DateTime(databaseObject.toLocalDate());
    }

    @Override
    public Date to(DateTime userObject) {
        return new Date(userObject.getMillis());
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<DateTime> toType() {
        return DateTime.class;
    }
}
