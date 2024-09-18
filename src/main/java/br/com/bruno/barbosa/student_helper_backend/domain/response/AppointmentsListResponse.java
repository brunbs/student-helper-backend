package br.com.bruno.barbosa.student_helper_backend.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentsListResponse {

    private List<WeekAppointmentsResponse> weeksAppointments;

}
