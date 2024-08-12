package br.com.bruno.barbosa.student_helper_backend.domain.dto;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentDto extends UserDto {

    SchoolAgeEnum schoolAge;

    public StudentDto(UserDto userDto) {
        this.setUserId(userDto.getUserId());
        this.setUsername(userDto.getUsername());
        this.setEmail(userDto.getEmail());
        this.setRole(userDto.getRole());
    }

}
