package br.com.bruno.barbosa.student_helper_backend.domain.response;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentResponse {

    private LocalDate day;
    private String time;
    private String status;
    private ObjectId teacherId;
    private String teacherName;
    private ObjectId studentId;
    private String studentName;
    private String url;

    public AppointmentResponse(AppointmentEntity appointmentEntity) {
        this.day = appointmentEntity.getDate();
        this.time = appointmentEntity.getTime();
        this.status = appointmentEntity.getStatus();
        this.teacherId = appointmentEntity.getTeacherId();
        this.studentId = appointmentEntity.getStudentId();
        this.url = appointmentEntity.getLessonUrl();
    }

}
