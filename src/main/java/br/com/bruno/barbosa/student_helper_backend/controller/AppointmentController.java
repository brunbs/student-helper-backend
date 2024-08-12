package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.service.AppointmentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/{appointmentId}/finish")
    ResponseEntity<Void> finishAppointment(@PathVariable ObjectId appointmentId) {
        appointmentService.finishAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{appointmentId}/cancel")
    ResponseEntity<Void> cancelAppointment(@PathVariable ObjectId appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{appointmentId}/book")
    ResponseEntity<Void> bookAppointment(@PathVariable ObjectId appointmentId) {
        appointmentService.bookAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

}
