package br.com.bruno.barbosa.student_helper_backend.service;


import br.com.bruno.barbosa.student_helper_backend.domain.entity.SchoolEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateSchoolRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.SchoolRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private UserService userService;

    public SchoolEntity createSchool(CreateSchoolRequest createSchoolRequest) {
        userService.validateUser(createSchoolRequest);
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(createSchoolRequest.getEmail());
        createUserRequest.setPassword(createSchoolRequest.getPassword());
        createUserRequest.setUsername(createSchoolRequest.getUsername());
        createUserRequest.setRoleName(RoleEnum.SCHOOL);
        User createdUser = userService.createUser(createUserRequest);

        SchoolEntity schoolEntity = new SchoolEntity();
        schoolEntity.setUserId(createdUser.getId());
        schoolEntity.setName(createSchoolRequest.getName());
        schoolEntity.setAddress(createSchoolRequest.getAddress());
        schoolEntity.setPhone(createSchoolRequest.getPhone());
        return schoolRepository.save(schoolEntity);
    }

    public Optional<SchoolEntity> getSchool(ObjectId id) {
        return schoolRepository.findById(id);
    }

    public SchoolEntity updateSchool(ObjectId id, SchoolEntity schoolEntity) {
        if (!schoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("School not found with id " + id);
        }
        schoolEntity.setId(id);
        return schoolRepository.save(schoolEntity);
    }

    public void deleteSchool(ObjectId id) {
        if (!schoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("School not found with id " + id);
        }
        schoolRepository.deleteById(id);
    }
}