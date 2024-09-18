package br.com.bruno.barbosa.student_helper_backend.util;

import org.springframework.core.convert.converter.Converter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public LocalTime convert(String source) {
        return LocalTime.parse(source, formatter);
    }
}