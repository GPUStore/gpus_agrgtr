package ru.mephi.gpus_agrgtr.utils;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public interface DateUtils {
    Date getNow();
}