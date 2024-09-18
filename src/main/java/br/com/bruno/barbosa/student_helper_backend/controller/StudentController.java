package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateStudentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.service.AppointmentService;
import br.com.bruno.barbosa.student_helper_backend.service.StudentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/register")
    public ResponseEntity<StudentEntity> createStudent(@RequestBody CreateStudentRequest createStudentRequest) {
        StudentEntity savedStudent = studentService.createStudent(createStudentRequest);
        return ResponseEntity.ok(savedStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentEntity> getStudent(@PathVariable ObjectId id) {
        return studentService.getStudent(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> updateStudent(@PathVariable ObjectId id, @RequestBody StudentEntity studentEntity) {
        try {
            StudentEntity updatedStudent = studentService.updateStudent(id, studentEntity);
            return ResponseEntity.ok(updatedStudent);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable ObjectId id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getStudentsAppointments() {
        return ResponseEntity.ok(appointmentService.getStudentBookedAppointments());
    }
}
