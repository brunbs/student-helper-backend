package br.com.bruno.barbosa.student_helper_backend.service;


import br.com.bruno.barbosa.student_helper_backend.domain.entity.SchoolEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.repository.SchoolRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    public SchoolEntity createSchool(SchoolEntity schoolEntity) {
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