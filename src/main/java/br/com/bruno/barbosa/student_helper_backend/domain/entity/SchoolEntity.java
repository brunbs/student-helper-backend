package br.com.bruno.barbosa.student_helper_backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "schools")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolEntity {
    @Id
    private ObjectId id;
    private String name;
    private ObjectId userId;
    private String address;
    private String phone;

}