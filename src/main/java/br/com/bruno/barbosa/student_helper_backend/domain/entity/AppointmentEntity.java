package br.com.bruno.barbosa.student_helper_backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "appointments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentEntity {

    @Id
    private ObjectId id;
    private LocalDate date;
    private String time;
    private ObjectId teacherId;
    private String status;
    private ObjectId studentId;
    private String lessonUrl;

}
