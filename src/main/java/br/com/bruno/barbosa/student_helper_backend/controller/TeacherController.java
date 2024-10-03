package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentsListResponse;
import br.com.bruno.barbosa.student_helper_backend.service.AppointmentService;
import br.com.bruno.barbosa.student_helper_backend.service.StudentService;
import br.com.bruno.barbosa.student_helper_backend.service.TeacherService;
import jakarta.websocket.server.PathParam;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/profile")
    public ResponseEntity<TeacherEntity> getTeacherProfile() {
        return ResponseEntity.ok(teacherService.getTeacherProfile());
    }

    @PutMapping("/profile:update")
    public ResponseEntity<TeacherEntity> updateTeacher(@RequestBody CreateTeacherRequest createTeacherRequest) {
        try {
            TeacherEntity updatedTeacher = teacherService.updateTeacher(createTeacherRequest);
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
