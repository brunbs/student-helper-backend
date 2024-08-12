package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<AppointmentEntity, ObjectId> {
}
