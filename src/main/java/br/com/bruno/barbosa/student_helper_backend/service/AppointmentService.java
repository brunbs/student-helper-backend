package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.*;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.AppointmentStatusEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.handler.NotAuthorizedException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentsListResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.WeekAppointmentsResponse;
import br.com.bruno.barbosa.student_helper_backend.repository.AppointmentRepository;
import br.com.bruno.barbosa.student_helper_backend.util.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.bruno.barbosa.student_helper_backend.util.DateUtils.convertTwoDigitYearToFullYear;
import static br.com.bruno.barbosa.student_helper_backend.util.DateUtils.getLocalDateFromString;

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
            for (String time : request.getTimes()) {
                AppointmentEntity appointment = new AppointmentEntity();
                appointment.setDate(request.getDate());
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

    public AppointmentsListResponse getTeachersAppointments(String month, String year) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();

        List<WeekAppointmentsResponse> weekAppointmentsResponses = new ArrayList<>();

        int yearInt = convertTwoDigitYearToFullYear(Integer.parseInt(year));
        int monthInt = Integer.parseInt(month);

        YearMonth yearMonthObj = YearMonth.of(yearInt, monthInt);
        LocalDate firstDayOfMonth = yearMonthObj.atDay(1);
        LocalDate lastDayOfMonth = yearMonthObj.atEndOfMonth();

        // Ajusta para começar na semana (domingo anterior, se necessário)
        LocalDate startDate = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);

        // Último dia da busca pode ser o final da semana em que o último dia do mês está ou o final do próximo mês.
        LocalDate endDate = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        // Busca todas as consultas no intervalo ajustado
        List<AppointmentEntity> foundAppointments = appointmentRepository.findAllByTeacherIdAndDateBetweenOrderByDateAsc(
                loggedTeacher.getId(), startDate, endDate);

        List<AppointmentInfoDto> appointmentInfos = foundAppointments.stream().map(AppointmentInfoDto::new).toList();
        appointmentInfos.forEach(appointment -> {
            if(appointment.getStudentId() != null) {
                studentService.getStudent(appointment.getStudentId()).ifPresent(studentEntity -> appointment.setStudentName(studentEntity.getName()));
            }
        });

        List<WeekInfoDto> weeksInMonth = DateUtils.getWeeksInMonth(yearInt, monthInt);

        for (WeekInfoDto weekInfoDto : weeksInMonth) {
            WeekAppointmentsResponse weekResponse = new WeekAppointmentsResponse();
            weekResponse.setStartDay(weekInfoDto.getStartDate());
            weekResponse.setEndDay(weekInfoDto.getEndDate());

            List<AppointmentResponse> weekAppointments = new ArrayList<>();

            // Gerar todos os horários para o intervalo de datas
            for (LocalDate date = weekInfoDto.getStartDate(); !date.isAfter(weekInfoDto.getEndDate()); date = date.plusDays(1)) {
                for (int hour = 8; hour <= 22; hour++) {
                    LocalTime time = LocalTime.of(hour, 0);
                    LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

                    LocalDate finalDate = date;
                    // Verifique se o atendimento já existe no banco de dados
                    AppointmentEntity existingAppointment = appointmentRepository.findByTeacherIdAndDateAndTime(
                            loggedTeacher.getId(), finalDate, time.toString());

                    if (existingAppointment != null) {
                        weekAppointments.add(new AppointmentResponse(new AppointmentInfoDto(existingAppointment)));
                    } else {
                        // Adicionar o horário como CLOSED
                        AppointmentEntity newAppointment = new AppointmentEntity();
                        newAppointment.setTeacherId(loggedTeacher.getId());
                        newAppointment.setDate(finalDate);
                        newAppointment.setTime(time.toString());
                        newAppointment.setStatus(AppointmentStatusEnum.CLOSED.name());
                        AppointmentEntity savedAppointment = appointmentRepository.save(newAppointment);

                        AppointmentInfoDto closedAppointment = new AppointmentInfoDto(savedAppointment);

                        weekAppointments.add(new AppointmentResponse(closedAppointment));
                    }
                }
            }

            weekResponse.setAppointments(weekAppointments);
            weekAppointmentsResponses.add(weekResponse);
        }

        AppointmentsListResponse response = new AppointmentsListResponse();
        response.setWeeksAppointments(weekAppointmentsResponses);
        return response;
    }

    public void openAppointment(ObjectId appointmentId) {
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        foundAppointment.get().setStudentId(null);
        foundAppointment.get().setStatus(AppointmentStatusEnum.AVAILABLE.name());
        appointmentRepository.save(foundAppointment.get());
    }

    public void closeAppointment(ObjectId appointmentId) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        if(!foundAppointment.get().getTeacherId().equals(loggedTeacher.getId())) {
            throw new NotAuthorizedException("Apenas o professor do atendimento pode alterar o atendimento.");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.CLOSED.name());
        appointmentRepository.save(foundAppointment.get());
    }

    public void addLinkToAppointment(ObjectId appointmentId, String url) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        if(!foundAppointment.get().getTeacherId().equals(loggedTeacher.getId())) {
            throw new NotAuthorizedException("Apenas o professor do atendimento pode alterar o atendimento.");
        }
        foundAppointment.get().setLessonUrl(url);
        appointmentRepository.save(foundAppointment.get());
    }

}
