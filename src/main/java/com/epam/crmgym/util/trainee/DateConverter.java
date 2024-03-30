package com.epam.crmgym.util.trainee;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {

    public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.systemDefault();

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(DEFAULT_TIME_ZONE).toLocalDate();
    }
}