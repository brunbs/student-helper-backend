package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.TeacherDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.TeacherRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public TeacherEntity createTeacher(CreateTeacherRequest createTeacherRequest) {
        userService.validateUser(createTeacherRequest);
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(createTeacherRequest.getEmail());
        createUserRequest.setPassword(createTeacherRequest.getPassword());
        createUserRequest.setUsername(createTeacherRequest.getUsername());
        createUserRequest.setRoleName(RoleEnum.TEACHER);
        User createdUser = userService.createUser(createUserRequest);

        TeacherEntity teacher = new TeacherEntity();
        teacher.setUserId(createdUser.getId());
        teacher.setSchoolAges(createTeacherRequest.getSchoolAges());
        teacher.setPhone(createTeacherRequest.getPhone());
        teacher.setAddress(createTeacherRequest.getAddress());
        teacher.setName(createTeacherRequest.getName());
        return teacherRepository.save(teacher);
    }

    public Optional<TeacherEntity> getTeacher(ObjectId id) {
        return teacherRepository.findById(id);
    }

    public TeacherEntity updateTeacher(ObjectId id, TeacherEntity teacherEntity) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id " + id);
        }
        teacherEntity.setId(id);
        return teacherRepository.save(teacherEntity);
    }

    public void deleteTeacher(ObjectId id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id " + id);
        }
        teacherRepository.deleteById(id);
    }

    public TeacherDto findLoggedTeacher() {
        UserDto userFromToken = userService.getUserFromToken();
        Optional<TeacherEntity> foundTeacher = teacherRepository.findByUserId(userFromToken.getId());
        if(foundTeacher.isEmpty()) {
            throw new UsernameNotFoundException("Teacher not found with given username");
        }
        TeacherDto teacherDto = new TeacherDto(userFromToken);
        teacherDto.setId(foundTeacher.get().getId());
        return teacherDto;
    }
}