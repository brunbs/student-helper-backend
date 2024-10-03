package br.com.bruno.barbosa.student_helper_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.TeacherDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.TeacherResponseToList;
import br.com.bruno.barbosa.student_helper_backend.repository.TeacherRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class TeacherServiceUnitTests {

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private TeacherService teacherService;

  @Test
  void testCreateTeacher_Success() {
    CreateTeacherRequest createTeacherRequest = new CreateTeacherRequest();
    createTeacherRequest.setUsername("teacheruser");
    createTeacherRequest.setEmail("teacher@example.com");
    createTeacherRequest.setPassword("password");
    createTeacherRequest.setSchoolAges(Set.of(SchoolAgeEnum.EF_1));
    createTeacherRequest.setName("Teacher User");
    createTeacherRequest.setAddress("123 Teacher St.");
    createTeacherRequest.setPhone("987654321");

    User mockUser = new User();
    mockUser.setId(new ObjectId());

    when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser);
    when(teacherRepository.save(any(TeacherEntity.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    TeacherEntity createdTeacher = teacherService.createTeacher(createTeacherRequest);

    assertNotNull(createdTeacher);
    assertEquals(mockUser.getId(), createdTeacher.getUserId());
    assertEquals("Teacher User", createdTeacher.getName());
    verify(userService, times(1)).validateUser(createTeacherRequest);
    verify(teacherRepository, times(1)).save(any(TeacherEntity.class));
  }

  @Test
  void testGetTeacher_Success() {
    ObjectId teacherId = new ObjectId();
    TeacherEntity mockTeacher = new TeacherEntity();
    mockTeacher.setId(teacherId);

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(mockTeacher));

    Optional<TeacherEntity> foundTeacher = teacherService.getTeacher(teacherId);

    assertTrue(foundTeacher.isPresent());
    assertEquals(mockTeacher, foundTeacher.get());
    verify(teacherRepository, times(1)).findById(teacherId);
  }

  @Test
  void testGetTeacher_NotFound() {
    ObjectId teacherId = new ObjectId();

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

    Optional<TeacherEntity> foundTeacher = teacherService.getTeacher(teacherId);

    assertFalse(foundTeacher.isPresent());
    verify(teacherRepository, times(1)).findById(teacherId);
  }

  @Test
  void testUpdateTeacher_Success() {
    ObjectId teacherId = new ObjectId();
    TeacherEntity teacherEntity = new TeacherEntity();
    teacherEntity.setName("editado");

    CreateTeacherRequest request = new CreateTeacherRequest();
    request.setName("editado");

    UserDto userDto = new UserDto();
    userDto.setId(teacherId);
    userDto.setUserId(teacherId);

    when(userService.getUserFromToken()).thenReturn(userDto);
    when(teacherRepository.findByUserId(any())).thenReturn(Optional.of(teacherEntity));
    when(teacherRepository.findById(any())).thenReturn(Optional.of(teacherEntity));
    when(teacherRepository.save(any(TeacherEntity.class))).thenReturn(teacherEntity);

    TeacherEntity updatedTeacher = teacherService.updateTeacher(request);

    assertNotNull(updatedTeacher);
    assertEquals("editado", updatedTeacher.getName());
    verify(teacherRepository, times(1)).save(teacherEntity);
  }

  @Test
  void testUpdateTeacher_NotFound() {
    CreateTeacherRequest request = new CreateTeacherRequest();

    when(userService.getUserFromToken()).thenReturn(mock(UserDto.class));

    assertThrows(UsernameNotFoundException.class,
        () -> teacherService.updateTeacher(request));

    verify(teacherRepository, times(0)).save(any(TeacherEntity.class));
  }

  @Test
  void testDeleteTeacher_Success() {
    ObjectId teacherId = new ObjectId();

    when(teacherRepository.existsById(teacherId)).thenReturn(true);

    teacherService.deleteTeacher(teacherId);

    verify(teacherRepository, times(1)).existsById(teacherId);
    verify(teacherRepository, times(1)).deleteById(teacherId);
  }

  @Test
  void testDeleteTeacher_NotFound() {
    ObjectId teacherId = new ObjectId();

    when(teacherRepository.existsById(teacherId)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> teacherService.deleteTeacher(teacherId));

    verify(teacherRepository, times(1)).existsById(teacherId);
    verify(teacherRepository, times(0)).deleteById(teacherId);
  }

  @Test
  void testFindLoggedTeacher_Success() {
    UserDto mockUserDto = new UserDto();
    mockUserDto.setId(new ObjectId());

    TeacherEntity mockTeacher = new TeacherEntity();
    mockTeacher.setId(new ObjectId());

    when(userService.getUserFromToken()).thenReturn(mockUserDto);
    when(teacherRepository.findByUserId(mockUserDto.getId())).thenReturn(Optional.of(mockTeacher));

    TeacherDto foundTeacher = teacherService.findLoggedTeacher();

    assertNotNull(foundTeacher);
    assertEquals(mockTeacher.getId(), foundTeacher.getId());
    verify(teacherRepository, times(1)).findByUserId(mockUserDto.getId());
  }

  @Test
  void testFindLoggedTeacher_TeacherNotFound() {
    UserDto mockUserDto = new UserDto();
    mockUserDto.setId(new ObjectId());

    when(userService.getUserFromToken()).thenReturn(mockUserDto);
    when(teacherRepository.findByUserId(mockUserDto.getId())).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> teacherService.findLoggedTeacher());

    verify(teacherRepository, times(1)).findByUserId(mockUserDto.getId());
  }

  @Test
  void testFindAllAvailableTeachers_Success() {
    TeacherEntity teacher1 = new TeacherEntity();
    teacher1.setId(new ObjectId());
    teacher1.setName("Teacher 1");

    TeacherEntity teacher2 = new TeacherEntity();
    teacher2.setId(new ObjectId());
    teacher2.setName("Teacher 2");

    when(teacherRepository.findAllBySchoolAgesContaining(SchoolAgeEnum.EF_1.name()))
        .thenReturn(List.of(teacher1, teacher2));

    List<TeacherResponseToList> availableTeachers = teacherService.findAllAvailableTeachers(
        SchoolAgeEnum.EF_1);

    assertNotNull(availableTeachers);
    assertEquals(2, availableTeachers.size());
    assertEquals("Teacher 1", availableTeachers.get(0).getName());
    assertEquals("Teacher 2", availableTeachers.get(1).getName());
    verify(teacherRepository, times(1)).findAllBySchoolAgesContaining(SchoolAgeEnum.EF_1.name());
  }

}
