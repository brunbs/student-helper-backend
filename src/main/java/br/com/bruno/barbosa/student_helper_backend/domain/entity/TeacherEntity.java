package br.com.bruno.barbosa.student_helper_backend.domain.entity;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "teachers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherEntity {
    @Id
    private ObjectId id;
    private String name;
    private ObjectId userId;
    private String address;
    private String phone;
    private Set<SchoolAgeEnum> schoolAges;
}
