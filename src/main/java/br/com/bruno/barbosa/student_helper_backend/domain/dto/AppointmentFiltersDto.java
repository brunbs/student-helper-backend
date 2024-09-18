package br.com.bruno.barbosa.student_helper_backend.domain.dto;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentFiltersDto {

    private List<String> teacherId;
    private LocalDate date;
    private List<LocalTime> time;
    private List<String> status;
    private SchoolAgeEnum schoolAge;

}
