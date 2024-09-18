package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.StudentDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentsListResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.TeacherResponseToList;
import br.com.bruno.barbosa.student_helper_backend.service.AppointmentService;
import br.com.bruno.barbosa.student_helper_backend.service.StudentService;
import br.com.bruno.barbosa.student_helper_backend.service.TeacherService;
import jakarta.websocket.server.PathParam;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private StudentService studentService;

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

    @PostMapping("/appointments")
    public ResponseEntity<Void> createAppointments(@RequestBody List<CreateAppointmentRequest> appointmentRequests) {
        appointmentService.createAppointments(appointmentRequests);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/appointments")
    public ResponseEntity<AppointmentsListResponse> getTeachersAppointments(@PathParam("month") String month,
                                                                            @PathParam("year") String year) {
        AppointmentsListResponse teachersAppointments = appointmentService.getTeachersAppointments(month, year);
        return ResponseEntity.ok(teachersAppointments);
    }

    @GetMapping("/appointments/today")
    public ResponseEntity<List<AppointmentResponse>> getTeachersAppointments() {
        List<AppointmentResponse> teachersAppointments = appointmentService.getTeacherTodaysAppointment();
        return ResponseEntity.ok(teachersAppointments);
    }
}
