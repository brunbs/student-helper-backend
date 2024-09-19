package br.com.bruno.barbosa.student_helper_backend.domain.dto;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.request.AppointmentFilterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentFiltersDto {

    private List<ObjectId> teacherId;
    private LocalDate date;
    private List<LocalTime> time;
    private List<String> status;
    private SchoolAgeEnum schoolAge;

    public AppointmentFiltersDto (AppointmentFilterRequest appointmentFilterRequest) {
        this.date = appointmentFilterRequest.getDate() != null ? appointmentFilterRequest.getDate() : null;
        this.time = appointmentFilterRequest.getTime() != null ? appointmentFilterRequest.getTime() : new ArrayList<>();
        this.status = appointmentFilterRequest.getStatus() != null ? appointmentFilterRequest.getStatus() : new ArrayList<>();
    }

}
