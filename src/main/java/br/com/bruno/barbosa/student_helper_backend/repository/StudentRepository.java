package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StudentRepository extends MongoRepository<StudentEntity, ObjectId> {

    Optional<StudentEntity> findByUserId(ObjectId userId);

}
