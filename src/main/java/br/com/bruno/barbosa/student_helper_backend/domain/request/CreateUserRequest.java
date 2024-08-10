package br.com.bruno.barbosa.student_helper_backend.domain.request;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import lombok.Data;

@Data
public class CreateUserRequest {

    private String username;
    private String password;
    private RoleEnum roleName;
    private String email;
    private String name;
    private String address;
    private String phone;

}
