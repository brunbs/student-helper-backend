package br.com.bruno.barbosa.student_helper_backend.domain.dto;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentInfoDto {

    private String appointmentId;
    private LocalDate date;
    private String time;
    private ObjectId teacherId;
    private String status;
    private ObjectId studentId;
    private String studentName;
    private String teacherName;
    private String lessonUrl;

    public AppointmentInfoDto(AppointmentEntity appointmentEntity) {
        this.appointmentId = appointmentEntity.getId().toString();
        this.date = appointmentEntity.getDate();
        this.time = appointmentEntity.getTime();
        this.status = appointmentEntity.getStatus();
        this.teacherId = appointmentEntity.getTeacherId();
        this.studentId = appointmentEntity.getStudentId();
        this.lessonUrl = appointmentEntity.getLessonUrl();
    }

}
