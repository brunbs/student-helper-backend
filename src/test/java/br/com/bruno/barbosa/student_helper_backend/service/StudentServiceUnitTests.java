package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.StudentDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateStudentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.StudentRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceUnitTests {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private StudentService studentService;

    private final ObjectId roleId = new ObjectId();
    private final ObjectId userId = new ObjectId();

    @Test
    void testCreateStudent_Success() {
        CreateStudentRequest createStudentRequest = new CreateStudentRequest();
        createStudentRequest.setUsername("testuser");
        createStudentRequest.setEmail("test@example.com");
        createStudentRequest.setPassword("password");
        createStudentRequest.setSchoolAge(SchoolAgeEnum.EF_1);
        createStudentRequest.setName("Test User");
        createStudentRequest.setAddress("123 Test St.");
        createStudentRequest.setPhone("123456789");

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser);
        when(studentRepository.save(any(StudentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentEntity createdStudent = studentService.createStudent(createStudentRequest);

        assertNotNull(createdStudent);
        assertEquals(mockUser.getId(), createdStudent.getUserId());
        assertEquals("Test User", createdStudent.getName());
        verify(userService, times(1)).validateUser(createStudentRequest);
        verify(studentRepository, times(1)).save(any(StudentEntity.class));
    }

    @Test
    void testGetStudent_Success() {
        ObjectId studentId = new ObjectId();
        StudentEntity mockStudent = new StudentEntity();
        mockStudent.setId(studentId);
        mockStudent.setName("Test Student");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockStudent));

        Optional<StudentEntity> foundStudent = studentService.getStudent(studentId);

        assertTrue(foundStudent.isPresent());
        assertEquals("Test Student", foundStudent.get().getName());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testGetStudent_NotFound() {
        ObjectId studentId = new ObjectId();

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        Optional<StudentEntity> foundStudent = studentService.getStudent(studentId);

        assertFalse(foundStudent.isPresent());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testUpdateStudent_Success() {
        ObjectId studentId = new ObjectId();
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName("Updated Name");

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(studentEntity);

        StudentEntity updatedStudent = studentService.updateStudent(studentId, studentEntity);

        assertNotNull(updatedStudent);
        assertEquals("Updated Name", updatedStudent.getName());
        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(1)).save(studentEntity);
    }

    @Test
    void testUpdateStudent_NotFound() {
        ObjectId studentId = new ObjectId();
        StudentEntity studentEntity = new StudentEntity();

        when(studentRepository.existsById(studentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> studentService.updateStudent(studentId, studentEntity));

        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(0)).save(any(StudentEntity.class));
    }

    @Test
    void testDeleteStudent_Success() {
        ObjectId studentId = new ObjectId();

        when(studentRepository.existsById(studentId)).thenReturn(true);

        studentService.deleteStudent(studentId);

        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    void testDeleteStudent_NotFound() {
        ObjectId studentId = new ObjectId();

        when(studentRepository.existsById(studentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> studentService.deleteStudent(studentId));

        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(0)).deleteById(studentId);
    }

    @Test
    void testFindLoggedStudent_Success() {
        UserDto mockUserDto = new UserDto();
        mockUserDto.setId(userId);
        mockUserDto.setUsername("loggedUser");

        StudentEntity mockStudent = new StudentEntity();
        mockStudent.setId(new ObjectId());
        mockStudent.setSchoolAge(SchoolAgeEnum.EF_1);

        when(userService.getUserFromToken()).thenReturn(mockUserDto);
        when(studentRepository.findByUserId(mockUserDto.getId())).thenReturn(Optional.of(mockStudent));

        StudentDto studentDto = studentService.findLoggedStudent();

        assertNotNull(studentDto);
        assertEquals(mockStudent.getId(), studentDto.getId());
        assertEquals(SchoolAgeEnum.EF_1, studentDto.getSchoolAge());
        verify(studentRepository, times(1)).findByUserId(mockUserDto.getId());
    }

    @Test
    void testFindLoggedStudent_StudentNotFound() {
        UserDto mockUserDto = new UserDto();
        mockUserDto.setId(userId);
        mockUserDto.setUsername("loggedUser");

        when(userService.getUserFromToken()).thenReturn(mockUserDto);
        when(studentRepository.findByUserId(mockUserDto.getId())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> studentService.findLoggedStudent());

        verify(studentRepository, times(1)).findByUserId(mockUserDto.getId());
    }

}
