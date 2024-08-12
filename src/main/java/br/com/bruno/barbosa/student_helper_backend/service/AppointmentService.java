package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.StudentDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.TeacherDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.UserDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.AppointmentStatusEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.repository.AppointmentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Transactional
    public void createAppointments(List<CreateAppointmentRequest> appointmentRequests) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();

        List<AppointmentEntity> appointments = new ArrayList<>();
        for (CreateAppointmentRequest request : appointmentRequests) {
            String date = request.getDate();
            for (String time : request.getTimes()) {
                AppointmentEntity appointment = new AppointmentEntity();
                appointment.setDay(date);
                appointment.setTime(time);
                appointment.setStatus(AppointmentStatusEnum.AVAILABLE.name());
                appointment.setTeacherId(loggedTeacher.getId());
                appointments.add(appointment);
            }
        }

        appointmentRepository.saveAll(appointments);
    }

    public void finishAppointment(ObjectId appointmentId) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Appointment not found");
        }
        if(foundAppointment.get().getTeacherId() != loggedTeacher.getId()) {
            throw new AppointmentException("User can't finish this appointment");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.COMPLETED.name());
        appointmentRepository.save(foundAppointment.get());
    }

    public void cancelAppointment(ObjectId appointmentId) {
        userService.isUserAuthenticated();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.AVAILABLE.name());
    }

    public void bookAppointment(ObjectId appointmentId) {
        StudentDto loggedStudent = studentService.findLoggedStudent();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.BOOKED.name());
        foundAppointment.get().setStudentId(loggedStudent.getId());
        appointmentRepository.save(foundAppointment.get());
    }

}
