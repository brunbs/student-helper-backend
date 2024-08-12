package br.com.bruno.barbosa.student_helper_backend.domain.exception;

public class AppointmentException extends RuntimeException {
    public AppointmentException(String message) {
        super(message);
    }
}