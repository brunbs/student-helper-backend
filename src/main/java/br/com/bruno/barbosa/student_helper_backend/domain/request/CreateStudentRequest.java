package br.com.bruno.barbosa.student_helper_backend.domain.request;

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
public class CreateStudentRequest extends CreateUserRequest{

    private SchoolAgeEnum schoolAge;

}
