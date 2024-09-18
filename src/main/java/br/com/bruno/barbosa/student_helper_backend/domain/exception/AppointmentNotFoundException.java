package br.com.bruno.barbosa.student_helper_backend.domain.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }
}