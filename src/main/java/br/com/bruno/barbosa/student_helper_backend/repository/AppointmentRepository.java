package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<AppointmentEntity, ObjectId> {

    List<AppointmentEntity> findAllByTeacherIdAndDateBetweenOrderByDateAsc(ObjectId teacherId, LocalDate startDate, LocalDate endDate);

    AppointmentEntity findByTeacherIdAndDateAndTime(ObjectId id, LocalDate finalDate, String string);

    List<AppointmentEntity>findByTeacherIdAndDate(ObjectId id, LocalDateTime date);

    @Query("{ 'teacherId' : ?0, 'date' : { '$gte' : ?1, '$lt' : ?2 } }")
    List<AppointmentEntity>findByTeacherIdAndDateRange(ObjectId id, LocalDateTime start, LocalDateTime end);

    @Query("{ 'studentId': ?0, 'date': { $gte: ?1 } }")
    List<AppointmentEntity> findByStudentIdAndDateGreaterThanEqualOrderByDateAscTimeAsc(ObjectId studentId, LocalDate date);
}
