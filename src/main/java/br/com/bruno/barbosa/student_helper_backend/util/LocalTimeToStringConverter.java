package br.com.bruno.barbosa.student_helper_backend.util;

import org.springframework.core.convert.converter.Converter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeToStringConverter implements Converter<LocalTime, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String convert(LocalTime source) {
        return source.format(formatter);
    }
}