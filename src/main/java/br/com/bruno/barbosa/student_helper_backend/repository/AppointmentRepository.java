package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<AppointmentEntity, ObjectId> {

    List<AppointmentEntity> findAllByTeacherIdAndDateBetweenOrderByDateAsc(ObjectId teacherId, LocalDate startDate, LocalDate endDate);

}
