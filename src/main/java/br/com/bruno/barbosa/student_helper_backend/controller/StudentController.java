package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.StudentDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.AppointmentStatusEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.AppointmentFilterRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateStudentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.TeacherResponseToList;
import br.com.bruno.barbosa.student_helper_backend.service.AppointmentService;
import br.com.bruno.barbosa.student_helper_backend.service.StudentService;
import br.com.bruno.barbosa.student_helper_backend.service.TeacherService;
import java.util.Optional;
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
    @Autowired
    private TeacherService teacherService;

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

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherResponseToList>> getTeacher() {
        StudentDto loggedStudent = studentService.findLoggedStudent();
        List<TeacherResponseToList> allAvailableTeachers = teacherService.findAllAvailableTeachers(loggedStudent.getSchoolAge());
        return ResponseEntity.ok(allAvailableTeachers);
    }

    @PostMapping("/appointments/search")
    ResponseEntity<List<AppointmentResponse>> searchAppointment(@RequestBody AppointmentFilterRequest filters) {
        StudentDto loggedStudent = studentService.findLoggedStudent();
        filters.setSchoolAge(loggedStudent.getSchoolAge());
        filters.setStatus(List.of(AppointmentStatusEnum.AVAILABLE.name()));
        return ResponseEntity.ok(appointmentService.getAppointmentsByFilter(filters));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentEntity> getStudentProfile() {
        StudentDto loggedStudent = studentService.findLoggedStudent();
        Optional<StudentEntity> student = studentService.getStudent(loggedStudent.getId());
        if(student.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.get());
    }

    @PutMapping("/profile:update")
    public ResponseEntity<StudentEntity> updateStudent(@RequestBody CreateStudentRequest createStudentRequest) {
        try {
            StudentEntity updatedstudent = studentService.updateStudent(createStudentRequest);
            return ResponseEntity.ok(updatedstudent);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
