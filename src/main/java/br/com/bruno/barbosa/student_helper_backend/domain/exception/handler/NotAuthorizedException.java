package br.com.bruno.barbosa.student_helper_backend.domain.exception.handler;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}