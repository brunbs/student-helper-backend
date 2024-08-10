package br.com.bruno.barbosa.student_helper_backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private ObjectId roleId;
    private boolean enabled;
}