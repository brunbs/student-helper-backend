package br.com.bruno.barbosa.student_helper_backend.domain.enumeration;

import lombok.Getter;

@Getter
public enum RoleEnum {

    TEACHER ("PROFESSOR"),
    STUDENT ("ESTUDANTE"),
    SCHOOL ("ESCOLA");

    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

}
