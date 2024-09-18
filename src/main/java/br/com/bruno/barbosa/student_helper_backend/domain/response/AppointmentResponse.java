package br.com.bruno.barbosa.student_helper_backend.domain.response;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.AppointmentInfoDto;
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

    private String appointmentId;
    private LocalDate day;
    private String time;
    private String status;
    private ObjectId teacherId;
    private String teacherName;
    private ObjectId studentId;
    private String studentName;
    private String url;

    public AppointmentResponse(AppointmentEntity appointmentEntity) {
        this.appointmentId = appointmentEntity.getId().toString();
        this.day = appointmentEntity.getDate();
        this.time = appointmentEntity.getTime().toString();
        this.status = appointmentEntity.getStatus();
        this.teacherId = appointmentEntity.getTeacherId();
        this.studentId = appointmentEntity.getStudentId();
        this.url = appointmentEntity.getLessonUrl();
    }

    public AppointmentResponse(AppointmentInfoDto appointmentInfoDto) {
        this.appointmentId = appointmentInfoDto.getAppointmentId();
        this.day = appointmentInfoDto.getDate();
        this.time = appointmentInfoDto.getTime();
        this.status = appointmentInfoDto.getStatus();
        this.teacherId = appointmentInfoDto.getTeacherId();
        this.studentId = appointmentInfoDto.getStudentId();
        this.url = appointmentInfoDto.getLessonUrl();
        this.studentName = appointmentInfoDto.getStudentName();
        this.teacherName = appointmentInfoDto.getTeacherName();
    }
}
