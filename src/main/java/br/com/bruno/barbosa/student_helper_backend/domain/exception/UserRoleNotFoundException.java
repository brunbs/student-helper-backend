package br.com.bruno.barbosa.student_helper_backend.domain.exception;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException(String message) {
        super(message);
    }
}