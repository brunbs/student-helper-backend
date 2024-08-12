package br.com.bruno.barbosa.student_helper_backend.domain.dto;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private ObjectId userId;
    private ObjectId id;
    private RoleEnum role;
    private String username;
    private String email;

}
