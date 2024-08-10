package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUsername(String username);
}