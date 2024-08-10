package br.com.bruno.barbosa.student_helper_backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Role {

    @Id
    private ObjectId id;
    private String roleName;
}
