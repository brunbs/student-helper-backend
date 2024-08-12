package br.com.bruno.barbosa.student_helper_backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class WeekInfoDto {

    private final LocalDate startDate;
    private final LocalDate endDate;

}
