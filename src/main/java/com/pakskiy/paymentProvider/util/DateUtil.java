package com.pakskiy.paymentProvider.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public class DateUtil {
    public static LocalDateTime getStartOtTheDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return LocalDateTime.now().with(LocalTime.MIN);
        }
        return dateTime;
    }

    public static LocalDateTime getEndOtTheDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return LocalDateTime.now().with(LocalTime.MAX);
        }
        return dateTime;
    }
}
