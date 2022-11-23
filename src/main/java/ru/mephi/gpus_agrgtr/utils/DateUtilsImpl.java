package ru.mephi.gpus_agrgtr.utils;

import java.time.Instant;
import java.util.Date;

public class DateUtilsImpl implements DateUtils {
    @Override
    public Date getNow() {
        return Date.from(Instant.now());
    }
}