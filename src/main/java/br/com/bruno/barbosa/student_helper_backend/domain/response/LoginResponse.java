package br.com.bruno.barbosa.student_helper_backend.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {

    private String token;
    private String redirectUrl;


}
