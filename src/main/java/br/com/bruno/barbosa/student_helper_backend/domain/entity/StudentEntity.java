package br.com.bruno.barbosa.student_helper_backend.domain.entity;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentEntity {
    @Id
    private ObjectId id;
    private String name;
    private String address;
    private String phone;
    private ObjectId userId;
    private SchoolAgeEnum schoolAge;
}