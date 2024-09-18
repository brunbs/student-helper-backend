package br.com.bruno.barbosa.student_helper_backend.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeekAppointmentsResponse {

    private LocalDate startDay;
    private LocalDate endDay;
    private List<AppointmentResponse> appointments;

}
