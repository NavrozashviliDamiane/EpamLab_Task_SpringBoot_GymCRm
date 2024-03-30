package com.epam.crmgym.exception;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeSerializer extends StdDeserializer<Date> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public DateDeSerializer() {
        this(null);
    }

    public DateDeSerializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.readValueAs(String.class);
        if (!isValidDateFormat(value)) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy/MM/dd");
        }
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy/MM/dd");
        }
    }

    private boolean isValidDateFormat(String dateOfBirth) {
        return dateOfBirth.matches("\\d{4}/\\d{2}/\\d{2}");
    }
}