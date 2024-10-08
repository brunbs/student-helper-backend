package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.TeacherDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.TeacherEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.SchoolAgeEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.ResourceNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateTeacherRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.TeacherResponseToList;
import br.com.bruno.barbosa.student_helper_backend.repository.TeacherRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public TeacherEntity updateTeacher(CreateTeacherRequest createTeacherRequest) {
        TeacherDto loggedTeacher = findLoggedTeacher();
        Optional<TeacherEntity> teacher = getTeacher(loggedTeacher.getId());
        if(teacher.isEmpty()) {
            throw new ResourceNotFoundException("Teacher not found");
        }
        teacher.get().setId(loggedTeacher.getId());
        teacher.get().setName(createTeacherRequest.getName());
        teacher.get().setPhone(createTeacherRequest.getPhone());
        teacher.get().setAddress(createTeacherRequest.getAddress());
        teacher.get().setSchoolAges(createTeacherRequest.getSchoolAges());
        return teacherRepository.save(teacher.get());
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

    public List<TeacherResponseToList> findAllAvailableTeachers(SchoolAgeEnum schoolAge) {
        List<TeacherEntity> teachers = teacherRepository.findAllBySchoolAgesContaining(schoolAge.name());
        return teachers.stream().map(teacher -> new TeacherResponseToList(teacher.getId(), teacher.getName())).toList();
    }

    public TeacherEntity getTeacherProfile() {
        TeacherDto loggedTeacher = findLoggedTeacher();
        return getTeacher(loggedTeacher.getId()).get();
    }

}