package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.request.EditAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.service.AppointmentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
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

    @PostMapping("/{appointmentId}/open")
    ResponseEntity<Void> openAppointment(@PathVariable ObjectId appointmentId) {
        appointmentService.openAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{appointmentId}/close")
    ResponseEntity<Void> closeAppointment(@PathVariable ObjectId appointmentId) {
        appointmentService.closeAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{appointmentId}/addlink")
    ResponseEntity<Void> addLinkToAppointment(@PathVariable ObjectId appointmentId, @RequestBody EditAppointmentRequest editAppointmentRequest) {
        appointmentService.addLinkToAppointment(appointmentId, editAppointmentRequest.getUrl());
        return ResponseEntity.ok().build();
    }

}
