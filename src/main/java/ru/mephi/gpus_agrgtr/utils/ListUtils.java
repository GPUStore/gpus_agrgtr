package ru.mephi.gpus_agrgtr.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListUtils {
    public static <T> List<T> of(T... entities){
        return new ArrayList<>(Arrays.asList(entities));
    }
}
