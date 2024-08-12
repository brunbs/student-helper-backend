package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.Role;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.UserAlreadyExistsException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.UserRoleNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.RoleRepository;
import br.com.bruno.barbosa.student_helper_backend.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public UserDetails getUserDetailsFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Usuário não encontrado ou não autenticado.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado ou não autenticado.");
        }
    }

    public UserDto getUserFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Usuário não encontrado ou não autenticado.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> byUsername = userRepository.findByUsername(username);
        if(byUsername.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado ou não autenticado.");
        }
        Optional<Role> userRole = roleRepository.findById(byUsername.get().getRoleId());
        if(userRole.isEmpty()) {
            throw new UserRoleNotFoundException("Função não encontrada");
        }
        UserDto foundUser = new UserDto();
        foundUser.setId(byUsername.get().getId());
        foundUser.setUsername(byUsername.get().getUsername());
        foundUser.setEmail(byUsername.get().getEmail());
        foundUser.setRole(RoleEnum.valueOf(userRole.get().getRoleName()));
        return foundUser;
    }

    public void isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Usuário não encontrado ou não autenticado.");
        }
    }
}
