package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.StudentDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.TeacherDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateStudentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.StudentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    public StudentEntity createStudent(CreateStudentRequest createStudentRequest) {
        userService.validateUser(createStudentRequest);
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(createStudentRequest.getEmail());
        createUserRequest.setPassword(createStudentRequest.getPassword());
        createUserRequest.setUsername(createStudentRequest.getUsername());
        createUserRequest.setRoleName(RoleEnum.STUDENT);
        User createdUser = userService.createUser(createUserRequest);

        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setUserId(createdUser.getId());
        studentEntity.setSchoolAge(createStudentRequest.getSchoolAge());
        studentEntity.setName(createStudentRequest.getName());
        studentEntity.setAddress(createStudentRequest.getAddress());
        studentEntity.setPhone(createStudentRequest.getPhone());
        return studentRepository.save(studentEntity);
    }

    public Optional<StudentEntity> getStudent(ObjectId id) {
        return studentRepository.findById(id);
    }

    public StudentEntity updateStudent(CreateStudentRequest createStudentRequest) {
        StudentDto loggedStudent = findLoggedStudent();
        Optional<StudentEntity> student = getStudent(loggedStudent.getId());
        if(student.isEmpty()) {
            throw new ResourceNotFoundException("Student not found");
        }
        student.get().setId(loggedStudent.getId());
        student.get().setName(createStudentRequest.getName());
        student.get().setPhone(createStudentRequest.getPhone());
        student.get().setAddress(createStudentRequest.getAddress());
        student.get().setSchoolAge(createStudentRequest.getSchoolAge());
        return studentRepository.save(student.get());
    }

    public void deleteStudent(ObjectId id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id " + id);
        }
        studentRepository.deleteById(id);
    }

    public StudentDto findLoggedStudent() {
        UserDto userFromToken = userService.getUserFromToken();
        Optional<StudentEntity> foundStudent = studentRepository.findByUserId(userFromToken.getId());
        if(foundStudent.isEmpty()) {
            throw new UsernameNotFoundException("Student not found with given username");
        }
        StudentDto studentDto = new StudentDto(userFromToken);
        studentDto.setId(foundStudent.get().getId());
        studentDto.setSchoolAge(foundStudent.get().getSchoolAge());
        return studentDto;
    }
}