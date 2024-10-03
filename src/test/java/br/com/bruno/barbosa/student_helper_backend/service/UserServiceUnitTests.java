package br.com.bruno.barbosa.student_helper_backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.Role;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.UserAlreadyExistsException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.RoleRepository;
import br.com.bruno.barbosa.student_helper_backend.repository.UserRepository;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith({MockitoExtension.class})
class UserServiceUnitTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private CreateUserRequest createUserRequest;
  private Role role;
  private User user;

  private final ObjectId roleId = new ObjectId();
  private final ObjectId userId = new ObjectId();

  @BeforeEach
  void setUp() {
    // Setup objects to be reused in tests
    createUserRequest = new CreateUserRequest();
    createUserRequest.setUsername("testuser");
    createUserRequest.setPassword("password123");
    createUserRequest.setEmail("test@example.com");
    createUserRequest.setRoleName(RoleEnum.STUDENT);

    role = new Role();
    role.setId(roleId);
    role.setRoleName(RoleEnum.STUDENT.name());

    user = new User();
    user.setId(userId);
    user.setUsername("testuser");
    user.setPassword("encodedPassword");
    user.setEmail("test@example.com");
    user.setRoleId(roleId);
    user.setEnabled(true);
  }

  @Test
  void testCreateUserSuccess() {
    when(roleRepository.findByRoleName(RoleEnum.STUDENT.name())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

    User createdUser = userService.createUser(createUserRequest);

    assertNotNull(createdUser);
    assertEquals("testuser", createdUser.getUsername());
    assertEquals("encodedPassword", createdUser.getPassword());
    assertEquals("test@example.com", createdUser.getEmail());
    assertTrue(createdUser.isEnabled());
  }

  @Test
  void testCreateUserRoleNotFound() {
    when(roleRepository.findByRoleName(RoleEnum.STUDENT.name())).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      userService.createUser(createUserRequest);
    });

    assertEquals("Role not found", exception.getMessage());
  }

  @Test
  void testFindByUsername() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findByUsername("testuser");

    assertTrue(foundUser.isPresent());
    assertEquals("testuser", foundUser.get().getUsername());
  }

  @Test
  void testFindByEmail() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findByEmail("test@example.com");

    assertTrue(foundUser.isPresent());
    assertEquals("test@example.com", foundUser.get().getEmail());
  }

  @Test
  void testValidateUserThrowsUsernameExists() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
      userService.validateUser(createUserRequest);
    });

    assertEquals("Username já em uso", exception.getMessage());
  }

  @Test
  void testValidateUserThrowsEmailExists() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
      userService.validateUser(createUserRequest);
    });

    assertEquals("E-mail já em uso", exception.getMessage());
  }

  @Test
  void testGetUserDetailsFromTokenSuccess() {
    UserDetails userDetails = mock(UserDetails.class);
    Authentication authentication = mock(Authentication.class);

    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(authentication.isAuthenticated()).thenReturn(true);

    UserDetails result = userService.getUserDetailsFromToken();
    assertEquals(userDetails, result);
  }

  @Test
  void testGetUserDetailsFromTokenThrowsException() {
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThrows(UsernameNotFoundException.class, () -> {
      userService.getUserDetailsFromToken();
    });
  }

  @Test
  void testGetUserFromTokenSuccess() {
    UserDetails userDetails = mock(UserDetails.class);
    Authentication authentication = mock(Authentication.class);

    when(userDetails.getUsername()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(authentication.isAuthenticated()).thenReturn(true);

    UserDto foundUser = userService.getUserFromToken();

    assertNotNull(foundUser);
    assertEquals("testuser", foundUser.getUsername());
    assertEquals("test@example.com", foundUser.getEmail());
    assertEquals(RoleEnum.STUDENT, foundUser.getRole());
  }

  @Test
  void testIsUserAuthenticated() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);

    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    assertDoesNotThrow(() -> userService.isUserAuthenticated());
  }

  @Test
  void testIsUserAuthenticatedThrowsException() {
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThrows(UsernameNotFoundException.class, () -> {
      userService.isUserAuthenticated();
    });
  }

}
