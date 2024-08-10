package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, ObjectId> {
    Optional<Role> findByRoleName(String roleName);
}