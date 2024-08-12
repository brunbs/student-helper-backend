package br.com.bruno.barbosa.student_helper_backend.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentRequest {

    private String date;
    private List<String> times;

}
