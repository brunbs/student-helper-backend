package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.SchoolEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.service.SchoolService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @PostMapping
    public ResponseEntity<SchoolEntity> createSchool(@RequestBody SchoolEntity schoolEntity) {
        SchoolEntity savedSchool = schoolService.createSchool(schoolEntity);
        return ResponseEntity.ok(savedSchool);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolEntity> getSchool(@PathVariable ObjectId id) {
        return schoolService.getSchool(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolEntity> updateSchool(@PathVariable ObjectId id, @RequestBody SchoolEntity schoolEntity) {
        try {
            SchoolEntity updatedSchool = schoolService.updateSchool(id, schoolEntity);
            return ResponseEntity.ok(updatedSchool);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable ObjectId id) {
        try {
            schoolService.deleteSchool(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}