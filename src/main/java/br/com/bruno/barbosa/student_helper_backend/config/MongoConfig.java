package br.com.bruno.barbosa.student_helper_backend.config;

import br.com.bruno.barbosa.student_helper_backend.util.LocalTimeToStringConverter;
import br.com.bruno.barbosa.student_helper_backend.util.StringToLocalTimeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new StringToLocalTimeConverter(),
                new LocalTimeToStringConverter()
        ));
    }
}