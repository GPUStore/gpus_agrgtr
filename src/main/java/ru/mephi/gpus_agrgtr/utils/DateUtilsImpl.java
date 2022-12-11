package ru.mephi.gpus_agrgtr.utils;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Date;
@Component
public class DateUtilsImpl implements DateUtils {
    @Override
    public Date getNow() {
        return Date.from(Instant.now());
    }
}