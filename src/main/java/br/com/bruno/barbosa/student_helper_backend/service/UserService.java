package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.Role;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.UserAlreadyExistsException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateStudentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.RoleRepository;
import br.com.bruno.barbosa.student_helper_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(CreateUserRequest createUserRequest) {
        Optional<Role> role = roleRepository.findByRoleName(createUserRequest.getRoleName().name());
                if(role.isEmpty()) throw new RuntimeException("Role not found");

        String encodedPassword = passwordEncoder.encode(createUserRequest.getPassword());

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setRoleId(role.get().getId());
        user.setEmail(createUserRequest.getEmail());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void validateUser(CreateUserRequest createUserRequest) {
        if(userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username já em uso");
        }
        if(userRepository.findByEmail(createUserRequest.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("E-mail já em uso");
        }
    }
}
