package br.com.bruno.barbosa.student_helper_backend.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherResponseToList {

    private ObjectId teacherId;
    private String name;

}
