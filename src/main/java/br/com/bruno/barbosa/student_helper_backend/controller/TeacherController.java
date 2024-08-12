package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.service.TeacherService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/register")
    public ResponseEntity<TeacherEntity> createTeacher(@RequestBody CreateTeacherRequest createTeacherRequest) {
        TeacherEntity savedTeacher = teacherService.createTeacher(createTeacherRequest);
        return ResponseEntity.ok(savedTeacher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherEntity> getTeacher(@PathVariable ObjectId id) {
        return teacherService.getTeacher(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherEntity> updateTeacher(@PathVariable ObjectId id, @RequestBody TeacherEntity teacherEntity) {
        try {
            TeacherEntity updatedTeacher = teacherService.updateTeacher(id, teacherEntity);
            return ResponseEntity.ok(updatedTeacher);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable ObjectId id) {
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
